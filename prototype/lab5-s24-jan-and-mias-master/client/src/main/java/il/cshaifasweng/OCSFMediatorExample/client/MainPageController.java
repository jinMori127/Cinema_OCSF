package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Movie;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import static il.cshaifasweng.OCSFMediatorExample.client.SimpleClient.Current_Message;
import javax.swing.*;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.List;


public class MainPageController implements Initializable {
    @FXML
    private TableView<Movie> catalog;

    @FXML
    private TableColumn<Movie, String> actors;

    @FXML
    private TableColumn<Movie, String> category;

    @FXML
    private TableColumn<Movie, String> description;

    @FXML
    private TableColumn<Movie, String> name;

    @FXML
    private TableColumn<Movie, String> time;

    @FXML
    private TableColumn<Movie, Integer> year;

    private ObservableList<Movie> list;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Set up the columns in the table
        name.setCellValueFactory(new PropertyValueFactory<>("movie_name"));
        actors.setCellValueFactory(new PropertyValueFactory<>("main_actors"));
        category.setCellValueFactory(new PropertyValueFactory<>("category"));
        description.setCellValueFactory(new PropertyValueFactory<>("description_"));
        time.setCellValueFactory(new PropertyValueFactory<>("time_"));
        year.setCellValueFactory(new PropertyValueFactory<>("year_"));

        // Load the data from the database
        list = FXCollections.observableArrayList((List<Movie>)(Current_Message.getObject()));
        catalog.setItems(list);
    }

}

