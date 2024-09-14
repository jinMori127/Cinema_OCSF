package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.collections.ObservableList;
import il.cshaifasweng.OCSFMediatorExample.entities.*;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.text.SimpleDateFormat;

import java.util.*;

public class UserPurchasesController {

    @FXML
    private Button cancel_purchase;

    @FXML
    private TextArea purchase_detailed_text;

    @FXML
    private Text multi_ticket_info;

    @FXML
    private TableColumn<UserPurchases, Integer> auto_number_purchase;

    @FXML
    private TableColumn<UserPurchases, String> seats_column;

    @FXML
    private TableColumn<UserPurchases, String> payment_type_column;

    @FXML
    private TableColumn<UserPurchases, Double> payment_amount_column;

    @FXML
    private TableColumn<UserPurchases, String> link_column;

    @FXML
    private TableColumn<UserPurchases, Date> date_column;


    @FXML
    private TableColumn<UserPurchases, String> movie_name_column;

    @FXML
    private TableColumn<UserPurchases, Date> date_screening_column;


    @FXML
    private Text ErrorMessage;


    @FXML
    private TableView<UserPurchases> table_view;

    private ObservableList<UserPurchases> purchasesList = FXCollections.observableArrayList();

    private double refund;

    @Subscribe
    public void delete_purchases_for_user(BaseEventBox event)
    {
        if (event.getId() == 1) {
            Platform.runLater(() -> {
                update_list(event.getMessage());
            });
        }
    }

    @Subscribe
    public void show_purchases_for_user(BaseEventBox event)
    {
        if(event.getId()==3) {
            Platform.runLater(() -> {
                String text_multi_ticket = "Remaining Ticket: " + event.getMessage().getObject2().toString();
                multi_ticket_info.setText(text_multi_ticket);
                create_user_purchases(event.getMessage());
            });
        }
        else if (event.getId()==BaseEventBox.get_event_id("ADDTickets")) {
            String text_multi_ticket = "Remaining Ticket: " + event.getMessage().getObject().toString();
            multi_ticket_info.setText(text_multi_ticket);

        }
    }


    private ObservableList<UserPurchases> list;

    public void create_user_purchases(Message message) {
        List<UserPurchases> user_list = (List<UserPurchases>) message.getObject();

        auto_number_purchase.setCellValueFactory(new PropertyValueFactory<>("auto_number_purchase"));
        date_screening_column.setCellValueFactory(new PropertyValueFactory<>("screening_time"));
        seats_column.setCellValueFactory(new PropertyValueFactory<>("seats"));
        payment_type_column.setCellValueFactory(new PropertyValueFactory<>("payment_type"));
        payment_amount_column.setCellValueFactory(new PropertyValueFactory<>("payment_amount"));
        link_column.setCellValueFactory(new PropertyValueFactory<>("link"));
        date_column.setCellValueFactory(new PropertyValueFactory<>("date_of_purchase"));
        movie_name_column.setCellValueFactory(new PropertyValueFactory<>("movie_name"));

        list = FXCollections.observableArrayList(user_list);
        table_view.setItems(list);
    }
    public void update_list(Message message){
        create_user_purchases(message);
    }


