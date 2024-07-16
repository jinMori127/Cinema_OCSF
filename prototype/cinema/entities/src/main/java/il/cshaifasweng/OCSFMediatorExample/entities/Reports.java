package il.cshaifasweng.OCSFMediatorExample.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "Reports")
public class Reports implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int auto_number_report;

    @ElementCollection
    @CollectionTable(name = "report_ticket_sells", joinColumns = @JoinColumn(name = "report_id"))
    @Column(name = "ticket_sell")
    private List<String> report_ticket_sells;

    @ElementCollection
    @CollectionTable(name = "report_multy_entry_ticket", joinColumns = @JoinColumn(name = "report_id"))
    @Column(name = "multy_entry_ticket")
    private List<String> report_multy_entry_ticket;

    @ElementCollection
    @CollectionTable(name = "report_complains", joinColumns = @JoinColumn(name = "report_id"))
    @Column(name = "complain")
    private List<String> report_complains;

    @Temporal(TemporalType.DATE)
    private Date date;

    private String branch;

    // default constructor
    public Reports() {
    }

    // constructor
    public Reports(List<String> report_ticket_sells, List<String> report_multy_entry_ticket, List<String> report_complains, Date date, String branch) {
        this.report_ticket_sells = report_ticket_sells;
        this.report_multy_entry_ticket = report_multy_entry_ticket;
        this.report_complains = report_complains;
        this.date = date;
        this.branch = branch;
    }

    // getters and setters
    public int get_auto_number_report() {
        return auto_number_report;
    }

    public List<String> get_report_ticket_sells() {
        return report_ticket_sells;
    }

    public void set_report_ticket_sells(List<String> report_ticket_sells) {
        this.report_ticket_sells = report_ticket_sells;
    }

    public List<String> get_report_multy_entry_ticket() {
        return report_multy_entry_ticket;
    }

    public void set_report_multy_entry_ticket(List<String> report_multy_entry_ticket) {
        this.report_multy_entry_ticket = report_multy_entry_ticket;
    }

    public List<String> get_report_complains() {
        return report_complains;
    }

    public void set_report_complains(List<String> report_complains) {
        this.report_complains = report_complains;
    }

    public Date get_date() {
        return date;
    }

    public void set_date(Date date) {
        this.date = date;
    }

    public String get_branch() {
        return branch;
    }

    public void set_branch(String branch) {
        this.branch = branch;
    }
}
