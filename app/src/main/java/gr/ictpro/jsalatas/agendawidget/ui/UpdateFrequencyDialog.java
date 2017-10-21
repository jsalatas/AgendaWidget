package gr.ictpro.jsalatas.agendawidget.ui;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;
import gr.ictpro.jsalatas.agendawidget.R;
import gr.ictpro.jsalatas.agendawidget.model.settings.types.Setting;
import gr.ictpro.jsalatas.agendawidget.ui.widgets.SettingDialog;

public class UpdateFrequencyDialog extends SettingDialog<Long> {
    public UpdateFrequencyDialog(Activity activity, Setting<Long> setting) {
        super(activity, setting, R.layout.dialog_update_frequency);

        // TODO: Fill setting
    }

    @Override
    protected Long getSetting() {
        EditText editUpdateFrequency = (EditText) findViewById(R.id.editUpdateFrequency);

        return null;
    }
}
