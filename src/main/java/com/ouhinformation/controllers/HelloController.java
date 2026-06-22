package com.ouhinformation.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.geometry.Pos;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.ouhinformation.components.ContentComponent;
import com.ouhinformation.utils.MongoConfig;
import com.ouhinformation.utils.DateFormatter;
import com.ouhinformation.utils.Router;

import org.bson.Document;
import java.util.List;

public class HelloController {
    @FXML private Button loginButton;
    @FXML private VBox navbarVBox;
    @FXML private Label contentTitle;
    @FXML private Label contentDesc;
    @FXML private VBox contentContainer;
    @FXML private Label contentCreated;
    @FXML private Label contentUpdated;
    @FXML private TextField commentNameField;
    @FXML private TextArea commentMessageField;
    @FXML private Button submitCommentButton;
    @FXML private VBox commentsListContainer;

    private String currentSectionId;

    @FXML
    private void initialize() {
        loginButton.setOnAction(event -> handleLogin());
        if (submitCommentButton != null) submitCommentButton.setOnAction(e -> submitComment());
        loadSections();
    }

    private void handleLogin() {
        Router.navigate("login");
    }

    private Button activeNavButton;

    private void setActiveButton(Button btn, boolean indent) {
        if (activeNavButton != null) {
            boolean wasIndented = activeNavButton.getUserData() != null && (Boolean) activeNavButton.getUserData();
            activeNavButton.setStyle("-fx-font-size: 14px; -fx-font-weight: 500; -fx-text-fill: #d1fae5; " +
                    (wasIndented ? "-fx-padding: 10 15 10 30; " : "-fx-padding: 12 15 12 15; ") +
                    "-fx-cursor: hand; -fx-background-radius: 6; -fx-background-color: transparent;");
        }
        activeNavButton = btn;
        if (activeNavButton != null) {
            activeNavButton.setStyle("-fx-font-size: 14px; -fx-font-weight: 600; -fx-text-fill: #047857; " +
                    (indent ? "-fx-padding: 10 15 10 30; " : "-fx-padding: 12 15 12 15; ") +
                    "-fx-cursor: hand; -fx-background-radius: 6; -fx-background-color: white;");
            activeNavButton.setUserData(indent);
        }
    }

    private Button createNavButton(Document doc, boolean indent) {
        String title = doc.getString("title");
        Button btn = new Button(title != null ? title : "Untitled");
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.getStyleClass().add("flat");
        btn.setAlignment(javafx.geometry.Pos.BASELINE_LEFT);
        btn.setUserData(indent);
        
        String baseStyle = "-fx-font-size: 14px; -fx-font-weight: 500; -fx-text-fill: #d1fae5; " +
                (indent ? "-fx-padding: 10 15 10 30; " : "-fx-padding: 12 15 12 15; ") +
                "-fx-cursor: hand; -fx-background-radius: 6; -fx-background-color: transparent;";
        
        btn.setStyle(baseStyle);
        
        btn.setOnMouseEntered(e -> {
            if (btn != activeNavButton) {
                btn.setStyle("-fx-font-size: 14px; -fx-font-weight: 600; -fx-text-fill: white; " +
                        (indent ? "-fx-padding: 10 15 10 30; " : "-fx-padding: 12 15 12 15; ") +
                        "-fx-cursor: hand; -fx-background-radius: 6; -fx-background-color: rgba(255, 255, 255, 0.1);");
            }
        });
        btn.setOnMouseExited(e -> {
            if (btn != activeNavButton) {
                btn.setStyle(baseStyle);
            }
        });

        btn.setOnAction(e -> {
            setActiveButton(btn, indent);
            displayContent(doc);
        });
        
        return btn;
    }

