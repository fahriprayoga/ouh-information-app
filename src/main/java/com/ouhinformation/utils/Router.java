package com.ouhinformation.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

/**
 * Utility class for managing routing and screen navigation in JavaFX.
 */
public class Router {

    private static Stage primaryStage;

    /**
     * Sets the primary stage of the application. 
     * This should be called once from the main Application class (e.g. HelloApplication).
     *
     * @param stage The main application Stage.
     */
    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    /**
     * Gets the main application Stage.
     */
    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * Navigates to a new view and returns the FXMLLoader for further interaction with the controller.
     */
    public static FXMLLoader navigate(String fxmlFile, String title) {
        if (primaryStage == null) {
            throw new IllegalStateException("Primary stage not set in Router. Call Router.setPrimaryStage() first.");
        }

        try {
            URL resource = Router.class.getResource("/com/ouhinformation/fxml/" + fxmlFile+".fxml");
            if (resource == null) {
                resource = Router.class.getResource("/fxml/" + fxmlFile+".fxml");
            }
            
            if (resource == null) {
                throw new IOException("Cannot find FXML file: " + fxmlFile);
            }

            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();

            if (primaryStage.getScene() == null) {
                primaryStage.setScene(new Scene(root));
            } else {
                primaryStage.getScene().setRoot(root);
            }

            if (title != null && !title.isEmpty()) {
                primaryStage.setTitle(title);
            }

            if (!primaryStage.isShowing()) {
                primaryStage.show();
            }

            return loader;

        } catch (IOException e) {
            System.err.println("Error navigating to view: " + fxmlFile);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Navigates with a parameter specifically for SectionDetailController.
     */
    public static void navigateWithParam(String fxmlFile, String title, String sectionId) {
        FXMLLoader loader = navigate(fxmlFile, title);
        if (loader != null && loader.getController() instanceof com.ouhinformation.controllers.admin.SectionDetailController) {
            com.ouhinformation.controllers.admin.SectionDetailController controller = loader.getController();
            controller.loadSectionData(sectionId);
        }
    }

    /**
     * Navigates to a new view given the FXML filename without changing title.
     *
     * @param fxmlFile The FXML filename.
     */
    public static void navigate(String fxmlFile) {
        navigate(fxmlFile, null);
    }
}
