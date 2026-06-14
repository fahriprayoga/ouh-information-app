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
import org.bson.Document;

public class ManageDataController {
    @FXML
    private VBox sectionsListContainer;

    private AdminController adminController;

    public void setAdminController(AdminController adminController) {
        this.adminController = adminController;
    }

    @FXML
    public void initialize() {
        loadSections();
    }

    private void loadSections() {
        sectionsListContainer.getChildren().clear();
        MongoDatabase db = MongoConfig.getDatabase();
        if (db == null) return;

        MongoCollection<Document> collection = db.getCollection("sections");
        for (Document doc : collection.find()) {
            String id = doc.getObjectId("_id").toString();
            String title = doc.getString("title");

            HBox row = new HBox();
            row.setAlignment(Pos.CENTER_LEFT);
            row.setSpacing(15);
            row.setStyle("-fx-background-color: #f8fafc; -fx-padding: 15; -fx-background-radius: 8; -fx-border-color: #e2e8f0; -fx-border-radius: 8;");

            Label titleLabel = new Label(title != null ? title : "Untitled");
            titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Button detailButton = new Button("Detail / Edit");
            detailButton.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 15; -fx-background-radius: 6; -fx-cursor: hand;");
            detailButton.setOnAction(e -> showDetail(id));

            row.getChildren().addAll(titleLabel, spacer, detailButton);
            sectionsListContainer.getChildren().add(row);
        }
    }

    private void showDetail(String sectionId) {
        if (adminController == null) return;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ouhinformation/fxml/admin/section_detail.fxml"));
            Node view = loader.load();
            
            SectionDetailController controller = loader.getController();
            controller.setAdminController(adminController);
            controller.loadSectionData(sectionId);

            adminController.setCenterView(view);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