    private void loadSections() {
        MongoDatabase db = MongoConfig.getDatabase();
        if (db == null) return;
        
        MongoCollection<Document> collection = db.getCollection("sections");
        boolean first = true;
        
        java.util.Map<String, java.util.List<Document>> categoryMap = new java.util.LinkedHashMap<>();
        java.util.List<Document> uncategorized = new java.util.ArrayList<>();
        
        for (Document doc : collection.find()) {
            String category = doc.getString("category");
            if (category == null || category.trim().isEmpty()) {
                uncategorized.add(doc);
            } else {
                categoryMap.computeIfAbsent(category.trim(), k -> new java.util.ArrayList<>()).add(doc);
            }
        }
        
        for (java.util.Map.Entry<String, java.util.List<Document>> entry : categoryMap.entrySet()) {
            String category = entry.getKey();
            
            Button toggleBtn = new Button(category.toUpperCase());
            toggleBtn.setMaxWidth(Double.MAX_VALUE);
            toggleBtn.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            toggleBtn.setStyle("-fx-font-size: 13px; -fx-font-weight: 800; -fx-text-fill: #a7f3d0; -fx-padding: 10 15 5 15; -fx-cursor: hand; -fx-background-color: transparent;");
            
            javafx.scene.shape.SVGPath arrowIcon = new javafx.scene.shape.SVGPath();
            String arrowDown = "M19.5 8.25l-7.5 7.5-7.5-7.5";
            String arrowRight = "M8.25 4.5l7.5 7.5-7.5 7.5";
            arrowIcon.setContent(arrowDown);
            arrowIcon.setFill(javafx.scene.paint.Color.TRANSPARENT);
            arrowIcon.setStroke(javafx.scene.paint.Color.web("#a7f3d0"));
            arrowIcon.setStrokeWidth(2.0);
            arrowIcon.setStrokeLineCap(javafx.scene.shape.StrokeLineCap.ROUND);
            arrowIcon.setStrokeLineJoin(javafx.scene.shape.StrokeLineJoin.ROUND);
            
            toggleBtn.setGraphic(arrowIcon);
            toggleBtn.setGraphicTextGap(10.0);
            
            VBox dropdownItems = new VBox();
            
            toggleBtn.setOnAction(e -> {
                boolean isVisible = !dropdownItems.isVisible();
                dropdownItems.setVisible(isVisible);
                dropdownItems.setManaged(isVisible);
                arrowIcon.setContent(isVisible ? arrowDown : arrowRight);
            });
            
            dropdownItems.setVisible(true);
            dropdownItems.setManaged(true);
            
            navbarVBox.getChildren().add(toggleBtn);
            
            for (Document doc : entry.getValue()) {
                Button btn = createNavButton(doc, true);
                dropdownItems.getChildren().add(btn);
                if (first) {
                    setActiveButton(btn, true);
                    displayContent(doc);
                    first = false;
                }
            }
            navbarVBox.getChildren().add(dropdownItems);
        }

        for (Document doc : uncategorized) {
            Button btn = createNavButton(doc, false);
            navbarVBox.getChildren().add(btn);
            if (first) {
                setActiveButton(btn, false);
                displayContent(doc);
                first = false;
            }
        }
    }

    private void displayContent(Document doc) {
        String title = doc.getString("title");
        String desc = doc.getString("description");
        contentTitle.setText(title != null ? title : "");
        contentDesc.setText(desc != null ? desc : "");
        
        contentContainer.getChildren().clear();
        List<Document> contents = doc.getList("content", Document.class);
        if (contents != null) {
            for (Document contentDoc : contents) {
                ContentComponent comp = ContentComponent.fromDocument(contentDoc);
                if (comp != null) {
                    javafx.scene.Node viewNode = comp.renderView();
                    int mb = comp.getMarginBottom() != null ? comp.getMarginBottom() : 15;
                    VBox.setMargin(viewNode, new javafx.geometry.Insets(0, 0, mb, 0));
                    contentContainer.getChildren().add(viewNode);
                }
            }
        }
        
        String createdBy = doc.getString("createdBy");
        Object createdAtObj = doc.get("createdAt");
        String createdAt = createdAtObj != null ? com.ouhinformation.utils.DateFormatter.format(createdAtObj.toString()) : null;
        
        if (createdBy != null && createdAt != null && !createdAt.isEmpty()) {
            contentCreated.setText("Dibuat oleh " + createdBy + " pada " + createdAt);
        } else {
            contentCreated.setText("");
        }
        
        List<Document> updated = doc.getList("updated", Document.class);
        if (updated != null && !updated.isEmpty()) {
            Document lastUpdate = updated.get(updated.size() - 1);
            String by = lastUpdate.getString("by");
            Object atObj = lastUpdate.get("at");
            String at = atObj != null ? com.ouhinformation.utils.DateFormatter.format(atObj.toString()) : null;
            if (by != null && at != null && !at.isEmpty()) {
                contentUpdated.setText("Terakhir diubah oleh " + by + " pada " + at);
            } else {
                 contentUpdated.setText("");
            }
        } else {
            contentUpdated.setText("");
        }

        // Load comments for this section
        Object idObj = doc.get("_id");
        if (idObj != null) {
            currentSectionId = idObj.toString();
            loadComments(currentSectionId);
        }
    }

