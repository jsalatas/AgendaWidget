package gr.ictpro.jsalatas.agendawidget.ui;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import gr.ictpro.jsalatas.agendawidget.R;
import gr.ictpro.jsalatas.agendawidget.application.AgendaWidgetApplication;
import gr.ictpro.jsalatas.agendawidget.model.settings.*;
import gr.ictpro.jsalatas.agendawidget.model.settings.types.Setting;

public class DateFormatDialog extends Dialog {
    private String format;
    private Setting setting;
    private DateTimeFormatPickerCallback callback;

    private DateFormatDialog(Activity activity) {
        super(activity);
    }

    public DateFormatDialog(Activity activity, String format, Setting setting) {
        this(activity);
        this.format = format;
        this.setting = setting;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_datetime_format);

        ListView l = (ListView) findViewById(R.id.lst_date_format);

        DateTimeFormatListAdapter adapter = new DateTimeFormatListAdapter(getContext(), setting);
        l.setAdapter(adapter);
        int selectedPos = -1;
        for (int i = 0; i < adapter.getCount(); i++) {
            if (getDateTimeFormat(findViewById(android.R.id.content), adapter.getItem(i)).equals(format)) {
                selectedPos = i;
                break;
            }
        }
        if (selectedPos == -1) {
            // custom is selected
            selectedPos = adapter.getCount() - 1;
            findViewById(R.id.tvDateTimeFormatHelp).setVisibility(View.VISIBLE);
            EditText editCustom = (EditText) findViewById(R.id.edtCustomFormat);
            editCustom.setText(format);
            editCustom.setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.tvDateTimeFormatHelp).setVisibility(View.GONE);
            EditText editCustom = (EditText) findViewById(R.id.edtCustomFormat);
            editCustom.setVisibility(View.GONE);
            editCustom.setText("");
        }

        l.setItemChecked(selectedPos, true);


        TextView tv = (TextView) findViewById(R.id.tvDateTimeFormatOk);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListView l = (ListView) findViewById(R.id.lst_date_format);
                String selected = (String) l.getAdapter().getItem(l.getCheckedItemPosition());
                callback.onFormatChosen(getDateTimeFormat(DateFormatDialog.this.findViewById(android.R.id.content), selected));
            }
        });
    }

    public void setCallback(DateTimeFormatPickerCallback callback) {
        this.callback = callback;
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
}
