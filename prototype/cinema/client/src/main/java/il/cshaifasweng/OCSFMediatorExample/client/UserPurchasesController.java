package il.cshaifasweng.OCSFMediatorExample.client;

import static il.cshaifasweng.OCSFMediatorExample.client.MovieEditingDetailsController.go_to_screening_movie;
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
public class UserPurchasesController {

    @FXML
    private Button cancel_purchase;

    @FXML
    private TextArea purchase_detailed_text;

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
    private TableColumn<Screening, String> branch_column;

    @FXML
    private TableColumn<UserPurchases, Date> date_column;


    @FXML
    private TableColumn<UserPurchases, String> movie_name_column;




    @FXML
    private TableColumn<UserPurchases, Date> date_screening_column;


    @FXML
    private Text ErrorMessage;

    @FXML
    private Text WarningMessage;


    @FXML
    private TableView<UserPurchases> table_view;

    private ObservableList<UserPurchases> purchasesList = FXCollections.observableArrayList();

    @Subscribe
    public void delete_purchases_for_user(DeletePurchasesBoxEvent event)
    {
        Platform.runLater(()->{
            update_list(event.getMessage());
        });
    }

    @Subscribe

    public void show_purchases_for_user(ShowPurchasesBoxEvent event)
    {
        Platform.runLater(()->{
            create_user_purchases(event.getMessage());
        });
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


    private String curr_id = "327876116";
    @FXML
    public void initialize() {
        EventBus.getDefault().register(this);
        Message insert_message = new Message(20,"#show_purchases");
        insert_message.setObject(curr_id);

        try {
            SimpleClient.getClient().sendToServer(insert_message);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Add double-click listener to table rows
        table_view.setOnMouseClicked((MouseEvent event) -> {
            if (event.getClickCount() == 2) {
                show_purchase_information();
              //  WarningMessage.setVisible(true);
               // WarningMessage.setText("select a screening");
            }
        });
    }
    @FXML
    public void show_purchase_information(){
// Get the selected row index
        int selectedRow = table_view.getSelectionModel().getSelectedIndex();
        //   WarningMessage.setVisible(false);


        // Check if the selected row is valid
        if (selectedRow >= 0 && selectedRow < table_view.getItems().size()) {
            // Get the columns
            ObservableList<TableColumn<UserPurchases, ?>> columns = table_view.getColumns();

            StringBuilder contentText = new StringBuilder();

            // Iterate through columns to get cell data
            for (TableColumn<UserPurchases, ?> column : columns) {
                Object cellData = column.getCellData(selectedRow);
                contentText.append(column.getText()).append(": ").append(cellData).append("\n");
            }

            purchase_detailed_text.setText(contentText.toString());

        } else {
            purchase_detailed_text.clear();
        }
    }
    @FXML
    private void CancelPurchase(ActionEvent event) {
        ErrorMessage.setVisible(false);

        int selectedRow = table_view.getSelectionModel().getSelectedIndex();
        int flag=0;

        int auto_num=-1;
        if (!table_view.getColumns().isEmpty()) {
            TableColumn<UserPurchases, ?> firstColumn = table_view.getColumns().get(0);
            Object cellData = firstColumn.getCellData(selectedRow);
            auto_num=(int)cellData;

            Date curr_date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");




            TableColumn<UserPurchases, ?> Sec_col = table_view.getColumns().get(1);
            cellData = Sec_col.getCellData(selectedRow);
            Date date_screening=(Date)cellData;




            if (date_screening.before(curr_date)) {
                ErrorMessage.setVisible(true);
                ErrorMessage.setText("Unable to remove purache, The movie already passed");
                flag=0;

                return;

            }


            else {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(curr_date);
                calendar.add(Calendar.HOUR, +3);
                Date curr_date_3 = calendar.getTime();

                calendar.add(Calendar.HOUR, +1);
                Date curr_date_1 = calendar.getTime();




                if (curr_date_3.before(date_screening)) {
                    ErrorMessage.setVisible(true);
                    ErrorMessage.setText("Ok, No problem");
                    flag = 100;
                                                      }

                else if (curr_date_1.before(date_screening)) {
                    ErrorMessage.setVisible(true);
                    ErrorMessage.setText("Ok, No problem,But you will got 50 %");
                    flag = 50;

                }

                else {
                    ErrorMessage.setVisible(true);
                    ErrorMessage.setText("You got 0 %");
                    flag = 0;
                     }
            }
        }

        if (auto_num >= 0 && selectedRow < table_view.getItems().size()) {
            Message delete_message = new Message(21,"#delete_purchases");
            delete_message.setObject(auto_num);
            try {
                SimpleClient.getClient().sendToServer(delete_message);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}

