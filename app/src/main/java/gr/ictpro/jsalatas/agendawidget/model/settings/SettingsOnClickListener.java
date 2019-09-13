package gr.ictpro.jsalatas.agendawidget.model.settings;

import android.Manifest;
import android.app.WallpaperManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.AdapterView;
import gr.ictpro.jsalatas.agendawidget.application.AgendaWidgetApplication;
import gr.ictpro.jsalatas.agendawidget.model.settings.types.Setting;
import gr.ictpro.jsalatas.agendawidget.model.settings.types.SettingBool;
import gr.ictpro.jsalatas.agendawidget.model.settings.types.SettingTransparentColor;
import gr.ictpro.jsalatas.agendawidget.ui.AgendaWidgetConfigureActivity;

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
            } else if (setting.getName().equals("backgroundColor")) {
                ((SettingTransparentColor) setting).setTransparent(false);
                // TODO: needs permission to read external storage
                //       properly ask for permission, before showing
                //       the background color picker.
                if (AgendaWidgetConfigureActivity.checkForPermission(AgendaWidgetApplication.getActivity(view.getContext()), Manifest.permission.READ_EXTERNAL_STORAGE, AgendaWidgetConfigureActivity.PERMISSIONS_REQUEST_ACCESS_EXTERNAL_STORAGE, true)) {
                    final WallpaperManager wallpaperManager = WallpaperManager.getInstance(view.getContext());
                    final Drawable wallpaperDrawable = wallpaperManager.getDrawable();
                    ((SettingTransparentColor) setting).setBackground(wallpaperDrawable);
                }
            }
        }

        setting.onClick(parent, view);



        // After onClick
        if (setting instanceof SettingBool) {
            if (setting.getName().equals("dropShadow") && Boolean.parseBoolean(setting.getStringValue())) {
                Setting bgColor = settings.getSetting("backgroundColor");
                int colorValue = Color.parseColor(bgColor.getStringValue());
                int newColor = Color.rgb(Color.red(colorValue), Color.green(colorValue), Color.blue(colorValue));
                bgColor.setStringValue(String.format("#%08X", newColor));
            }
            ((SettingsListAdapter) parent.getAdapter()).notifyDataSetChanged();
        }

    }
}
