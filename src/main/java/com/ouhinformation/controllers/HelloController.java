package com.ouhinformation.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.ouhinformation.components.ContentComponent;
import com.ouhinformation.utils.MongoConfig;
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

    @FXML
    private void initialize() {
        loginButton.setOnAction(event -> handleLogin());
        loadSections();
    }

    private void handleLogin() {
        Router.navigate("login");
    }

    private Button activeNavButton;

    private void setActiveButton(Button btn, boolean indent) {
        if (activeNavButton != null) {
            boolean wasIndented = activeNavButton.getUserData() != null && (Boolean) activeNavButton.getUserData();
            activeNavButton.setStyle("-fx-font-size: 14px; -fx-font-weight: 500; -fx-text-fill: #475569; " +
                    (wasIndented ? "-fx-padding: 10 15 10 30; " : "-fx-padding: 12 15 12 15; ") +
                    "-fx-cursor: hand; -fx-background-radius: 6; -fx-background-color: transparent;");
        }
        activeNavButton = btn;
        if (activeNavButton != null) {
            activeNavButton.setStyle("-fx-font-size: 14px; -fx-font-weight: 600; -fx-text-fill: white; " +
                    (indent ? "-fx-padding: 10 15 10 30; " : "-fx-padding: 12 15 12 15; ") +
                    "-fx-cursor: hand; -fx-background-radius: 6; -fx-background-color: #10b981;");
            activeNavButton.setUserData(indent);
        }
    }

    private Button createNavButton(Document doc, boolean indent) {
        String title = doc.getString("title");
        Button btn = new Button("›  " + (title != null ? title : "Untitled"));
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.getStyleClass().add("flat");
        btn.setAlignment(javafx.geometry.Pos.BASELINE_LEFT);
        btn.setUserData(indent);
        
        String baseStyle = "-fx-font-size: 14px; -fx-font-weight: 500; -fx-text-fill: #475569; " +
                (indent ? "-fx-padding: 10 15 10 30; " : "-fx-padding: 12 15 12 15; ") +
                "-fx-cursor: hand; -fx-background-radius: 6; -fx-background-color: transparent;";
        
        btn.setStyle(baseStyle);
        
        btn.setOnMouseEntered(e -> {
            if (btn != activeNavButton) {
                btn.setStyle("-fx-font-size: 14px; -fx-font-weight: 600; -fx-text-fill: #10b981; " +
                        (indent ? "-fx-padding: 10 15 10 30; " : "-fx-padding: 12 15 12 15; ") +
                        "-fx-cursor: hand; -fx-background-radius: 6; -fx-background-color: #ecfdf5;");
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
            
            Button toggleBtn = new Button("▼  " + category.toUpperCase());
            toggleBtn.setMaxWidth(Double.MAX_VALUE);
            toggleBtn.setAlignment(javafx.geometry.Pos.BASELINE_LEFT);
            toggleBtn.setStyle("-fx-font-size: 13px; -fx-font-weight: 800; -fx-text-fill: #64748b; -fx-padding: 10 15 5 15; -fx-cursor: hand; -fx-background-color: transparent;");
            
            VBox dropdownItems = new VBox();
            
            toggleBtn.setOnAction(e -> {
                boolean isVisible = !dropdownItems.isVisible();
                dropdownItems.setVisible(isVisible);
                dropdownItems.setManaged(isVisible);
                toggleBtn.setText((isVisible ? "▼  " : "▶  ") + category.toUpperCase());
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
    }
}
