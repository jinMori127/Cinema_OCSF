package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Message;
import il.cshaifasweng.OCSFMediatorExample.entities.Screening;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.text.Text;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.util.ArrayList;

public class PurchaseMovieTicketsController {
    @FXML
    private Text ErrorMessage;

    @FXML
    void pay_amount(ActionEvent event) {
        Screening screening = TheaterMapController.screening;
        ArrayList<ArrayList<Integer>> places_took = TheaterMapController.places_took;
        int [][] map = TheaterMapController.cerate_map(screening.getTheater_map());
        for (ArrayList<Integer> list : places_took) {
            map[list.get(0)][list.get(1)] = 2;
        }
        screening.setTheater_map(TheaterMapController.create_string_of_map(map));
        Message m = new Message(10000, "#Update_theater_map");
        m.setObject(screening);
        try {
            SimpleClient.getClient().sendToServer(m);
        } catch (IOException e) {
            ErrorMessage.setVisible(true);
            ErrorMessage.setText(e.getMessage());
            return;
        }
    }
    @FXML
    public void initialize() {
    EventBus.getDefault().register(this);
    }

    @Subscribe
    public void change_content1(BeginContentChangeEnent event)
    {

        System.out.println(event.getPage());
        EventBus.getDefault().unregister(this);
        EventBus.getDefault().post(new ContentChangeEvent(event.getPage()));
    }

}
