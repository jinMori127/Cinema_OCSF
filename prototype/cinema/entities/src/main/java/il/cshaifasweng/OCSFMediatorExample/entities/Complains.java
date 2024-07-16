package il.cshaifasweng.OCSFMediatorExample.entities;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "complains")
public class Complains implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int auto_number_complains;

    @ManyToOne
    @JoinColumn(name = "id_user")
    private IdUser id_user;

    private String complain_text;
    private String time_of_complain;
    private String respond;
    private bool respond_status;
    private String cinema_branch;


    public Complains() {}
    // Constructor
    public Complains(IdUser id_user, String complain_text, String time_of_complain, String respond, String status, String cinema_branch) {
        this.id_user = id_user;
        this.complain_text = complain_text;
        this.time_of_complain = time_of_complain;
        this.respond = respond;
        this.status = status;
        this.cinema_branch = cinema_branch;
    }

    // Default Constructor

    // getter and setter

    public int get_auto_number_complains() {
        return auto_number_complains;
    }

    public void set_auto_number_complains(int auto_number_complains) {
        this.auto_number_complains = auto_number_complains;
    }

    public IdUser get_id_user() {
        return id_user;
    }

    public void set_id_user(IdUser id_user) {
        this.id_user = id_user;
    }

    public String get_complain_text() {
        return complain_text;
    }

    public void set_complain_text(String complain_text) {
        this.complain_text = complain_text;
    }

    public String get_time_of_complain() {
        return time_of_complain;
    }

    public void set_time_of_complain(String time_of_complain) {
        this.time_of_complain = time_of_complain;
    }

    public String get_respond() {
        return respond;
    }

    public void set_respond(String respond) {
        this.respond = respond;
    }

    public String get_status() {
        return status;
    }

    public void set_status(String status) {
        this.status = status;
    }

    public String get_cinema_branch() {
        return cinema_branch;
    }

    public void set_cinema_branch(String cinema_branch) {
        this.cinema_branch = cinema_branch;
    }
}
