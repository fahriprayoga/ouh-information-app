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
    @FXML private javafx.scene.control.TextField newSectionField;
    @FXML private Button addSectionButton;
    @FXML private VBox sectionsContainer;

    @FXML
    public void initialize() {
        if (addSectionButton != null) addSectionButton.setOnAction(e -> addNewSection());
        loadData();
    }

    private void addNewSection() {
        String title = newSectionField.getText() == null ? "" : newSectionField.getText().trim();
        if (title.isEmpty()) return;

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
        
        newSectionField.clear();
        goToDetail(newId);
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
            card.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 10, 0, 0, 4); -fx-border-color: #f1f5f9; -fx-border-radius: 12; -fx-border-width: 1;");

            VBox infoBox = new VBox(8);
            Label titleLabel = new Label(title != null ? title : "Untitled");
            titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");
            
            HBox badgesBox = new HBox(10);
            badgesBox.setAlignment(Pos.CENTER_LEFT);
            
            String category = doc.getString("category");
            Label catBadge = new Label(category != null ? category : "Tanpa Kategori");
            catBadge.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #047857; -fx-background-color: #d1fae5; -fx-padding: 3 8; -fx-background-radius: 10;");
            
            java.util.List<Document> updated = doc.getList("updated", Document.class);
            String updateText = "Belum ada modifikasi";
            if (updated != null && !updated.isEmpty()) {
                Document lastUpdate = updated.get(updated.size() - 1);
                updateText = "Diubah: " + com.ouhinformation.utils.DateFormatter.format(lastUpdate.getString("at"));
            } else {
                Object createdAtObj = doc.get("createdAt");
                if (createdAtObj != null) {
                    updateText = "Dibuat: " + com.ouhinformation.utils.DateFormatter.format(createdAtObj.toString());
                }
            }
            Label metaLabel = new Label(updateText);
            metaLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #94a3b8;");
            
            badgesBox.getChildren().addAll(catBadge, metaLabel);
            infoBox.getChildren().addAll(titleLabel, badgesBox);

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Button detailButton = new Button("Detail / Edit");
            detailButton.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 15; -fx-background-radius: 6; -fx-cursor: hand;");
            detailButton.setOnAction(e -> goToDetail(id));

            Button deleteButton = new Button("Hapus");
            deleteButton.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 15; -fx-background-radius: 6; -fx-cursor: hand;");
            deleteButton.setOnAction(e -> deleteSection(id, title));

            card.getChildren().addAll(infoBox, spacer, detailButton, deleteButton);
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
