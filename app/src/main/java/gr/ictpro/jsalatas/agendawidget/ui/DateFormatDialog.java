package gr.ictpro.jsalatas.agendawidget.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import gr.ictpro.jsalatas.agendawidget.R;
import gr.ictpro.jsalatas.agendawidget.application.AgendaWidgetApplication;
import gr.ictpro.jsalatas.agendawidget.model.settings.*;
import gr.ictpro.jsalatas.agendawidget.model.settings.types.Setting;
import gr.ictpro.jsalatas.agendawidget.ui.widgets.SettingDialog;

public class DateFormatDialog extends SettingDialog<String> {

    public DateFormatDialog(Activity activity, Setting<String> setting) {
        super(activity, setting, R.layout.dialog_datetime_format);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ListView l = (ListView) findViewById(R.id.lst_date_format);

        DateTimeFormatListAdapter adapter = new DateTimeFormatListAdapter(getContext(), setting);
        l.setAdapter(adapter);
        int selectedPos = -1;
        for (int i = 0; i < adapter.getCount(); i++) {
            if (getDateTimeFormat(findViewById(android.R.id.content), adapter.getItem(i)).equals(setting.getValue())) {
                selectedPos = i;
                break;
            }
        }
        if (selectedPos == -1) {
            // custom is selected
            selectedPos = adapter.getCount() - 1;
            findViewById(R.id.tvDateTimeFormatHelp).setVisibility(View.VISIBLE);
            EditText editCustom = (EditText) findViewById(R.id.edtCustomFormat);
            editCustom.setText(setting.getValue());
            editCustom.setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.tvDateTimeFormatHelp).setVisibility(View.GONE);
            EditText editCustom = (EditText) findViewById(R.id.edtCustomFormat);
            editCustom.setVisibility(View.GONE);
            editCustom.setText("");
        }

        l.setItemChecked(selectedPos, true);
    }

    public static String getDateTimeFormat(View dialog, String item) {
        if (item.equals(AgendaWidgetApplication.getContext().getString(R.string.full_format))) {
            return "FULL";
        } else if (item.equals(AgendaWidgetApplication.getContext().getString(R.string.long_format))) {
            return "LONG";
        } else if (item.equals(AgendaWidgetApplication.getContext().getString(R.string.medium_format))) {
            return "MEDIUM";
        } else if (item.equals(AgendaWidgetApplication.getContext().getString(R.string.short_format))) {
            return "SHORT";
        } else {
            EditText tvCustom = (EditText) dialog.findViewById(R.id.edtCustomFormat);
            return tvCustom.getText().toString();
        }
    }

    @Override
    protected String getSetting() {
        ListView l = (ListView) findViewById(R.id.lst_date_format);
        if(l.getCheckedItemPosition() == l.getAdapter().getCount()-1 ) {
            // custom is selected, return editor's value
            EditText editCustom = (EditText) findViewById(R.id.edtCustomFormat);
            return editCustom.getText().toString();
        }
        return getDateTimeFormat(findViewById(android.R.id.content), (String)l.getAdapter().getItem(l.getCheckedItemPosition()));
    }
}
