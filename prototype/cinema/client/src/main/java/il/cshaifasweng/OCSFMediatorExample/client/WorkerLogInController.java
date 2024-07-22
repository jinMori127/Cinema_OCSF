/**
 * Sample Skeleton for 'WorkerLogIn.fxml' Controller Class
 */

package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import il.cshaifasweng.OCSFMediatorExample.entities.Message;

public class WorkerLogInController {

    @FXML
    private Text output;

    @FXML // fx:id="LogInBut"
    private Button LogInBut; // Value injected by FXMLLoader

    @FXML // fx:id="passwordTF"
    private TextField passwordTF; // Value injected by FXMLLoader

    @FXML // fx:id="userNameTF"
    private TextField userNameTF; // Value injected by FXMLLoader


    @FXML
    void LogIn(ActionEvent event) {
        if (userNameTF.getText().isEmpty() || passwordTF.getText().isEmpty()) {
            output.setText("Please enter your username and password");
        }

        Message message = new Message(21,"#LogIn_worker");
        message.setObject(userNameTF.getText());
        message.setObject2(passwordTF.getText());
        try {
            SimpleClient.getClient().sendToServer(message);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @FXML
    public void initialize() {
        EventBus.getDefault().register(this);
    }

    @Subscribe
    public void onUpdateIdUserEvent(UpdateIdUserEvent event) {
        Message message = event.getMessage();
        Platform.runLater(() -> {
            switch (message.getMessage()) {
                case "#loginWorkerFailedUserName":
                    output.setText("Incorrect username or password");
                    break;
                case "#loginWorker":
                    output.setText("Successfully logged in");
                    break;
                case "#loginWorkerFailedPass":
                    output.setText("Incorrect username or password");
                    break;
            }
        });


}
