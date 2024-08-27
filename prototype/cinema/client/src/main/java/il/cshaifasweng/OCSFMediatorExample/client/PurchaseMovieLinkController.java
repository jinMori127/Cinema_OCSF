package il.cshaifasweng.OCSFMediatorExample.client;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.event.ActionEvent;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Calendar;
import java.util.Date;
import java.time.LocalDateTime;
import java.time.ZoneId;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import il.cshaifasweng.OCSFMediatorExample.entities.Movie;
import il.cshaifasweng.OCSFMediatorExample.entities.UserPurchases;
import java.time.LocalTime;
import java.text.SimpleDateFormat;

import javafx.application.Platform;
import javafx.scene.control.Button;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import il.cshaifasweng.OCSFMediatorExample.entities.IdUser;
import java.io.IOException;

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
    private Text end_time;


    @FXML
    private Text MULTI;

    @FXML
    private Button purchase_multi; // Value injected by FXMLLoader

    @FXML
    public void initialize() {
        EventBus.getDefault().register(this);
        if(UserLogInWithIDController.idUser != null) {
            IdUser old_id_user = UserLogInWithIDController.idUser;
            String full_name = old_id_user.getName();
            String split_first_name = full_name;
            String split_last_name = "";
            if (old_id_user.getName().contains(" ")) {
                split_first_name = full_name.split(" ")[0];
                split_last_name = full_name.split(" ")[1];
            }
            user_id.setText(old_id_user.getUser_id());
            user_first_name.setText(split_first_name);
            user_last_name.setText(split_last_name);
            user_email.setText(old_id_user.getEmail());
            user_phone_number.setText(old_id_user.getPhone_number());
        }
    }

    @FXML
    public void purchase_multi_func(ActionEvent event) {
        if (!validateInputFields(false)) return;

        LocalDateTime wantedDateTime = LocalDateTime.of(
                LocalDate.parse(wanted_date.getText(), DateTimeFormatter.ISO_LOCAL_DATE),
                LocalTime.parse(wanted_Time.getText(), DateTimeFormatter.ISO_LOCAL_TIME)
        );
        Date wantedDateObj = Date.from(wantedDateTime.atZone(ZoneId.systemDefault()).toInstant());

        // Assuming MovieDetailsController.current_movie.getTime_() returns film length in hours and minutes
        Date screeningTimeDate = MovieDetailsController.current_movie.getTime_(); // Assuming Date type

        // Convert the Date to minutes
        Calendar cal = Calendar.getInstance();
        cal.setTime(screeningTimeDate);
        int hours = cal.get(Calendar.HOUR_OF_DAY);
        int minutes = cal.get(Calendar.MINUTE);
        long filmLengthMinutes = hours * 60 + minutes;

        // Convert wantedDateObj to Calendar
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(wantedDateObj);

        // Add film length to the start time
        calendar.add(Calendar.MINUTE, (int) filmLengthMinutes);

        // Get the end time from Calendar
        Date endDateObj = calendar.getTime();

        // Format the end time
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String endTimeString = sdf.format(endDateObj);

        // Set the end time to the UI component
        end_time.setVisible(true);
        end_time.setText(endTimeString);
        String phone_str = user_phone_number.getText();
        String curr_first_name = user_first_name.getText();
        String curr_last_name = user_last_name.getText();
        String full_name = curr_first_name + " " + curr_last_name;
        String curr_email = user_email.getText();

        Movie movie1 = MovieDetailsController.current_movie;
        IdUser id_user = new IdUser(user_id.getText(), full_name, phone_str, curr_email);


        String baseUrl = "https://www.LunaAura.com";
        String movieName = "Inception";

        String formattedMovieName = movieName.replaceAll("\\s+", "-").toLowerCase();
        sdf = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = sdf.format(wantedDateObj);
        String movieLink = baseUrl + "/movies/" + formattedMovieName + "/" + formattedDate;

        UserPurchases p1 = new UserPurchases("Crtesea", 0.0, id_user, wantedDateObj,endDateObj, "", movie1.getMovie_name());
        Message message = new Message(35, "#purchase_movie_link_by_multi_ticket");
        message.setObject(p1);

        try {
            SimpleClient.getClient().sendToServer(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @FXML
    public void purchase_button(ActionEvent event) {
        if (!validateInputFields(true)) return;
        processPurchase();
    }

    private boolean validateInputFields(boolean check_credit) {
        if (isFieldEmpty(user_id, "Please enter your ID.")) return false;
        else if (isFieldEmpty(user_first_name, "Please enter your first name.")) return false;
        else if (isFieldEmpty(user_last_name, "Please enter your last name.")) return false;
        else if (isFieldEmpty(user_email, "Please enter your email.")) return false;
        else if (isFieldEmpty(user_phone_number, "Please enter your phone number.")) return false;

        else if ((check_credit)&&isFieldEmpty(user_card_number, "Please enter your card number.")) return false;
        else if ((check_credit)&&isFieldEmpty(user_cvv, "Please enter your CVV.")) return false;
        else if ((check_credit)&&isFieldEmpty(card_date, "Please enter your card expiry date.")) return false;
        else if (isFieldEmpty(wanted_date, "Please enter your Wanted Date.")) return false;
        else if (isFieldEmpty(wanted_Time, "Please enter your wanted Time")) return false;

        String user_id_str = user_id.getText();
        if (user_id_str.length() != 9 || !user_id_str.matches("\\d{9}")) {
            error_message.setVisible(true);
            error_message.setText("ID must contain exactly 9 digits");
            user_id.setText("");
            return false;
        }

        String user_phone_number_str = user_phone_number.getText();
        if (!user_phone_number_str.matches("\\d{10}")) {
            error_message.setVisible(true);
            error_message.setText("Phone number must contain exactly 10 digits");
            user_phone_number.setText("");
            return false;
        }

        String email_str = user_email.getText();
        String format  = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        if (!email_str.matches(format)) {
            error_message.setVisible(true);
            error_message.setText("Please enter a valid email address (e.g., user@example.com).");
            user_email.setText("");
            return false;
        }

        if(check_credit) {

            String user_card_number_str = user_card_number.getText();
            if (!user_card_number_str.matches("\\d{16}")) {
                error_message.setVisible(true);
                error_message.setText("Card number must contain exactly 16 digits");
                user_card_number.setText("");
                return false;
            }

            String user_cvv_str = user_cvv.getText();
            if (!user_cvv_str.matches("\\d{3}")) {
                error_message.setVisible(true);
                error_message.setText("CVV must contain exactly 3 digits");
                user_cvv.setText("");
                return false;
            }

            // Date validation
            String date_str = card_date.getText();
            String date_pattern = "^(0[1-9]|1[0-2])/\\d{2}$";
            if (!date_str.matches(date_pattern)) {
                error_message.setVisible(true);
                error_message.setText("Date must be in the format mm/yy and mm should be a valid month (01-12)");
                card_date.setText("");
                return false;
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yy");
            YearMonth inputDate;
            try {
                inputDate = YearMonth.parse(date_str, formatter);
            } catch (DateTimeParseException e) {
                error_message.setVisible(true);
                error_message.setText("Date parsing error. Please check the format.");
                card_date.setText("");
                return false;
            }

            YearMonth currentMonth = YearMonth.now();
            if (inputDate.isBefore(currentMonth)) {
                error_message.setVisible(true);
                error_message.setText("The expiry date cannot be in the past.");
                card_date.setText("");
                return false;
            }

        }

        String wantedDateStr = wanted_date.getText();
        LocalDate wantedDate;
        try {
            wantedDate = LocalDate.parse(wantedDateStr, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException e) {
            error_message.setVisible(true);
            error_message.setText("Wanted Date must be in the format yyyy-MM-dd.");
            wanted_date.setText("");
            return false;
        }

        // Validate wanted_Time
        String wantedTimeStr = wanted_Time.getText();
        LocalTime wantedTime;
        try {
            wantedTime = LocalTime.parse(wantedTimeStr, DateTimeFormatter.ISO_LOCAL_TIME);
        } catch (DateTimeParseException e) {
            error_message.setVisible(true);
            error_message.setText("Wanted Time must be in the format HH:mm.");
            wanted_Time.setText("");
            return false;
        }

        return true;
    }

    private void processPurchase() {
        LocalDateTime wantedDateTime = LocalDateTime.of(
                LocalDate.parse(wanted_date.getText(), DateTimeFormatter.ISO_LOCAL_DATE),
                LocalTime.parse(wanted_Time.getText(), DateTimeFormatter.ISO_LOCAL_TIME)
        );
        Date wantedDateObj = Date.from(wantedDateTime.atZone(ZoneId.systemDefault()).toInstant());

        Date screeningTimeDate = MovieDetailsController.current_movie.getTime_(); // Assuming Date type

        Calendar cal = Calendar.getInstance();
        cal.setTime(screeningTimeDate);
        int hours = cal.get(Calendar.HOUR_OF_DAY);
        int minutes = cal.get(Calendar.MINUTE);
        long filmLengthMinutes = hours * 60 + minutes;

        // Convert wantedDateObj to Calendar
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(wantedDateObj);

        // Add film length to the start time
        calendar.add(Calendar.MINUTE, (int) filmLengthMinutes);

        // Get the end time from Calendar
        Date endDateObj = calendar.getTime();

        // Format the end time
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String endTimeString = sdf.format(endDateObj);

        // Set the end time to the UI component
        end_time.setVisible(true);
        end_time.setText(endTimeString);
        String phone_str = user_phone_number.getText();
        String curr_first_name = user_first_name.getText();
        String curr_last_name = user_last_name.getText();
        String full_name = curr_first_name + " " + curr_last_name;
        String curr_email = user_email.getText();

        Movie movie1 = MovieDetailsController.current_movie;
        IdUser id_user = new IdUser(user_id.getText(), full_name, phone_str, curr_email);

        String baseUrl = "https://www.LunaAura.com";
        String movieName = "Inception";

        String formattedMovieName = movieName.replaceAll("\\s+", "-").toLowerCase();
        sdf = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = sdf.format(wantedDateObj);
        String movieLink = baseUrl + "/movies/" + formattedMovieName + "/" + formattedDate;

        UserPurchases p1 = new UserPurchases("Credit", movie1.getPrice(), id_user, wantedDateObj,endDateObj, movieLink, movie1.getMovie_name());
        Message message = new Message(84, "#purchase_movie_link");
        message.setObject(p1);

        try {
            SimpleClient.getClient().sendToServer(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isFieldEmpty(TextField field, String errorMessage) {
        if (field.getText().isEmpty()) {
            error_message.setVisible(true);
            error_message.setText(errorMessage);
            return true;
        }
        return false;
    }

    @Subscribe
    public void change_content1(BeginContentChangeEnent event) {
        System.out.println(event.getPage());
        EventBus.getDefault().unregister(this);
        EventBus.getDefault().post(new ContentChangeEvent(event.getPage()));
    }

    @Subscribe
    public void purchases_MULTI_sucess(BaseEventBox event) {
        if (event.getId() == BaseEventBox.get_event_id("PURCHASE_LINK_USING_MULTI")) {
            Platform.runLater(() -> {
                print_success(event.getMessage());
            });
        }
    }

    public void print_success(Message message) {
        error_message.setVisible(false);
        MULTI.setVisible(true);
        MULTI.setText((String)(message.getObject()));
    }


    @Subscribe
    public void purchases_Link_sucess(BaseEventBox event) {
        if (event.getId() == BaseEventBox.get_event_id("PURCHASE_LINK_N")) {
            Platform.runLater(() -> {
                error_message.setVisible(false);
                MULTI.setVisible(true);
                MULTI.setText("Purchaes Success");
            });
        }
    }
}
