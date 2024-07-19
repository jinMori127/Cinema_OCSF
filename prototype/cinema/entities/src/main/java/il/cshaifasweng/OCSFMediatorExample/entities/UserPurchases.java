package il.cshaifasweng.OCSFMediatorExample.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "UserPurchases")
public class UserPurchases implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int auto_number_purchase;
    private String seats;
    private String payment_type;
    private double payment_amount;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", referencedColumnName = "auto_number_id_user")
    private IdUser id_user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "screening_id", referencedColumnName = "auto_number_screening")
    private Screening screening;

    private String purchase_type;
    private String link;

    @Temporal(TemporalType.TIMESTAMP)
    private Date date_of_purchase;

    // constructors
    public UserPurchases() {
    }

    public UserPurchases(String seats, String payment_type, double payment_amount, IdUser id_user,Screening screening, String purchase_type, Date date_of_purchase){
        this.seats = seats;
        this.payment_type = payment_type;
        this.payment_amount = payment_amount;
        this.id_user = id_user;
        this.screening = screening;
        this.purchase_type = purchase_type;
        this.date_of_purchase = date_of_purchase;
    }

    public UserPurchases(String seats, String payment_type, double payment_amount, IdUser id_user,Screening screening, String purchase_type, String link, Date date_of_purchase){
        this.seats = seats;
        this.payment_type = payment_type;
        this.payment_amount = payment_amount;
        this.id_user = id_user;
        this.screening = screening;
        this.purchase_type = purchase_type;
        this.link = link;
        this.date_of_purchase = date_of_purchase;
    }

    // get/set methods
    public int getAuto_number_purchase() {
        return auto_number_purchase;
    }

    public String getSeats() {
        return seats;
    }
    public void setSeats(String seats) {
        this.seats = seats;
    }
    public String getPayment_type() {
        return payment_type;
    }
    public void setPayment_type(String payment_type) {
        this.payment_type = payment_type;
    }
    public double getPayment_amount() {
        return payment_amount;
    }
    public void setPayment_amount(double payment_amount) {
        this.payment_amount = payment_amount;
    }
    public IdUser getId_user(){
        return id_user;
    }
    public void setId_user(IdUser id_user){
        this.id_user = id_user;
    }
    public Screening getScreening() {
        return screening;
    }
    public void setScreening(Screening screening){
        this.screening = screening;
    }
    public String getPurchase_type() {
        return purchase_type;
    }
    public void setPurchase_type(String purchase_type) {
        this.purchase_type = purchase_type;
    }
    public String getLink() {
        return link;
    }
    public void setLink(String link) {
        this.link = link;
    }
    public Date getDate_of_purchase() { return date_of_purchase; }
    public void setDate_of_purchase(Date date_of_purchase) {this.date_of_purchase = date_of_purchase;}
}
