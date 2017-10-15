package gr.ictpro.jsalatas.agendawidget.ui.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import gr.ictpro.jsalatas.agendawidget.R;

/*
 * This class is useful for using inside of ListView that needs to have checkable items.
 */
public class CheckableLinearLayout extends LinearLayout implements Checkable {
    private RadioButton radioButton;

    public CheckableLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        radioButton = (RadioButton) findViewById(R.id.radio_button);
    }

    @Override
    public boolean isChecked() {
        return radioButton != null && radioButton.isChecked();
    }

    @Override
    public void setChecked(boolean checked) {
        if (radioButton != null) {
            radioButton.setChecked(checked);
        }
    }

    @Override
    public void toggle() {
        if (radioButton != null) {
            radioButton.toggle();
        }
    }
}