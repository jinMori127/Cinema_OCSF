package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;


import il.cshaifasweng.OCSFMediatorExample.entities.Message;
import il.cshaifasweng.OCSFMediatorExample.entities.Movie;
import il.cshaifasweng.OCSFMediatorExample.entities.Screening;

import javafx.application.Platform;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.hibernate.transform.DistinctRootEntityResultTransformer;

import java.io.IOException;
import java.util.*;

public class MovieDetailsController {
    @FXML
    private Text Director;

    @FXML
    private Text catgory;

    @FXML
    private Text leading_actor;

    @FXML
    private TextArea movie_details;

    @FXML
    private Text movie_title;

    @FXML
    private Text price;

    @FXML
    private Button purchase_butt;

    @FXML
    private Text rating;

    @FXML
    private Text time;

    @FXML
    private Text year;

    @FXML
    private ComboBox<String> branchesBox;

    @FXML
    private ImageView image;

    @FXML
    private TableView<Screening> screening_table;

    @FXML
    private TableColumn<Screening, String> branch_col;

    @FXML
    private TableColumn<Screening, Date> date_col;

    @FXML
    private Text ErrorMessage;

    @FXML
    private DatePicker date;


    public static Movie current_movie;

    private static List<Screening> screenings_list;

    @FXML
    void initialize() {
        ErrorMessage.setVisible(false);
        EventBus.getDefault().register(this);

        assert branchesBox != null : "fx:id=\"branchesBox\" was not injected: check your FXML file 'MovieDetails.fxml'.";
        branchesBox.getItems().clear();
        branchesBox.getItems().addAll(SimpleChatClient.get_branches());

        if (current_movie != null) {
            Lay_out_Movie_details();
            Message message = new Message (1000,"#GetScreening");
            message.setObject(current_movie);

            try {
                SimpleClient.getClient().sendToServer(message);
            } catch (IOException e) {
                ErrorMessage.setText(e.getMessage());
                ErrorMessage.setVisible(true);
                return;
            }

        }
    }

    private void Lay_out_Movie_details()
    {
        if (current_movie != null) {
            Director.setText("Directed by: ");
            leading_actor.setText("Leading actor: ");
            rating.setText("Rating: ");
            catgory.setText("Category: ");
            time.setText("Time: ");
            price.setText("Price: ");
            year.setText("Year: ");
            movie_title.setText(current_movie.getMovie_name());
            movie_details.setText(current_movie.getDescription_());
            //System.out.println(Director.getLayoutBounds().getWidth());
            Director.setText(Director.getText() + current_movie.getDirector());
            //System.out.println(Director.getLayoutBounds().getWidth());
            double spacing = 25;
            leading_actor.setLayoutX(Director.getLayoutBounds().getWidth() + Director.getLayoutX() + spacing);
            leading_actor.setText(leading_actor.getText() + current_movie.getMain_actors());
            rating.setLayoutX(leading_actor.getLayoutBounds().getWidth() + leading_actor.getLayoutX() + spacing);
            rating.setText(rating.getText() + current_movie.getRating());
            catgory.setText(catgory.getText() + current_movie.getCategory());
            price.setText(price.getText() + current_movie.getPrice());
            year.setText(year.getText() + current_movie.getYear_());
            byte[] file1 = current_movie.getImage_location();

            if (file1 != null) {
                //Image image1 = new Image(file1.toURI().toString());
                //selected_image.setImage(image1);
                image.setImage(SwingFXUtils.toFXImage(Movie.convertByteArrayToImage(file1), null));
            } else {
                image.setImage(null);
            }
            SimpleDateFormat timeFormate = new SimpleDateFormat("HH:mm");

            time.setText(time.getText() + timeFormate.format(current_movie.getTime_()));

            time.setLayoutX(catgory.getLayoutX() + catgory.getLayoutBounds().getWidth() + spacing);
            price.setLayoutX(time.getLayoutX() + time.getLayoutBounds().getWidth() + spacing);
            year.setLayoutX(price.getLayoutX() + price.getLayoutBounds().getWidth() + spacing);
        }
    }



    @FXML
    void chooseBranch(ActionEvent event) {
        changeTable();
    }

    @Subscribe
    public void change_content1(BeginContentChangeEnent event)
    {

        System.out.println(event.getPage());
        EventBus.getDefault().unregister(this);
        EventBus.getDefault().post(new ContentChangeEvent(event.getPage()));
    }

