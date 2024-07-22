package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Message;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;

public class UserLogInWithIDController {

    @FXML
    private TextField user_id;

    private int id = -1;

    @FXML
    public void initialize() {
        EventBus.getDefault().register(this);
    }

    @FXML
    public void destroy() {
        EventBus.getDefault().unregister(this);
    }

    @FXML
    void back_button(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MasterPage.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void log_in_button(ActionEvent event) {
        if (user_id.getText().isEmpty()) {
            showAlert("Validation Error", "Please fill in your ID");
            return;
        }

        try {
            id = Integer.parseInt(user_id.getText());
        } catch (NumberFormatException e) {
            Platform.runLater(() -> showAlert("Input Error", "ID must contain only numbers"));
            user_id.setText("");
            return;
        }

        String numString = Integer.toString(id);

        if (id < 0 || numString.length() != 9) {
            Platform.runLater(() -> showAlert("Input Error", "ID must contain 9 digits"));
            user_id.setText("");
            return;
        }

        try {
            LogIn(numString);
        } catch (IOException e) {
            showAlert("Server Error", "Could not communicate with the server.");
            e.printStackTrace();
        }
    }

    private void LogIn(String numString) throws IOException {
        Message message = new Message(10, "#login");
        message.setObject2(numString);
        SimpleClient.getClient().sendToServer(message);
    }

    @Subscribe
    public void onUpdateIdUserEvent(UpdateIdUserEvent event) {
        Message message = event.getMessage();
        Platform.runLater(() -> {
            switch (message.getMessage()) {
                case "#userNotFound":
                    showAlert("Login Error", "User ID not found.");
                    break;
                case "#alreadyLoggedIn":
                    showAlert("Login Error", "This user ID is already logged in.");
                    break;
                case "#loginConfirmed":
                    showAlert("Login Successful", "You have successfully logged in.");
                    break;
                case "#serverError":
                    showAlert("Server Error", "An error occurred on the server.");
                    break;
                default:
                    showAlert("Error", "An unknown error occurred.");
                    break;
            }
        });
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
