package il.cshaifasweng.OCSFMediatorExample.entities;

import javafx.scene.image.Image;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;

import javax.imageio.ImageIO;
import javax.persistence.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.nio.file.Files;
import java.util.Objects;

@Entity
@Table(name = "movies")
public class Movie implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int auto_number_movie;

    private String movie_name;
    private String main_actors;
    private String category;
    private String description_;

    @Temporal(TemporalType.TIME)
    private Date time_;

    @Lob
    @Column(name = "image_movie", columnDefinition = "LONGBLOB")
    private byte[] image_movie;
    private int year_;
    private int price;
    private String director;
    private double rating;


    private boolean notified;

    private String movie_link;

    @OneToMany(mappedBy = "movie",fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Screening> screenings = new ArrayList<Screening>();;

    public Movie(int auto_number_movie, String movie_name, String main_actors, String category, String description_, Date time_, int year_, String movie_link) {
        this.auto_number_movie = auto_number_movie;
        this.movie_name = movie_name;
        this.main_actors = main_actors;
        this.category = category;
        this.description_ = description_;
        this.time_ = time_;
        this.year_ = year_;
        this.movie_link = movie_link;
        notified = false;
    }

    public Movie() {
    }

    // Getters and Setters
    public double getRating()
    {
        return rating;
    }
    public void setRating(double rating)
    {
        this.rating = rating;
    }
    public int getAuto_number_movie() {
        return auto_number_movie;
    }

    public String getMovie_name() {
        return movie_name;
    }

    public void setMovie_name(String movie_name) {
        this.movie_name = movie_name;
    }

    public String getMain_actors() {
        return main_actors;
    }

    public void setMain_actors(String main_actors) {
        this.main_actors = main_actors;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription_() {
        return description_;
    }

    public void setDescription_(String description_) {
        this.description_ = description_;
    }

    public Date getTime_() {
        return time_;
    }

    public void setTime_(Date time_) {
        this.time_ = time_;
    }

    public int getYear_() {
        return year_;
    }

    public void setYear_(int year_) {
        this.year_ = year_;
    }

    public void setImageLocation(byte[] image_location)
    {
       this.image_movie = image_location;
    }
    public static BufferedImage convertByteArrayToImage(byte [] byteArray){
        BufferedImage image = null;
        try(ByteArrayInputStream bais = new ByteArrayInputStream(byteArray)){
            image = ImageIO.read(bais);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return image;
    }
    public byte[] getImage_location()
    {
        /*
        BufferedImage buffered_image = convertByteArrayToImage(image_movie);
        if(buffered_image != null)
        {
            Image fxImage = SwingFXUtils.toFXImage(buffered_image, null);
            return fxImage;
        }
        return null;*/
        return image_movie;
    }
    public List<Screening> getScreenings() {return screenings;}
    public void setScreenings(List<Screening> screenings) {this.screenings = screenings;}

    public void setPrice(int price){this.price = price;}
    public int getPrice(){return price;}
    public void setDirector(String director){this.director = director;}
    public String getDirector(){return director;}

    public String getMovie_link() {return movie_link;}
    public void setMovie_link(String movie_link) {this.movie_link = movie_link;}

    public String toString(){
        return "Movie Description: " + description_ + "\n" +
                "Main Actors: " + main_actors + "\n" +
                "Rating: " + rating + " / 10" + "\n" +
                "Category: " + category + "\n" +
                "Production Year: " + year_ + "\n";
    }

    public boolean isNotified() {
        return notified;
    }

    public void setNotified(boolean notified) {
        this.notified = notified;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Movie movie = (Movie) o;
        return movie.auto_number_movie == auto_number_movie;
    }

    @Override
    public int hashCode() {
        return Objects.hash(auto_number_movie);
    }
}
