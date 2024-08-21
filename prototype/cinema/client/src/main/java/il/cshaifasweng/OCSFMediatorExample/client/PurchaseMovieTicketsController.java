package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.text.Text;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PurchaseMovieTicketsController {

    @FXML
    private Text ErrorMessage;

    @FXML
    private TextField credit_card_number;

    @FXML
    private TextField cvv;

    @FXML
    private TextField date;

    @FXML
    private TextField email;

    @FXML
    private TextField first_name;

    @FXML
    private TextField id;

    @FXML
    private TextField last_name;

    @FXML
    private TextField phone_number;

    @FXML
    private Button use_multiTicket_butt;


    List<MultiEntryTicket> multiTickets;

    @FXML
    void multiTicket_pay(ActionEvent event) {

        if (isFieldEmpty(id, "Please enter your ID.")) return;
        if (isFieldEmpty(first_name, "Please enter your first name.")) return;
        if (isFieldEmpty(last_name, "Please enter your last name.")) return;
        if (isFieldEmpty(email, "Please enter your email.")) return;
        if (isFieldEmpty(phone_number, "Please enter your phone number.")) return;


        String user_id_str = id.getText();
        if (user_id_str.length() != 9 || !user_id_str.matches("\\d{9}")) {
            ErrorMessage.setVisible(true);
            ErrorMessage.setText("ID must contain exactly 9 digits");
            id.setText("");
            return;
        }


        String user_phone_number_str = phone_number.getText();

        if (!user_phone_number_str.matches("\\d{10}")) {
            ErrorMessage.setVisible(true);
            ErrorMessage.setText("Phone number must contain exactly 10 digits");
            phone_number.setText("");
            return;
        }
        Screening screening = TheaterMapController.screening;
        ArrayList<ArrayList<Integer>> places_took = TheaterMapController.places_took;
        int reserved = places_took.size();
        Message message = new Message (2000,"#PayMultiTicket");
        message.setObject(id.getText());
        message.setObject2(reserved);

        try {
            SimpleClient.getClient().sendToServer(message);
        } catch (IOException e) {
            ErrorMessage.setText(e.getMessage());
            ErrorMessage.setVisible(true);
            return;
        }



    }


    @FXML
    void pay_amount(ActionEvent event) {

        if (isFieldEmpty(id, "Please enter your ID.")) return;
        if (isFieldEmpty(first_name, "Please enter your first name.")) return;
        if (isFieldEmpty(last_name, "Please enter your last name.")) return;
        if (isFieldEmpty(email, "Please enter your email.")) return;
        if (isFieldEmpty(phone_number, "Please enter your phone number.")) return;
        if (isFieldEmpty(credit_card_number, "Please enter your card number.")) return;
        if (isFieldEmpty(cvv, "Please enter your CVV.")) return;
        if (isFieldEmpty(date, "Please enter your card expiry date.")) return;

        String user_id_str = id.getText();
        if (user_id_str.length() != 9 || !user_id_str.matches("\\d{9}")) {
            ErrorMessage.setVisible(true);
            ErrorMessage.setText("ID must contain exactly 9 digits");
            id.setText("");
            return;
        }


        String user_phone_number_str = phone_number.getText();

        if (!user_phone_number_str.matches("\\d{10}")) {
            ErrorMessage.setVisible(true);
            ErrorMessage.setText("Phone number must contain exactly 10 digits");
            phone_number.setText("");
            return;
        }

        String user_card_number_str = credit_card_number.getText();

        if (!user_card_number_str.matches("\\d{16}")) {
            ErrorMessage.setVisible(true);
            ErrorMessage.setText("Card number must contain exactly 12 digits");
            credit_card_number.setText("");
            return;
        }

        String user_cvv_str = cvv.getText();
        if (!user_cvv_str.matches("\\d{3}")) {
            ErrorMessage.setVisible(true);
            ErrorMessage.setText("CVV must contain exactly 3 digits");
            cvv.setText("");
            return;
        }


        String date_str = date.getText();
        String date_pattern = "^(0[1-9]|1[0-2])/\\d{2}$";

        if (!date_str.matches(date_pattern)) {
            ErrorMessage.setVisible(true);
            ErrorMessage.setText("Date must be in the format mm/yy and mm should be a valid month (01-12)");
            date.setText("");
            return;
        }        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yy");
        YearMonth inputDate;
        try {
            inputDate = YearMonth.parse(date_str, formatter);
        } catch (DateTimeParseException e) {
            ErrorMessage.setVisible(true);
            ErrorMessage.setText("Date parsing error. Please check the format.");
            date.setText("");
            return;
        }

        YearMonth currentMonth = YearMonth.now();

        // Compare the current date with the input date
        if (inputDate.isBefore(currentMonth)) {
            ErrorMessage.setVisible(true);
            ErrorMessage.setText("The expiry date cannot be in the past.");
            date.setText("");
            return;
        }


        Screening screening = TheaterMapController.screening;
        ArrayList<ArrayList<Integer>> places_took = TheaterMapController.places_took;
        int [][] map = TheaterMapController.cerate_map(screening.getTheater_map());

        for (ArrayList<Integer> list : places_took) {
            map[list.get(0)][list.get(1)] = 2;
        }

        screening.setTheater_map(TheaterMapController.create_string_of_map(map));
        Message m = new Message(10000, "#Update_theater_map");
        m.setObject(screening);

        try {
            SimpleClient.getClient().sendToServer(m);
        } catch (IOException e) {
            ErrorMessage.setVisible(true);
            ErrorMessage.setText(e.getMessage());
            return;
        }

        // pay: seats * price
        //to do send to the server and add to the purchases entity
        //to do: save the seats as following in the data row1::column1 , row2::column2 , ... rown::columnn

    }
    @FXML
    public void initialize() {
        ErrorMessage.setVisible(false);
        EventBus.getDefault().register(this);
        if (UserLogInWithIDController.idUser != null) {
            IdUser user= UserLogInWithIDController.idUser;
            id.setText(user.getUser_id());
            id.setEditable(false);
            first_name.setText(user.getName().split(" ")[0]);
            try {
                last_name.setText(user.getName().split(" ")[1]);
            }
            catch (Exception e) {
                last_name.setText("");
            }
            email.setText(user.getEmail());
            phone_number.setText(user.getPhone_number());
        }
    }

    @Subscribe
    public void change_content1(BeginContentChangeEnent event)
    {

        System.out.println(event.getPage());
        if(!event.getPage().equals("#TheaterMap"))
        {
            TheaterMapController.places_took = null;
            TheaterMapController.screening = null;
        }
        EventBus.getDefault().unregister(this);
        EventBus.getDefault().post(new ContentChangeEvent(event.getPage()));

    }

    private boolean isFieldEmpty(TextField field, String errorMessage) {
        if (field.getText().isEmpty()) {
            ErrorMessage.setVisible(true);
            ErrorMessage.setText(errorMessage);
            return true;
        }
        return false;
    }


    @Subscribe
    public void A(BaseEventBox eventBox) {
        if (eventBox.getId() == BaseEventBox.get_event_id("FAILED_MT")) {
            Platform.runLater(() -> {
                ErrorMessage.setVisible(true);
                ErrorMessage.setText("You dont have enough entries in your Multicket");
            });
        } else if (eventBox.getId() == BaseEventBox.get_event_id("DONE_PAY_MULTITICKET")) {
            Screening screening = TheaterMapController.screening;
            ArrayList<ArrayList<Integer>> places_took = TheaterMapController.places_took;

            String seats_str = "";

            int [][] map = TheaterMapController.cerate_map(screening.getTheater_map());

            for (ArrayList<Integer> list : places_took) {
                map[list.get(0)][list.get(1)] = 2;
                if(list != places_took.getLast()) {
                    seats_str += list.get(0) + "::" + list.get(1) + " , ";
                }
            }

            screening.setTheater_map(TheaterMapController.create_string_of_map(map));
            Message m = new Message(10000, "#Update_theater_map");
            m.setObject(screening);
            try {
                SimpleClient.getClient().sendToServer(m);
            } catch (IOException e) {
                ErrorMessage.setVisible(true);
                ErrorMessage.setText(e.getMessage());
                return;
            }


            Date currentDate = new Date();
            IdUser idUser = (IdUser) eventBox.getMessage().getObject();
            double price = screening.getMovie().getPrice() * places_took.size();


            UserPurchases userPurchases = new UserPurchases(seats_str, "Multi Ticket", price, idUser, screening, "Ticket", currentDate);
            idUser.getUser_purchases().add(userPurchases);

            m.setMessage("#Save_user_purchases");
            m.setObject(userPurchases);
            try {
                SimpleClient.getClient().sendToServer(m);
            } catch (IOException e) {
                ErrorMessage.setVisible(true);
                ErrorMessage.setText(e.getMessage());
                return;
            }

            TheaterMapController.places_took = null;
            TheaterMapController.screening = null;
        } else if (eventBox.getId() ==BaseEventBox.get_event_id("SAVED_USER_PURCHASES")) {
            UserPurchases userPurchases = (UserPurchases)eventBox.getMessage().getObject();
            if(userPurchases.getId_user().getUser_id().trim().equals(id.getText().trim())) {
                ErrorMessage.setVisible(true);
                ErrorMessage.setText("Thank you for you purchase, an email will be sent to you");
            }
        }
    }
}
