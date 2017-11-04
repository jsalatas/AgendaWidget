package gr.ictpro.jsalatas.agendawidget.model.settings.types;

import android.app.Activity;
import android.view.View;
import gr.ictpro.jsalatas.agendawidget.ui.TaskProviderDialog;

public class SettingTaskProvider extends SettingString {
    @Override
    protected TaskProviderDialog getDialog(View view) {
        return new TaskProviderDialog((Activity) view.getContext(), this);
    }

    @Override
    boolean shouldRefreshList() {
        return true;
    }
}
