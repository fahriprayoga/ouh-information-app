module com.ouhinformation {
    requires javafx.controls;
    requires javafx.fxml;
    requires atlantafx.base;

    // MongoDB modules
    requires org.mongodb.driver.sync.client;
    requires org.mongodb.bson;
    requires org.mongodb.driver.core;

    // Buka package controller untuk JavaFX FXML
    opens com.ouhinformation.controllers to javafx.fxml;

    opens com.ouhinformation to javafx.fxml;
    exports com.ouhinformation;
}