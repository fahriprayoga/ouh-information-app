package com.ouhinformation.controllers.admin;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

import com.ouhinformation.utils.Session;
import com.ouhinformation.utils.Router;

public class AdminController {
   @FXML
   private Button logoutButton;

   @FXML
   private Button manageDataButton;

   @FXML
   private Button settingsButton;

   @FXML
   private VBox adminContentContainer;

   @FXML
   public void initialize() {
       logoutButton.setOnAction(e -> handleLogout());
   }

   private void handleLogout() {
       Session.getInstance().clearSession();
       Router.navigate("login", "Login");
   }
}
