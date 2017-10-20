package gr.ictpro.jsalatas.agendawidget.ui.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import gr.ictpro.jsalatas.agendawidget.R;

/*
 * This class is useful for using inside of ListView that needs to have checkable items.
 */
public class CheckableLinearLayout extends LinearLayout implements Checkable {
    private CompoundButton compoundButton;

    public CheckableLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        compoundButton = (CompoundButton) findViewById(R.id.compound_button);
    }

    @Override
    public boolean isChecked() {
        return compoundButton != null && compoundButton.isChecked();
    }

    @Override
    public void setChecked(boolean checked) {
        if (compoundButton != null) {
            compoundButton.setChecked(checked);
        }
    }

    @Override
    public void toggle() {
        if (compoundButton != null) {
            compoundButton.toggle();
        }
    }
}