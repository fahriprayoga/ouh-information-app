package com.ouhinformation.controllers.admin;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.ouhinformation.utils.MongoConfig;
import com.ouhinformation.utils.DateFormatter;
import com.ouhinformation.utils.Session;
import org.bson.Document;
import org.bson.types.ObjectId;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class ManageCommentsController {
    @FXML private VBox commentsContainer;

    @FXML
    public void initialize() {
        loadData();
    }

    private void loadData() {
        commentsContainer.getChildren().clear();
        MongoDatabase db = MongoConfig.getDatabase();
        if (db == null) return;

        MongoCollection<Document> commentsColl = db.getCollection("comments");
        MongoCollection<Document> sectionsColl = db.getCollection("sections");

        // Group comments by sectionId
        java.util.Map<String, java.util.List<Document>> grouped = new java.util.LinkedHashMap<>();
        for (Document c : commentsColl.find().sort(new Document("createdAt", -1))) {
            String sid = c.getString("sectionId");
            if (sid == null) continue;
            grouped.computeIfAbsent(sid, k -> new java.util.ArrayList<>()).add(c);
        }

        if (grouped.isEmpty()) {
            Label empty = new Label("Belum ada komentar dari pengunjung.");
            empty.setStyle("-fx-text-fill: #94a3b8; -fx-font-style: italic; -fx-font-size: 14px;");
            commentsContainer.getChildren().add(empty);
            return;
        }

        for (java.util.Map.Entry<String, java.util.List<Document>> entry : grouped.entrySet()) {
            String sectionId = entry.getKey();
            java.util.List<Document> comments = entry.getValue();

            // Find section title
            String sectionTitle = "Informasi Tidak Ditemukan";
            try {
                Document sec = sectionsColl.find(new Document("_id", new ObjectId(sectionId))).first();
                if (sec != null && sec.getString("title") != null) {
                    sectionTitle = sec.getString("title");
                }
            } catch (Exception ignored) {}

            // Section header
            Label sectionHeader = new Label("📄 " + sectionTitle);
            sectionHeader.setStyle("-fx-font-size: 16px; -fx-font-weight: 800; -fx-text-fill: #1e293b; -fx-padding: 5 0 5 0;");
            commentsContainer.getChildren().add(sectionHeader);

            for (Document comment : comments) {
                VBox card = buildCommentCard(comment);
                commentsContainer.getChildren().add(card);
            }

            // Divider
            Region divider = new Region();
            divider.setPrefHeight(1);
            divider.setStyle("-fx-background-color: #e2e8f0;");
            VBox.setMargin(divider, new javafx.geometry.Insets(10, 0, 10, 0));
            commentsContainer.getChildren().add(divider);
        }
    }

    private VBox buildCommentCard(Document comment) {
        VBox card = new VBox(8);
        card.setStyle("-fx-background-color: #f8fafc; -fx-padding: 15; -fx-background-radius: 8; -fx-border-color: #e2e8f0; -fx-border-radius: 8; -fx-border-width: 1;");

        // Header row: name + timestamp
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);

        String name = comment.getString("name");
        Label nameLabel = new Label(name != null ? name : "Anonim");
        nameLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #334155;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Object createdAt = comment.get("createdAt");
        Label dateLabel = new Label(createdAt != null ? DateFormatter.format(createdAt.toString()) : "");
        dateLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #94a3b8;");

        header.getChildren().addAll(nameLabel, spacer, dateLabel);

        // Message
        String message = comment.getString("message");
        Label msgLabel = new Label(message != null ? message : "");
        msgLabel.setWrapText(true);
        msgLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #475569; -fx-line-spacing: 4;");

        card.getChildren().addAll(header, msgLabel);

        // Show existing reply if present
        String reply = comment.getString("reply");
        if (reply != null && !reply.isEmpty()) {
            VBox replyBox = new VBox(4);
            replyBox.setStyle("-fx-background-color: #ecfdf5; -fx-padding: 10; -fx-background-radius: 6; -fx-border-color: #6ee7b7; -fx-border-radius: 6; -fx-border-width: 1;");

            String repliedBy = comment.getString("repliedBy");
            Object repliedAt = comment.get("repliedAt");

            Label replyHeader = new Label("↪ Balasan dari " + (repliedBy != null ? repliedBy : "Admin") +
                    (repliedAt != null ? " • " + DateFormatter.format(repliedAt.toString()) : ""));
            replyHeader.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #059669;");

            Label replyLabel = new Label(reply);
            replyLabel.setWrapText(true);
            replyLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #064e3b;");

            replyBox.getChildren().addAll(replyHeader, replyLabel);
            card.getChildren().add(replyBox);
        }

        // Action buttons
        HBox actions = new HBox(10);
        actions.setAlignment(Pos.CENTER_RIGHT);

        Button replyBtn = new Button(reply != null && !reply.isEmpty() ? "Edit Balasan" : "Balas");
        replyBtn.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 6 15; -fx-background-radius: 6; -fx-cursor: hand; -fx-font-size: 12px;");
        replyBtn.setOnAction(e -> replyComment(comment));

        Button deleteBtn = new Button("Hapus");
        deleteBtn.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 6 15; -fx-background-radius: 6; -fx-cursor: hand; -fx-font-size: 12px;");
        deleteBtn.setOnAction(e -> deleteComment(comment));

        actions.getChildren().addAll(replyBtn, deleteBtn);
        card.getChildren().add(actions);

        return card;
    }

    private void replyComment(Document comment) {
        String existing = comment.getString("reply");
        javafx.scene.control.TextInputDialog dialog = new javafx.scene.control.TextInputDialog(existing != null ? existing : "");
        dialog.setTitle("Balas Komentar");
        dialog.setHeaderText("Balas komentar dari: " + (comment.getString("name") != null ? comment.getString("name") : "Anonim"));
        dialog.setContentText("Balasan:");

        dialog.showAndWait().ifPresent(replyText -> {
            if (!replyText.trim().isEmpty()) {
                MongoDatabase db = MongoConfig.getDatabase();
                if (db != null) {
                    String now = java.time.ZonedDateTime.now(java.time.ZoneOffset.UTC)
                            .format(java.time.format.DateTimeFormatter.ISO_INSTANT);
                    String username = Session.getInstance().getUsername();

                    db.getCollection("comments").updateOne(
                            new Document("_id", comment.getObjectId("_id")),
                            new Document("$set", new Document("reply", replyText.trim())
                                    .append("repliedBy", username != null ? username : "Admin")
                                    .append("repliedAt", now))
                    );
                    loadData();
                }
            }
        });
    }

    private void deleteComment(Document comment) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Konfirmasi Hapus");
        alert.setHeaderText("Hapus komentar ini?");
        alert.setContentText("Tindakan ini tidak dapat dibatalkan.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                MongoDatabase db = MongoConfig.getDatabase();
                if (db != null) {
                    db.getCollection("comments").deleteOne(new Document("_id", comment.getObjectId("_id")));
                    loadData();
                }
            }
        });
    }
}
