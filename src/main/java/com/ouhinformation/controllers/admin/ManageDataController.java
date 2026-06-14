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
    @FXML private VBox sectionsContainer;

    @FXML
    public void initialize() {
        if (refreshButton != null) refreshButton.setOnAction(e -> loadData());
        loadData();
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

            card.getChildren().addAll(titleLabel, spacer, detailButton);
            sectionsContainer.getChildren().add(card);
        }
    }

    private void goToDetail(String sectionId) {
        Router.navigateWithParam("admin/section_detail", "Edit Section", sectionId);
    }
}
