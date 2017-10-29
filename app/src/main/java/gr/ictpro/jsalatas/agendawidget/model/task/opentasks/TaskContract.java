package gr.ictpro.jsalatas.agendawidget.model.task.opentasks;

public class TaskContract {
    public static final String BASE_URI = "content://org.dmfs.tasks";

    public interface TaskList {
        String CONTENT_URI = "/tasklists";

    }
    public interface TaskListColumns {
        String _ID = "_id";
        String LIST_NAME = "list_name";
        String ACCOUNT_NAME = "account_name";
        String LIST_COLOR = "list_color";
        String ACCESS_LEVEL = "list_access_level";
        String VISIBLE = "visible";
        String SYNC_ENABLED = "sync_enabled";
        String OWNER = "list_owner";
    }

}