    private void changeTable() {

        String selectedBranch = branchesBox.getValue();
        date_col.setCellValueFactory(new PropertyValueFactory<>("date_time"));
        branch_col.setCellValueFactory(new PropertyValueFactory<>("branch"));
        List<Screening> screenings = new ArrayList<Screening>();
        if(selectedBranch==null||selectedBranch.equals("All")){
            screenings.addAll(screenings_list);
        }
        else {
            for (Screening s : screenings_list) {
                if (s.getBranch().equals(selectedBranch)) {
                    screenings.add(s);
                }
            }
        }
        List<Screening> real_screenings = new ArrayList<>();
        if (date.getValue()!=null)
        {
            for (Screening s : screenings) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                String date_str = dateFormat.format(Date.from(date.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()));
                String s_date = dateFormat.format(s.getDate_time());
                //System.out.println(date_str);
                //System.out.println(s_date);
                if(date_str.equals(s_date))
                {
                    real_screenings.add(s);
                }
            }
        }
        else
        {
            real_screenings = screenings;
        }
        //screening_table.setItems(FXCollections.observableArrayList(screenings));

        screening_table.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Screening>(){
            @Override
            public void changed(ObservableValue<? extends Screening> observable, Screening oldValue, Screening newValue) {
                if (newValue != null) {
                    // add here the static variable change and the page switch
                    Date time_of_screening = newValue.getDate_time();
                    Date currentTime = new Date();

                    if (time_of_screening.before(currentTime)) {
                        ErrorMessage.setText("This screening has already passed. Please choose another one.");
                        ErrorMessage.setVisible(true);
                        return;
                    }
                    TheaterMapController.screening_id = newValue.getAuto_number_screening();
                    try {
                        SimpleChatClient.setRoot("TheaterMap");
                    } catch (IOException e) {
                        ErrorMessage.setText(e.getMessage());
                        ErrorMessage.setVisible(true);
                        return;
                    }
                }
            }
        });

        screening_table.setItems(FXCollections.observableArrayList(real_screenings));
    }


    @Subscribe
    public void A(BaseEventBox eventBox){
        if(eventBox.getId() == BaseEventBox.get_event_id("GET_SCREENING_DONE")){
            Platform.runLater(()->{

                screenings_list = (List<Screening>)eventBox.getMessage().getObject();
                Iterator<Screening> iterator = screenings_list.iterator();
                while (iterator.hasNext()) {
                    Screening s = iterator.next();
                    if (s.getDate_time().before(new Date())) {
                        iterator.remove();  // Safely remove the element using Iterator
                    }
                }
                changeTable();

            });
        }

        else if(eventBox.getId() == BaseEventBox.get_event_id("UPDATE_SCREENING_FOR_MOVIE")){
            Platform.runLater(()->{
                screenings_list = (List<Screening>)eventBox.getMessage().getObject();
                Iterator<Screening> iterator = screenings_list.iterator();
                while (iterator.hasNext()) {
                    Screening s = iterator.next();
                    System.out.println(new Date());
                    if (s.getDate_time().before(new Date())) {
                        iterator.remove();  // Safely remove the element using Iterator
                    }
                }
                changeTable();
            });
        } else if (eventBox.getId() == BaseEventBox.get_event_id("UPDATE_MOVIE_LIST")) {
            Platform.runLater(()->{
                List<Movie> movies = (List<Movie>)eventBox.getMessage().getObject();
                boolean found = false;
                for (Movie m : movies) {
                    if(m.getAuto_number_movie() == current_movie.getAuto_number_movie()) {
                        current_movie = m;
                        Lay_out_Movie_details();
                        found = true;
                    }
                }
                if (!found) {
                    ErrorMessage.setText("Movie has been deleted");
                    ErrorMessage.setVisible(true);
                    return;
                }
            });

        }
        else if (eventBox.getId()==BaseEventBox.get_event_id("SHOW_CM_CHANGES")) {
            Platform.runLater(()->{
                List<Movie> movies = (List<Movie>)eventBox.getMessage().getObject2();
                boolean found = false;
                for (Movie m : movies) {
                    if(m.getAuto_number_movie() == current_movie.getAuto_number_movie()) {
                        current_movie = m;
                        Lay_out_Movie_details();
                        found = true;
                    }
                }
            });
        }
    }

    @FXML
    void go_purchase(ActionEvent event) {
        try {
            SimpleChatClient.setRoot("PurchaseMovieLink");
        } catch (IOException e) {
            ErrorMessage.setText(e.getMessage());
            ErrorMessage.setVisible(true);
            return;
        }
    }

    @FXML
    void date_changed(ActionEvent event) {
        changeTable();
    }


}

