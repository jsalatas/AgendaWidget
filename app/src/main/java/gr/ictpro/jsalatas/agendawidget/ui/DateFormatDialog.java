package gr.ictpro.jsalatas.agendawidget.ui;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import gr.ictpro.jsalatas.agendawidget.R;

public class DateFormatDialog extends Dialog {

    public DateFormatDialog(Activity activity) {
        super(activity);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_datetime_format);
    }
}
