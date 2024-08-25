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
    private boolean is_worker_loggedIn;
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
        this.is_worker_loggedIn = false;
    }

    // getters and setters
    public int getAuto_number_worker() {
        return auto_number_worker;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean getIs_worker_loggedIn() {return is_worker_loggedIn;}

    public void setIs_worker_loggedIn(boolean is_worker_loggedIn) {this.is_worker_loggedIn = is_worker_loggedIn;}
}
