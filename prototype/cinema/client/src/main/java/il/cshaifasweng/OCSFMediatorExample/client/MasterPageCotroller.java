package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.event.ActionEvent;

import java.io.IOException;

public class MasterPageCotroller {
    public static String page = null;

    @FXML
    private StackPane content_area;

    @FXML
    private Menu catalog_menu;

    @FXML
    public void initialize() {
        // Initialize common UI components and behavior here
        // catalog_menu.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> setContent("Movie_editing_details.fxml"));
        if (page == null || page.equals(""))
            setContent("HomePage.fxml");
        else
            setContent(page+".fxml");

    }

    public void setContent(String file) {
        FXMLLoader new_content = null;
        try {
             new_content = new FXMLLoader(getClass().getResource(file));
             System.out.println("we are here");

        } catch (Exception e) {

            e.printStackTrace();
            return;
        }

        content_area.getChildren().clear();
        try {
            content_area.getChildren().setAll((Node) new_content.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @FXML
    void handle_action(ActionEvent event) {
        MenuItem source = (MenuItem) event.getSource();
        String menuItemText = source.getText();
        if (menuItemText.equals("Home page")) {
            setContent("HomePage.fxml");
        }
        else if(menuItemText.equals("id")){
            setContent("UserLoginWithID.fxml");
        }
        else if (menuItemText.equals("worker"))
        {
            setContent("WorkerLogIn.fxml");
        } else if (menuItemText.equals("Purchases")) {
            setContent("UserPurchases.fxml");
        }
        else if(menuItemText.equals("Complains"))
        {
            setContent("UserComplains.fxml");
        }
        else if (menuItemText.equals("Sing out")) {
            setContent("Movie_editing_details.fxml");
        }


    }


}
