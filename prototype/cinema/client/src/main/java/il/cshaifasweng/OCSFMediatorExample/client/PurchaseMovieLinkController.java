package il.cshaifasweng.OCSFMediatorExample.client;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.event.ActionEvent;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import il.cshaifasweng.OCSFMediatorExample.entities.Movie;
import il.cshaifasweng.OCSFMediatorExample.entities.UserPurchases;
import java.time.LocalTime;
import java.text.SimpleDateFormat;
import java.util.Date;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;
import java.util.Date;
import java.time.ZoneId;
import java.time.ZonedDateTime;


import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import il.cshaifasweng.OCSFMediatorExample.entities.IdUser;
import java.io.IOException;
import javafx.collections.ObservableList;

import com.mysql.cj.protocol.x.XMessage;
import il.cshaifasweng.OCSFMediatorExample.client.MovieDetailsController;

import il.cshaifasweng.OCSFMediatorExample.entities.Message;


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
    private TextField wanted_date;

    @FXML
    private TextField wanted_Time;



    @FXML
    private Button purchase;

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
        else if (isFieldEmpty(user_first_name, "Please enter your first name.")) return;
        else if (isFieldEmpty(user_last_name, "Please enter your last name.")) return;
        else if (isFieldEmpty(user_email, "Please enter your email.")) return;
        else if (isFieldEmpty(user_phone_number, "Please enter your phone number.")) return;
        else if (isFieldEmpty(user_card_number, "Please enter your card number.")) return;
        else if (isFieldEmpty(user_cvv, "Please enter your CVV.")) return;
        else if (isFieldEmpty(card_date, "Please enter your card expiry date.")) return;
        else if (isFieldEmpty(wanted_date, "Please enter your Wanted Date.")) return;
        else if (isFieldEmpty(wanted_Time, "Please enter your wanted Time")) return;





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

        ///////////////////////////////






        // Validate wanted_date (example format: yyyy-MM-dd)
        String wantedDateStr = wanted_date.getText();
        LocalDate wantedDate;
        try {
            wantedDate = LocalDate.parse(wantedDateStr, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException e) {
            error_message.setVisible(true);
            error_message.setText("Wanted Date must be in the format yyyy-MM-dd.");
            wanted_date.setText("");
            return;
        }

        // Validate wanted_Time (example format: HH:mm)
        String wantedTimeStr = wanted_Time.getText();
        LocalTime wantedTime;
        try {
            wantedTime = LocalTime.parse(wantedTimeStr, DateTimeFormatter.ISO_LOCAL_TIME);
        } catch (DateTimeParseException e) {
            error_message.setVisible(true);
            error_message.setText("Wanted Time must be in the format HH:mm.");
            wanted_Time.setText("");
            return;
        }





        String phone_str = user_phone_number.getText();
        String curr_first_name = user_first_name.getText();
        String curr_last_name = user_last_name.getText();
        String full_name = curr_first_name + " " + curr_last_name;
        String curr_email = user_email.getText();

        Movie movie1= MovieDetailsController.current_movie;
        IdUser id_user = new IdUser(user_id_str, full_name, phone_str, curr_email);
        LocalDateTime wantedDateTime = LocalDateTime.of(wantedDate, wantedTime);
        Date wantedDateObj = Date.from(wantedDateTime.atZone(ZoneId.systemDefault()).toInstant());
        String baseUrl = "https://www.example.com";  // Replace with your actual domain
        String movieName = "Inception";

        String formattedMovieName = movieName.replaceAll("\\s+", "-").toLowerCase();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = dateFormat.format(wantedDateObj);
        String movieLink = baseUrl + "/movies/" + formattedMovieName + "/" + formattedDate;


        UserPurchases p1=new UserPurchases("Credit",movie1.getPrice(),id_user,wantedDateObj,movieLink);
        Message message = new Message(25, "#purchase_movie_link");
        message.setObject(p1);



        try {
            SimpleClient.getClient().sendToServer(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }






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
