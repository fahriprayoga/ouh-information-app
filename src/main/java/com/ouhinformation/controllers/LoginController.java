package com.ouhinformation.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField visiblePasswordField;

    @FXML
    private CheckBox showPasswordCheckBox;

    @FXML
    private Button loginButton;

    @FXML
    private Label messageLabel;

    @FXML
    public void initialize() {
        // Initialize show/hide password functionality
        showPasswordCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                // Show password
                visiblePasswordField.setText(passwordField.getText());
                visiblePasswordField.setVisible(true);
                visiblePasswordField.setManaged(true);
                passwordField.setVisible(false);
                passwordField.setManaged(false);
            } else {
                // Hide password
                passwordField.setText(visiblePasswordField.getText());
                passwordField.setVisible(true);
                passwordField.setManaged(true);
                visiblePasswordField.setVisible(false);
                visiblePasswordField.setManaged(false);
            }
        });

        // Add listener for password field changes to keep both fields in sync
        passwordField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (showPasswordCheckBox.isSelected()) {
                visiblePasswordField.setText(newVal);
            }
        });

        visiblePasswordField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!showPasswordCheckBox.isSelected()) {
                passwordField.setText(newVal);
            }
        });

        // Add action for login button
        loginButton.setOnAction(e -> handleLogin());
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = showPasswordCheckBox.isSelected() ? visiblePasswordField.getText() : passwordField.getText();
        
        // Simple validation
        if (username == null || username.trim().isEmpty() || password == null || password.isEmpty()) {
            messageLabel.setText("Please enter both username and password");
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        // TODO: Add actual authentication logic here
        com.ouhinformation.utils.Router.navigate("hello-view", "Dashboard");
    }
}
