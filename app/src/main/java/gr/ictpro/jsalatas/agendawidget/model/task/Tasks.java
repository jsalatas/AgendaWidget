package gr.ictpro.jsalatas.agendawidget.model.task;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import gr.ictpro.jsalatas.agendawidget.application.AgendaWidgetApplication;
import gr.ictpro.jsalatas.agendawidget.model.calendar.Calendar;
import gr.ictpro.jsalatas.agendawidget.model.task.opentasks.TaskContract;

import java.util.ArrayList;
import java.util.List;

public class Tasks {
    public final static String READ_TASKS_PERMISSION = "org.dmfs.permission.READ_TASKS";
    private static List<Task> taskList;

    public static void refreshTaskList() {
        if (!checkPermissions()) {
            Log.d("Tasks", ">>>>> permission denied");
            return;
        }
        Log.d("Tasks", ">>>>> no permission granted");
        final String[] TASK_PROJECTION = new String[]{
                TaskContract.TaskListColumns._ID,
                TaskContract.TaskListColumns.ACCOUNT_NAME,
                TaskContract.TaskListColumns.LIST_NAME,
                TaskContract.TaskListColumns.LIST_COLOR,
                TaskContract.TaskListColumns.SYNC_ENABLED
        };

        final ContentResolver cr = AgendaWidgetApplication.getContext().getContentResolver();
        final Uri uri = Uri.parse(TaskContract.BASE_URI + TaskContract.TaskList.CONTENT_URI);
        Cursor cur = cr.query(uri, TASK_PROJECTION, null, null, null);
        final List<Task> result = new ArrayList<>();

        while (cur.moveToNext()) {
            Long id = cur.getLong(0);
            String accountName = cur.getString(1);
            String name = cur.getString(2);
            int color = cur.getInt(3);

            boolean syncEnabled = cur.getInt(4) == 1;
            if(syncEnabled) {
                Task t = new Task(id, accountName, name, color);
                result.add(t);
            }
        }
        cur.close();
        taskList = result;
    }

    private static boolean checkPermissions() {
        int permissionCheck = ContextCompat.checkSelfPermission(AgendaWidgetApplication.getContext(), READ_TASKS_PERMISSION);
        return permissionCheck != PackageManager.PERMISSION_DENIED;
    }

    public static List<Task> getTaskList() {
        return taskList;
    }


}
