package gr.ictpro.jsalatas.agendawidget.model.settings;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import gr.ictpro.jsalatas.agendawidget.R;
import gr.ictpro.jsalatas.agendawidget.application.AgendaWidgetApplication;
import gr.ictpro.jsalatas.agendawidget.model.settings.types.Setting;
import gr.ictpro.jsalatas.agendawidget.model.settings.types.SettingBool;
import gr.ictpro.jsalatas.agendawidget.model.settings.types.SettingString;
import gr.ictpro.jsalatas.agendawidget.model.task.TaskContract;
import gr.ictpro.jsalatas.agendawidget.model.task.TaskProvider;

import java.util.List;

public class SettingsListAdapter extends ArrayAdapter<ListItem> {

    private final LayoutInflater inflater;

    public SettingsListAdapter(Context context, List<ListItem> items) {
        super(context, 0, items);

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        ListItem item = getItem(position);

        if (item instanceof ListItemCategory) {
            v = inflater.inflate(R.layout.settings_list_category, parent, false);
            v.setClickable(true);

            TextView tv = (TextView) v.findViewById(R.id.tvCategory);
            tv.setText(AgendaWidgetApplication.getResourceString(((ListItemCategory) item).getCategory()));
        } else if (item instanceof ListItemSetting) {
            Setting setting = ((ListItemSetting) item).getSetting();

            v = setting.getView(this.getContext(), isEnabled(position));
            v.setClickable(false);

            LinearLayout root = (LinearLayout) v.findViewById(R.id.root);

            if (position != getCount() - 1 && getItem(position + 1) instanceof ListItemSetting) {
                root.setShowDividers(LinearLayout.SHOW_DIVIDER_END);
                root.setDividerDrawable(this.getContext().getResources().getDrawable(R.drawable.settings_divider));
            }
        }

        return v;
    }

    @Override
    public boolean isEnabled(int position) {
        boolean isEnabled = true;
        if(getItem(position) instanceof ListItemSetting) {
            // Checking other properties
            Setting setting = ((ListItemSetting) getItem(position)).getSetting();
            String disabledByItems = setting.getDisabledBy();
            if(disabledByItems != null) {
                String[] disabledByList = disabledByItems.split(",");
                for (String disabledBy : disabledByList) {
                    Boolean negate = disabledBy.startsWith("!");
                    int index = indexOf(negate?disabledBy.substring(1): disabledBy);
                    if (index != -1) {
                        if(isEnabled(index)) {
                            Boolean val = Boolean.parseBoolean(((ListItemSetting) getItem(index)).getSetting().getStringValue());
                            boolean enabled = negate?!val:val;
                            if(!enabled && setting instanceof SettingBool) {
                                ((SettingBool) setting).setValue(false);
                            }
                            isEnabled = enabled;
                            break;
                        }
                    }
                }
            }

            // Checking providers
            String disabledByProviders = setting.getDisabledByProvider();
            if(disabledByProviders != null && !setting.getName().equals("taskProvider")) {
                int index = indexOf("taskProvider");
                if(index != -1) {
                    TaskContract taskProvider = TaskProvider.getTaskContract(((ListItemSetting) getItem(index)).getSetting().getStringValue());
                    String[] disabledByList = disabledByProviders.split(",");
                    for (String disabledBy : disabledByList) {
                        TaskContract t = TaskProvider.getTaskContract(disabledBy);
                        boolean enabled = t==null || taskProvider==null || !t.getClass().equals(taskProvider.getClass());
                        if (!enabled) {
                            if (setting instanceof SettingBool) {
                                ((SettingBool) setting).setValue(false);
                            } else if (setting instanceof SettingString) {
                                setting.setStringValue("");
                            }
                            isEnabled = false;
                            break;
                        }
                    }
                }
            }

        }
        return isEnabled;
    }

    public int indexOf(String settingName) {
        for(int i=0; i< getCount(); i++) {
            if (getItem(i) instanceof ListItemSetting) {
                ListItemSetting item = (ListItemSetting) getItem(i);
                if(item.getSetting().getName().equals(settingName)) {
                    return i;
                }
            }
        }
        return -1;
    }
}
