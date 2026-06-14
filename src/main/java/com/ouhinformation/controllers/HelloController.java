package com.ouhinformation.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
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

    private void loadSections() {
        MongoDatabase db = MongoConfig.getDatabase();
        if (db == null) return;
        
        MongoCollection<Document> collection = db.getCollection("sections");
        boolean first = true;
        
        for (Document doc : collection.find()) {
            String title = doc.getString("title");
            
            Button btn = new Button(title != null ? title : "Untitled");
            btn.setMaxWidth(Double.MAX_VALUE);
            btn.getStyleClass().add("flat");
            btn.setAlignment(javafx.geometry.Pos.BASELINE_LEFT);
            btn.setStyle("-fx-font-size: 14px; -fx-font-weight: 500; -fx-text-fill: #475569; -fx-padding: 12 15 12 15; -fx-cursor: hand; -fx-background-radius: 6;");
            
            btn.setOnAction(e -> displayContent(doc));
            
            navbarVBox.getChildren().add(btn);
            
            if (first) {
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
                String type = contentDoc.getString("type");
                String contentStr = contentDoc.getString("content");
                
                if ("paragraf".equals(type) || "text".equals(type)) {
                    Label p = new Label(contentStr);
                    p.setWrapText(true);
                    p.setStyle("-fx-font-size: 15px; -fx-text-fill: #475569; -fx-line-spacing: 5px;");
                    contentContainer.getChildren().add(p);
                } else if ("image".equals(type)) {
                    // Logic for image if applicable
                }
            }
        }
        
        String createdBy = doc.getString("createdBy");
        Object createdAtObj = doc.get("createdAt");
        String createdAt = createdAtObj != null ? createdAtObj.toString() : null;
        
        if (createdBy != null && createdAt != null) {
            contentCreated.setText("Dibuat oleh " + createdBy + " pada " + createdAt);
        } else {
            contentCreated.setText("");
        }
        
        List<Document> updated = doc.getList("updated", Document.class);
        if (updated != null && !updated.isEmpty()) {
            Document lastUpdate = updated.get(updated.size() - 1);
            String by = lastUpdate.getString("by");
            Object atObj = lastUpdate.get("at");
            String at = atObj != null ? atObj.toString() : null;
            if (by != null && at != null) {
                contentUpdated.setText("Terakhir diubah oleh " + by + " pada " + at);
            } else {
                 contentUpdated.setText("");
            }
        } else {
            contentUpdated.setText("");
        }
    }
}
