package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.collections.ObservableList;

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


public class CustomerServiceController {

    @FXML
    private TableColumn<?, ?> auto_number_complains;

    @FXML
    private TableColumn<?, ?> client_name;

    @FXML
    private TableColumn<?, ?> complain_text;

    @FXML
    private TextArea complains_detailes;

    @FXML
    private TableColumn<?, ?> date_f;

    @FXML
    private TextArea respond;

    @FXML
    private Button submit_respond;

    @FXML
    private TableView<?> table_view;

    // initialize page function
    @FXML
    public void initialize() {
        EventBus.getDefault().register(this);
        Message insert_message = new Message(40,"#show_complains");

        try {
            SimpleClient.getClient().sendToServer(insert_message);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Add double-click listener to table rows
        table_view.setOnMouseClicked((MouseEvent event) -> {
            if (event.getClickCount() == 2) {
                show_complain_information();

            }
        });
    }

    void show_complain_information() {}

    @FXML
    void CancelPurchase(ActionEvent event) {

    }

}
