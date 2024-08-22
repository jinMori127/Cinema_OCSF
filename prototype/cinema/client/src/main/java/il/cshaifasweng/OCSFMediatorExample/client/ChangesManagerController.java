package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.EditedDetails;
import il.cshaifasweng.OCSFMediatorExample.entities.Message;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.util.List;

public class ChangesManagerController {

    @FXML
    private Button approveAllButton;

    @FXML
    private VBox changesContainer;

    @FXML
    private void initialize() {
        System.out.println("ChangesManagerController.initialize");
        EventBus.getDefault().register(this); // Registering with EventBus

        // Create and send a message to request all changes from the server
        Message requestMessage = new Message(81, "#GetCMEditedDetails");
        try {
            SimpleClient.getClient().sendToServer(requestMessage);
            System.out.println("Request for edited details sent to server");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Subscribe
    public void handle_show_cmchanges_event(BaseEventBox event) {
        if(event.getId()==BaseEventBox.get_event_id("SHOW_CM_CHANGES")) {
            Platform.runLater(() -> {
                Message message = event.getMessage();
                List<EditedDetails> editedDetailsList = (List<EditedDetails>) message.getObject();

                // Clear any existing children in the changes container
                changesContainer.getChildren().clear();

                // Populate the UI with the received edited details
                for (EditedDetails details : editedDetailsList) {
                    addChange(details);
                }
            });
        }
    }

    private void addChange(EditedDetails change) {
        HBox changeBox = new HBox(10); // 10 is the spacing between elements

        // Create the label for the change details
        Label changeDetailsLabel = new Label("Movie: " + change.getMovie().getMovie_name() + ", New Price: " + change.getChanged_price());

        // Create the approve button
        Button approveButton = new Button("Approve");
        approveButton.setOnAction(event -> approvePriceChange(change));

        // Create the deny button
        Button denyButton = new Button("Deny");
        denyButton.setOnAction(event -> denyPriceChange(change));

        // Add the label and buttons to the HBox
        changeBox.getChildren().addAll(changeDetailsLabel, approveButton, denyButton);

        // Add the HBox to the changes container
        changesContainer.getChildren().add(changeBox);
    }

    private void approvePriceChange(EditedDetails change) {
        change.getMovie().setPrice((int)change.getChanged_price());

        // Create a message to send the updated movie to the server
        Message update_message = new Message(82, "#UpdateMoviePrice");
        update_message.setObject(change);

        try {
            SimpleClient.getClient().sendToServer(update_message);
            //System.out.println("Approved: " + change.getMovie().getMovie_name() + ", New Price: " + change.getChanged_price());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void denyPriceChange(EditedDetails change) {
        System.out.println("DenyPriceChange");
        Message deny_message = new Message(83, "#DenyMoviePrice");
        deny_message.setObject(change);

        try {
            SimpleClient.getClient().sendToServer(deny_message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Subscribe
    public void change_content1(BeginContentChangeEnent event)
    {

        System.out.println(event.getPage());
        EventBus.getDefault().unregister(this);
        EventBus.getDefault().post(new ContentChangeEvent(event.getPage()));
    }
}