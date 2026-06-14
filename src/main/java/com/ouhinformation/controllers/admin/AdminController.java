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
       manageDataButton.setOnAction(e -> loadManageDataView());
   }

   private void handleLogout() {
       Session.getInstance().clearSession();
       Router.navigate("login", "Login");
   }

   private void loadManageDataView() {
       try {
           javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/com/ouhinformation/fxml/admin/manage_data.fxml"));
           javafx.scene.Node view = loader.load();
           
           ManageDataController controller = loader.getController();
           controller.setAdminController(this);

           adminContentContainer.getChildren().setAll(view);
       } catch (java.io.IOException e) {
           e.printStackTrace();
       }
   }
   
   public void setCenterView(javafx.scene.Node view) {
       adminContentContainer.getChildren().setAll(view);
   }
}
