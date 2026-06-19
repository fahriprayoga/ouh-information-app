package com.ouhinformation.components;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.bson.Document;

import java.io.File;

/**
 * Komponen gambar — mendukung URL atau file lokal, dengan caption.
 * Type: "image"
 */
public class ImageComponent extends ContentComponent {

    private TextField urlEditor;
    private TextField captionEditor;
    private String caption;

    public ImageComponent(String url, String caption) {
        super(url);
        this.caption = caption != null ? caption : "";
    }

    @Override
    public String getType() {
        return "image";
    }

    @Override
    public String getLabel() {
        return "Gambar";
    }

    @Override
    public Node renderEditor() {
        VBox root = new VBox(10);
        
        VBox urlBox = new VBox(5);
        Label urlLabel = new Label("URL Gambar atau Path:");
        urlLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");
        
        HBox inputRow = new HBox(10);
        urlEditor = new TextField(content);
        urlEditor.setPromptText("https://example.com/image.jpg atau C:\\path\\to\\image.jpg");
        HBox.setHgrow(urlEditor, javafx.scene.layout.Priority.ALWAYS);
        
        Button browseBtn = new Button("Browse...");
        browseBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Pilih Gambar");
            fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
            );
            File selectedFile = fileChooser.showOpenDialog(browseBtn.getScene().getWindow());
            if (selectedFile != null) {
                urlEditor.setText(selectedFile.toURI().toString());
            }
        });
        
        inputRow.getChildren().addAll(urlEditor, browseBtn);
        urlBox.getChildren().addAll(urlLabel, inputRow);

        VBox captionBox = new VBox(5);
        Label captionLabel = new Label("Caption:");
        captionLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");
        captionEditor = new TextField(caption);
        captionEditor.setPromptText("Keterangan gambar...");
        captionBox.getChildren().addAll(captionLabel, captionEditor);

        root.getChildren().addAll(urlBox, captionBox);
        return root;
    }

    @Override
    public Node renderView() {
        VBox container = new VBox(10);
        
        String align = textAlign != null ? textAlign : "CENTER";
        if ("LEFT".equals(align)) {
            container.setAlignment(Pos.CENTER_LEFT);
        } else if ("RIGHT".equals(align)) {
            container.setAlignment(Pos.CENTER_RIGHT);
        } else {
            container.setAlignment(Pos.CENTER);
        }

        try {
            if (content != null && !content.isEmpty()) {
                ImageView imageView = new ImageView();
                Image img = new Image(content, true); // background loading
                imageView.setImage(img);
                imageView.setPreserveRatio(true);
                
                // Map fontSize as percentage of container width
                int widthPercent = (fontSize != null) ? fontSize : 100;
                imageView.fitWidthProperty().bind(container.widthProperty().multiply(widthPercent / 100.0));
                
                container.getChildren().add(imageView);
            }
        } catch (Exception e) {
            container.getChildren().add(new Label("Gagal memuat gambar: " + content));
        }

        if (caption != null && !caption.isEmpty()) {
            Label capLabel = new Label(caption);
            capLabel.setWrapText(true);
            capLabel.setStyle(
                "-fx-font-size: 14px; " +
                "-fx-text-fill: #64748b; " +
                "-fx-font-style: italic;"
            );
            container.getChildren().add(capLabel);
        }

        return container;
    }

    @Override
    public String getContentFromEditor() {
        if (captionEditor != null) caption = captionEditor.getText();
        return urlEditor != null ? urlEditor.getText() : content;
    }

    @Override
    public Document toDocument() {
        return super.toDocument().append("caption", caption);
    }

    public static ImageComponent fromDoc(Document doc) {
        return new ImageComponent(doc.getString("content"), doc.getString("caption"));
    }
}
