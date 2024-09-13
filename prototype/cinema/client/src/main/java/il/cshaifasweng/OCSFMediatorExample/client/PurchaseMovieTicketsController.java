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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        ErrorMessage.setVisible(false);
        //Screening screening = TheaterMapController.screening;
        if(TheaterMapController.places_took.isEmpty())
        {
            ErrorMessage.setVisible(true);
            ErrorMessage.setText("You have already purchased");
            return;
        }
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


        String _email_str = email.getText();
        String format  = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        if (!_email_str.matches(format)) {
            ErrorMessage.setVisible(true);
            ErrorMessage.setText("Please enter a valid email address (e.g., user@example.com).");
            return ;
        }


        if (!user_phone_number_str.matches("\\d{10}")) {
            ErrorMessage.setVisible(true);
            ErrorMessage.setText("Phone number must contain exactly 10 digits");
            phone_number.setText("");
            return;
        }
        ArrayList<ArrayList<Integer>> places_took = TheaterMapController.places_took;
        int reserved = places_took.size();

        Message message = new Message (1563,"#PayMultiTicket");
        message.setObject(id.getText());
        message.setObject2(reserved);
        IdUser current_id_user =new IdUser();
        current_id_user.setIsLoggedIn(false);
        current_id_user.setUser_id(id.getText());
        current_id_user.setName(first_name.getText()+" "+last_name.getText());
        current_id_user.setEmail(email.getText());
        current_id_user.setPhone_number(phone_number.getText());
        message.setObject3(current_id_user);

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
        ErrorMessage.setVisible(false);
        //Screening screening = TheaterMapController.screening;
        if(TheaterMapController.places_took.isEmpty())
        {
            ErrorMessage.setVisible(true);
            ErrorMessage.setText("You have already purchased");
            return;
        }
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


        String _email_str = email.getText();
        String format  = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        if (!_email_str.matches(format)) {
            ErrorMessage.setVisible(true);
            ErrorMessage.setText("Please enter a valid email address (e.g., user@example.com).");
            return ;
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
            ErrorMessage.setText("Card number must contain exactly 16 digits");
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
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yy");
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

        // pay: seats * price
        //to do send to the server and add to the purchases entity
        IdUser current_id_user =new IdUser();
        current_id_user.setIsLoggedIn(false);
        current_id_user.setUser_id(id.getText());
        current_id_user.setName(first_name.getText()+" "+last_name.getText());
        current_id_user.setEmail(email.getText());
        current_id_user.setPhone_number(phone_number.getText());
        Message m = new Message(1893,"#Success_CC");
        m.setMessage("#Success_CC");
        m.setObject(id.getText());
        m.setObject2(current_id_user);
        try {
            SimpleClient.getClient().sendToServer(m);
        } catch (IOException e) {
            ErrorMessage.setVisible(true);
            ErrorMessage.setText(e.getMessage());
            return;
        }

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
        if(!event.getPage().equals("TheaterMap"))
        {
            if(TheaterMapController.screening !=null) {
                int[][] map = TheaterMapController.cerate_map(TheaterMapController.screening.getTheater_map());
                for (ArrayList<Integer> list : TheaterMapController.places_took) {
                    map[list.get(0)][list.get(1)] = 0;
                }
                TheaterMapController.screening.setTheater_map(TheaterMapController.create_string_of_map(map));
                Message m = new Message(10000, "#Update_theater_map");
                m.setObject(TheaterMapController.screening);
                try {
                    SimpleClient.getClient().sendToServer(m);
                } catch (IOException e) {
                    ErrorMessage.setVisible(true);
                    ErrorMessage.setText(e.getMessage());
                    return;
                }
            }
           TheaterMapController.places_took.clear();
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

        System.out.println("We now in A function in controller yo ");

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
                if (list!= places_took.getLast()) {
                    seats_str += list.get(0) + "::" + list.get(1) + " , ";
                }
                else {
                    seats_str += list.get(0) + "::" + list.get(1);
                }

            }
            screening.setTheater_map(TheaterMapController.create_string_of_map(map));
            Message m = new Message(1986, "#Update_theater_map");
            m.setObject(screening);
            try {
                SimpleClient.getClient().sendToServer(m);
            } catch (IOException e) {
                ErrorMessage.setVisible(true);
                ErrorMessage.setText(e.getMessage());
                return;
            }


            System.out.println("after update theater map");

            Date currentDate = new Date();
            IdUser idUser = (IdUser) eventBox.getMessage().getObject();


            UserPurchases userPurchases = new UserPurchases(seats_str.toString(), "Multi Ticket", 0.0, idUser, screening);
            userPurchases.setSeats(seats_str);
            m.setMessage("#Save_user_purchases");
            m.setObject(userPurchases);
            try {
                SimpleClient.getClient().sendToServer(m);
            } catch (IOException e) {
                ErrorMessage.setVisible(true);
                ErrorMessage.setText(e.getMessage());
                return;
            }
            TheaterMapController.places_took.clear();
            //TheaterMapController.screening = null;

        }

        else if (eventBox.getId() == BaseEventBox.get_event_id("DONE_CC")) {

                Screening screening = TheaterMapController.screening;
                ArrayList<ArrayList<Integer>> places_took = TheaterMapController.places_took;
                String seats_str = "";
                int[][] map = TheaterMapController.cerate_map(screening.getTheater_map());
                System.out.println("length "+ places_took.size());
                for (ArrayList<Integer> list : places_took) {
                    map[list.get(0)][list.get(1)] = 2;
                    if (list!= places_took.getLast()) {
                        seats_str += list.get(0) + "::" + list.get(1) + " , ";
                    }
                    else {
                        seats_str += list.get(0) + "::" + list.get(1);
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
                
                UserPurchases userPurchases = new UserPurchases(seats_str.toString(), "Credit", price, idUser, screening);
                userPurchases.setSeats(seats_str);

                m.setMessage("#Save_user_purchases");
                m.setObject(userPurchases);
                try {
                    SimpleClient.getClient().sendToServer(m);
                } catch (IOException e) {
                    ErrorMessage.setVisible(true);
                    ErrorMessage.setText(e.getMessage());
                    return;
                }

                TheaterMapController.places_took.clear();
                //TheaterMapController.screening = null;

        }
        else if (eventBox.getId() == BaseEventBox.get_event_id("THEATER_MAP_UPDATED")) {
            Screening current_screening = (Screening) eventBox.getMessage().getObject();
            if (current_screening.getAuto_number_screening() == TheaterMapController.screening.getAuto_number_screening()) {
                TheaterMapController.screening = (Screening) eventBox.getMessage().getObject();
            }
        }

    }

    @Subscribe
    public void B(BaseEventBox eventBox) {
        if (eventBox.getId() == BaseEventBox.get_event_id("SAVED_USER_PURCHASES")) {

            UserPurchases userPurchases = (UserPurchases)eventBox.getMessage().getObject();
            if(userPurchases.getId_user().getUser_id().trim().equals(id.getText().trim())) {
                ErrorMessage.setVisible(true);
                ErrorMessage.setText("Thank you for you purchase, an email will be sent to you");
                Message m = new Message(1785, "#Send_mail");
                m.setObject(userPurchases);
                try {
                    SimpleClient.getClient().sendToServer(m);
                } catch (IOException e) {
                    ErrorMessage.setVisible(true);
                    ErrorMessage.setText(e.getMessage());
                    return;
                }
            }
        }

    }
    @FXML
    void go_back(ActionEvent event) {
        ErrorMessage.setVisible(false);
        try {
            SimpleChatClient.setRoot("TheaterMap");
        } catch (IOException e) {
            ErrorMessage.setVisible(true);
            ErrorMessage.setText(e.getMessage());
            return;
        }
    }
}
