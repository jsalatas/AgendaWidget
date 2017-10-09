package gr.ictpro.jsalatas.agendawidget.service;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

public class AgendaWidgetService extends RemoteViewsService {
    public AgendaWidgetService() {
    }

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new AgendaWidgetRemoteViewsFactory(this.getApplicationContext(), intent);
    }

}

class AgendaWidgetRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private Context appContext;
    private int agendaWidgetId;

    AgendaWidgetRemoteViewsFactory(Context context, Intent intent) {
        appContext = context;
        agendaWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        return null;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 0;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}
