package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Message;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.TextField;
import org.greenrobot.eventbus.EventBus;

import java.awt.*;
import java.io.IOException;
import static il.cshaifasweng.OCSFMediatorExample.client.SimpleClient.client;

public class HostController {
    static String current_host = "localhost";

    @FXML
    private TextField hostTextField;
    @FXML
    private TextField portTextField;


    @FXML
    public void submit_func(javafx.event.ActionEvent actionEvent) {
        String host = hostTextField.getText();
        current_host = host;
        int portg = Integer.parseInt(portTextField.getText());
        client =  new SimpleClient(host,portg);
        try{
            client.openConnection();
            Platform.runLater(() -> {
				SimpleChatClient.setWindowTitle("masterPage");
				try {
					SimpleChatClient.setRoot("");
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }
    @FXML
    public void initialize() {
    hostTextField.setText("localhost");
    portTextField.setText("3000");
    }
}
