package il.cshaifasweng.OCSFMediatorExample.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "Reports")
public class Reports implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int auto_number_report;

    private String report_ticket_sells;
    private String report_multy_entry_ticket;
    private String report_complains;

    @Temporal(TemporalType.DATE)
    private Date report_date;

    private String branch;

    // default constructor
    public Reports() {
    }

    // constructor
    private static String create_String_of_list(List<Integer> l) {
        StringBuilder s = new StringBuilder();
        int i = 1;
        for (Integer integer : l) {
            s.append(i).append("::").append(integer).append("\n");
            i++;
        }
        return s.toString();
    }

    private static List<Integer> Create_List_From_String(Object obj) {
        List<Integer> l = new ArrayList<>();

        if (obj instanceof String) {
            String s = (String) obj;
            String[] lines = s.split("\n");

            for (String line : lines) {
                try {
                    String[] parts = line.split("::");

                    if (parts.length != 2 || parts[0].trim().isEmpty() || parts[1].trim().isEmpty()) {
                        System.err.println("Skipping malformed line: " + line);
                        l.add(0);
                        continue;
                    }

                    int day = Integer.parseInt(parts[0].trim());
                    int value = Integer.parseInt(parts[1].trim());

                    while (l.size() < day) {
                        l.add(0);
                    }

                    l.set(day - 1, value);
                } catch (NumberFormatException e) {
                    System.err.println("Error parsing line: " + line);
                    e.printStackTrace();
                    l.add(0);
                }
            }

            while (l.size() < 31) {
                l.add(0);
            }

        } else if (obj instanceof List) {
            return (List<Integer>) obj;
        } else {
            System.err.println("Unexpected data type: " + obj.getClass().getName());
        }

        return l;
    }

    public Reports(Date report_date, String branch) {
        this.report_date = report_date;
        this.branch = branch;
    }

    public int getAuto_number_report() {
        return auto_number_report;
    }

    public List<Integer> getReport_ticket_sells() {
        return Create_List_From_String(report_ticket_sells);
    }

    public void setReport_ticket_sells(List<Integer> report_ticket_sells) {
        this.report_ticket_sells = create_String_of_list(report_ticket_sells);
    }

    public List<Integer> getReport_multy_entry_ticket() {
        return Create_List_From_String(report_multy_entry_ticket);
    }

    public void setReport_multy_entry_ticket(List<Integer> report_multy_entry_ticket) {
        this.report_multy_entry_ticket = create_String_of_list(report_multy_entry_ticket);
    }

    public List<Integer> getReport_complains() {
        return Create_List_From_String(report_complains);
    }

    public void setReport_complains(List<Integer> report_complains) {
        this.report_complains = create_String_of_list(report_complains);
    }

    public Date getReportDate() {
        return report_date;
    }

    public void setReportDate(Date report_date) {
        this.report_date = report_date;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }
}
