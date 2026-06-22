package com.ouhinformation.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import com.ouhinformation.utils.Router;

public class WelcomeController {

    @FXML
    private Button startExploreButton;

    @FXML
    private Button adminLoginButton;

    @FXML
    private javafx.scene.image.ImageView bannerImage;

    @FXML
    public void initialize() {
        startExploreButton.setOnAction(e -> Router.navigate("hello-view"));
        adminLoginButton.setOnAction(e -> Router.navigate("login"));
    }
}
