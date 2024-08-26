package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
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
    private TableColumn<Complains, String> respond_col;

    @FXML
    private TextArea complains_detailes;

    @FXML
    private TableColumn<Complains, Date> date_f;

    @FXML
    private TextArea respond;

    @FXML
    private Button submit_respond;

    @FXML
    private Button show_responded;

    @FXML
    private Button not_responded;

    @FXML
    private TableView<Complains> table_view;

    @FXML
    private ComboBox<String> branch;

    @FXML
    private TextField returned_price;

    private boolean phase;

    ////////////////////////////////////////////////////////////////////////////
    // Subscribe function //
    ///////////////////////////////////////////////////////////////////////////
    @Subscribe
    public void show_complains(BaseEventBox event) {
        if (event.getId() == BaseEventBox.get_event_id("SHOW_COMPLAINS")) {
            Platform.runLater(() -> {
                create_complains_table(event.getMessage());
            });
        } else if (event.getId() == BaseEventBox.get_event_id("SHOW_COMPLAINS_AND_MESSAGE")) {
            Platform.runLater(() -> {
                create_complains_table_and_message(event.getMessage());
            });
        }
        else if (event.getId() == BaseEventBox.get_event_id("SHOW_COMPLAINS_RESPOND")) {
            Platform.runLater(() -> {
                create_complains_table(event.getMessage());
            });
        }
    }

    @Subscribe
    public void change_content1(BeginContentChangeEnent event)
    {
        System.out.println(event.getPage());
        EventBus.getDefault().unregister(this);
        EventBus.getDefault().post(new ContentChangeEvent(event.getPage()));
    }

    ////////////////////////////////////////////////////////////////////////////
    // FXML function //
    ///////////////////////////////////////////////////////////////////////////
    @FXML
    public void initialize() {
        EventBus.getDefault().register(this);
        Message insert_message = new Message(40, "#show_complains");
        respond.setEditable(false);
        submit_respond.setDisable(true);
        respond_col.setVisible(false);
        phase = true;

        branch.getItems().clear();
        branch.getItems().add("All");
        branch.getItems().add("Sakhnin");
        branch.getItems().add("Haifa");
        branch.getItems().add("Nazareth");
        branch.getItems().add("Nhif");

        try {
            SimpleClient.getClient().sendToServer(insert_message);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Add double-click listener to table rows
        table_view.setOnMouseClicked((MouseEvent event) -> {
            if (event.getClickCount() == 2) {
                show_complain_information();
                respond.setEditable(true);
                submit_respond.setDisable(false);
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
        List<Object> respond_then_phase = new ArrayList<>();
        respond_then_phase.add(respond.getText());
        respond_then_phase.add(phase);
        insert_message.setObject(respond_then_phase);
        insert_message.setObject2(auto_number);
        String  return_price_text = returned_price.getText();
        if(return_price_text.isEmpty())
        {
            return_price_text = "0";
        }
        int convert_price = Integer.parseInt(return_price_text);
        insert_message.setObject3(convert_price);
        try {
            SimpleClient.getClient().sendToServer(insert_message);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void handle_show_respond_complains(ActionEvent event) {
        complains_detailes.setText("");
        respond.setText("");
        returned_price.setText("");
        respond.setEditable(false);
        submit_respond.setDisable(true);
        phase = false;
        respond_col.setVisible(true);
        Message insert_message = new Message(90, "#show_respond");
        try {
            SimpleClient.getClient().sendToServer(insert_message);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void handle_show_complains(ActionEvent event) {
        complains_detailes.setText("");
        respond.setText("");
        returned_price.setText("");
        respond.setEditable(false);
        submit_respond.setDisable(true);
        phase = true;
        respond_col.setVisible(false);
        Message insert_message = new Message(23, "#show_complains");
        try {
            SimpleClient.getClient().sendToServer(insert_message);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void choose_branch(ActionEvent event) {
        String c_branch = branch.getValue();
        if (c_branch == null || c_branch.isEmpty()) {
            return;
        }
        if (c_branch.equals("All")) {
            create_complains_table_c(list);
            return;
        }
        ObservableList<Complains> filteredList = FXCollections.observableArrayList();

        // Check if the original list is not null and not empty
        if (list != null && !list.isEmpty()) {
            for (Complains complain : list) {
                // Assuming 'Complains' has a method getCinemaBranch() to get the branch name
                if (c_branch.equals(complain.getCinema_branch())) {
                    filteredList.add(complain);  // Add the complaint to the filtered list if it matches the branch
                }
            }
            create_complains_table_c(filteredList);
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
                // Check if the column is visible
                if (column.isVisible()) {
                    Object cellData = column.getCellData(selectedRow);
                    contentText.append(column.getText()).append(": ").append(cellData).append("\n");
                }
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
        respond_col.setCellValueFactory(new PropertyValueFactory<>("respond"));

        list = FXCollections.observableArrayList(user_list);
        table_view.setItems(list);
    }

    private void create_complains_table_c(ObservableList<Complains> filtered_list) {
            auto_number_complains.setCellValueFactory(new PropertyValueFactory<>("auto_number_complains"));
            client_name.setCellValueFactory(new PropertyValueFactory<>("client_name"));
            complain_text.setCellValueFactory(new PropertyValueFactory<>("complain_text"));
            date_f.setCellValueFactory(new PropertyValueFactory<>("time_of_complain"));
            branch_name.setCellValueFactory(new PropertyValueFactory<>("cinema_branch"));
            respond_col.setCellValueFactory(new PropertyValueFactory<>("respond"));

            table_view.setItems(filtered_list);
    }



    private void create_complains_table_and_message(Message message) {
        create_complains_table(message);

        // Display a success message
        JOptionPane.showMessageDialog(null, "Response sent successfully!", "Success",
                JOptionPane.INFORMATION_MESSAGE);

        // set the text to empty
        complains_detailes.setText("");
        respond.setText("");
        returned_price.setText("");
        respond.setEditable(false);
        submit_respond.setDisable(true);
    }
}
