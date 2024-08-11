package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.control.TextArea;
import java.util.List;


import il.cshaifasweng.OCSFMediatorExample.entities.Message;
import il.cshaifasweng.OCSFMediatorExample.entities.Movie;
import il.cshaifasweng.OCSFMediatorExample.entities.Screening;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.cell.PropertyValueFactory;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.util.*;

public class MovieDetailsController {

    @FXML // fx:id="branchesBox"
    private ComboBox<String> branchesBox; // Value injected by FXMLLoader

    @FXML // fx:id="image"
    private ImageView image; // Value injected by FXMLLoader

    @FXML // fx:id="movie_details"
    private TextArea movie_details; // Value injected by FXMLLoader

    @FXML // fx:id="movie_title"
    private TextArea movie_title; // Value injected by FXMLLoader

    @FXML // fx:id="screening_table"
    private TableView<Screening> screening_table; // Value injected by FXMLLoader

    @FXML // fx:id="ST_col"
    private TableColumn<Screening, String> branch_col; // Value injected by FXMLLoader

    @FXML // fx:id="date_col"
    private TableColumn<Screening, Date> date_col; // Value injected by FXMLLoader

    @FXML
    private Button seats_butt;

    @FXML
    private Button purchase_butt;

    public static Movie current_movie;

    private String selectedBranch; // Variable to store the selected branch

    private static List<Screening> screenings_list;

    @FXML
    void initialize() {

        EventBus.getDefault().register(this);

        assert branchesBox != null : "fx:id=\"branchesBox\" was not injected: check your FXML file 'primary.fxml'.";
        branchesBox.getItems().addAll("Downtown", "Uptown", "Sakhnin", "Nahef", "Paradise");

        if (current_movie != null) {
            movie_title.setText(current_movie.getMovie_name());
            movie_details.setText(current_movie.getDescription_());

            Message message = new Message (1000,"#GetScreening");
            message.setObject(current_movie);

            try {
                SimpleClient.getClient().sendToServer(message);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }

    }



    @FXML
    void chooseBranch(ActionEvent event) {
        selectedBranch = branchesBox.getValue();

        date_col.setCellValueFactory(new PropertyValueFactory<>("date_time"));
        branch_col.setCellValueFactory(new PropertyValueFactory<>("branch"));

        List<Screening> screenings = new ArrayList<Screening>();

        for (Screening s : screenings_list) {
            if (s.getBranch().equals(selectedBranch)) {
                screenings.add(s);
            }
        }


        screening_table.setItems(FXCollections.observableArrayList(screenings));

    }

    @Subscribe
    public void change_content1(BeginContentChangeEnent event)
    {

        System.out.println(event.getPage());
        EventBus.getDefault().unregister(this);
        EventBus.getDefault().post(new ContentChangeEvent(event.getPage()));
    }

    private void changeTable(List<Screening> screenings) {

        date_col.setCellValueFactory(new PropertyValueFactory<>("date_time"));
        branch_col.setCellValueFactory(new PropertyValueFactory<>("branch"));

        screening_table.setItems(FXCollections.observableArrayList(screenings));
    }


    @Subscribe
    public void A(BaseEventBox eventBox){
        if(eventBox.getId() == BaseEventBox.get_event_id("GET_SCREENING_DONE")){
            Platform.runLater(()->{

                screenings_list = (List<Screening>)eventBox.getMessage().getObject();

                changeTable(screenings_list);

            });
        }
    }

    @FXML
    void go_purchase(ActionEvent event) {

    }

    @FXML
    void go_theater_map(ActionEvent event) {

    }

}

