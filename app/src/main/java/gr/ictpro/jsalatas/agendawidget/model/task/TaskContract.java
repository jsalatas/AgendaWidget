package gr.ictpro.jsalatas.agendawidget.model.task;

import android.support.annotation.NonNull;

public interface TaskContract {
    @NonNull String getProviderName();

    @NonNull String getBaseURI();

    @NonNull String getTasksURI();

    @NonNull String getTaskListsURI();

    @NonNull String getListId();

    @NonNull String getListName();

    @NonNull String getListAccountName();

    @NonNull String getListColor();

    @NonNull String getListSyncEnabled();

    @NonNull String getItemId();

    @NonNull String getItemTaskColor();

    @NonNull String getItemListId();

    @NonNull String getItemTitle();

    @NonNull String getItemLocation();

    @NonNull String getItemDescription();

    @NonNull String getItemTz();

    @NonNull String getItemDtstart();

    @NonNull String getItemIsAllday();

    @NonNull String getItemDue();

    @NonNull String getItemPriority();

    @NonNull String getItemCompleted();

}
