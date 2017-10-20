package gr.ictpro.jsalatas.agendawidget.model.settings;

import android.app.Activity;
import android.app.WallpaperManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.AdapterView;
import com.pes.androidmaterialcolorpickerdialog.ColorPicker;
import com.pes.androidmaterialcolorpickerdialog.ColorPickerCallback;
import gr.ictpro.jsalatas.agendawidget.R;
import gr.ictpro.jsalatas.agendawidget.model.calendar.CalendarSelectionCallback;
import gr.ictpro.jsalatas.agendawidget.model.settings.types.SettingBool;
import gr.ictpro.jsalatas.agendawidget.model.settings.types.SettingColor;
import gr.ictpro.jsalatas.agendawidget.model.settings.types.SettingTransparentColor;
import gr.ictpro.jsalatas.agendawidget.ui.CalendarSelectionDialog;
import gr.ictpro.jsalatas.agendawidget.ui.DateFormatDialog;

public class SettingsOnClickListener implements AdapterView.OnItemClickListener {
    private final Settings settings;

    public SettingsOnClickListener(Settings settings) {
        this.settings = settings;
    }

    @Override
    public void onItemClick(final AdapterView<?> parent, View view, int position, long id) {
        ListItemSetting item = (ListItemSetting) parent.getItemAtPosition(position);
        final Setting<?> setting = item.getSetting();

        // Before onClick
        if (setting instanceof SettingTransparentColor) {
            if (setting.getName().equals("backgroundColor") && Boolean.parseBoolean(settings.getSetting("dropShadow").getStringValue())) {
                ((SettingTransparentColor) setting).setTransparent(true);
                ((SettingTransparentColor) setting).setBackground(null);
            } else {
                ((SettingTransparentColor) setting).setTransparent(false);
                final WallpaperManager wallpaperManager = WallpaperManager.getInstance(view.getContext());
                final Drawable wallpaperDrawable = wallpaperManager.getDrawable();
                ((SettingTransparentColor) setting).setBackground(wallpaperDrawable);
            }
        }

        setting.onClick(parent, view);



        // After onClick
        if (setting instanceof SettingBool) {
            if (setting.getName().equals("dropShadow") && (Boolean) setting.getValue()) {
                Setting bgColor = settings.getSetting("backgroundColor");
                int colorValue = Color.parseColor(bgColor.getStringValue());
                int newColor = Color.rgb(Color.red(colorValue), Color.green(colorValue), Color.blue(colorValue));
                bgColor.setStringValue(String.valueOf(newColor));
            }
        }

    }
}