    private void submitComment() {
        if (currentSectionId == null) return;

        String name = commentNameField.getText() == null ? "" : commentNameField.getText().trim();
        String message = commentMessageField.getText() == null ? "" : commentMessageField.getText().trim();

        if (name.isEmpty() || message.isEmpty()) return;

        MongoDatabase db = MongoConfig.getDatabase();
        if (db == null) return;

        String now = java.time.ZonedDateTime.now(java.time.ZoneOffset.UTC)
                .format(java.time.format.DateTimeFormatter.ISO_INSTANT);

        Document comment = new Document("sectionId", currentSectionId)
                .append("name", name)
                .append("message", message)
                .append("createdAt", now)
                .append("reply", null)
                .append("repliedBy", null)
                .append("repliedAt", null);

        db.getCollection("comments").insertOne(comment);

        commentNameField.clear();
        commentMessageField.clear();
        loadComments(currentSectionId);
    }

    private void loadComments(String sectionId) {
        if (commentsListContainer == null) return;
        commentsListContainer.getChildren().clear();

        MongoDatabase db = MongoConfig.getDatabase();
        if (db == null) return;

        MongoCollection<Document> coll = db.getCollection("comments");
        java.util.List<Document> comments = new java.util.ArrayList<>();
        for (Document c : coll.find(new Document("sectionId", sectionId)).sort(new Document("createdAt", -1))) {
            comments.add(c);
        }

        if (comments.isEmpty()) {
            Label empty = new Label("Belum ada komentar. Jadilah yang pertama!");
            empty.setStyle("-fx-text-fill: #94a3b8; -fx-font-style: italic; -fx-font-size: 13px;");
            commentsListContainer.getChildren().add(empty);
            return;
        }

        for (Document c : comments) {
            VBox card = new VBox(6);
            card.setStyle("-fx-background-color: #f8fafc; -fx-padding: 12; -fx-background-radius: 8; -fx-border-color: #e2e8f0; -fx-border-radius: 8; -fx-border-width: 1;");

            // Header
            HBox header = new HBox(8);
            header.setAlignment(Pos.CENTER_LEFT);

            String cName = c.getString("name");
            Label nameLabel = new Label(cName != null ? cName : "Anonim");
            nameLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #334155;");

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Object cDate = c.get("createdAt");
            Label dateLabel = new Label(cDate != null ? DateFormatter.format(cDate.toString()) : "");
            dateLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #94a3b8;");

            header.getChildren().addAll(nameLabel, spacer, dateLabel);

            // Message
            String msg = c.getString("message");
            Label msgLabel = new Label(msg != null ? msg : "");
            msgLabel.setWrapText(true);
            msgLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #475569;");

            card.getChildren().addAll(header, msgLabel);

            // Admin reply
            String reply = c.getString("reply");
            if (reply != null && !reply.isEmpty()) {
                VBox replyBox = new VBox(3);
                replyBox.setStyle("-fx-background-color: #ecfdf5; -fx-padding: 10; -fx-background-radius: 6; -fx-border-color: #6ee7b7; -fx-border-radius: 6; -fx-border-width: 1;");

                String repliedBy = c.getString("repliedBy");
                Object repliedAt = c.get("repliedAt");

                Label replyHeader = new Label("\u21AA Balasan dari " + (repliedBy != null ? repliedBy : "Admin") +
                        (repliedAt != null ? " \u2022 " + DateFormatter.format(repliedAt.toString()) : ""));
                replyHeader.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #059669;");

                Label replyLabel = new Label(reply);
                replyLabel.setWrapText(true);
                replyLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #064e3b;");

                replyBox.getChildren().addAll(replyHeader, replyLabel);
                card.getChildren().add(replyBox);
            }

            commentsListContainer.getChildren().add(card);
        }
    }
}
