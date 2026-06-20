package com.ouhinformation.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.animation.PauseTransition;
import javafx.scene.shape.SVGPath;
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
    private Button togglePasswordButton;

    @FXML
    private SVGPath eyeIcon;

    private boolean isPasswordVisible = false;

    private static final String EYE_SVG = "M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z M 12 9 a 3 3 0 1 1 0 6 a 3 3 0 1 1 0 -6";
    private static final String EYE_OFF_SVG = "M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24 M 1 1 L 23 23";

    @FXML
    private Button loginButton;

    @FXML
    private Label messageLabel;

    @FXML
    private Button backButton;

    @FXML
    public void initialize() {
        // Initialize show/hide password functionality
        togglePasswordButton.setOnAction(e -> {
            isPasswordVisible = !isPasswordVisible;
            if (isPasswordVisible) {
                // Show password
                visiblePasswordField.setText(passwordField.getText());
                visiblePasswordField.setVisible(true);
                visiblePasswordField.setManaged(true);
                passwordField.setVisible(false);
                passwordField.setManaged(false);
                eyeIcon.setContent(EYE_OFF_SVG);
            } else {
                // Hide password
                passwordField.setText(visiblePasswordField.getText());
                passwordField.setVisible(true);
                passwordField.setManaged(true);
                visiblePasswordField.setVisible(false);
                visiblePasswordField.setManaged(false);
                eyeIcon.setContent(EYE_SVG);
            }
        });

        // Add listener for password field changes to keep both fields in sync
        passwordField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (isPasswordVisible) {
                visiblePasswordField.setText(newVal);
            }
        });

        visiblePasswordField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!isPasswordVisible) {
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
        String password = isPasswordVisible ? visiblePasswordField.getText() : passwordField.getText();
        
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
                Session.getInstance().setUsername(username);
                Router.navigate("admin/manage_data", "Kelola Data");
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
