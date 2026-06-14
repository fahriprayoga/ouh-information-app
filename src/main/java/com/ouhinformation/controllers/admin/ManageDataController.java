package com.ouhinformation.controllers.admin;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.ouhinformation.utils.MongoConfig;
import com.ouhinformation.utils.Router;
import org.bson.Document;

public class ManageDataController {
    @FXML private Button refreshButton;
    @FXML private Button addSectionButton;
    @FXML private VBox sectionsContainer;

    @FXML
    public void initialize() {
        if (refreshButton != null) refreshButton.setOnAction(e -> loadData());
        if (addSectionButton != null) addSectionButton.setOnAction(e -> addNewSection());
        loadData();
    }

    private void addNewSection() {
        javafx.scene.control.TextInputDialog dialog = new javafx.scene.control.TextInputDialog();
        dialog.setTitle("Tambah Informasi Baru");
        dialog.setHeaderText("Masukkan judul informasi baru:");
        dialog.setContentText("Judul:");

        dialog.showAndWait().ifPresent(title -> {
            if (title.trim().isEmpty()) return;

            MongoDatabase db = MongoConfig.getDatabase();
            if (db == null) return;

            MongoCollection<Document> collection = db.getCollection("sections");
            
            String username = com.ouhinformation.utils.Session.getInstance().getUsername();
            if (username == null) username = "admin"; // Fallback

            String now = java.time.ZonedDateTime.now(java.time.ZoneOffset.UTC)
                    .format(java.time.format.DateTimeFormatter.ISO_INSTANT);

            Document newSection = new Document("title", title)
                    .append("content", new java.util.ArrayList<Document>())
                    .append("createdBy", username)
                    .append("createdAt", now)
                    .append("updated", new java.util.ArrayList<Document>());
            
            collection.insertOne(newSection);
            String newId = newSection.getObjectId("_id").toString();
            
            loadData(); // Refresh list
            goToDetail(newId); // Go to editor
        });
    }

    private void loadData() {
        sectionsContainer.getChildren().clear();
        MongoDatabase db = MongoConfig.getDatabase();
        if (db == null) return;

        MongoCollection<Document> collection = db.getCollection("sections");
        for (Document doc : collection.find()) {
            String id = doc.getObjectId("_id").toString();
            String title = doc.getString("title");

            HBox card = new HBox();
            card.setAlignment(Pos.CENTER_LEFT);
            card.setSpacing(15);
            card.setStyle("-fx-background-color: #f8fafc; -fx-padding: 15; -fx-background-radius: 8; -fx-border-color: #e2e8f0; -fx-border-radius: 8;");

            Label titleLabel = new Label(title != null ? title : "Untitled");
            titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Button detailButton = new Button("Detail / Edit");
            detailButton.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 15; -fx-background-radius: 6; -fx-cursor: hand;");
            detailButton.setOnAction(e -> goToDetail(id));

            Button deleteButton = new Button("Hapus");
            deleteButton.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 15; -fx-background-radius: 6; -fx-cursor: hand;");
            deleteButton.setOnAction(e -> deleteSection(id, title));

            card.getChildren().addAll(titleLabel, spacer, detailButton, deleteButton);
            sectionsContainer.getChildren().add(card);
        }
    }

    private void goToDetail(String sectionId) {
        Router.navigateWithParam("admin/section_detail", "Edit Section", sectionId);
    }

    private void deleteSection(String id, String title) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
        alert.setTitle("Konfirmasi Hapus");
        alert.setHeaderText("Hapus informasi: " + title + "?");
        alert.setContentText("Tindakan ini tidak dapat dibatalkan.");

        alert.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                MongoDatabase db = MongoConfig.getDatabase();
                if (db == null) return;

                MongoCollection<Document> collection = db.getCollection("sections");
                collection.deleteOne(new org.bson.Document("_id", new org.bson.types.ObjectId(id)));
                
                loadData();
            }
        });
    }
}
