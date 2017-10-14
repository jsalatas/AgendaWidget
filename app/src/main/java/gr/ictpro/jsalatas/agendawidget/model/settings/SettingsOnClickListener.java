package gr.ictpro.jsalatas.agendawidget.model.settings;

import android.app.Activity;
import android.app.WallpaperManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import com.pes.androidmaterialcolorpickerdialog.ColorPicker;
import com.pes.androidmaterialcolorpickerdialog.ColorPickerCallback;
import gr.ictpro.jsalatas.agendawidget.R;

public class SettingsOnClickListener implements AdapterView.OnItemClickListener {
    private Settings settings;

    public SettingsOnClickListener(Settings settings) {
        this.settings = settings;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ListItemSetting item = (ListItemSetting) parent.getItemAtPosition(position);
        final Setting setting = item.getSetting();
        if (setting.getType() == SettingType.BOOL) {
            Boolean newValue = !Boolean.parseBoolean(setting.getValue());
            setting.setValue(newValue.toString());
            SwitchCompat s = (SwitchCompat) view.findViewById(R.id.swcValue);
            s.setChecked(newValue);
            if(setting.getName().equals("dropShadow") && newValue) {
                Setting bgColor = settings.getSetting("backgroundColor");
                int colorValue = Color.parseColor(bgColor.getValue());
                int newColor = Color.rgb(Color.red(colorValue), Color.green(colorValue), Color.blue(colorValue));
                bgColor.setValue("#" + Integer.toHexString(newColor));
            }
        } else if (setting.getType() == SettingType.TRANSPARENT_COLOR) {
            int colorValue = Color.parseColor(setting.getValue());
            final ColorPicker cp;
            if(setting.getName().equals("backgroundColor") && Boolean.parseBoolean(settings.getSetting("dropShadow").getValue())) {
                cp = new ColorPicker((Activity) view.getContext(), Color.red(colorValue), Color.green(colorValue), Color.blue(colorValue));
            } else {
                cp = new ColorPicker((Activity) view.getContext(), Color.alpha(colorValue), Color.red(colorValue), Color.green(colorValue), Color.blue(colorValue));
            }

            cp.setCallback(new ColorPickerCallback() {
                @Override
                public void onColorChosen(@ColorInt int color) {
                    setting.setValue("#" + Integer.toHexString(color));
                    cp.cancel();
                }
            });

            cp.show();
        } else if (setting.getType() == SettingType.COLOR) {
            int colorValue = Color.parseColor(setting.getValue());

            final ColorPicker cp = new ColorPicker((Activity) view.getContext(), Color.red(colorValue), Color.green(colorValue), Color.blue(colorValue));
            cp.setCallback(new ColorPickerCallback() {
                @Override
                public void onColorChosen(@ColorInt int color) {
                    setting.setValue("#" + Integer.toHexString(color));
                    cp.cancel();
                }
            });

            cp.show();
        }

    }
}
