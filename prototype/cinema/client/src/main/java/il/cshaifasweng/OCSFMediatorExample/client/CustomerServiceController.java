package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.collections.ObservableList;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.fxml.Initializable;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import il.cshaifasweng.OCSFMediatorExample.entities.Message;
import il.cshaifasweng.OCSFMediatorExample.entities.Complains;

import java.io.IOException;
import java.util.List;
import java.util.*;
import javax.swing.*;


public class CustomerServiceController {

    @FXML
    private TableColumn<Complains, Integer> auto_number_complains;

    @FXML
    private TableColumn<Complains, String> branch_name;

    @FXML
    private TableColumn<Complains, String> client_name;

    @FXML
    private TableColumn<Complains, String> complain_text;

    @FXML
    private TextArea complains_detailes;

    @FXML
    private TableColumn<Complains, Date> date_f;

    @FXML
    private TextArea respond;

    @FXML
    private Button submit_respond;

    @FXML
    private TableView<Complains> table_view;

    ////////////////////////////////////////////////////////////////////////////
    // Subscribe function //
    ///////////////////////////////////////////////////////////////////////////
    @Subscribe
    public void show_complains(BaseEventBox event) {
        if (event.getEnum_name().equals("SHOW_COMPLAINS")) {
            Platform.runLater(() -> {
                create_complains_table(event.getMessage());
            });
        } else if (event.getEnum_name().equals("SHOW_COMPLAINS_AND_MESSAGE")) {
            System.out.println("la la ");

            Platform.runLater(() -> {
                create_complains_table_and_message(event.getMessage());
            });
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // FXML function //
    ///////////////////////////////////////////////////////////////////////////
    @FXML
    public void initialize() {
        EventBus.getDefault().register(this);
        Message insert_message = new Message(40, "#show_complains");

        try {
            SimpleClient.getClient().sendToServer(insert_message);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Add double-click listener to table rows
        table_view.setOnMouseClicked((MouseEvent event) -> {
            if (event.getClickCount() == 2) {
                show_complain_information();
            }
        });
    }

    @FXML
    public void handle_submit_respond(ActionEvent event) {
        Message insert_message = new Message(60, "#submit_respond");

        int selectedRow = table_view.getSelectionModel().getSelectedIndex();

        int auto_number = -1;
        if (!table_view.getColumns().isEmpty()) {
            TableColumn<Complains, ?> firstColumn = table_view.getColumns().get(0);
            Object cellData = firstColumn.getCellData(selectedRow);
            auto_number = (int) cellData;
        }
        System.out.println("la la ");
        insert_message.setObject(respond.getText());
        insert_message.setObject2(auto_number);
        try {
            SimpleClient.getClient().sendToServer(insert_message);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // Helper function //
    ///////////////////////////////////////////////////////////////////////////
    private ObservableList<Complains> list;

    private void show_complain_information() {
        // Get the selected row index
        int selectedRow = table_view.getSelectionModel().getSelectedIndex();

        // Check if the selected row is valid
        if (selectedRow >= 0 && selectedRow < table_view.getItems().size()) {
            // Get the columns
            ObservableList<TableColumn<Complains, ?>> columns = table_view.getColumns();

            StringBuilder contentText = new StringBuilder();

            // Iterate through columns to get cell data
            for (TableColumn<Complains, ?> column : columns) {
                Object cellData = column.getCellData(selectedRow);
                contentText.append(column.getText()).append(": ").append(cellData).append("\n");
            }
            complains_detailes.setText(contentText.toString());
        }
    }

    private void create_complains_table(Message message) {
        List<Complains> user_list = (List<Complains>) message.getObject();

        auto_number_complains.setCellValueFactory(new PropertyValueFactory<>("auto_number_complains"));
        client_name.setCellValueFactory(new PropertyValueFactory<>("client_name"));
        complain_text.setCellValueFactory(new PropertyValueFactory<>("complain_text"));
        date_f.setCellValueFactory(new PropertyValueFactory<>("time_of_complain"));
        branch_name.setCellValueFactory(new PropertyValueFactory<>("cinema_branch"));

        list = FXCollections.observableArrayList(user_list);
        table_view.setItems(list);
    }

    private void create_complains_table_and_message(Message message) {
        create_complains_table(message);
        System.out.println("sadsfafs number");

        // Display a success message
        JOptionPane.showMessageDialog(null, "Response sent successfully!", "Success",
                JOptionPane.INFORMATION_MESSAGE);
    }
}
