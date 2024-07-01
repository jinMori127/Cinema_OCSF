package il.cshaifasweng.OCSFMediatorExample.client;
import static il.cshaifasweng.OCSFMediatorExample.client.SimpleClient.Current_Message;

import il.cshaifasweng.OCSFMediatorExample.entities.Message;
import il.cshaifasweng.OCSFMediatorExample.entities.Movie;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.image.Image;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.EventBus;
import org.hibernate.type.TrueFalseType;


import java.awt.*;
import java.nio.file.Files;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;



public class MovieEditingDetailsController {

    @FXML
    private Text ErrorMessage;

    @FXML
    private TextField Movie_id;

    @FXML
    private TextField SearchText;

    @FXML
    private VBox Vbox_movies;

    @FXML
    private Button add_movie;

    @FXML
    private ComboBox<String> catgory;

    @FXML
    private TextArea description;

    @FXML
    private TextField director;

    @FXML
    private TextField duration;

    @FXML
    private Button edit_movie;

    @FXML
    private Button image;

    @FXML
    private TextField lead_actor;

    @FXML
    private TextField movie_name;

    @FXML
    private TextField price;

    @FXML
    private TextField year;

    @FXML
    private ImageView selected_image;

    @FXML
    void add_movie(ActionEvent event) {
        ErrorMessage.setVisible(false);
        if(movie_name.getText().trim().isEmpty())
        {
            ErrorMessage.setVisible(true);
            ErrorMessage.setText("Please enter a movie name");
            return;
        } else if (lead_actor.getText().trim().isEmpty()) {
            ErrorMessage.setVisible(true);
            ErrorMessage.setText("Please enter a leader actor");
            return;
        } else if (catgory.getValue()==null||catgory.getValue().trim().isEmpty()) {
            ErrorMessage.setVisible(true);
            ErrorMessage.setText("Please enter a category");
            return;
        } else if (year.getText().isEmpty()) {
            ErrorMessage.setVisible(true);
            ErrorMessage.setText("Please enter a year");
            return;
        }
        else if(duration.getText().trim().isEmpty())
        {
            ErrorMessage.setVisible(true);
            ErrorMessage.setText("Please enter a duration");
            return;
        } else if (director.getText().isEmpty()) {
            ErrorMessage.setVisible(true);
            ErrorMessage.setText("Please enter a director");
            return;
        }
        else if(price.getText().trim().isEmpty())
        {
            ErrorMessage.setVisible(true);
            ErrorMessage.setText("Please enter a price");
            return;
        } else if (description.getText().trim().isEmpty()) {
            ErrorMessage.setVisible(true);
            ErrorMessage.setText("Please enter a description");
            return;
        }
        Movie movie = new Movie();
        if(File_uploaded == null)
        {
            ErrorMessage.setVisible(true);
            ErrorMessage.setText("Image not Uploaded");
            return;
        }
        File f = File_uploaded;

        try{
            Integer.parseInt(year.getText());
        }
        catch(NumberFormatException e)
        {
            ErrorMessage.setVisible(true);
            ErrorMessage.setText("year must be an integer");
            return;
        }
        try{
            Integer.parseInt(price.getText());
        }
        catch(NumberFormatException e)
        {
            ErrorMessage.setVisible(true);
            ErrorMessage.setText("price must be an integer");
            return;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            sdf.setLenient(false); // Strict parsing
            sdf.parse(duration.getText());
        }
        catch(ParseException e){
            ErrorMessage.setVisible(true);
            ErrorMessage.setText("Invalid duration must be HH:mm");
            return;

        }
        String name = movie_name.getText();
        String main_actor = lead_actor.getText();
        String catgoryC =catgory.getValue();
        int yearC = Integer.parseInt(year.getText());
        String durationC = duration.getText();
        String directorC = director.getText();
        int priceC = Integer.parseInt(price.getText());
        String descriptionC = description.getText();

        SimpleDateFormat timeFormate = new SimpleDateFormat("HH:mm");
        movie.setMovie_name(name);
        movie.setMain_actors(main_actor);
        movie.setCategory(catgoryC);
        movie.setYear_(yearC);
        try {
            movie.setTime_(timeFormate.parse(durationC));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        movie.setDirector(directorC);
        movie.setPrice(priceC);
        movie.setDescription_(descriptionC);
        movie.setImageLocation(f);

        Message insert_message = new Message(3,"#InsertMovie");
        insert_message.setObject(movie);
        try {
            SimpleClient.getClient().sendToServer(insert_message);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }



    }

    private File File_uploaded;
    @FXML
    void choose_image(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        List<String> lstFile = new ArrayList<String>();
        lstFile.add("*.png");
        lstFile.add("*.jpg");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("picture",lstFile));
        File f = fileChooser.showOpenDialog(null);

        if (f != null) {
            Image image = new Image(f.toURI().toString());
            selected_image.setImage(image);
            File_uploaded = f;
        }
    }
    public void search_movie_name_function()
    {
        ErrorMessage.setVisible(false);
        Message message = new Message(3,"#SearchMovies");
        message.setObject(search_movie_name);


        try {
            SimpleClient.getClient().sendToServer(message);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public String search_movie_name = "";
    @FXML
    void search_movies(ActionEvent event) {
        ErrorMessage.setVisible(false);
        search_movie_name = SearchText.getText();
        search_movie_name_function();

    }

    @FXML
    void edit_movie(ActionEvent event) {
        ErrorMessage.setVisible(false);
        if(Movie_id.getText().trim().isEmpty())
        {
            ErrorMessage.setVisible(true);
            ErrorMessage.setText("Please select a movie");
            return;
        }
        if(movie_name.getText().trim().isEmpty())
        {
            ErrorMessage.setVisible(true);
            ErrorMessage.setText("Please enter a movie name");
            return;
        } else if (lead_actor.getText().trim().isEmpty()) {
            ErrorMessage.setVisible(true);
            ErrorMessage.setText("Please enter a leader actor");
            return;
        } else if (catgory.getValue()==null||catgory.getValue().trim().isEmpty()) {
            ErrorMessage.setVisible(true);
            ErrorMessage.setText("Please enter a category");
            return;
        } else if (year.getText().isEmpty()) {
            ErrorMessage.setVisible(true);
            ErrorMessage.setText("Please enter a year");
            return;
        }
        else if(duration.getText().trim().isEmpty())
        {
            ErrorMessage.setVisible(true);
            ErrorMessage.setText("Please enter a duration");
            return;
        } else if (director.getText().isEmpty()) {
            ErrorMessage.setVisible(true);
            ErrorMessage.setText("Please enter a director");
            return;
        }
        else if(price.getText().trim().isEmpty())
        {
            ErrorMessage.setVisible(true);
            ErrorMessage.setText("Please enter a price");
            return;
        } else if (description.getText().trim().isEmpty()) {
            ErrorMessage.setVisible(true);
            ErrorMessage.setText("Please enter a description");
            return;
        }
        Movie movie = SelectedMovie;
        if(File_uploaded == null)
        {
            ErrorMessage.setVisible(true);
            ErrorMessage.setText("Image not Uploaded");
            return;
        }
        File f = File_uploaded;

        try{
            Integer.parseInt(year.getText());
        }
        catch(NumberFormatException e)
        {
            ErrorMessage.setVisible(true);
            ErrorMessage.setText("year must be an integer");
            return;
        }
        try{
            Integer.parseInt(price.getText());
        }
        catch(NumberFormatException e)
        {
            ErrorMessage.setVisible(true);
            ErrorMessage.setText("price must be an integer");
            return;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            sdf.setLenient(false); // Strict parsing
            sdf.parse(duration.getText());
        }
        catch(ParseException e){
            ErrorMessage.setVisible(true);
            ErrorMessage.setText("Invalid duration must be HH:mm");
            return;

        }
        String name = movie_name.getText();
        String main_actor = lead_actor.getText();
        String catgoryC =catgory.getValue();
        int yearC = Integer.parseInt(year.getText());
        String durationC = duration.getText();
        String directorC = director.getText();
        int priceC = Integer.parseInt(price.getText());
        String descriptionC = description.getText();

        movie.setMovie_name(name);
        movie.setMain_actors(main_actor);
        movie.setCategory(catgoryC);
        movie.setYear_(yearC);
        SimpleDateFormat timeFormate = new SimpleDateFormat("HH:mm");
        try {
            movie.setTime_(timeFormate.parse(durationC));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        movie.setDirector(directorC);
        movie.setPrice(priceC);
        movie.setDescription_(descriptionC);
        movie.setImageLocation(f);
        /*if (!image_name.equals( movie.getImage_location())) {
            File f4 = new File("src/main/resources/images/"+image_name);
            f4.delete();
        }*/
        Message insert_message = new Message(3,"#UpdateMovie");
        insert_message.setObject(movie);

        try {
            SimpleClient.getClient().sendToServer(insert_message);


        } catch (IOException e) {
            throw new RuntimeException(e);
        }



    }
    private static Movie SelectedMovie;
    @Subscribe
    public void update_each_user_catalog(UpdateEachUserCatalogEvent event)
    {
        Platform.runLater(()->{
            create_catalog(event.getMessage());
        });
    }

    @Subscribe
    public void create_catlog_event(UpdateCatalogEvent E)
    {
        Platform.runLater(()->{
            search_movie_name_function();
        });
    }
    @Subscribe
    public void change_Movie_ID(UpdateMovieIdBoxEvent event){
        Platform.runLater(()->{
            Movie the_movie = (Movie) event.getMessage().getObject();
            Movie_id.setText(Integer.toString(the_movie.getAuto_number_movie()));
            SelectedMovie = the_movie;
        });
    }

    @FXML
    void change_all_prices(ActionEvent event) {
        ErrorMessage.setVisible(false);

        if(price.getText().trim().isEmpty())
        {
            ErrorMessage.setVisible(true);
            ErrorMessage.setText("Price cannot be empty");
        }
        try{
            Integer.parseInt(price.getText());
        }
        catch(NumberFormatException e)
        {
            ErrorMessage.setVisible(true);
            ErrorMessage.setText("price must be an integer");
            return;
        }
        Message message = new Message(3,"#ChangeAllPrices");
        message.setObject(Integer.parseInt(price.getText()));

        try {
            SimpleClient.getClient().sendToServer(message);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void create_catalog(Message M)
    {
        Vbox_movies.getChildren().clear();
        Vbox_movies.setPrefHeight(0);
        List<Movie> movies = (List<Movie>)M.getObject();
        for (Movie movie : movies) {
            Vbox_movies.setPrefHeight(Vbox_movies.getPrefHeight()+180);
            HBox hbox_movies = new HBox();
            hbox_movies.setSpacing(10);
            ImageView imageView = new ImageView();
            File file = movie.getImage_location();
            if(file != null) {
                Image image = new Image(file.toURI().toString());
                imageView.setImage(image);
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
            Button remove_movie = new Button();
            remove_movie.setText("Remove Movie");
            remove_movie.setOnAction(event->{
                int current_movie_id = movie.getAuto_number_movie();
                /*File f = new File("src/main/resources/images/"+movie.getImage_location());
                f.delete();*/
                Message delete_message = new Message(1, "#DeleteMovie");
                delete_message.setObject(movie);

                try {
                    SimpleClient.getClient().sendToServer(delete_message);

                } catch (IOException e) {
                    ErrorMessage.setText("movie did not get deleted");
                    ErrorMessage.setVisible(true);
                    throw new RuntimeException(e);
                }
                if(Integer.toString(current_movie_id).equals(Movie_id.getText()))
                {
                    Movie_id.setText("");
                    movie_name.setText("");
                    lead_actor.setText("");
                    catgory.setValue("");
                    year.setText("");
                    duration.setText("");
                    selected_image.setImage(null);
                    director.setText("");
                    price.setText("");
                    description.setText("");
                }
            });
            vboxButtons.getChildren().add(remove_movie);

            Button Button_Select = new Button();
            Button_Select.setText("Select");
            Button_Select.setOnAction(event->{
                File file1 = movie.getImage_location();
                File_uploaded = file1;
                if(file1!=null) {
                    Image image1 = new Image(file1.toURI().toString());
                    selected_image.setImage(image1);
                }
                else
                {
                    selected_image.setImage(null);
                }
                Movie_id.setText(Integer.toString(movie.getAuto_number_movie()));
                movie_name.setText(movie.getMovie_name());
                lead_actor.setText(movie.getMain_actors());
                catgory.setValue(movie.getCategory());
                SimpleDateFormat time_format = new SimpleDateFormat("HH:mm");
                duration.setText(time_format.format(movie.getTime_()));
                director.setText(movie.getDirector());
                price.setText(Integer.toString(movie.getPrice()));
                description.setText(movie.getDescription_());
                year.setText(Integer.toString(movie.getYear_()));
                SelectedMovie = movie;

            });
            vboxButtons.getChildren().add(Button_Select);



            Button go_to_screening_Button = new Button();
            go_to_screening_Button.setText("Screenings");
            go_to_screening_Button.setOnAction(event->{
                Message screening_message = new Message(2, "#GoToScreenings");
                screening_message.setObject(movie);

                try {
                    go_to_screening_movie = movie;
                    SimpleClient.getClient().sendToServer(screening_message);

                } catch (IOException e) {
                    ErrorMessage.setText("cant go to screening");
                    ErrorMessage.setVisible(true);
                    throw new RuntimeException(e);
                }
            });
            vboxButtons.getChildren().add(go_to_screening_Button);
            hbox_movies.getChildren().add(current_movie_vbox);
            hbox_movies.getChildren().add(vboxButtons);
            Vbox_movies.getChildren().add(hbox_movies);
        }
        ErrorMessage.setVisible(false);
    }

    public static Movie go_to_screening_movie;


    @FXML
    public void initialize() {
        EventBus.getDefault().register(this);

        catgory.getItems().add("Comedy");
        catgory.getItems().add("Sci-Fi");
        catgory.getItems().add("Action");
        catgory.getItems().add("Romance");
        catgory.getItems().add("Family");
        create_catalog(Current_Message);
    }

}
