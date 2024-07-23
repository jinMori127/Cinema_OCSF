package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import static il.cshaifasweng.OCSFMediatorExample.client.MovieEditingDetailsController.go_to_screening_movie;
import javafx.collections.ObservableList;
import il.cshaifasweng.OCSFMediatorExample.entities.Worker;
import com.mysql.cj.protocol.x.XMessage;
import il.cshaifasweng.OCSFMediatorExample.entities.Message;
import il.cshaifasweng.OCSFMediatorExample.entities.Movie;
import il.cshaifasweng.OCSFMediatorExample.entities.Screening;
import il.cshaifasweng.OCSFMediatorExample.entities.UserPurchases;
import il.cshaifasweng.OCSFMediatorExample.entities.IdUser;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;
import javafx.fxml.Initializable;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.DateStringConverter;
import javafx.scene.input.MouseEvent;

import il.cshaifasweng.OCSFMediatorExample.entities.Message;

import javax.persistence.criteria.CriteriaBuilder;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.net.URL;
import java.sql.Time;
import java.util.*;
import javafx.scene.control.TextArea;


import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import static il.cshaifasweng.OCSFMediatorExample.client.MovieEditingDetailsController.go_to_screening_movie;
import static il.cshaifasweng.OCSFMediatorExample.client.SimpleClient.Current_Message;
import static il.cshaifasweng.OCSFMediatorExample.client.SimpleClient.getClient;

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
    }

    static Worker worker;

    @Subscribe
    public void onUpdateIdUserEvent(LogInworkerEventBox event) {
        Message message = event.getMessage();
        Platform.runLater(() -> {
            switch (message.getMessage()) {
                case "#loginWorkerFailedUserName":
                    output.setText("Incorrect username or password");
                    break;
                case "#loginWorker":
                    output.setText("Successfully logged in");
                    worker = (Worker) message.getObject();
                    break;
                case "#loginWorkerFailedPass":
                    output.setText("Incorrect username or password");
                    break;
            }
        });
    }
}
