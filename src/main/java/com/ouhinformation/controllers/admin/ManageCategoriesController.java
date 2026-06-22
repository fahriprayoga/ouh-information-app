package com.ouhinformation.controllers.admin;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.ouhinformation.utils.MongoConfig;
import org.bson.Document;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class ManageCategoriesController {
    @FXML private Button addCategoryButton;
    @FXML private TextField newCategoryField;
    @FXML private VBox categoriesContainer;

    @FXML
    public void initialize() {
        if (addCategoryButton != null) addCategoryButton.setOnAction(e -> addNewCategory());
        loadData();
    }

    private void addNewCategory() {
        String name = newCategoryField.getText() == null ? "" : newCategoryField.getText().trim();
        if (name.isEmpty()) return;

        MongoDatabase db = MongoConfig.getDatabase();
        if (db == null) return;

        MongoCollection<Document> collection = db.getCollection("categories");
        // Simple duplicate check could be added here, but for simplicity we rely on inserts.
        collection.insertOne(new Document("name", name));
        
        newCategoryField.clear();
        loadData();
    }

    private void loadData() {
        categoriesContainer.getChildren().clear();
        MongoDatabase db = MongoConfig.getDatabase();
        if (db == null) return;

        MongoCollection<Document> collection = db.getCollection("categories");
        MongoCollection<Document> sectionCollection = db.getCollection("sections");

        for (Document doc : collection.find()) {
            String name = doc.getString("name");
            if (name == null || name.isEmpty()) continue;

            java.util.List<Document> mySections = new java.util.ArrayList<>();
            for (Document sDoc : sectionCollection.find(new Document("category", name))) {
                mySections.add(sDoc);
            }

            VBox card = new VBox();
            card.setSpacing(15);
            card.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 5, 0, 0, 2); -fx-border-color: #f1f5f9; -fx-border-radius: 8; -fx-border-width: 1;");

            HBox headerBox = new HBox();
            headerBox.setAlignment(Pos.CENTER_LEFT);
            headerBox.setSpacing(15);

            Label titleLabel = new Label(name);
            titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Button addButton = new Button("+ Tambah Informasi");
            addButton.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 15; -fx-background-radius: 6; -fx-cursor: hand;");
            addButton.setOnAction(e -> addSectionToCategory(name));

            Button editButton = new Button("Edit Kategori");
            editButton.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 15; -fx-background-radius: 6; -fx-cursor: hand;");
            editButton.setOnAction(e -> editCategory(name));

            Button deleteButton = new Button("Hapus Kategori");
            deleteButton.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 15; -fx-background-radius: 6; -fx-cursor: hand;");
            deleteButton.setOnAction(e -> deleteCategory(name));

            headerBox.getChildren().addAll(titleLabel, spacer, addButton, editButton, deleteButton);

            VBox sectionsBox = new VBox(5);
            if (mySections.isEmpty()) {
                 Label noInfo = new Label("Belum ada informasi di kategori ini.");
                 noInfo.setStyle("-fx-text-fill: #94a3b8; -fx-font-style: italic;");
                 sectionsBox.getChildren().add(noInfo);
            } else {
                 for (Document sDoc : mySections) {
                     String sTitle = sDoc.getString("title");
                     Label sLabel = new Label("• " + (sTitle != null ? sTitle : "Untitled"));
                     sLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #475569;");
                     sectionsBox.getChildren().add(sLabel);
                 }
            }

            card.getChildren().addAll(headerBox, sectionsBox);
            categoriesContainer.getChildren().add(card);
        }
    }

    private void deleteCategory(String name) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Konfirmasi Hapus");
        alert.setHeaderText("Hapus Kategori: " + name + "?");
        alert.setContentText("Tindakan ini tidak dapat dibatalkan.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                MongoDatabase db = MongoConfig.getDatabase();
                if (db == null) return;

                MongoCollection<Document> collection = db.getCollection("categories");
                collection.deleteOne(new Document("name", name));
                
                // Unset category from orphaned sections
                db.getCollection("sections").updateMany(
                        new Document("category", name),
                        new Document("$set", new Document("category", null))
                );
                
                loadData();
            }
        });
    }

    private void editCategory(String currentName) {
        javafx.scene.control.TextInputDialog dialog = new javafx.scene.control.TextInputDialog(currentName);
        dialog.setTitle("Edit Kategori");
        dialog.setHeaderText("Ubah nama kategori: " + currentName);
        dialog.setContentText("Nama Baru:");

        dialog.showAndWait().ifPresent(newName -> {
            String trimmedName = newName.trim();
            if (!trimmedName.isEmpty() && !trimmedName.equals(currentName)) {
                MongoDatabase db = MongoConfig.getDatabase();
                if (db != null) {
                    // Update the master category list
                    db.getCollection("categories").updateOne(
                            new Document("name", currentName),
                            new Document("$set", new Document("name", trimmedName))
                    );

                    // Sync the update across all associated sections
                    db.getCollection("sections").updateMany(
                            new Document("category", currentName),
                            new Document("$set", new Document("category", trimmedName))
                    );

                    loadData();
                }
            }
        });
    }

    private static class SectionItem {
        org.bson.types.ObjectId id;
        String title;
        public SectionItem(org.bson.types.ObjectId id, String title) {
            this.id = id;
            this.title = title == null || title.isEmpty() ? "Untitled" : title;
        }
        @Override
        public String toString() {
            return title;
        }
    }

    private void addSectionToCategory(String categoryName) {
        MongoDatabase db = MongoConfig.getDatabase();
        if (db == null) return;
        
        MongoCollection<Document> collection = db.getCollection("sections");
        java.util.List<SectionItem> availableSections = new java.util.ArrayList<>();
        
        for (Document doc : collection.find()) {
            String currentCat = doc.getString("category");
            if (currentCat == null || !currentCat.equals(categoryName)) {
                availableSections.add(new SectionItem(doc.getObjectId("_id"), doc.getString("title")));
            }
        }
        
        if (availableSections.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Info");
            alert.setHeaderText(null);
            alert.setContentText("Tidak ada data informasi yang tersedia untuk dipindahkan ke kategori ini.");
            alert.showAndWait();
            return;
        }

        javafx.scene.control.ChoiceDialog<SectionItem> dialog = new javafx.scene.control.ChoiceDialog<>(availableSections.get(0), availableSections);
        dialog.setTitle("Tambah Informasi ke Kategori");
        dialog.setHeaderText("Pilih informasi untuk dimasukkan ke kategori: " + categoryName);
        dialog.setContentText("Pilih Informasi:");

        dialog.showAndWait().ifPresent(selected -> {
            collection.updateOne(
                    new Document("_id", selected.id),
                    new Document("$set", new Document("category", categoryName))
            );
            loadData();
        });
    }
}
