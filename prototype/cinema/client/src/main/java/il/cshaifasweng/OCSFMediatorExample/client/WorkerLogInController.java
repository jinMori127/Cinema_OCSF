package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.scene.control.PasswordField;
import il.cshaifasweng.OCSFMediatorExample.entities.Worker;
import il.cshaifasweng.OCSFMediatorExample.entities.Message;
import javafx.application.Platform;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import java.io.IOException;
import javafx.scene.control.CheckBox;


public class WorkerLogInController {

    @FXML
    private Text output;

    @FXML
    private PasswordField passwordTF;

    @FXML
    private TextField password_text;

    @FXML
    private TextField userNameTF;

    @FXML
    private CheckBox show_password_check_bock;

    @FXML
    void LogIn(ActionEvent event) {
        if (userNameTF.getText().isEmpty() || passwordTF.getText().isEmpty()) {
            output.setText("Please enter your username and password");
            return;
        }
        Message message = new Message(21, "#LogIn_worker");
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
        passwordTF.textProperty().bindBidirectional(password_text.textProperty());
    }

    public static Worker worker;

    @Subscribe
    public void onUpdateIdUserEvent(BaseEventBox event) {
        if(event.getId() == BaseEventBox.get_event_id("LOGIN")) {
            Message message = event.getMessage();
            Platform.runLater(() -> {
                switch (message.getMessage()) {
                    case "#loginWorkerFailedUserName", "#loginWorkerFailedPass":
                        output.setText("Incorrect username or password");
                        break;
                    case "#loginWorker":
                        output.setText("Successfully logged in");
                        worker = (Worker) message.getObject();
                        try {
                            SimpleChatClient.setRoot("HomePage");
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        break;
                }
            });
        }
    }

    @FXML
    private void handleShowPassword(ActionEvent event) {
        if (show_password_check_bock.isSelected()) {
            password_text.setVisible(true);
            password_text.setManaged(true);
            passwordTF.setVisible(false);
            passwordTF.setManaged(false);
        } else {
            password_text.setVisible(false);
            password_text.setManaged(false);
            passwordTF.setVisible(true);
            passwordTF.setManaged(true);
        }
    }

    @Subscribe
    public void change_content1(BeginContentChangeEnent event)
    {
        System.out.println(event.getPage());
        EventBus.getDefault().unregister(this);
        EventBus.getDefault().post(new ContentChangeEvent(event.getPage()));
    }
}
