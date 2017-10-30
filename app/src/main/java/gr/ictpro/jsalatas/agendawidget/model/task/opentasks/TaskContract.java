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
        String SYNC_ENABLED = "sync_enabled";
    }

    public interface Tasks {
        String CONTENT_URI = "/tasks";
    }
    public interface TaskColumns {

        String _ID = "_id";
        String TASK_COLOR = "task_color";
        String LIST_ID = "list_id";
        String TITLE = "title";
        String LOCATION = "location";
        String DESCRIPTION = "description";
        String TZ = "tz";
        String DTSTART = "dtstart";
        String IS_ALLDAY = "is_allday";
        String DUE = "due";
        String PRIORITY = "priority";
        String COMPLETED = "completed";
    }
}