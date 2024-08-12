package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;

import java.util.List;


import il.cshaifasweng.OCSFMediatorExample.entities.Message;
import il.cshaifasweng.OCSFMediatorExample.entities.Movie;
import il.cshaifasweng.OCSFMediatorExample.entities.Screening;

import javafx.application.Platform;
import javafx.scene.control.cell.PropertyValueFactory;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.util.*;

public class MovieDetailsController {

    @FXML
    private ComboBox<String> branchesBox;

    @FXML
    private ImageView image;

    @FXML
    private TextArea movie_details;

    @FXML
    private TextArea movie_title;

    @FXML
    private TableView<Screening> screening_table;

    @FXML
    private TableColumn<Screening, String> branch_col;

    @FXML
    private TableColumn<Screening, Date> date_col;

    @FXML
    private Button seats_butt;

    @FXML
    private Button purchase_butt;

    public static Movie current_movie;

    private static List<Screening> screenings_list;

    private static Screening selectedScreening;

    @FXML
    void initialize() {

        EventBus.getDefault().register(this);

        assert branchesBox != null : "fx:id=\"branchesBox\" was not injected: check your FXML file 'MovieDetails.fxml'.";
        branchesBox.getItems().clear();
        branchesBox.getItems().addAll("All", "Nazareth", "Sakhnin", "Nhif", "Haifa");

        if (current_movie != null) {
            movie_title.setText("Movie Title: " + current_movie.getMovie_name());
            movie_details.setText(current_movie.toString());

            Message message = new Message (1000,"#GetScreening");
            message.setObject(current_movie);

            try {
                SimpleClient.getClient().sendToServer(message);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }

        screening_table.setRowFactory(tv -> {
            TableRow<Screening> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()) {
                    Screening rowData = row.getItem();
                    handleRowClick(rowData);
                }
            });
            return row;
        });

    }

    private void handleRowClick(Screening screening) {

        for (Screening s : screenings_list) {
            if (s.getBranch().equals(screening.getBranch()) && s.getDate_time().equals(screening.getDate_time())) {
                this.selectedScreening = s;
                break;
            }
        }

    }

    @FXML
    void chooseBranch(ActionEvent event) {
        String selectedBranch = branchesBox.getValue();

        date_col.setCellValueFactory(new PropertyValueFactory<>("date_time"));
        branch_col.setCellValueFactory(new PropertyValueFactory<>("branch"));

        List<Screening> screenings = new ArrayList<Screening>();

        if(selectedBranch.equals("All")){
            screenings.addAll(screenings_list);
        }

        else
            for (Screening s : screenings_list) {
                if (s.getBranch().equals(selectedBranch)) {
                    screenings.add(s);
                }
            }


        screening_table.setItems(FXCollections.observableArrayList(screenings));

        screening_table.setRowFactory(tv -> {
            TableRow<Screening> row = new TableRow<>();
            row.setOnMouseClicked(event1 -> {
                if (!row.isEmpty()) {
                    Screening rowData = row.getItem();
                    handleRowClick(rowData);
                }
            });
            return row;
        });

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

        else if(eventBox.getId() == BaseEventBox.get_event_id("UPDATE_SCREENING_FOR_MOVIE")){
            Platform.runLater(()->{

                screenings_list = (List<Screening>)eventBox.getMessage().getObject();

                changeTable(screenings_list);

            });
        }

    }

    @FXML
    void go_purchase(ActionEvent event) {
        try {
            SimpleChatClient.setRoot("HomePage");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void go_theater_map(ActionEvent event) {
        try {
            SimpleChatClient.setRoot("HomePage");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}

