package com.ouhinformation.controllers.admin;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import com.ouhinformation.utils.Session;
import com.ouhinformation.utils.Router;

public class AdminController {
   @FXML private Button logoutButton;
   @FXML private Button manageDataButton;
   @FXML private Button settingsButton;
   @FXML private Button dashboardButton;

   @FXML
   public void initialize() {
       if (logoutButton != null) logoutButton.setOnAction(e -> handleLogout());
       if (manageDataButton != null) manageDataButton.setOnAction(e -> Router.navigate("admin/manage_data", "Kelola Data"));
       if (settingsButton != null) settingsButton.setOnAction(e -> Router.navigate("admin/settings", "Pengaturan"));
       if (dashboardButton != null) dashboardButton.setOnAction(e -> Router.navigate("admin/admin", "Admin Dashboard"));
   }

   private void handleLogout() {
       Session.getInstance().clearSession();
       Router.navigate("login", "Login");
   }
}
