package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Message;
import il.cshaifasweng.OCSFMediatorExample.entities.Movie;

import javafx.application.Platform;
import org.greenrobot.eventbus.EventBus;
import il.cshaifasweng.OCSFMediatorExample.client.ocsf.AbstractClient;
import java.io.IOException;

public class SimpleClient extends AbstractClient {
	public static Message Current_Message;
	public static SimpleClient client = null;

	public SimpleClient(String host, int port) {
		super(host, port);
	}

	@Override
	protected void handleMessageFromServer(Object msg) {
		Message message = (Message) msg;
		if(message.getMessage().equals("#GotAllMovies")){
			EventBus.getDefault().post(new BaseEventBox("MOVIES_GOT", message));
			/*Platform.runLater(() -> {
				SimpleChatClient.setWindowTitle("editing_details");
				try {
					SimpleChatClient.setRoot("Movie_editing_details");
				} catch (IOException e) {
					e.printStackTrace();
				}
			});*/
        }
		else if (message.getMessage().equals("#GoToHomePage")){
			//System.out.println("we are here");
			//System.out.println(BaseEventBox.get_event_id("MOVIES_GOT"));
			//BaseEventBox b = new BaseEventBox("MOVIES_GO", message);

			EventBus.getDefault().post(new BaseEventBox("MOVIES_GOT", message));

		}
		else if (message.getMessage().equals("#UpdateMovieList")){	
			EventBus.getDefault().post(new BaseEventBox("UPDATE_MOVIE_LIST", message));
		} else if (message.getMessage().equals("#ScreeningsGot")) {
			Current_Message = message;
			Platform.runLater(() -> {
				SimpleChatClient.setWindowTitle("edit_screenings");
				try {
					SimpleChatClient.setRoot("EditScreening");
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
		} else if (message.getMessage().equals("#UpdateScreeningForMovie")) {
			EventBus.getDefault().post(new BaseEventBox("UPDATE_SCREENING_FOR_MOVIE", message));
		} else if (message.getMessage().equals("#UpdateBoxesInScreening")) {

			EventBus.getDefault().post(new BaseEventBox("UPDATE_BOXES_IN_SCREENING", message));
		} else if (message.getMessage().equals("#ChangeMovieIdBox")) {
			System.out.println("I got your message");
			EventBus.getDefault().post(new BaseEventBox("CHANGE_MOVIE_ID_BOX", message));
		} else if (message.getMessage().equals("#UpdateMovieList_Eatch")) {
			EventBus.getDefault().post(new BaseEventBox("UPDATE_MOVIE_LIST_EACH", message));
		} else if (message.getMessage().equals("#UpdateScreeningForMovie_each")) {
			EventBus.getDefault().post(new BaseEventBox("UPDATE_SCREENING_FOR_MOVIE_EACH",message));
		} else if (message.getMessage().equals("#ServerError")) {
			EventBus.getDefault().post(new BaseEventBox("SERVER_ERROR_MESSAGE", message));
		}

		else if (message.getMessage().equals("#GotSearchMovieFillter"))
		{
			EventBus.getDefault().post(new BaseEventBox("GOT_SEARCH_MOVIE_FILTER", message));
		}

		else if(message.getMessage().equals("#show_purchases_client"))
		{
			EventBus.getDefault().post(new BaseEventBox("SHOW_PURCHASES", message));
		}
		else if(message.getMessage().equals("#delete_purchases_client")){
			EventBus.getDefault().post(new BaseEventBox("DELETE_PURCHASE", message));
		}

		else if(message.getMessage().equals("#loginWorkerFailedUserName") ||
				message.getMessage().equals("#loginWorker") ||
				message.getMessage().equals("#loginWorkerFailedPass")){
			EventBus.getDefault().post(new BaseEventBox("LOGIN", message));
		}
		else if (message.getMessage().equals("#show_complains_for_client")) {
			EventBus.getDefault().post(new BaseEventBox("SHOW_COMPLAINS", message));
		}
		else if(message.getMessage().equals("#show_respond_complains_for_client"))
		{
			EventBus.getDefault().post(new BaseEventBox("SHOW_COMPLAINS_RESPOND", message));
		}
		else if (message.getMessage().equals("#submit_respond_for_client")) {
			EventBus.getDefault().post(new BaseEventBox("SHOW_COMPLAINS_AND_MESSAGE", message));
		}
		else if (message.getMessage().equals("#userNotFound") ||
				message.getMessage().equals("#alreadyLoggedIn") ||
				message.getMessage().equals("#loginConfirmed") ||
				message.getMessage().equals("#serverError")) {
			// Handle login related messages
		EventBus.getDefault().post(new BaseEventBox("SERVER_ERROR_MESSAGE1", message));
		}

		else if(message.getMessage().equals("#purchase_multi_ticket_client")){
			EventBus.getDefault().post(new BaseEventBox("SAVE_MULTI_TICKET", message));
		}
		else if (message.getMessage().equals("#theater_map_updated")){
			EventBus.getDefault().post(new BaseEventBox("THEATER_MAP_UPDATED", message));
		}

		else {
			EventBus.getDefault().post(new BaseEventBox("WRONG_NAMEING", message));
		}
	}


	public static SimpleClient getClient() {
		if (client == null) {
			client = new SimpleClient("localhost", 3000);
		}
		return client;
	}
}
