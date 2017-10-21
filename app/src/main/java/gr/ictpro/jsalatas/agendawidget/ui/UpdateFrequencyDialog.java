package gr.ictpro.jsalatas.agendawidget.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;
import gr.ictpro.jsalatas.agendawidget.R;
import gr.ictpro.jsalatas.agendawidget.application.AgendaWidgetApplication;
import gr.ictpro.jsalatas.agendawidget.model.settings.types.Setting;
import gr.ictpro.jsalatas.agendawidget.ui.widgets.SettingDialog;

public class UpdateFrequencyDialog extends SettingDialog<Long> {
    private static final long MINUTES = 1000 * 60;
    private static final long HOURS = 1000 * 60 * 60;
    private static final long DAYS = 1000 * 60 * 60 * 24;

    public UpdateFrequencyDialog(Activity activity, Setting<Long> setting) {
        super(activity, setting, R.layout.dialog_update_frequency);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EditText editUpdateFrequency = (EditText) findViewById(R.id.editUpdateFrequency);
        editUpdateFrequency.setFilters(new InputFilter[]{ new InputFilterMinMax("1", "99")});

        final TextView tvOK = (TextView) findViewById(R.id.tvDialogOk);

        editUpdateFrequency.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().isEmpty()) {
                    tvOK.setClickable(false);
                    tvOK.setTextColor(getContext().getResources().getColor(R.color.colorGray));
                } else {
                    tvOK.setClickable(true);
                    tvOK.setTextColor(getContext().getResources().getColor(R.color.colorPrimary));
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        AppCompatSpinner spinner = (AppCompatSpinner) findViewById(R.id.spinnerTimeUnit);
        UpdateFrequency uf = getUpdateFrequency(setting.getValue());
        editUpdateFrequency.setText(String.valueOf(uf.getValue()));
        spinner.setSelection(uf.getTimeUnitIndex());
    }

    @Override
    protected Long getSetting() {
        EditText editUpdateFrequency = (EditText) findViewById(R.id.editUpdateFrequency);
        long value = Long.parseLong(editUpdateFrequency.getText().toString());
        AppCompatSpinner spinner = (AppCompatSpinner) findViewById(R.id.spinnerTimeUnit);

        return value * getBase(spinner.getSelectedItem().toString());
    }

    private static int indexOf(String[] array, String search) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(search)) {
                return i;
            }
        }
        return -1;
    }
    private long getBase(String item) {
        String[] stringArray = getContext().getResources().getStringArray(R.array.update_frequency);
        int index = indexOf(stringArray, item);
        switch (index) {
            case 0:
                return MINUTES;
            case 1:
                return HOURS;
            case 2:
                return DAYS;
        }
        return 0;
    }

    public static UpdateFrequency getUpdateFrequency(long value) {
        String timeUnit = null;
        int timeUnitIndex = 0;
        long v = 0;
        String[] stringArray = AgendaWidgetApplication.getContext().getResources().getStringArray(R.array.update_frequency);
        if((value % DAYS) == 0) {
            v = value / DAYS;
            timeUnit = stringArray[2];
            timeUnitIndex = 2;
        } else if((value % HOURS) == 0) {
            v = value / HOURS;
            timeUnit = stringArray[1];
            timeUnitIndex = 1;
        } else if((value % MINUTES) == 0) {
            v = value / MINUTES;
            timeUnit = stringArray[0];
            timeUnitIndex = 0;
        }

        return new UpdateFrequency(timeUnit, v, timeUnitIndex);

    }

    private class InputFilterMinMax implements InputFilter {

        private final int min, max;

        InputFilterMinMax(String min, String max) {
            this.min = Integer.parseInt(min);
            this.max = Integer.parseInt(max);
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            try {
                int input = Integer.parseInt(dest.toString() + source.toString());
                if (isInRange(min, max, input))
                    return null;
            } catch (NumberFormatException nfe) {
                // do nothing
            }
            return "";
        }

        private boolean isInRange(int a, int b, int c) {
            return b > a ? c >= a && c <= b : c >= b && c <= a;
        }
    }

    public static class UpdateFrequency {
        private final String timeUnit;
        private final int timeUnitIndex;
        private final long value;

        UpdateFrequency(String timeUnit, long value, int timeUnitIndex) {
            this.timeUnit = timeUnit;
            this.timeUnitIndex = timeUnitIndex;
            this.value = value;
        }

        public String getTimeUnit() {
            return timeUnit;
        }

        public long getValue() {
            return value;
        }

        int getTimeUnitIndex() {
            return timeUnitIndex;
        }
    }

}
