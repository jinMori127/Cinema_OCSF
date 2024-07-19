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
    private TableView<UserPurchases> table_view;

    private ObservableList<UserPurchases> purchasesList = FXCollections.observableArrayList();

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
        System.out.println(user_list.size());

        auto_number_purchase.setCellValueFactory(new PropertyValueFactory<>("auto_number_purchase"));
        seats_column.setCellValueFactory(new PropertyValueFactory<>("seats"));
        payment_type_column.setCellValueFactory(new PropertyValueFactory<>("payment_type"));
        payment_amount_column.setCellValueFactory(new PropertyValueFactory<>("payment_amount"));
        link_column.setCellValueFactory(new PropertyValueFactory<>("link"));
        date_column.setCellValueFactory(new PropertyValueFactory<>("date_of_purchase"));

        list = FXCollections.observableArrayList(user_list);
        table_view.setItems(list);
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
            }
        });
    }
    @FXML
    public void show_purchase_information(){
// Get the selected row index
        int selectedRow = table_view.getSelectionModel().getSelectedIndex();

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

            // Set the information in the TextArea
            purchase_detailed_text.setText(contentText.toString());

        } else {
            // If no valid row is selected, clear the TextArea and show a warning alert
            purchase_detailed_text.clear();
        }
    }
}