    private String curr_id = UserLogInWithIDController.idUser.getUser_id();
    @FXML
    public void initialize() {
        EventBus.getDefault().register(this);
        Message insert_message = new Message(20,"#show_purchases");
        insert_message.setObject(curr_id);
        cancel_purchase.setDisable(true);

        try {
            SimpleClient.getClient().sendToServer(insert_message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Add double-click listener to table rows
        table_view.setOnMouseClicked((MouseEvent event) -> {
            if (event.getClickCount() == 2) {
                int selectedRow = table_view.getSelectionModel().getSelectedIndex();
                if (selectedRow != -1 && table_view.getItems().get(selectedRow) != null) {
                    cancel_purchase.setDisable(false);
                }
                show_purchase_information();
            }
        });
    }
    @FXML

    public void show_purchase_information() {
        try {
            ErrorMessage.setVisible(false);
            int selectedRow = table_view.getSelectionModel().getSelectedIndex();
            if (selectedRow >= 0 && selectedRow < table_view.getItems().size()) {
                ObservableList<TableColumn<UserPurchases, ?>> columns = table_view.getColumns();

                StringBuilder contentText = new StringBuilder();

                for (TableColumn<UserPurchases, ?> column : columns) {
                    Object cellData = column.getCellData(selectedRow);
                    contentText.append(column.getText()).append(": ").append(cellData).append("\n");
                }

                purchase_detailed_text.setText(contentText.toString());
                TableColumn<UserPurchases, ?> link = table_view.getColumns().get(3);
                Object cellData = link.getCellData(selectedRow);
                String link_text = (String) cellData;

                ErrorMessage.setVisible(true);
                if(link_text == null || link_text.isEmpty()) {
                    ErrorMessage.setText("Note:\nif still more than 3 hours you will get 100%\nif still between 1-3 hours you will get 50%\n" +
                            "if you used MultiTicket and still  more than 1 hour you will get your tikcet"+"\nin Other cases you will get 0 %");
                }
                else {
                    ErrorMessage.setText("Note:\nYou can return the home link till hour before it's activation and get 50%(Take into account if  you used MultiTicket You will get 0)\n" +
                            "in Other cases you will get 0 %");
                }
            } else {
                purchase_detailed_text.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
            ErrorMessage.setVisible(true);
            ErrorMessage.setText("Error: " + e.getMessage());
        }
    }

    @FXML
    private void CancelPurchase(ActionEvent event) {
        ErrorMessage.setVisible(false);

        int selectedRow = table_view.getSelectionModel().getSelectedIndex();
        if(selectedRow == -1)
        {
            return;
        }
        int percent_return=0;

        int auto_num=-1;
        if (!table_view.getColumns().isEmpty()) {
            TableColumn<UserPurchases, ?> firstColumn = table_view.getColumns().get(0);
            Object cellData = firstColumn.getCellData(selectedRow);
            auto_num=(int)cellData;

            Date curr_date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            TableColumn<UserPurchases, ?> Sec_col = table_view.getColumns().get(4);
            cellData = Sec_col.getCellData(selectedRow);
            Date date_screening=(Date)cellData;

            TableColumn<UserPurchases, ?> link = table_view.getColumns().get(3);
            cellData = link.getCellData(selectedRow);
            String link_text = (String) cellData;

            TableColumn<UserPurchases, ?> seats = table_view.getColumns().get(2);
            cellData = seats.getCellData(selectedRow);
            String seats_text = (String) cellData;

            seats_text = seats_text.replace("seats:", "").trim();
            String[] seatPairs = seats_text.split(",");
            int numOfSeats = seatPairs.length;





                Calendar calendar = Calendar.getInstance();
                calendar.setTime(curr_date);
                calendar.add(Calendar.HOUR, +3);// adding here to the current date three hours so we will
                                                // be able to check whether the time of the screening have more that 3 hours left
                                                                            // current_date_plus_3
                Date curr_date_3 = calendar.getTime();


                Calendar calendar2 = Calendar.getInstance();
                calendar2.setTime(curr_date);
                calendar2.add(Calendar.HOUR, +1);
                Date curr_date_1 = calendar2.getTime();

                TableColumn<UserPurchases, ?> five_col = table_view.getColumns().get(5);
                cellData = five_col.getCellData(selectedRow);
                double price =(double)cellData;

                TableColumn<UserPurchases, ?> seven_col = table_view.getColumns().get(7);
                cellData = seven_col.getCellData(selectedRow);
                String payment_type =(String) cellData;
                if (date_screening.before(curr_date)) {
                     ErrorMessage.setVisible(true);
                     percent_return=0;
                     ErrorMessage.setText("The screening time already passed So your refund is 0");
                 }


                else if (curr_date_3.before(date_screening) && (link_text == null||link_text.isEmpty())) {
                    if (payment_type.equals("Multi Ticket")){
                        Message message = new Message(102, "#return_tickets");
                        message.setObject(auto_num);
                        message.setObject2(numOfSeats);
                        ErrorMessage.setVisible(true);
                        ErrorMessage.setText("Value returned is "+numOfSeats+ " tickets" );
                        try {
                            SimpleClient.getClient().sendToServer(message);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                    }
                    else {
                        ErrorMessage.setVisible(true);
                        ErrorMessage.setText("Value returned 100%,Your Total Will be:" + price);
                        refund = price;
                        percent_return = 100;
                    }
                }

                else if (curr_date_1.before(date_screening)) {
                    if (!payment_type.equals("Multi Ticket")) {

                        ErrorMessage.setVisible(true);
                        ErrorMessage.setText("Value returned 50%,Your Total Will be:" + (price / 2));
                        refund = price / 2;
                        percent_return = 50;
                    }
                    else if (link_text != null && !link_text.trim().isEmpty()) {
                        ErrorMessage.setVisible(true);
                        ErrorMessage.setText("Value returned 0%,Your Total Will be:"+0);
                        refund = 0;

                    }
                    else {
                        ErrorMessage.setVisible(true);
                        ErrorMessage.setText("Value returned 0%,Your Total Will be:"+0);
                        refund = 0;
                    }
                }

                else {
                    ErrorMessage.setVisible(true);
                    ErrorMessage.setText("Value returned 0%,Your Total Will be:"+0);
                    refund = 0;
                    percent_return = 0;
                }
            }


        if (auto_num >= 0 && selectedRow < table_view.getItems().size()) {
            Message delete_message = new Message(21,"#delete_purchases");
            delete_message.setObject(auto_num);
            delete_message.setObject2(curr_id);
            delete_message.setObject3(refund);
            try {
                SimpleClient.getClient().sendToServer(delete_message);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    @Subscribe
    public void change_content1(BeginContentChangeEnent event)
    {
        System.out.println(event.getPage());
        EventBus.getDefault().unregister(this);
        EventBus.getDefault().post(new ContentChangeEvent(event.getPage()));
    }

}

