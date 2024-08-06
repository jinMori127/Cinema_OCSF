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
			EventBus.getDefault().post(new BaseEventBox(5, message));
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
			EventBus.getDefault().post(new BaseEventBox(5, message));

		}
		else if (message.getMessage().equals("#UpdateMovieList")){	
			EventBus.getDefault().post(new BaseEventBox(4, message));
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
			EventBus.getDefault().post(new BaseEventBox(11, message));
		} else if (message.getMessage().equals("#UpdateBoxesInScreening")) {
			EventBus.getDefault().post(new BaseEventBox(10, message));
		} else if (message.getMessage().equals("#ChangeMovieIdBox")) {
			System.out.println("I got your message");
			EventBus.getDefault().post(new BaseEventBox(9, message));
		} else if (message.getMessage().equals("#UpdateMovieList_Eatch")) {
			EventBus.getDefault().post(new BaseEventBox(6, message));
		} else if (message.getMessage().equals("#UpdateScreeningForMovie_each")) {
			EventBus.getDefault().post(new BaseEventBox(7,message));
		} else if (message.getMessage().equals("#ServerError")) {
			EventBus.getDefault().post(new BaseEventBox(12, message));
		}

		else if(message.getMessage().equals("#show_purchases_client"))
		{
			EventBus.getDefault().post(new BaseEventBox(3, message));
		}
		else if(message.getMessage().equals("#delete_purchases_client")){
			EventBus.getDefault().post(new BaseEventBox(1, message));
		}

		else if(message.getMessage().equals("#loginWorkerFailedUserName") ||
				message.getMessage().equals("#loginWorker") ||
				message.getMessage().equals("#loginWorkerFailedPass")){
			EventBus.getDefault().post(new BaseEventBox(2, message));
		}

		else if (message.getMessage().equals("#userNotFound") ||
				message.getMessage().equals("#alreadyLoggedIn") ||
				message.getMessage().equals("#loginConfirmed") ||
				message.getMessage().equals("#serverError")) {
			// Handle login related messages
			EventBus.getDefault().post(new BaseEventBox(8, message));
		}

		else if(message.getMessage().equals("#purchase_multi_ticket_client")){
			System.out.println("get to Client");
			EventBus.getDefault().post(new BaseEventBox(75, message));
		}

		else {
			EventBus.getDefault().post(new BaseEventBox(300, message));
		}
	}


	public static SimpleClient getClient() {
		if (client == null) {
			client = new SimpleClient("localhost", 3000);
		}
		return client;
	}
}
