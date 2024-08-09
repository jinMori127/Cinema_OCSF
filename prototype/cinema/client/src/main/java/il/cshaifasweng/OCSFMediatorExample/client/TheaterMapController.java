package il.cshaifasweng.OCSFMediatorExample.client;

import antlr.debug.MessageEvent;
import com.mysql.cj.xdevapi.Client;
import il.cshaifasweng.OCSFMediatorExample.entities.Message;
import il.cshaifasweng.OCSFMediatorExample.entities.Movie;
import il.cshaifasweng.OCSFMediatorExample.entities.Screening;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class TheaterMapController {

    @FXML
    private AnchorPane anchore_pane;

    @FXML
    private VBox buttons_vbox;

    @FXML
    private Button buy_button;

    @FXML
    private Text ErrorMessage;

    public static int screening_id = 10;
    public static Screening screening = null;
    public static ArrayList<ArrayList<Integer>> places_took = new ArrayList<>();

    @FXML
    public void initialize() {
        EventBus.getDefault().register(this);
        ErrorMessage.setVisible(false);
        Message m = new Message(1000, "#get_screening_from_id");
        m.setObject(screening_id);
        try {
            System.out.println(m.getMessage());
            SimpleClient.getClient().sendToServer(m);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @FXML
    void buy_action(ActionEvent event) {
        if(places_took.isEmpty()) {
            ErrorMessage.setVisible(true);
            ErrorMessage.setText("You have not choosen any seats");
            return;
        }
        EventBus.getDefault().post(new BeginContentChangeEnent("PurchaseMovieTickets"));

    }


    @Subscribe
    public void handle(BaseEventBox event) {
        if (event.getId() == BaseEventBox.get_event_id("UPDATE_BOXES_IN_SCREENING")) {

            if(event.getMessage().getObject() == null) {

                Platform.runLater(() -> {

                    try {
                        SimpleChatClient.setRoot("HomePage");
                    } catch (IOException e) {
                        ErrorMessage.setVisible(true);
                        ErrorMessage.setText(e.getMessage());
                        return;
                    }


                });
            }
            else
            {Platform.runLater(()->{
                screening = (Screening) event.getMessage().getObject();
                create_the_page();
            });

            }
        }
        else if (event.getId() == BaseEventBox.get_event_id("THEATER_MAP_UPDATED")) {

            if (screening.getAuto_number_screening() == screening_id) {
                Platform.runLater(() -> {
                    screening = (Screening) event.getMessage().getObject();

                    create_the_page();

                });
            }
        } else if (event.getId() == BaseEventBox.get_event_id("UPDATE_SCREENING_FOR_MOVIE")) {
            if (screening.getMovie().getAuto_number_movie() == ((Movie)event.getMessage().getObject()).getAuto_number_movie()) {
                Message m = new Message(1000, "#get_screening_from_id");
                m.setObject(screening_id);
                try {
                    System.out.println(m.getMessage());
                    SimpleClient.getClient().sendToServer(m);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static int[][] cerate_map(String s)
    {
        String[] tokens = s.split("\n");
        int[][] map = new int[tokens.length][tokens[0].split(",").length];
        for (int i = 0; i < tokens.length; i++)
        {
            String[] places = tokens[i].split(",");
            for(int j=0; j<places.length; j++)
            {
                //System.out.println(i+"::"+j);
                map [i][j] = Integer.parseInt(places[j].trim());
            }
        }
        return map;
    }
    public static String create_string_of_map(int[][] map)
    {
        String s = "";
        for(int i=0; i< map.length; i++)
        {
            for(int j=0; j<map[i].length; j++)
            {
                if(j == map[i].length-1)
                {
                    s += map[i][j];
                }
                else {
                    s += map[i][j] + " ,";
                }
            }
            if(i != map.length-1)
            {
                s += "\n";
            }
        }
        return s;
    }
    private void create_the_page()
    {
        System.out.println(anchore_pane.getWidth());
        buttons_vbox.getChildren().clear();
        String theater_map = screening.getTheater_map();
        //System.out.println(theater_map);
        int[][] map = cerate_map(theater_map);
        for (int row = 0; row < map.length; row++) {
            HBox current_hobx = new HBox();
            for (int col = 0; col < map[row].length; col++) {
                Button button = new Button();
                int finalRow = row;
                int finalCol = col;
                button.setOnAction(event->{
                    if (map[finalRow][finalCol] == 1) {
                        for(ArrayList<Integer>list:places_took )
                        {
                            if (list.get(0) == finalRow && list.get(1) == finalCol)
                            {
                                places_took.remove(list);
                                break;
                            }
                        }
                        map[finalRow][finalCol] = 0;
                    }
                    else {
                        map[finalRow][finalCol] = 1;
                        ArrayList<Integer> l1 = new ArrayList<Integer>();
                        l1.add(finalRow);
                        l1.add(finalCol);
                        places_took.add(l1);
                    }
                    screening.setTheater_map(create_string_of_map(map));
                    Message m = new Message(10000, "#Update_theater_map");
                    m.setObject(screening);
                    try {
                        SimpleClient.getClient().sendToServer(m);
                    } catch (IOException e) {
                        ErrorMessage.setVisible(true);
                        ErrorMessage.setText(e.getMessage());
                    }
                });
                if (map[row][col] == 0) {
                    button.setStyle("-fx-background-color: green;");
                    button.setDisable(false);
                }
                else if (map[row][col] == 1) {
                    button.setStyle("-fx-background-color: yellow;");
                    button.setDisable(true);
                    for (ArrayList<Integer> list : places_took) {

                        if (list.get(0) == row && list.get(1) == col) {
                            button.setDisable(false);
                            break;
                        }
                    }
                }
                else {
                    button.setStyle("-fx-background-color: red;");
                    button.setDisable(true);
                }
                button.setText(row +"::"+col);
                if (map[row][col] == -1) {
                    button.setVisible(false);
                }
                current_hobx.getChildren().add(button);
            }
            buttons_vbox.getChildren().add(current_hobx);
        }

        Platform.runLater(()->{
            buttons_vbox.layout();
            anchore_pane.layout();
            buy_button.setLayoutY(buttons_vbox.getLayoutY()+buttons_vbox.getHeight()+10);
            buy_button.setLayoutX(anchore_pane.getLayoutX()+anchore_pane.getWidth()/2);
            buy_button.layout();
            anchore_pane.layout();
        });


    }
    @Subscribe
    public void change_content1(BeginContentChangeEnent event)
    {
        if (!event.getPage().equals("PurchaseMovieTickets"))
        {
            if(screening !=null) {
                int[][] map = cerate_map(screening.getTheater_map());
                for (ArrayList<Integer> list : places_took) {
                    map[list.get(0)][list.get(1)] = 0;
                }
                screening.setTheater_map(create_string_of_map(map));
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
        }
        System.out.println(event.getPage());
        EventBus.getDefault().unregister(this);
        EventBus.getDefault().post(new ContentChangeEvent(event.getPage()));
    }

}
