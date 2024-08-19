package il.cshaifasweng.OCSFMediatorExample.client;
import il.cshaifasweng.OCSFMediatorExample.entities.Message;


public class BaseEventBox {
    public enum EventEnum {
        DELETE_PURCHASE,
        LOGIN,
        SHOW_PURCHASES,
        UPDATE_MOVIE_LIST,
        MOVIES_GOT,
        UPDATE_MOVIE_LIST_EACH,
        UPDATE_SCREENING_FOR_MOVIE_EACH,
        SERVER_ERROR_MESSAGE1,
        CHANGE_MOVIE_ID_BOX,
        UPDATE_BOXES_IN_SCREENING,
        UPDATE_SCREENING_FOR_MOVIE,
        SERVER_ERROR_MESSAGE,
        WRONG_NAMEING,
        GET_SCREENING_DONE,
        SAVE_MULTI_TICKET,
        SHOW_COMPLAINS,
        SHOW_COMPLAINS_AND_MESSAGE,
        SHOW_COMPLAINS_RESPOND,
        GOT_SEARCH_MOVIE_FILTER,
        SIGN_OUT,
        PURCHASE_LINK_USING_MULTI,
        PURCHASE_LINK_N,
        THEATER_MAP_UPDATED;

        public static EventEnum getByName(String name) {
            try {
                return EventEnum.valueOf(name);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("No enum constant with name " + name, e);
            }
        }

        private int value;
        private static int nextValue = 1;

        // Assign the automatic value using a static block
        static {
            for (EventEnum counter : EventEnum.values()) {
                counter.value = nextValue++;
            }
        }
        public int getValue() {
            return value;
        }

    }
    private Message message;
    private int id;
    public BaseEventBox(String id, Message message) {
        this.message = message;
        this.id = EventEnum.getByName(id).getValue();
    }
    public Message getMessage() {
        return message;
    }
    public static int get_event_id(String event){
        return EventEnum.getByName(event).getValue();
    }
    public int getId() {
        return id;
    }
}