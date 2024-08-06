package il.cshaifasweng.OCSFMediatorExample.client;

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


public class MultiEntryTicketController {

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
    private Button purchase; // Value injected by FXMLLoader




    @FXML
    private Text error_message;



    @Subscribe
    public void delete_purchases_for_user(BaseEventBox event) {
        if (event.getId() == 75) {
            Platform.runLater(() -> {
                print_success(event.getMessage());
            });
        }
    }

    public void print_success(Message message) {
        System.out.println("success");

    }


    @FXML
    public void purchase_button(ActionEvent event) {
        System.out.println("start purchase");


            if (id.getText().isEmpty()) {
                error_message.setVisible(true);
                error_message.setText("Please enter your ID.");
                return;
            }
            System.out.println("id is ");
            if (first_name.getText().isEmpty()) {
                error_message.setVisible(true);
                error_message.setText("Please enter your first name.");
                return;

            }
            System.out.println("FIRST is ");

        if (last_name.getText().isEmpty()) {
                error_message.setVisible(true);
                error_message.setText("Please enter your last name.");
                return;

            }
        System.out.println("LAST is ");

        if (email_col.getText().isEmpty()) {
                error_message.setVisible(true);
                error_message.setText("Please enter your email .");
                return;

            }
        System.out.println("email is ");

        if (phone_number.getText().isEmpty()) {
                error_message.setVisible(true);
                error_message.setText("Please enter your number .");
                return;
            }
        System.out.println("phone is ");



        int curr_id = Integer.parseInt(id.getText());
            String id_str = Integer.toString(curr_id);
            if (curr_id < 0 || id_str.length() != 9) {
                error_message.setVisible(true);
                error_message.setText("ID must contain 9 digits");
                id.setText("");
                return;
            }
        System.out.println("END_CECKS44");


       // int curr_phone = Integer.parseInt(phone_number.getText());
        System.out.println("END_CECKS55");

        String phone_str = (String) (phone_number.getText());
        System.out.println("END_CECKS66");
         //   if (curr_phone < 0 || phone_str.length() != 10) {
           //     error_message.setVisible(true);
             //   error_message.setText("phone number must contain 10 digits");
               // phone_number.setText("");
                //return;
            //}
            System.out.println("END_CECKS");

            String curr_first_name = (String)first_name.getText();
            String curr_last_name =(String) last_name.getText();
            String curr_email =(String) email_col.getText();
            System.out.println("enter purcahseeeeeeeee abree");
            IdUser id_user=new IdUser(id_str,curr_first_name,phone_str,curr_email);
            if(id_user==null)
                System.out.println("ID IS NULL");
            MultiEntryTicket multiTicket = new MultiEntryTicket(id_user,20);


            Message message = new Message(25, "#purchase_multi_ticket");
            message.setObject(multiTicket);
            try {
                SimpleClient.getClient().sendToServer(message);
                System.out.println("ended");

            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }








    }
    @FXML
    public void initialize() {

        System.out.println("initialize");
        EventBus.getDefault().register(this);
    }

}