package il.cshaifasweng.OCSFMediatorExample.entities;

import javax.persistence.*;
import java.io.Serializable;

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

    public UserPurchases() {

    }

    public UserPurchases(String seats, String payment_type, double payment_amount, IdUser id_user,Screening screening, String purchase_type){
        this.seats = seats;
        this.payment_type = payment_type;
        this.payment_amount = payment_amount;
        this.id_user = id_user;
        this.screening = screening;
        this.purchase_type = purchase_type;
    }

    public UserPurchases(String seats, String payment_type, double payment_amount, IdUser id_user,Screening screening, String purchase_type, String link){
        this.seats = seats;
        this.payment_type = payment_type;
        this.payment_amount = payment_amount;
        this.id_user = id_user;
        this.screening = screening;
        this.purchase_type = purchase_type;
        this.link = link;
    }

    public int get_auto_number_purchase() {
        return auto_number_purchase;
    }

    public String get_seats() {
        return seats;
    }
    public void set_seats(String seats) {
        this.seats = seats;
    }
    public String get_payment_type() {
        return payment_type;
    }
    public void set_payment_type(String payment_type) {
        this.payment_type = payment_type;
    }
    public double get_payment_amount() {
        return payment_amount;
    }
    public void set_payment_amount(double payment_amount) {
        this.payment_amount = payment_amount;
    }
    public IdUser get_id_user(){
        return id_user;
    }
    public void set_id_user(IdUser id_user){
        this.id_user = id_user;
    }
    public Screening get_screening() {
        return screening;
    }
    public void set_screening(Screening screening){
        this.screening = screening;
    }
    public String get_purchase_type() {
        return purchase_type;
    }
    public void set_purchase_type(String purchase_type) {
        this.purchase_type = purchase_type;
    }
    public String getLink() {
        return link;
    }
    public void set_link(String link) {
        this.link = link;
    }
}
