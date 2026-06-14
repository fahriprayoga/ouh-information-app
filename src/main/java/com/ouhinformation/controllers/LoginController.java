package com.ouhinformation.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import com.ouhinformation.utils.MongoConfig;
import com.ouhinformation.utils.Auth;
import com.ouhinformation.utils.Session;
import com.ouhinformation.utils.Router;

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
    private Button backButton;

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
        
        // Add action for back button
        backButton.setOnAction(e -> Router.navigate("hello-view", "OUH Information App"));
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = showPasswordCheckBox.isSelected() ? visiblePasswordField.getText() : passwordField.getText();
        
        // Simple validation
        if (username == null || username.trim().isEmpty() || password == null || password.isEmpty()) {
            showMessage("Username atau password tidak boleh kosong", "-fx-text-fill: red;");
            return;
        }

        // Auth and get user data
        MongoDatabase database = MongoConfig.getDatabase();
        MongoCollection<Document> usersCollection = database.getCollection("users");
        
        Document user = usersCollection.find(Filters.eq("username", username)).first();

        if (user != null) {
            String storedPassword = user.getString("password");
            if (Auth.verifyPassword(password, storedPassword)) {
                Session session = Session.getInstance();
                
                Object idObj = user.get("id");
                if (idObj instanceof Integer) {
                    session.setUserId((Integer) idObj);
                } else {
                    session.setAttribute("userId", idObj);
                }
                
                session.setAttribute("name", user.getString("name"));
                session.setUsername(user.getString("username"));
                session.setAttribute("created_at", user.get("created_at"));

                Router.navigate("admin/admin", "Admin Dashboard");
            } else {
                showMessage("Password anda salah", "-fx-text-fill: red;");
            }
        } else {
            showMessage("Username tidak ditemukan", "-fx-text-fill: red;");
        }
    }

    private void showMessage(String message, String style) {
        messageLabel.setText(message);
        messageLabel.setStyle(style);
        
        PauseTransition pause = new PauseTransition(Duration.seconds(5));
        pause.setOnFinished(event -> messageLabel.setText(""));
        pause.play();
    }
}
