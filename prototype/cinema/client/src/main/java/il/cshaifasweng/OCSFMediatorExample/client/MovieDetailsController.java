package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Movie;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import javafx.scene.control.TableColumn;
import java.io.InputStream;
import java.io.ByteArrayInputStream;





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
    private TableView<?> screening_table; // Value injected by FXMLLoader

    @FXML // fx:id="ST_col"
    private TableColumn<?, ?> ST_col; // Value injected by FXMLLoader

    @FXML // fx:id="date_col"
    private TableColumn<?, ?> date_col; // Value injected by FXMLLoader

    public static Movie current_movie;

    private String selectedBranch; // Variable to store the selected branch

    @FXML
    void initialize() {
        assert branchesBox != null : "fx:id=\"branchesBox\" was not injected: check your FXML file 'primary.fxml'.";
        branchesBox.getItems().addAll("Karmiel", "Haifa", "Sakhnin", "Nahef", "Paradise");

        if (current_movie != null) {
            movie_title.setText(current_movie.getMovie_name());
            movie_details.setText(current_movie.getDescription_());

            byte[] imageBytes = current_movie.getImage_location();
            InputStream inputStream = new ByteArrayInputStream(imageBytes);
            Image movieImage = new Image(inputStream);
            image.setImage(movieImage);



            // Initialize the screening table
            //screeningTimeColumn.setCellValueFactory(new PropertyValueFactory<>("screeningTime"));
            //screening_table.setItems(screenings);
        }

    }


    @FXML
    void chooseBranch(ActionEvent event) {
        selectedBranch = branchesBox.getValue();

        // Assuming current_branch is set based on selectedBranch
        //List<Screening> filteredScreenings = getScreeningsForMovieAndBranch(current_movie, current_branch);

        // Update the screening table
        //screenings.setAll(filteredScreenings);
    }



}

