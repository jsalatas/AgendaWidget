package gr.ictpro.jsalatas.agendawidget.model.settings.types;

import android.app.Activity;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.view.View;
import android.widget.AdapterView;
import com.pes.androidmaterialcolorpickerdialog.ColorPicker;
import com.pes.androidmaterialcolorpickerdialog.ColorPickerCallback;
import gr.ictpro.jsalatas.agendawidget.R;
import gr.ictpro.jsalatas.agendawidget.ui.NumberOfLinesDialog;
import gr.ictpro.jsalatas.agendawidget.ui.widgets.SettingDialog;

public class SettingNumberOfLines extends SettingInteger {
    @Override
    protected SettingDialog<Integer> getDialog(View view) {
        return new NumberOfLinesDialog((Activity) view.getContext(), this);
    }
}
