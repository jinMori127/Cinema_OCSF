package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Message;
import il.cshaifasweng.OCSFMediatorExample.entities.Reports;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.util.*;

public class ReportsController {

    @FXML
    private Text Error_Message;

    @FXML
    private StackedBarChart<String, Number> complain_chart;

    @FXML
    private StackedBarChart<String, Number> multientry_chart;

    @FXML
    private StackedBarChart<String, Number> purchases_chart;

    @FXML
    private ComboBox<String> choosed_branch;

    @FXML
    private ComboBox<String> choosed_month;

    @FXML
    private ComboBox<String> choosed_year;

    @FXML
    private TextArea report_text;


    @FXML
    void initialize() {
        EventBus.getDefault().register(this);
        Error_Message.setVisible(false);

        choosed_branch.getItems().clear();
        choosed_branch.getItems().addAll("", "Sakhnin", "Haifa", "Nazareth", "Nhif");
        choosed_year.getItems().clear();
        choosed_year.getItems().addAll("", "2024", "2023", "2022");
        choosed_year.setOnAction(event -> updateMonthsBasedOnYear());

        report_text.setVisible(false);
        complain_chart.setVisible(false);
        multientry_chart.setVisible(false);
        purchases_chart.setVisible(false);

        try {
            create_reports();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void updateMonthsBasedOnYear() {
        String selectedYear = choosed_year.getValue();
        Calendar today = Calendar.getInstance();
        int currentYear = today.get(Calendar.YEAR);
        int currentMonth = today.get(Calendar.MONTH) + 1;

        choosed_month.getItems().clear();

        if (selectedYear == null || selectedYear.isEmpty()) {
            choosed_month.getItems().addAll("", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12");
        } else {
            int selectedYearInt = Integer.parseInt(selectedYear);
            if (selectedYearInt < currentYear) {
                choosed_month.getItems().addAll("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12");
            } else if (selectedYearInt == currentYear) {
                for (int i = 1; i <= currentMonth; i++) {
                    choosed_month.getItems().add(String.valueOf(i));
                }
            }
        }
    }

    private void create_reports() throws IOException {
        System.out.println("got into create_reports");
        // deleteAllReports();
        Message message = new Message(BaseEventBox.get_event_id("REPORTS"), "#createReports");
        SimpleClient.getClient().sendToServer(message);
    }

    private void handleCreateReports(Message message) {
        System.out.println("got into handleCreateReports");
    }

    private void deleteAllReports() throws IOException {
        System.out.println("Deleting all existing reports...");
        Message deleteMessage = new Message(BaseEventBox.get_event_id("REPORTS"), "#deleteAllReports");
        SimpleClient.getClient().sendToServer(deleteMessage);
    }

    public void onSearchReports(javafx.event.ActionEvent actionEvent) {
        String selectedBranch = choosed_branch.getValue();
        String selectedMonth = choosed_month.getValue();
        String selectedYear = choosed_year.getValue();

        if (selectedBranch == null || selectedMonth == null || selectedYear == null) {
            Error_Message.setVisible(true);
            Error_Message.setText("Please select branch, month, and year.");
            report_text.setVisible(false);
            complain_chart.setVisible(false);
            multientry_chart.setVisible(false);
            purchases_chart.setVisible(false);
            return;
        }
        if (selectedBranch.isEmpty() || selectedMonth.isEmpty() || selectedYear.isEmpty()) {
            Error_Message.setVisible(true);
            Error_Message.setText("Please select branch, month, and year.");
            report_text.setVisible(false);
            complain_chart.setVisible(false);
            multientry_chart.setVisible(false);
            purchases_chart.setVisible(false);
            return;
        } else {
            Error_Message.setVisible(false);
            report_text.setVisible(true);
        }

        Message message = new Message(BaseEventBox.get_event_id("REPORTS"), "#SearchReport");
        message.setObject(selectedBranch);
        message.setObject2(Integer.parseInt(selectedYear));
        message.setObject3(Integer.parseInt(selectedMonth));
        try {
            SimpleClient.getClient().sendToServer(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleSearchedReports(Message message) {
        Platform.runLater(() -> {
            Object responseObject = message.getObject();
            StringBuilder reportContent = new StringBuilder();

            String selectedBranch = choosed_branch.getValue();
            String selectedMonth = choosed_month.getValue();
            String selectedYear = choosed_year.getValue();

            reportContent.append("Report for:\n");
            reportContent.append("Branch: ").append(selectedBranch).append(", ");
            reportContent.append("Month: ").append(selectedMonth).append(", ");
            reportContent.append("Year: ").append(selectedYear).append("\n\n");

            complain_chart.getData().clear();
            purchases_chart.getData().clear();
            multientry_chart.getData().clear();

            XYChart.Series<String, Number> complaintsSeries = new XYChart.Series<>();
            complaintsSeries.setName("Number of Complaints");

            XYChart.Series<String, Number> purchasesSeries = new XYChart.Series<>();
            purchasesSeries.setName("purchases profit");

            XYChart.Series<String, Number> mutlientrySeries = new XYChart.Series<>();
            mutlientrySeries.setName("Number of mutli-entry tickets");

            if (responseObject instanceof Reports) {
                Reports report = (Reports) responseObject;
                List<Integer> dailyComplaints = parseReportData(report.getReport_complains(), Integer.parseInt(selectedMonth), Integer.parseInt(selectedYear));
                List<Integer> dailyPurchases = parseReportData(report.getReport_ticket_sells(), Integer.parseInt(selectedMonth), Integer.parseInt(selectedYear));
                List<Integer> dailyMultiEntry = parseReportData(report.getReport_multy_entry_ticket(), Integer.parseInt(selectedMonth), Integer.parseInt(selectedYear));

                for (int day = 1; day <= dailyComplaints.size(); day++) {
                    int numComplaints = dailyComplaints.get(day - 1);
                    int numPurchases = dailyPurchases.get(day - 1);
                    int numMultiEntry = dailyMultiEntry.get(day - 1);

                    reportContent.append("Day ").append(day).append(": ");
                    reportContent.append("Num of Complaints: ").append(numComplaints).append(", ");
                    reportContent.append("purchases profit: ").append(numPurchases).append(",");
                    reportContent.append("Num of Multi-Entry Tickets: ").append(numMultiEntry).append("\n");

                    complaintsSeries.getData().add(new XYChart.Data<>(String.valueOf(day), numComplaints));
                    purchasesSeries.getData().add(new XYChart.Data<>(String.valueOf(day), numPurchases));
                    mutlientrySeries.getData().add(new XYChart.Data<>(String.valueOf(day), numMultiEntry));
                }

                complain_chart.getData().add(complaintsSeries);
                purchases_chart.getData().add(purchasesSeries);
                multientry_chart.getData().add(mutlientrySeries);

                complain_chart.setVisible(true);
                multientry_chart.setVisible(true);
                purchases_chart.setVisible(true);

            } else if (responseObject instanceof String) {
                reportContent.append("No data found for this month").append("\n");
                int daysInMonth = getDaysInMonth(Integer.parseInt(selectedMonth), Integer.parseInt(selectedYear));
                for (int day = 1; day <= daysInMonth; day++) {
                    reportContent.append("Day ").append(day).append(": ");
                    reportContent.append("Num of Complaints: 0, ");
                    reportContent.append("purchases profit: 0\n");
                    reportContent.append("Num of Multi-Entry Tickets: 0\n");

                    complaintsSeries.getData().add(new XYChart.Data<>(String.valueOf(day), 0));
                    purchasesSeries.getData().add(new XYChart.Data<>(String.valueOf(day), 0));
                    mutlientrySeries.getData().add(new XYChart.Data<>(String.valueOf(day), 0));
                }

                complain_chart.getData().add(complaintsSeries);
                purchases_chart.getData().add(purchasesSeries);
                multientry_chart.getData().add(mutlientrySeries);

                complain_chart.setVisible(true);
                multientry_chart.setVisible(true);
                purchases_chart.setVisible(true);

            } else {
                reportContent.append("Unexpected response from server.");
            }

            report_text.setText(reportContent.toString());
        });
    }

    private List<Integer> parseReportData(Object data, int selectedMonth, int selectedYear) {
        List<Integer> list = new ArrayList<>();

        int daysInMonth = getDaysInMonth(selectedMonth, selectedYear);

        list = new ArrayList<>(Collections.nCopies(daysInMonth, 0));

        if (data instanceof List) {
            List<Integer> existingData = (List<Integer>) data;
            // Ensure the existing data fits the month; if not, truncate or extend as needed
            for (int i = 0; i < Math.min(existingData.size(), daysInMonth); i++) {
                list.set(i, existingData.get(i));
            }
            return list;
        }

        String s = (String) data;
        String[] lines = s.split("\n");

        for (String line : lines) {
            try {
                String[] parts = line.split("::");

                if (parts.length != 2 || parts[0].trim().isEmpty() || parts[1].trim().isEmpty()) {
                    System.err.println("Skipping malformed line: " + line);
                    continue;
                }

                int day = Integer.parseInt(parts[0].trim());
                int value = Integer.parseInt(parts[1].trim());

                if (day >= 1 && day <= daysInMonth) {
                    list.set(day - 1, value);
                }
            } catch (NumberFormatException e) {
                System.err.println("Error parsing line: " + line);
                e.printStackTrace();
            }
        }

        return list;
    }

    private int getDaysInMonth(int month, int year) {
        switch (month) {
            case 2: // February
                return (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) ? 29 : 28; // Leap year check
            case 4:
            case 6:
            case 9:
            case 11: // April, June, September, November
                return 30;
            default: // January, March, May, July, August, October, December
                return 31;
        }
    }

    @Subscribe
    public void implementations(BaseEventBox event) {
        if (event.getId() == BaseEventBox.get_event_id("REPORTS")) {
            Message message = event.getMessage();
            switch (message.getMessage()) {
                case "#reportsCreated":
                    handleCreateReports(message);
                    break;
                case "#searchedReports":
                    handleSearchedReports(message);
                    break;
                case "#reportsDeleted":
                    break;
                case "updatedReports":
                    break;
                default:
                    break;
            }

        }
    }

    @Subscribe
    public void change_content1(BeginContentChangeEnent event) {
        System.out.println(event.getPage());
        EventBus.getDefault().unregister(this);
        EventBus.getDefault().post(new ContentChangeEvent(event.getPage()));
    }
}
