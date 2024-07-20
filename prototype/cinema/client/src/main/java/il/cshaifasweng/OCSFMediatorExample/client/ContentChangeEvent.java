package il.cshaifasweng.OCSFMediatorExample.client;

public class ContentChangeEvent {
    private String page;
    public ContentChangeEvent(String page) {
        this.page = page;
    }
    public String getPage() {
        return page;
    }
}
