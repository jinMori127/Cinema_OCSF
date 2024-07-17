package il.cshaifasweng.OCSFMediatorExample.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "id_users")
public class IdUser implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int auto_number_id_user;

    private String user_id;
    private String name;
    private String phone_number;
    private String email;

    @OneToMany(mappedBy = "id_user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UserPurchases> user_purchases;

    @OneToMany(mappedBy = "id_user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MultiEntryTicket> multi_entry_ticket;

    @OneToMany(mappedBy = "id_user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Complains> complains;

    // Default constructor
    public IdUser() {}

    // constructor
    public IdUser(String user_id, String name, String phone_number, String email) {
        this.user_id = user_id;
        this.name = name;
        this.phone_number = phone_number;
        this.email = email;
    }

    // getter and setter
    public int getAuto_number_id_users() {
        return auto_number_id_user;
    }


    public String get_user_id() {
        return user_id;
    }

    public void set_user_id(String user_id) {
        this.user_id = user_id;
    }



    public String get_name() {
        return name;
    }

    public void set_name(String name) {
        this.name = name;
    }



    public String get_phone_number() {
        return phone_number;
    }

    public void set_phone_number(String phone_number) {
        this.phone_number = phone_number;
    }



    public String get_email() {
        return email;
    }

    public void set_email(String email) {
        this.email = email;
    }



    public List<UserPurchases> get_user_purchases() {
        return user_purchases;
    }

    public void set_user_purchases(List<UserPurchases> user_purchases) {
        this.user_purchases = user_purchases;
    }



    public List<MultiEntryTicket> get_multi_entry_ticket() {
        return multi_entry_ticket;
    }

    public void set_multi_entry_ticket(List<MultiEntryTicket> multi_entry_ticket) {
        this.multi_entry_ticket = multi_entry_ticket;
    }



    public List<Complains> get_complains() {
        return complains;
    }

    public void set_complains(List<Complains> complains) {
        this.complains = complains;
    }
}
