package gr.ictpro.jsalatas.agendawidget.model.settings.types;

import android.app.Activity;
import android.app.WallpaperManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.view.View;
import android.widget.AdapterView;
import com.pes.androidmaterialcolorpickerdialog.ColorPicker;
import com.pes.androidmaterialcolorpickerdialog.ColorPickerCallback;
import gr.ictpro.jsalatas.agendawidget.R;
import gr.ictpro.jsalatas.agendawidget.model.settings.Setting;

public class SettingColor extends SettingInteger {
    @Override
    protected Integer getValue() {
        return Color.parseColor(getStringValue());
    }

    @Override
    protected void setValue(Integer value) {
        setStringValue(String.format("#%08X", value));
    }

    @Override
    public void onClick(final AdapterView<?> parent, View view) {

        int colorValue = getValue();
        final ColorPicker cp = new ColorPicker((Activity) view.getContext(), Color.red(colorValue), Color.green(colorValue), Color.blue(colorValue));

        cp.setCallback(new ColorPickerCallback() {
            @Override
            public void onColorChosen(@ColorInt int color) {
                setValue(color);
                cp.cancel();
            }
        });

        cp.show();
        cp.setButtonColor(view.getContext().getResources().getColor(R.color.colorPrimary));
    }

}
