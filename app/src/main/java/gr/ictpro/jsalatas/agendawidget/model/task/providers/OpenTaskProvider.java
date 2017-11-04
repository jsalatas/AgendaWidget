package gr.ictpro.jsalatas.agendawidget.model.task.providers;

import android.support.annotation.NonNull;
import gr.ictpro.jsalatas.agendawidget.model.task.TaskContract;

public class OpenTaskProvider implements TaskContract {
    @Override
    public @NonNull
    String getProviderName() {
        return "Open Tasks";
    }

    @Override
    public @NonNull
    String getBaseURI() {
        return "content://org.dmfs.tasks";
    }

    @Override
    public @NonNull
    String getTasksURI() {
        return getBaseURI() + "/tasks";
    }

    @Override
    public @NonNull
    String getTaskListsURI() {
        return getBaseURI() + "/tasklists";
    }

    @Override
    public @NonNull
    String getListId() {
        return "_id";
    }

    @Override
    public @NonNull
    String getListName() {
        return "list_name";
    }

    @Override
    public @NonNull
    String getListAccountName() {
        return "account_name";
    }

    @Override
    public @NonNull
    String getListColor() {
        return "list_color";
    }

    @Override
    public @NonNull
    String getListSyncEnabled() {
        return "sync_enabled";
    }

    @Override
    public @NonNull
    String getItemId() {
        return "_id";
    }

    @Override
    public @NonNull
    String getItemTaskColor() {
        return "task_color";
    }

    @Override
    public @NonNull
    String getItemListId() {
        return "list_id";
    }

    @Override
    public @NonNull
    String getItemTitle() {
        return "title";
    }

    @Override
    public @NonNull
    String getItemLocation() {
        return "location";
    }

    @Override
    public @NonNull
    String getItemDescription() {
        return "description";
    }

    @Override
    public @NonNull
    String getItemTz() {
        return "tz";
    }

    @Override
    public @NonNull
    String getItemDtstart() {
        return "dtstart";
    }

    @Override
    public @NonNull
    String getItemIsAllday() {
        return "is_allday";
    }

    @Override
    public @NonNull
    String getItemDue() {
        return "due";
    }

    @Override
    public @NonNull
    String getItemPriority() {
        return "priority";
    }

    @Override
    public @NonNull
    String getItemCompleted() {
        return "completed";
    }
}