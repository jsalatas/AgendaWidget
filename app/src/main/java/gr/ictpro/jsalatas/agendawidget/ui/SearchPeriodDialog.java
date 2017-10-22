package gr.ictpro.jsalatas.agendawidget.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.AppCompatSpinner;
import android.text.InputFilter;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import gr.ictpro.jsalatas.agendawidget.R;
import gr.ictpro.jsalatas.agendawidget.model.settings.types.Setting;
import gr.ictpro.jsalatas.agendawidget.ui.widgets.SettingDialog;
import gr.ictpro.jsalatas.agendawidget.utils.*;

public class SearchPeriodDialog extends SettingDialog<Long> implements Dialog.OnShowListener {
    private final TimePeriodUtils tpu;

    public enum SearchPeriod implements TimePeriodEnumInterface {
        DAYS (0, 1000L * 60 * 60 * 24),
        MONTHS (1, 1000L * 60 * 60 * 24 * 31),
        YEARS (2, 1000L * 60 * 60 * 24 * 365);

        private final int ord;
        private final long interval;

        SearchPeriod (int ord, long interval) {
            this.interval = interval;
            this.ord = ord;
        }

        @Override
        public long interval() {
            return interval;
        }

        @Override
        public int ord() {
            return ord;
        }

        @Override
        public TimePeriodEnumInterface getValue(int ordinal) {
            return SearchPeriod.values()[ordinal];
        }
    }

    public SearchPeriodDialog(Activity activity, Setting<Long> setting) {
        super(activity, setting, R.layout.dialog_time_period);

        setOnShowListener(this);

        String[] values = getContext().getResources().getStringArray(R.array.search_period);
        this.tpu = new TimePeriodUtils(values, SearchPeriod.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView tvTimePeriodlabel = (TextView) findViewById(R.id.tvTimePeriodlabel);
        tvTimePeriodlabel.setText(getContext().getString(R.string.next));

        EditText editUnitValue = (EditText) findViewById(R.id.editTimePeriodUnit);
        editUnitValue.setFilters(new InputFilter[]{ new InputFilterMinMax("1", "99")});

        TextView tvOK = (TextView) findViewById(R.id.tvDialogOk);
        editUnitValue.addTextChangedListener(new EmptyTextWatcher(tvOK));

        AppCompatSpinner spinner = (AppCompatSpinner) findViewById(R.id.spinnerTimeUnit);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.search_period, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        TimePeriod tp = tpu.getTimePeriod(setting.getValue());
        editUnitValue.setText(String.valueOf(tp.getValue()));
        spinner.setSelection(tp.getTimeUnitIndex());
    }

    @Override
    public void onShow(DialogInterface dialog) {
        EditText editUnitValue = (EditText) findViewById(R.id.editTimePeriodUnit);
        Ime.show(editUnitValue);
    }

    @Override
    protected Long getSetting() {
        EditText editUnitValue = (EditText) findViewById(R.id.editTimePeriodUnit);
        long value = Long.parseLong(editUnitValue.getText().toString());
        AppCompatSpinner spinner = (AppCompatSpinner) findViewById(R.id.spinnerTimeUnit);

        return value * tpu.getBase(spinner.getSelectedItem().toString());
    }

}
