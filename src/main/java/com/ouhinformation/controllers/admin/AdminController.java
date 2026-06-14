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
       if (dashboardButton != null) dashboardButton.setOnAction(e -> Router.navigate("admin/admin", "Admin Dashboard"));
       if (manageDataButton != null) manageDataButton.setOnAction(e -> Router.navigate("admin/manage_data", "Kelola Data"));
       if (settingsButton != null) settingsButton.setOnAction(e -> Router.navigate("admin/settings", "Pengaturan"));
       if (logoutButton != null) logoutButton.setOnAction(e -> handleLogout());
       
       highlightActiveMenu();
   }

   private void highlightActiveMenu() {
       String current = Router.getCurrentFxml();
       if (current == null) return;

       // Reset styles
       String inactiveStyle = "-fx-alignment: CENTER-LEFT; -fx-font-size: 14px; -fx-padding: 10 15 10 15; -fx-background-color: transparent; -fx-cursor: hand;";
       String activeStyle = "-fx-alignment: CENTER-LEFT; -fx-font-size: 14px; -fx-padding: 10 15 10 15; -fx-background-color: #f1f5f9; -fx-font-weight: bold;";

       if (dashboardButton != null) dashboardButton.setStyle(inactiveStyle);
       if (manageDataButton != null) manageDataButton.setStyle(inactiveStyle);
       if (settingsButton != null) settingsButton.setStyle(inactiveStyle);

       if (current.equals("admin/admin")) {
           if (dashboardButton != null) dashboardButton.setStyle(activeStyle);
       } else if (current.equals("admin/manage_data") || current.equals("admin/section_detail")) {
           if (manageDataButton != null) manageDataButton.setStyle(activeStyle);
       } else if (current.equals("admin/settings")) {
           if (settingsButton != null) settingsButton.setStyle(activeStyle);
       }
   }

    private void handleLogout() {
        Session.getInstance().clearSession();
        Router.navigate("hello-view", "OUH Information");
    }
}
