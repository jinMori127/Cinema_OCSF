package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Message;
import il.cshaifasweng.OCSFMediatorExample.entities.Movie;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import javafx.scene.text.Text;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CatalogController {
    @FXML
    private Button search_button;

    @FXML
    private Text ErrorMessage;

    @FXML
    private AnchorPane Fillter_Pane;

    @FXML
    private VBox Vbox_movies;

    @FXML
    private AnchorPane catalog_Pane;

    @FXML
    private ComboBox<String> catgory;

    @FXML
    private TextField director;

    @FXML
    private TextField first_year;

    @FXML
    private TextField last_year;

    @FXML
    private TextField lead_actor;

    @FXML
    private TextField movie_name;

    @FXML
    private TextField rating;

    @FXML
    private ComboBox<String> sort_atribute;

    @FXML
    private ComboBox<String> sort_direction;

    @FXML
    private Button Fillter_button;
    @FXML
    private ComboBox<String> branch_combobox;

    @FXML
    private DatePicker end_date;

    @FXML
    private ComboBox<String> link;

    @FXML
    private DatePicker start_date;

    @FXML
    void begin_filter(ActionEvent event) {
        if (Fillter_button.getText().equals("filter")) {
            Fillter_Pane.setVisible(true);
            catalog_Pane.layoutYProperty().setValue(Fillter_Pane.getHeight());
            Fillter_button.setText("close");
        }
        else{
            Fillter_Pane.setVisible(false);
            catalog_Pane.layoutYProperty().setValue(0);
            Fillter_button.setText("filter");

        }

    }

    @FXML
    void search_movies(ActionEvent event) {
        ErrorMessage.setVisible(false);
        int year1 = 0;
        int year2 = 0;
        double min_rating = 0 ;

        if (!first_year.getText().trim().equals("")) {
            try{
                year1 = Integer.parseInt(first_year.getText().trim());
                if (year1 < 1900 || year1 > 2100) {
                    ErrorMessage.setText("Invalid Year");
                    ErrorMessage.setVisible(true);
                    return;
                }
            }
            catch (NumberFormatException e){

                ErrorMessage.setText("year must be an intger");
                ErrorMessage.setVisible(true);
                return;
            }
        }

        if (!last_year.getText().trim().equals("")) {
            try{
                year2 = Integer.parseInt(last_year.getText().trim());
                if (year2 < 1900 || year2 > 2100) {
                    ErrorMessage.setText("year between 1900 and 2100");
                    ErrorMessage.setVisible(true);
                    return;
                }
            }
            catch (NumberFormatException e){
                ErrorMessage.setText("year must be an intger");
                ErrorMessage.setVisible(true);
                return;
            }
        }
        if(!rating.getText().trim().equals("")) {
            try{
                min_rating =Double.parseDouble(rating.getText().trim());
                if (min_rating < 0 || min_rating > 10) {
                    ErrorMessage.setText("rating must be between 0 and 10");
                    ErrorMessage.setVisible(true);
                    return;
                }
            }
            catch (NumberFormatException e){
                ErrorMessage.setText("rating must be a double");
                ErrorMessage.setVisible(true);
                return;
            }
        }
        if(start_date.getValue() == null) {
            ErrorMessage.setText("Start date cannot be empty");
            ErrorMessage.setVisible(true);
            return;
        }
        if(end_date.getValue() == null) {
            ErrorMessage.setText("End date cannot be empty");
            ErrorMessage.setVisible(true);
            return;
        }
        if(start_date.getValue().isAfter(end_date.getValue()))
        {
            ErrorMessage.setText("Start date cannot be after end date");
            ErrorMessage.setVisible(true);
            return;
        }
        String Sort_atribute = sort_atribute.getSelectionModel().getSelectedItem();
        if (Sort_atribute.equals("price")){
            Sort_atribute = "price";
        }
        else if (Sort_atribute.equals("rating")){
            Sort_atribute = "rating";
        }
        else if (Sort_atribute.equals("movie name")){
            Sort_atribute = "movie_name";
        }
        else if (Sort_atribute.equals("year")){
            Sort_atribute = "year_";
        }
        String Sort_direction = sort_direction.getSelectionModel().getSelectedItem();
        if (Sort_direction.equals("descending")){
            Sort_direction = "desc";
        }
        else if (Sort_direction.equals("ascending")){
            Sort_direction = "asc";
        }
        else if (Sort_direction.equals("dont"))
        {
            Sort_direction = "dont";
        }
        Movie movie = new Movie();
        movie.setRating(min_rating);
        movie.setYear_(year1);
        movie.setMovie_name(movie_name.getText());
        movie.setDirector(director.getText().trim());
        movie.setMain_actors(lead_actor.getText().trim());
        movie.setCategory(catgory.getSelectionModel().getSelectedItem());
        Map<String, String> dictionary = new HashMap<>();
        dictionary.put("Sort_atribute", Sort_atribute);
        dictionary.put("Sort_direction", Sort_direction);
        dictionary.put("year2", String.valueOf(year2));
        dictionary.put("branch", branch_combobox.getValue());
        dictionary.put("screening_start_date", String.valueOf(start_date.getValue()));
        dictionary.put("screening_end_date", String.valueOf(end_date.getValue().plusDays(1)));
        dictionary.put("need_link", link.getValue());
        dictionary_search = dictionary;
        current_search = movie;
        Message m = new Message(10,"#SearchMovieFillter");
        m.setObject(movie);
        m.setObject2(dictionary);
        try {
            SimpleClient.getClient().sendToServer(m);
        } catch (IOException e) {
            ErrorMessage.setText(e.getMessage());
            ErrorMessage.setVisible(true);
            return;
        }



    }
    @Subscribe
    public void change_content1(BeginContentChangeEnent event)
    {

        System.out.println(event.getPage());
        EventBus.getDefault().unregister(this);
        EventBus.getDefault().post(new ContentChangeEvent(event.getPage()));
    }
    private Movie current_search = null ;
    private Map<String, String> dictionary_search = null;
    public void create_catalog(Message M)
    {
        Vbox_movies.getChildren().clear();
        Vbox_movies.setPrefHeight(50);
        List<Movie> movies = (List<Movie>)M.getObject();
        for (Movie movie : movies) {
            //Vbox_movies.setPrefHeight(Vbox_movies.getPrefHeight()+190);
            HBox hbox_movies = new HBox();
            hbox_movies.setSpacing(10);
            hbox_movies.setPrefHeight(180);
            ImageView imageView = new ImageView();
            byte[] file = movie.getImage_location();
            if(file != null) {
                //Image image = new Image(file.toURI().toString());
                imageView.setImage(SwingFXUtils.toFXImage(Movie.convertByteArrayToImage(file),null));
            }
            else{
                imageView.setImage(null);
            }
            imageView.setFitWidth(200);
            imageView.setFitHeight(150);
            imageView.setPickOnBounds(true);
            imageView.setPreserveRatio(true);
            hbox_movies.getChildren().add(imageView);

            VBox current_movie_vbox = new VBox();
            current_movie_vbox.setSpacing(5);

            TextArea movie_title = new TextArea();
            movie_title.setText(movie.getMovie_name());
            movie_title.setPrefHeight(20);
            movie_title.setPrefWidth(300);
            movie_title.setWrapText(true);
            movie_title.setEditable(false);

            TextArea discreption = new TextArea();
            discreption.setText(movie.getDescription_());
            discreption.setPrefHeight(100);
            discreption.setPrefWidth(300);
            discreption.setWrapText(true);
            discreption.setEditable(false);
            current_movie_vbox.getChildren().add(movie_title);
            current_movie_vbox.getChildren().add(discreption);

            VBox vboxButtons = new VBox();
            Button Button_Select = new Button();
            Button_Select.setText("Select");
            Button_Select.setOnAction(event->{
                MovieDetailsController.current_movie = movie;
                try {
                    SimpleChatClient.setRoot("MovieDetails");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            vboxButtons.getChildren().add(Button_Select);
            hbox_movies.getChildren().add(current_movie_vbox);
            hbox_movies.getChildren().add(vboxButtons);
            Vbox_movies.getChildren().add(hbox_movies);
        }
        ErrorMessage.setVisible(false);
    }
    @FXML
    public void initialize() {

        ErrorMessage.setVisible(false);
        ErrorMessage.setText("");
        catalog_Pane.layoutYProperty().setValue(0);
        Fillter_Pane.setVisible(false);
        sort_direction.getItems().clear();
        sort_direction.getItems().addAll("dont","descending", "ascending");
        sort_direction.setValue("dont");
        sort_atribute.getItems().clear();
        sort_atribute.getItems().addAll("movie name","year","price","rating");
        sort_atribute.setValue("movie name");
        catgory.getItems().clear();
        catgory.getItems().add("");
        List<String> cats = SimpleChatClient.get_categories();

        for (String cat : cats) {
            catgory.getItems().add(cat);
        }
        branch_combobox.getItems().clear();
        branch_combobox.getItems().add("All");
        branch_combobox.setValue("All");
        branch_combobox.getItems().addAll(SimpleChatClient.get_branches());
        start_date.setValue(LocalDate.now());
        end_date.setValue(LocalDate.now().plusYears(1000));
        link.getItems().add("does not matter");
        link.setValue("does not matter");
        link.getItems().add("yes");
        link.getItems().add("no");
        EventBus.getDefault().register(this);
        search_button.fire();
        /*
        Message message = new Message(2, "#GetAllMovies");
        try {
            SimpleClient.getClient().sendToServer(message);
        }
        catch (IOException e) {
            ErrorMessage.setText(e.getMessage());
            ErrorMessage.setVisible(true);
        }
         */

    }
    @Subscribe
    public void show_all_movies(BaseEventBox event)
    {
        if(event.getId() == 5) {
            Platform.runLater(() -> {

                create_catalog(event.getMessage());
            });
        }
        else if(event.getId() == BaseEventBox.get_event_id("GOT_SEARCH_MOVIE_FILTER")) {
            Platform.runLater(() -> {
                System.out.println("GOT_SEARCH_MOVIE_FILTER");
                create_catalog(event.getMessage());
            });
        }
        else if(event.getId() == BaseEventBox.get_event_id("UPDATE_MOVIE_LIST")) {
            if (current_search == null)
            {
                Platform.runLater(() -> {create_catalog(event.getMessage());});
            }
            else {
                Message m = new Message(10,"#SearchMovieFillter");
                m.setObject(current_search);
                m.setObject2(dictionary_search);
                try {
                    SimpleClient.getClient().sendToServer(m);
                } catch (IOException e) {
                    ErrorMessage.setText(e.getMessage());
                    ErrorMessage.setVisible(true);
                    return;
                }
            }
        }
    }

}
