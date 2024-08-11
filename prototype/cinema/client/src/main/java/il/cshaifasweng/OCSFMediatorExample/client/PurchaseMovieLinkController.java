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

public class PurchaseMovieLinkController {

    @FXML
    private TextField user_id;

    @FXML
    private TextField user_first_name;

    @FXML
    private TextField user_last_name;

    @FXML
    private TextField user_email;

    @FXML
    private TextField user_phone_number;

    @FXML
    private TextField user_card_number;

    @FXML
    private TextField user_cvv;

    @FXML
    private TextField card_date;

    @FXML
    private Button purchase; // Value injected by FXMLLoader

    @FXML
    private Text error_message;

    @FXML
    private Text success_message;

    @FXML
    public void initialize() {
        EventBus.getDefault().register(this);
    }

    @FXML
    public void purchase_button(ActionEvent event) {

        if (isFieldEmpty(user_id, "Please enter your ID.")) return;
        if (isFieldEmpty(user_first_name, "Please enter your first name.")) return;
        if (isFieldEmpty(user_last_name, "Please enter your last name.")) return;
        if (isFieldEmpty(user_email, "Please enter your email.")) return;
        if (isFieldEmpty(user_phone_number, "Please enter your phone number.")) return;
        if (isFieldEmpty(user_card_number, "Please enter your card number.")) return;
        if (isFieldEmpty(user_cvv, "Please enter your CVV.")) return;
        if (isFieldEmpty(card_date, "Please enter your card expiry date.")) return;


        String user_id_str = user_id.getText();
        if (user_id_str.length() != 9 || !user_id_str.matches("\\d{9}")) {
            error_message.setVisible(true);
            error_message.setText("ID must contain exactly 9 digits");
            user_id.setText("");
            return;
        }


        String user_phone_number_str = user_phone_number.getText();

        if (!user_phone_number_str.matches("\\d{10}")) {
            error_message.setVisible(true);
            error_message.setText("Phone number must contain exactly 10 digits");
            user_phone_number.setText("");
            return;
        }


        String user_card_number_str = user_card_number.getText();

        if (!user_card_number_str.matches("\\d{12}")) {
            error_message.setVisible(true);
            error_message.setText("Card number must contain exactly 12 digits");
            user_card_number.setText("");
            return;
        }

        String user_cvv_str = user_cvv.getText();
        if (!user_cvv_str.matches("\\d{3}")) {
            error_message.setVisible(true);
            error_message.setText("CVV must contain exactly 3 digits");
            user_cvv.setText("");
            return;
        }



        ////////////////////////date checking
        String date_str = card_date.getText();
        String date_pattern = "^(0[1-9]|1[0-2])/\\d{2}$";

        if (!date_str.matches(date_pattern)) {
            error_message.setVisible(true);
            error_message.setText("Date must be in the format mm/yy and mm should be a valid month (01-12)");
            card_date.setText("");
            return;
        }        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yy");
        YearMonth inputDate;
        try {
            inputDate = YearMonth.parse(date_str, formatter);
        } catch (DateTimeParseException e) {
            error_message.setVisible(true);
            error_message.setText("Date parsing error. Please check the format.");
            card_date.setText("");
            return;
        }

        // Get the current date
        YearMonth currentMonth = YearMonth.now();

        // Compare the current date with the input date
        if (inputDate.isBefore(currentMonth)) {
            error_message.setVisible(true);
            error_message.setText("The expiry date cannot be in the past.");
            card_date.setText("");
            return;
        }




        String phone_str = user_phone_number.getText();
        String curr_first_name = user_first_name.getText();
        String curr_last_name = user_last_name.getText();
        String full_name = curr_first_name + " " + curr_last_name;
        String curr_email = user_email.getText();


        //IdUser id_user = new IdUser(id_str, full_name, phone_str, curr_email);
       // MultiEntryTicket multiTicket = new MultiEntryTicket(20);
        //multiTicket.setId_user(id_user);


    }

    private boolean isFieldEmpty(TextField field, String errorMessage) {
        if (field.getText().isEmpty()) {
            error_message.setVisible(true);
            error_message.setText(errorMessage);
            return true;
        }
        return false;
    }

    //@Subscribe
 //   public void purchases_success(Message event) {
   //     if (event.getId().equals("SAVE_MULTI_TICKET")) {
     //       Platform.runLater(() -> {
       //         print_success(event);
         //   });
        //}
    //}

    public void print_success(Message message) {
        success_message.setVisible(true);
        success_message.setText("Purchase Success.");
    }

    @Subscribe
    public void change_content1(BeginContentChangeEnent event) {
        System.out.println(event.getPage());
        EventBus.getDefault().unregister(this);
        EventBus.getDefault().post(new ContentChangeEvent(event.getPage()));
    }
}
