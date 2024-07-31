package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Message;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.event.ActionEvent;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import javafx.stage.Screen;

import java.io.IOException;

public class MasterPageCotroller {

    @FXML
    private BorderPane border_pane;

    @FXML
    private StackPane content_area;

    @FXML
    private Menu catalog_menu;

    @Subscribe
    public void change_content(ContentChangeEvent event)
    {

        Platform.runLater(()->{
            setContent(event.getPage()+".fxml");

        });
        System.out.println(content_area.getLayoutX());
        Platform.runLater(()->{
            javafx.geometry.Rectangle2D screenBounds = Screen.getPrimary().getBounds();
            double screenWidth = screenBounds.getWidth();
            double screenHeight = screenBounds.getHeight();
            content_area.layoutXProperty().setValue(30);
        });
        System.out.println(content_area.getLayoutX());

    }

    @FXML
    public void initialize() {

        EventBus.getDefault().register(this);

        // Initialize common UI components and behavior here
        // catalog_menu.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> setContent("Movie_editing_details.fxml"));

        setContent("HomePage.fxml");


    }

    public void setContent(String file) {
        FXMLLoader new_content = null;
        try {
             new_content = new FXMLLoader(getClass().getResource(file));


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
            EventBus.getDefault().post(new BeginContentChangeEnent("HomePage"));
        }
        else if(menuItemText.equals("id")){
            EventBus.getDefault().post(new BeginContentChangeEnent("UserLoginWithID"));
        }
        else if (menuItemText.equals("worker"))
        {
            EventBus.getDefault().post(new BeginContentChangeEnent("WorkerLogIn"));
        } else if (menuItemText.equals("Purchases")) {
            EventBus.getDefault().post(new BeginContentChangeEnent("UserPurchases"));
        }
        else if(menuItemText.equals("Complains"))
        {
            EventBus.getDefault().post(new BeginContentChangeEnent("UserComplains"));
        }

        else if (menuItemText.equals("Sign out")) {
            Message m = new Message(30,"#SignOut");
            try {
                SimpleClient.getClient().sendToServer(m);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            EventBus.getDefault().post(new BeginContentChangeEnent("HomePage"));
        }
        else if (menuItemText.equals("MovieEditDetails")) {
            EventBus.getDefault().post(new BeginContentChangeEnent("Movie_editing_details"));
        } else if (menuItemText.equals("Catalog")) {
            System.out.println("what the fuck");
            EventBus.getDefault().post(new BeginContentChangeEnent("Catalog"));

        }


    }



}
