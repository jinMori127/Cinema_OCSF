package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Message;
import il.cshaifasweng.OCSFMediatorExample.entities.Movie;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javax.swing.plaf.nimbus.State;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

import static il.cshaifasweng.OCSFMediatorExample.client.SimpleClient.Current_Message;

public class HomePageCotroller {
    static HomePageCotroller current_home_page;
    @FXML
    private Text ErrorMssage;

    @FXML
    private VBox Vbox_movies;

    @FXML
    public void initialize() {

        EventBus.getDefault().register(this);
        current_home_page = this;
        Message message = new Message(10,"#GetHomePage");
        try {
            SimpleClient.getClient().sendToServer(message);
        } catch (IOException e) {
            ErrorMssage.setText(e.getMessage());
            ErrorMssage.setVisible(true);
        }

    }
    @Subscribe
    public void show_list(BaseEventBox event)
    {
        System.out.println("show_list");
        System.out.println(event.getMessage().getMessage());
        if(event.getId()==5) {
            Platform.runLater(() -> {
                create_catalog(event.getMessage());
            });
        }
        else if(event.getId()==BaseEventBox.get_event_id("UPDATE_SCREENING_FOR_MOVIE")) {
            Message message = new Message(10,"#GetHomePage");
            try {
                SimpleClient.getClient().sendToServer(message);
            } catch (IOException e) {
                ErrorMssage.setText(e.getMessage());
                ErrorMssage.setVisible(true);
            }
        }

    }

    @Subscribe
    public void change_content1(BeginContentChangeEnent event)
    {

        System.out.println(event.getPage());
        EventBus.getDefault().unregister(this);
        EventBus.getDefault().post(new ContentChangeEvent(event.getPage()));
    }

    public void create_catalog(Message M)
    {
        Vbox_movies.getChildren().clear();
        Vbox_movies.setPrefHeight(50);
        List<Movie> movies = (List<Movie>)M.getObject();
        for (Movie movie : movies) {
            Vbox_movies.setPrefHeight(Vbox_movies.getPrefHeight()+190);
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
            });
            vboxButtons.getChildren().add(Button_Select);
            hbox_movies.getChildren().add(current_movie_vbox);
            hbox_movies.getChildren().add(vboxButtons);
            Vbox_movies.getChildren().add(hbox_movies);
        }
        ErrorMssage.setVisible(false);
    }

}
