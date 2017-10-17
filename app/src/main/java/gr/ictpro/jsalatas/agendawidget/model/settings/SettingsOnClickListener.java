package gr.ictpro.jsalatas.agendawidget.model.settings;

import android.app.Activity;
import android.app.WallpaperManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import com.pes.androidmaterialcolorpickerdialog.ColorPicker;
import com.pes.androidmaterialcolorpickerdialog.ColorPickerCallback;
import gr.ictpro.jsalatas.agendawidget.R;
import gr.ictpro.jsalatas.agendawidget.ui.CalendarSelectionActivity;
import gr.ictpro.jsalatas.agendawidget.ui.DateFormatDialog;

import java.util.Calendar;
import java.util.Date;

public class SettingsOnClickListener implements AdapterView.OnItemClickListener {
    private Settings settings;

    public SettingsOnClickListener(Settings settings) {
        this.settings = settings;
    }

    @Override
    public void onItemClick(final AdapterView<?> parent, View view, int position, long id) {
        ListItemSetting item = (ListItemSetting) parent.getItemAtPosition(position);
        final Setting setting = item.getSetting();
        if (setting.getType() == SettingType.BOOL) {
            Boolean newValue = !Boolean.parseBoolean(setting.getValue());
            setting.setValue(newValue.toString());
            SwitchCompat s = (SwitchCompat) view.findViewById(R.id.swcValue);
            s.setChecked(newValue);
            if (setting.getName().equals("dropShadow") && newValue) {
                Setting bgColor = settings.getSetting("backgroundColor");
                int colorValue = Color.parseColor(bgColor.getValue());
                int newColor = Color.rgb(Color.red(colorValue), Color.green(colorValue), Color.blue(colorValue));
                bgColor.setValue(String.format("#%08X", newColor));
            }
        } else if (setting.getType() == SettingType.TRANSPARENT_COLOR) {
            int colorValue = Color.parseColor(setting.getValue());
            final ColorPicker cp;
            boolean setWallpaper = false;
            if (setting.getName().equals("backgroundColor") && Boolean.parseBoolean(settings.getSetting("dropShadow").getValue())) {
                cp = new ColorPicker((Activity) view.getContext(), Color.red(colorValue), Color.green(colorValue), Color.blue(colorValue));
            } else {
                cp = new ColorPicker((Activity) view.getContext(), Color.alpha(colorValue), Color.red(colorValue), Color.green(colorValue), Color.blue(colorValue));
                setWallpaper = true;
            }

            cp.setCallback(new ColorPickerCallback() {
                @Override
                public void onColorChosen(@ColorInt int color) {
                    setting.setValue(String.format("#%08X", color));
                    cp.cancel();
                }
            });

            cp.show();
            cp.setButtonColor(view.getContext().getResources().getColor(R.color.colorPrimary));
            if (setWallpaper) {
                final WallpaperManager wallpaperManager = WallpaperManager.getInstance(view.getContext());
                final Drawable wallpaperDrawable = wallpaperManager.getDrawable();
                cp.setBackgroundDrawable(wallpaperDrawable);
            }
        } else if (setting.getType() == SettingType.COLOR) {
            int colorValue = Color.parseColor(setting.getValue());

            final ColorPicker cp = new ColorPicker((Activity) view.getContext(), Color.red(colorValue), Color.green(colorValue), Color.blue(colorValue));
            cp.setCallback(new ColorPickerCallback() {
                @Override
                public void onColorChosen(@ColorInt int color) {
                    setting.setValue(String.format("#%08X", color));
                    cp.cancel();
                }
            });

            cp.show();
            cp.setButtonColor(view.getContext().getResources().getColor(R.color.colorPrimary));
        } else if (setting.getType() == SettingType.DATE_SHORT || setting.getType() == SettingType.DATE_LONG || setting.getType() == SettingType.TIME) {
            final DateFormatDialog df = new DateFormatDialog((Activity) view.getContext(), setting.getValue(), setting.getType());
            df.setCallback(new DateTimeFormatPickerCallback() {
                @Override
                public void onFormatChosen(String format) {
                    setting.setValue(format);
                    ((SettingsListAdapter)parent.getAdapter()).notifyDataSetChanged();
                    df.cancel();
                }
            });
            df.show();
        } else if (setting.getType() == SettingType.CALENDARS) {
            final CalendarSelectionActivity cs = new CalendarSelectionActivity(view.getContext());
            cs.show();
            // TODO: add callback and handle result
        }

    }
}
