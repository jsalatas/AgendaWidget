package gr.ictpro.jsalatas.agendawidget.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;
import gr.ictpro.jsalatas.agendawidget.R;

public class EmptyTextWatcher implements TextWatcher {
    private final TextView tvOK;

    public EmptyTextWatcher(TextView tvOK) {
        super();
        this.tvOK = tvOK;
    }

    @Override
    public void afterTextChanged(Editable s) {
        if(s.toString().isEmpty()) {
            tvOK.setClickable(false);
            tvOK.setTextColor(tvOK.getContext().getResources().getColor(R.color.colorGray));
        } else {
            tvOK.setClickable(true);
            tvOK.setTextColor(tvOK.getContext().getResources().getColor(R.color.colorPrimary));
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

}
