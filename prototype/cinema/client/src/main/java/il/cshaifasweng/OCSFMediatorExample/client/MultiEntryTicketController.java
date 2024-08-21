package il.cshaifasweng.OCSFMediatorExample.client;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.event.ActionEvent;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import il.cshaifasweng.OCSFMediatorExample.entities.MultiEntryTicket;
import il.cshaifasweng.OCSFMediatorExample.entities.IdUser;
import java.io.IOException;
import static il.cshaifasweng.OCSFMediatorExample.client.MovieEditingDetailsController.go_to_screening_movie;
import javafx.collections.ObservableList;

import com.mysql.cj.protocol.x.XMessage;
import il.cshaifasweng.OCSFMediatorExample.entities.Message;
import il.cshaifasweng.OCSFMediatorExample.entities.Movie;
import il.cshaifasweng.OCSFMediatorExample.entities.Screening;
import il.cshaifasweng.OCSFMediatorExample.entities.UserPurchases;
import il.cshaifasweng.OCSFMediatorExample.entities.IdUser;
import il.cshaifasweng.OCSFMediatorExample.entities.MultiEntryTicket;

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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

public class MultiEntryTicketController {

   // private static final int INITIAL_REMAIN_TICKETS = 20;
    //private static final int INITIAL_PRICE = 200;

    @FXML
    private TextField id;

    @FXML
    private TextField first_name;

    @FXML
    private TextField last_name;

    @FXML
    private TextField email_col;

    @FXML
    private TextField phone_number;

    @FXML
    private TextField card_number_col;

    @FXML
    private TextField cvv_col;

    @FXML
    private TextField date_col;

    @FXML
    private Button purchase; // Value injected by FXMLLoader



    @FXML
    private Text error_message;

    @FXML
    private Text success_message;

    @FXML
    private Text price_message;


    @Subscribe
    public void purchases_sucess(BaseEventBox event) {
        if (event.getId() == BaseEventBox.get_event_id("SAVE_MULTI_TICKET")) {
            Platform.runLater(() -> {
                print_success(event.getMessage());
            });
        }
    }

    @Subscribe
    public void change_content1(BeginContentChangeEnent event) {
        System.out.println(event.getPage());
        EventBus.getDefault().unregister(this);
        EventBus.getDefault().post(new ContentChangeEvent(event.getPage()));
    }

    public void print_success(Message message) {
        error_message.setVisible(false);
        success_message.setVisible(true);
        success_message.setText("Purchaes Success");
    }

    @FXML
    public void purchase_button(ActionEvent event) {
        success_message.setVisible(false);

        if (id.getText().isEmpty()) {
            error_message.setVisible(true);
            error_message.setText("Please enter your ID.");
            return;
        }

        if (first_name.getText().isEmpty()) {
            error_message.setVisible(true);
            error_message.setText("Please enter your first name.");
            return;
        }

        if (last_name.getText().isEmpty()) {
            error_message.setVisible(true);
            error_message.setText("Please enter your last name.");
            return;
        }

        if (email_col.getText().isEmpty()) {
            error_message.setVisible(true);
            error_message.setText("Please enter your email.");
            return;
        }

        if (phone_number.getText().isEmpty()) {
            error_message.setVisible(true);
            error_message.setText("Please enter your number.");
            return;
        }

        if (card_number_col.getText().isEmpty()) {
            error_message.setVisible(true);
            error_message.setText("Please enter your card number.");
            return;
        }

        if (cvv_col.getText().isEmpty()) {
            error_message.setVisible(true);
            error_message.setText("Please enter your CVV.");
            return;
        }

        if (date_col.getText().isEmpty()) {
            error_message.setVisible(true);
            error_message.setText("Please enter your card expiry date.");
            return;
        }

        String id_str = id.getText();
        if (id_str.length() != 9 || !id_str.matches("\\d{9}")) {
            error_message.setVisible(true);
            error_message.setText("ID must contain exactly 9 digits");
            id.setText("");
            return;
        }

        String _email_str = email_col.getText();
        String format  = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        if (!_email_str.matches(format)) {
            error_message.setVisible(true);
            error_message.setText("Please enter a valid email address (e.g., user@example.com).");
            email_col.setText("");
            return ;
        }


        String phone_check_number = phone_number.getText();

        if (!phone_check_number.matches("\\d{10}")) {
            error_message.setVisible(true);
            error_message.setText("Phone number must contain exactly 10 digits");
            phone_number.setText("");
            return;
        }


        String card_number = card_number_col.getText();

        if (!card_number.matches("\\d{16}")) {
            error_message.setVisible(true);
            error_message.setText("Card number must contain exactly 12 digits");
            card_number_col.setText("");
            return;
        }
////////////////////////date checking
        String date_str = date_col.getText();
        String date_pattern = "^(0[1-9]|1[0-2])/\\d{2}$";

        if (!date_str.matches(date_pattern)) {
            error_message.setVisible(true);
            error_message.setText("Date must be in the format mm/yy and mm should be a valid month (01-12)");
            date_col.setText("");
            return;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yy");
        YearMonth inputDate;
        try {
            inputDate = YearMonth.parse(date_str, formatter);
        } catch (DateTimeParseException e) {
            error_message.setVisible(true);
            error_message.setText("Date parsing error. Please check the format.");
            date_col.setText("");
            return;
        }

        // Get the current date
        YearMonth currentMonth = YearMonth.now();

        // Compare the current date with the input date
        if (inputDate.isBefore(currentMonth)) {
            error_message.setVisible(true);
            error_message.setText("The expiry date cannot be in the past.");
            date_col.setText("");
            return;
        }


        String cvv_str = cvv_col.getText();
        if (!cvv_str.matches("\\d{3}")) {
            error_message.setVisible(true);
            error_message.setText("CVV must contain exactly 3 digits");
            cvv_col.setText("");
            return;
        }

        String phone_str = phone_number.getText();


        String curr_first_name = first_name.getText();
        String curr_last_name = last_name.getText();
        String full_name = curr_first_name + " " + curr_last_name;

        String curr_email = email_col.getText();

        IdUser id_user = new IdUser(id_str, full_name, phone_str, curr_email);
        MultiEntryTicket multiTicket = new MultiEntryTicket();

        multiTicket.setId_user(id_user);

        Message message = new Message(25, "#purchase_multi_ticket");
        message.setObject(multiTicket);
        clearFields();

        try {
            SimpleClient.getClient().sendToServer(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void clearFields() {
        id.setText("");
        first_name.setText("");
        last_name.setText("");
        email_col.setText("");
        phone_number.setText("");
        card_number_col.setText("");
        cvv_col.setText("");
        date_col.setText("");
        error_message.setText(""); // Clear the error message as well
    }


    @FXML
    public void initialize() {
        price_message.setVisible(true);
        price_message.setText(
                "Number of tickets in Crtesea: " + MultiEntryTicket.INITIAL_REMAIN_TICKETS +
                        ". The price of Crtesea is: " + MultiEntryTicket.INITIAL_PRICE
        );

        EventBus.getDefault().register(this);
    }

}