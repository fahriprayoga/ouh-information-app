package com.ouhinformation.controllers.admin;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.ouhinformation.components.ContentComponent;
import com.ouhinformation.components.HeadingComponent;
import com.ouhinformation.components.ImageComponent;
import com.ouhinformation.components.ListComponent;
import com.ouhinformation.components.ParagraphComponent;
import com.ouhinformation.utils.MongoConfig;
import javafx.scene.control.*;
import com.ouhinformation.utils.Router;
import javafx.scene.control.Alert;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SectionDetailController {
    @FXML private Button backButton;
    @FXML private Label headerTitle;
    @FXML private TextField titleField;
    @FXML private TextField descField;
    @FXML private ComboBox<String> categoryComboBox;
    @FXML private Button saveButton;
    @FXML private Button previewButton;
    @FXML private Button addHeadingBtn;
    @FXML private Button addParagrafBtn;
    @FXML private Button addListBtn;
    @FXML private Button addImageBtn;
    @FXML private VBox contentComponentsContainer;

    private String currentSectionId;
    private final List<ContentComponent> components = new ArrayList<>();

    @FXML
    public void initialize() {
        backButton.setOnAction(e -> goBack());
        saveButton.setOnAction(e -> saveChanges());
        previewButton.setOnAction(e -> handlePreview());
        addHeadingBtn.setOnAction(e -> addComponent("heading"));
        addParagrafBtn.setOnAction(e -> addComponent("paragraf"));
        addListBtn.setOnAction(e -> addComponent("list"));
        addImageBtn.setOnAction(e -> addComponent("image"));
    }

    public void loadSectionData(String sectionId) {
        this.currentSectionId = sectionId;
        MongoDatabase db = MongoConfig.getDatabase();
        if (db == null) return;

        MongoCollection<Document> collection = db.getCollection("sections");
        Document doc = collection.find(new Document("_id", new ObjectId(sectionId))).first();

        if (doc != null) {
            String title = doc.getString("title");
            String desc = doc.getString("description");
            String category = doc.getString("category");

            headerTitle.setText("Detail: " + (title != null ? title : "Unknown"));
            titleField.setText(title != null ? title : "");
            descField.setText(desc != null ? desc : "");
            
            categoryComboBox.getItems().clear();
            categoryComboBox.getItems().add("Tidak Ada");
            MongoCollection<Document> catColl = db.getCollection("categories");
            for (Document d : catColl.find()) {
                categoryComboBox.getItems().add(d.getString("name"));
            }
            categoryComboBox.setValue(category != null ? category : "Tidak Ada");

            // Load existing content components
            components.clear();
            List<Document> contents = doc.getList("content", Document.class);
            if (contents != null) {
                for (Document contentDoc : contents) {
                    ContentComponent comp = ContentComponent.fromDocument(contentDoc);
                    if (comp != null) {
                        components.add(comp);
                    }
                }
            }
            renderAllComponents();
        }
    }

    private void syncComponentsFromUI() {
        for (ContentComponent comp : components) {
            comp.setContent(comp.getContentFromEditor());
        }
    }

    private void addComponent(String type) {
        syncComponentsFromUI();
        ContentComponent comp;
        switch (type) {
            case "heading":
                comp = new HeadingComponent("");
                break;
            case "list":
                comp = new ListComponent("");
                break;
            case "image":
                comp = new ImageComponent("", "");
                break;
            default:
                comp = new ParagraphComponent("");
                break;
        }
        components.add(comp);
        renderAllComponents();
    }

    private void removeComponent(ContentComponent comp) {
        syncComponentsFromUI();
        components.remove(comp);
        renderAllComponents();
    }

    private void moveUp(ContentComponent comp) {
        syncComponentsFromUI();
        int index = components.indexOf(comp);
        if (index > 0) {
            Collections.swap(components, index, index - 1);
            renderAllComponents();
        }
    }

    private void moveDown(ContentComponent comp) {
        syncComponentsFromUI();
        int index = components.indexOf(comp);
        if (index < components.size() - 1) {
            Collections.swap(components, index, index + 1);
            renderAllComponents();
        }
    }

    private void renderAllComponents() {
        contentComponentsContainer.getChildren().clear();

        if (components.isEmpty()) {
            Label emptyLabel = new Label("Belum ada komponen. pilih menu disamping untuk menambahkan konten.");
            emptyLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #94a3b8; -fx-padding: 20;");
            contentComponentsContainer.getChildren().add(emptyLabel);
            return;
        }

        for (int i = 0; i < components.size(); i++) {
            ContentComponent comp = components.get(i);
            contentComponentsContainer.getChildren().add(createComponentCard(comp, i));
        }
    }

    private Node createComponentCard(ContentComponent comp, int index) {
        VBox card = new VBox(8);
        card.setStyle(
            "-fx-background-color: #f8fafc; -fx-background-radius: 8; " +
            "-fx-border-color: #e2e8f0; -fx-border-radius: 8; -fx-padding: 12;"
        );

        // Header row: type label + action buttons
        HBox header = new HBox(8);
        header.setAlignment(Pos.CENTER_LEFT);

        Label typeLabel = new Label(comp.getLabel());
        typeLabel.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: white; " +
                "-fx-background-color: #10b981; -fx-padding: 2 8; -fx-background-radius: 4;");

        // Styling Controls
        HBox stylingControls = new HBox(10);
        stylingControls.setAlignment(Pos.CENTER_LEFT);
        stylingControls.setStyle("-fx-padding: 0 10;");

        HBox alignGroup = new HBox(0);
        ToggleButton leftBtn = new ToggleButton("L");
        ToggleButton centerBtn = new ToggleButton("C");
        ToggleButton rightBtn = new ToggleButton("R");
        
        String btnStyle = "-fx-background-radius: 0; -fx-padding: 4 10;";
        leftBtn.setStyle("-fx-background-radius: 4 0 0 4; -fx-padding: 4 10;");
        centerBtn.setStyle(btnStyle);
        rightBtn.setStyle("-fx-background-radius: 0 4 4 0; -fx-padding: 4 10;");

        leftBtn.setSelected("LEFT".equals(comp.getTextAlign()));
        centerBtn.setSelected("CENTER".equals(comp.getTextAlign()));
        rightBtn.setSelected("RIGHT".equals(comp.getTextAlign()));
        
        leftBtn.setOnAction(e -> comp.setTextAlign("LEFT"));
        centerBtn.setOnAction(e -> comp.setTextAlign("CENTER"));
        rightBtn.setOnAction(e -> comp.setTextAlign("RIGHT"));
        
        ToggleGroup tg = new ToggleGroup();
        leftBtn.setToggleGroup(tg);
        centerBtn.setToggleGroup(tg);
        rightBtn.setToggleGroup(tg);
        
        alignGroup.getChildren().addAll(leftBtn, centerBtn, rightBtn);
        
        if ("image".equals(comp.getType())) {
            ComboBox<String> widthPicker = new ComboBox<>();
            widthPicker.getItems().addAll("25%", "50%", "75%", "100%");
            
            Integer currentSize = comp.getFontSize();
            int w = currentSize != null ? currentSize : 100;
            if (w <= 25) widthPicker.setValue("25%");
            else if (w <= 50) widthPicker.setValue("50%");
            else if (w <= 75) widthPicker.setValue("75%");
            else widthPicker.setValue("100%");

            widthPicker.setStyle("-fx-background-color: transparent; -fx-border-color: #e2e8f0; -fx-border-radius: 4;");
            widthPicker.setOnAction(e -> {
                String val = widthPicker.getValue().replace("%", "");
                comp.setFontSize(Integer.parseInt(val));
            });
            
            stylingControls.getChildren().addAll(widthPicker, alignGroup);
            
        } else {
            ColorPicker colorPicker = new ColorPicker(javafx.scene.paint.Color.valueOf(comp.getColor() != null ? comp.getColor() : "#475569"));
            colorPicker.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
            colorPicker.setPrefWidth(100);
            colorPicker.setOnAction(e -> {
                String hex = String.format("#%02X%02X%02X",
                    (int)(colorPicker.getValue().getRed() * 255),
                    (int)(colorPicker.getValue().getGreen() * 255),
                    (int)(colorPicker.getValue().getBlue() * 255));
                comp.setColor(hex);
            });

            ComboBox<Integer> sizePicker = new ComboBox<>();
            sizePicker.getItems().addAll(10, 12, 14, 16, 18, 20, 24, 28, 32);
            sizePicker.setValue(comp.getFontSize() != null ? comp.getFontSize() : 14);
            sizePicker.setStyle("-fx-background-color: transparent; -fx-border-color: #e2e8f0; -fx-border-radius: 4;");
            sizePicker.setOnAction(e -> comp.setFontSize(sizePicker.getValue()));
            
            stylingControls.getChildren().addAll(colorPicker, sizePicker, alignGroup);
        }

        ComboBox<Integer> marginPicker = new ComboBox<>();
        marginPicker.getItems().addAll(0, 5, 10, 15, 20, 30, 40, 50, 80, 100);
        marginPicker.setValue(comp.getMarginBottom() != null ? comp.getMarginBottom() : 15);
        marginPicker.setStyle("-fx-background-color: transparent; -fx-border-color: #e2e8f0; -fx-border-radius: 4;");
        marginPicker.setOnAction(e -> comp.setMarginBottom(marginPicker.getValue()));
        stylingControls.getChildren().add(marginPicker);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button moveUpBtn = new Button("▲");
        moveUpBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #64748b; -fx-cursor: hand; -fx-font-size: 12px; -fx-padding: 2 6;");
        moveUpBtn.setOnAction(e -> moveUp(comp));
        if (index == 0) moveUpBtn.setDisable(true);

        Button moveDownBtn = new Button("▼");
        moveDownBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #64748b; -fx-cursor: hand; -fx-font-size: 12px; -fx-padding: 2 6;");
        moveDownBtn.setOnAction(e -> moveDown(comp));
        if (index == components.size() - 1) moveDownBtn.setDisable(true);

        Button deleteBtn = new Button("🗑");
        deleteBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #ef4444; -fx-cursor: hand; -fx-font-size: 14px; -fx-padding: 2 6;");
        deleteBtn.setOnAction(e -> removeComponent(comp));

        header.getChildren().addAll(typeLabel, stylingControls, spacer, moveUpBtn, moveDownBtn, deleteBtn);

        // Editor
        Node editorNode = comp.renderEditor();

        card.getChildren().addAll(header, editorNode);
        return card;
    }

    private void saveChanges() {
        if (currentSectionId == null) return;

        MongoDatabase db = MongoConfig.getDatabase();
        if (db == null) return;

        MongoCollection<Document> collection = db.getCollection("sections");

        // Build content array from components
        List<Document> contentList = new ArrayList<>();
        for (ContentComponent comp : components) {
            contentList.add(comp.toDocument());
        }

        String username = com.ouhinformation.utils.Session.getInstance().getUsername();
        if (username == null) username = "admin";
        String now = java.time.ZonedDateTime.now(java.time.ZoneOffset.UTC)
                .format(java.time.format.DateTimeFormatter.ISO_INSTANT);

        Document updateLog = new Document("by", username).append("at", now);
        
        String selCat = categoryComboBox.getValue();
        String finalCat = (selCat == null || selCat.trim().isEmpty() || selCat.equals("Tidak Ada")) ? null : selCat.trim();
        
        Document updateDoc = new Document("$set", new Document("title", titleField.getText())
                .append("description", descField.getText())
                .append("category", finalCat)
                .append("content", contentList)
                .append("updatedAt", now))
                .append("$push", new Document("updated", updateLog));

        collection.updateOne(new Document("_id", new ObjectId(currentSectionId)), updateDoc);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sukses");
        alert.setHeaderText(null);
        alert.setContentText("Konten berhasil disimpan ke database!");
        alert.showAndWait();

        headerTitle.setText("Detail: " + titleField.getText());
    }

    private void handlePreview() {
        syncComponentsFromUI();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ouhinformation/fxml/admin/preview.fxml"));
            VBox previewRoot = loader.load();
            
            PreviewController controller = loader.getController();
            controller.setData(titleField.getText(), descField.getText(), components);
            
            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setTitle("Preview Konten - " + titleField.getText());
            stage.setScene(new javafx.scene.Scene(previewRoot));
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stage.show();
        } catch (java.io.IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Gagal membuka preview");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    private void goBack() {
        Router.navigate("admin/manage_data", "Kelola Data");
    }
}
