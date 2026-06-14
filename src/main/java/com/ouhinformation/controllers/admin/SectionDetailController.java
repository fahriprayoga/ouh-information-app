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
import com.ouhinformation.components.ListComponent;
import com.ouhinformation.components.ParagraphComponent;
import com.ouhinformation.utils.MongoConfig;
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
    @FXML private Button saveButton;
    @FXML private Button addHeadingBtn;
    @FXML private Button addParagrafBtn;
    @FXML private Button addListBtn;
    @FXML private VBox contentComponentsContainer;

    private AdminController adminController;
    private String currentSectionId;
    private final List<ContentComponent> components = new ArrayList<>();

    public void setAdminController(AdminController adminController) {
        this.adminController = adminController;
    }

    @FXML
    public void initialize() {
        backButton.setOnAction(e -> goBack());
        saveButton.setOnAction(e -> saveChanges());
        addHeadingBtn.setOnAction(e -> addComponent("heading"));
        addParagrafBtn.setOnAction(e -> addComponent("paragraf"));
        addListBtn.setOnAction(e -> addComponent("list"));
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

            headerTitle.setText("Detail: " + (title != null ? title : "Unknown"));
            titleField.setText(title != null ? title : "");
            descField.setText(desc != null ? desc : "");

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

    private void addComponent(String type) {
        ContentComponent comp;
        switch (type) {
            case "heading":
                comp = new HeadingComponent("");
                break;
            case "list":
                comp = new ListComponent("");
                break;
            default:
                comp = new ParagraphComponent("");
                break;
        }
        components.add(comp);
        renderAllComponents();
    }

    private void removeComponent(ContentComponent comp) {
        components.remove(comp);
        renderAllComponents();
    }

    private void moveUp(ContentComponent comp) {
        int index = components.indexOf(comp);
        if (index > 0) {
            Collections.swap(components, index, index - 1);
            renderAllComponents();
        }
    }

    private void moveDown(ContentComponent comp) {
        int index = components.indexOf(comp);
        if (index < components.size() - 1) {
            Collections.swap(components, index, index + 1);
            renderAllComponents();
        }
    }

    private void renderAllComponents() {
        contentComponentsContainer.getChildren().clear();

        if (components.isEmpty()) {
            Label emptyLabel = new Label("Belum ada komponen. Klik tombol di atas untuk menambahkan konten.");
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
        typeLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #6366f1; " +
                "-fx-background-color: #eef2ff; -fx-padding: 3 10; -fx-background-radius: 4;");

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

        header.getChildren().addAll(typeLabel, spacer, moveUpBtn, moveDownBtn, deleteBtn);

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
        List<Document> contentDocs = new ArrayList<>();
        for (ContentComponent comp : components) {
            contentDocs.add(comp.toDocument());
        }

        Document updates = new Document()
            .append("title", titleField.getText())
            .append("description", descField.getText())
            .append("content", contentDocs);

        Document updateDoc = new Document("$set", updates);

        collection.updateOne(new Document("_id", new ObjectId(currentSectionId)), updateDoc);

        headerTitle.setText("Detail: " + titleField.getText() + " (Tersimpan ✓)");
    }

    private void goBack() {
        if (adminController == null) return;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ouhinformation/fxml/admin/manage_data.fxml"));
            Node view = loader.load();

            ManageDataController controller = loader.getController();
            controller.setAdminController(adminController);

            adminController.setCenterView(view);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
