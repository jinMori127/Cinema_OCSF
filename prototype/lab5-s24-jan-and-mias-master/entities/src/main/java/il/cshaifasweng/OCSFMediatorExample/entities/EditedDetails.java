package il.cshaifasweng.OCSFMediatorExample.entities;

import javax.persistence.*;
import java.io.DataInput;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.io.File;

@Entity
@Table(name = "EDs")
public class EditedDetails implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToMany(mappedBy = "new_price", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Movie> movie = new ArrayList<Movie>();

    private double changed_price;

    public EditedDetails() {}

    public EditedDetails(int id, double changed_price) {
        this.id = id;
        this.changed_price = changed_price;
    }

    public int getId() {
        return id;
    }

    public double getChanged_price() {
        return changed_price;
    }

    public List<Movie> getMovie() {
        return movie;
    }

    public void setChanged_price(double changed_price) {
        this.changed_price = changed_price;
    }
}