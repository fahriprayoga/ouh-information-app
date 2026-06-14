package com.ouhinformation.controllers.admin;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import com.ouhinformation.utils.Session;
import com.ouhinformation.utils.Router;

public class SettingsController {

    @FXML private Button logoutButton;
    @FXML private Button manageDataButton;
    @FXML private Button dashboardButton;
    @FXML private Button saveSettingsButton;

    @FXML
    public void initialize() {
        if (logoutButton != null) logoutButton.setOnAction(e -> handleLogout());
        if (manageDataButton != null) manageDataButton.setOnAction(e -> Router.navigate("admin/manage_data", "Kelola Data"));
        if (dashboardButton != null) dashboardButton.setOnAction(e -> Router.navigate("admin/admin", "Admin Dashboard"));
        
        if (saveSettingsButton != null) {
            saveSettingsButton.setOnAction(e -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Sukses");
                alert.setHeaderText(null);
                alert.setContentText("Pengaturan aplikasi berhasil diperbarui!");
                alert.showAndWait();
            });
        }
    }

    private void handleLogout() {
        Session.getInstance().clearSession();
        Router.navigate("login", "Login");
    }
}
