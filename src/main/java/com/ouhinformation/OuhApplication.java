package com.ouhinformation;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

import com.ouhinformation.utils.Router;
import atlantafx.base.theme.PrimerLight;

public class OuhApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
        Router.setPrimaryStage(stage);
        FXMLLoader fxmlLoader = new FXMLLoader(OuhApplication.class.getResource("fxml/login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("OUH Information App");
        stage.setScene(scene);
        stage.setMinWidth(1280);
        stage.setMinHeight(720);
        stage.setResizable(true);
        stage.setMaximized(true);
        stage.show();
    }
}
