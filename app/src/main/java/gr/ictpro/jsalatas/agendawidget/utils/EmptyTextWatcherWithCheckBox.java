package gr.ictpro.jsalatas.agendawidget.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.CheckBox;
import android.widget.TextView;
import gr.ictpro.jsalatas.agendawidget.R;

public class EmptyTextWatcherWithCheckBox extends EmptyTextWatcher {
    private final CheckBox checkbox;

    public EmptyTextWatcherWithCheckBox(CheckBox checkbox, TextView tvOK) {
        super(tvOK);
        this.checkbox = checkbox;
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (checkbox.isChecked()) {
            tvOK.setClickable(true);
            tvOK.setTextColor(tvOK.getContext().getResources().getColor(R.color.colorPrimary));
        } else {
            super.afterTextChanged(s);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

}
