package il.cshaifasweng.OCSFMediatorExample.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

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
    private Date time_of_complain;
    private String respond;
    private boolean  respond_status;
    private String cinema_branch;


    public Complains() {}
    // Constructor
    public Complains(IdUser id_user, String complain_text,Date time_of_complain, String respond, boolean status, String cinema_branch) {
        this.id_user = id_user;
        this.complain_text = complain_text;
        this.time_of_complain = time_of_complain;
        this.respond = respond;
        this.respond_status = status;
        this.cinema_branch = cinema_branch;
    }

    // getter and setter
    public int getAuto_number_complains() {
        return auto_number_complains;
    }

    public IdUser getId_user() {
        return id_user;
    }
    public void setId_user(IdUser id_user) {
        this.id_user = id_user;
    }

    public String getComplain_text() {
        return complain_text;
    }
    public void setComplain_text(String complain_text) {
        this.complain_text = complain_text;
    }

    public Date getTime_of_complain() {
        return time_of_complain;
    }
    public void setTime_of_complain(Date time_of_complain) {
        this.time_of_complain = time_of_complain;
    }

    public String getRespond() {
        return respond;
    }
    public void setRespond(String respond) {
        this.respond = respond;
    }

    public boolean getStatus() {
        return respond_status;
    }
    public void setStatus(boolean respond_status) {
        this.respond_status = respond_status;
    }

    public String getCinema_branch() {
        return cinema_branch;
    }
    public void setCinema_branch(String cinema_branch) {
        this.cinema_branch = cinema_branch;
    }

    public String getClient_name() {
        return id_user.getName();
    }

}
