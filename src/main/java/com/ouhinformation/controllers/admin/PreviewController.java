package com.ouhinformation.controllers.admin;

import com.ouhinformation.components.ContentComponent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.List;

public class PreviewController {
    @FXML private Label previewTitle;
    @FXML private Label previewDesc;
    @FXML private VBox previewContentContainer;

    public void setData(String title, String description, List<ContentComponent> components) {
        previewTitle.setText(title);
        previewDesc.setText(description);
        
        previewContentContainer.getChildren().clear();
        for (ContentComponent comp : components) {
            previewContentContainer.getChildren().add(comp.renderView());
        }
    }
}
