package il.cshaifasweng.OCSFMediatorExample.entities;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "Worker")
public class Worker implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int auto_number_worker;

    private String user_name;
    private String password;
    private String name;
    private String branch;
    private String role;

    //default constructor
    public Worker() {
    }

    //constructor
    public Worker(String user_name, String password, String name, String branch, String role) {
        this.user_name = user_name;
        this.password = password;
        this.name = name;
        this.branch = branch;
        this.role = role;
    }

    // getters and setters
    public int get_auto_number_worker() {
        return auto_number_worker;
    }

    public String get_user_name() {
        return user_name;
    }

    public void set_user_name(String user_name) {
        this.user_name = user_name;
    }

    public String get_password() {
        return password;
    }

    public void set_password(String password) {
        this.password = password;
    }

    public String get_name() {
        return name;
    }

    public void set_name(String name) {
        this.name = name;
    }

    public String get_branch() {
        return branch;
    }

    public void set_branch(String branch) {
        this.branch = branch;
    }

    public String get_role() {
        return role;
    }

    public void set_role(String role) {
        this.role = role;
    }
}
