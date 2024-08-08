package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.IdUser;
import il.cshaifasweng.OCSFMediatorExample.entities.Message;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;

public class UserLogInWithIDController {

    @FXML
    private Text error_message;

    @FXML
    private TextField user_id;

    private int id = -1;

    public static IdUser idUser;

    @FXML
    public void initialize() {
        EventBus.getDefault().register(this);
        error_message.setVisible(false);
    }

    @FXML
    public void destroy() {
        EventBus.getDefault().unregister(this);
    }


    @FXML
    void log_in_button(ActionEvent event) {
        if (user_id.getText().isEmpty()) {
            error_message.setVisible(true);
            error_message.setText("Please fill in your ID");
            return;
        }

        try {
            id = Integer.parseInt(user_id.getText());
        } catch (NumberFormatException e) {
            user_id.setText("");
            error_message.setVisible(true);
            error_message.setText("ID must contain only numbers");
            return;
        }

        String numString = Integer.toString(id);

        if (id < 0 || numString.length() != 9) {
            error_message.setVisible(true);
            error_message.setText("ID must contain 9 digits");
            user_id.setText("");
            return;
        }

        try {
            LogIn(numString);
        } catch (IOException e) {
            e.printStackTrace();
            error_message.setVisible(true);
            error_message.setText("Could not communicate with the server!");
        }

    }

    private void LogIn(String numString) throws IOException {
        Message message = new Message(10, "#login");
        message.setObject2(numString);
        SimpleClient.getClient().sendToServer(message);
    }

    @Subscribe
    public void onUpdateIdUserEvent(BaseEventBox event) {
        if(event.getId()==8) {
            Message message = event.getMessage();
            Platform.runLater(() -> {
                switch (message.getMessage()) {
                    case "#userNotFound":
                        error_message.setVisible(true);
                        error_message.setText("You have not done any activity in our cinema yet!");
                        break;
                    case "#alreadyLoggedIn":
                        error_message.setVisible(true);
                        error_message.setText("This user ID is already logged in!");
                        break;
                    case "#loginConfirmed":
                        error_message.setVisible(true);
                        error_message.setText("You have successfully logged in");
                        idUser = (IdUser) message.getObject();
                        try {
                            SimpleChatClient.setRoot("HomePage");
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        break;
                    case "#serverError":
                        error_message.setVisible(true);
                        error_message.setText("An error occurred on the server!");
                        break;
                    default:
                        error_message.setVisible(true);
                        error_message.setText("An unknown error occurred!");
                        break;
                }
            });
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
