package gr.ictpro.jsalatas.agendawidget.model.settings.types;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.ColorInt;
import android.text.Layout;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;
import gr.ictpro.jsalatas.agendawidget.R;
import gr.ictpro.jsalatas.agendawidget.application.AgendaWidgetApplication;
import gr.ictpro.jsalatas.agendawidget.model.settings.SettingTab;
import gr.ictpro.jsalatas.agendawidget.model.settings.SettingsListAdapter;
import gr.ictpro.jsalatas.agendawidget.ui.widgets.SettingDialog;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

@Root
public abstract class Setting<T> {
    @Attribute
    private String name;
    @Attribute
    private SettingTab tab;
    @Attribute
    private String category;
    @Attribute
    private String title;
    @Attribute
    private String description;
    @Attribute
    private String defaultValue;
    @Attribute(required = false)
    private String disabledBy;
    @Attribute(required = false)
    private String disabledByProvider;

    private int appWidgetId;

    private String value;

    public String getName() {
        return name;
    }

    public SettingTab getTab() {
        return tab;
    }

    public String getCategory() {
        return category;
    }

    String getTitle() {
        return title;
    }

    String getDescription() {
        return description;
    }

    public String getDisabledBy() {
        return disabledBy;
    }

    public String getDisabledByProvider() {
        return disabledByProvider;
    }

    public String getStringValue() {
        return value != null ? value : defaultValue;
    }

    public void setStringValue(String value) {
        this.value = value;
    }

    public abstract T getValue();

    public abstract void setValue(T value);

    public View getView(Context context, boolean isEnabled) {
        View v = View.inflate(context, R.layout.settings_list_item, null);

        TextView tvTitle = (TextView) v.findViewById(R.id.tvTitle);
        tvTitle.setText(AgendaWidgetApplication.getResourceString(getTitle()));

        TextView tvDescription = (TextView) v.findViewById(R.id.tvDescription);
        tvDescription.setText(AgendaWidgetApplication.getResourceString(getDescription()));

        setEnabled(v, isEnabled);
        return v;
    }

    public int getAppWidgetId() {
        return appWidgetId;
    }

    public void setAppWidgetId(int appWidgetId) {
        this.appWidgetId = appWidgetId;
    }

    void setEnabled(View v, boolean isEnabled) {
        TextView tvTitle = (TextView) v.findViewById(R.id.tvTitle);
        TextView tvDescription = (TextView) v.findViewById(R.id.tvDescription);
        LinearLayout root = (LinearLayout) v.findViewById(R.id.root);

        @ColorInt int titleColor = v.getContext().getResources().getColor(isEnabled ? R.color.colorText : R.color.colorDisabledItem);
        tvTitle.setTextColor(titleColor);

        @ColorInt int descriptionColor = v.getContext().getResources().getColor(isEnabled ? R.color.colorDescriptionText : R.color.colorDisabledItemDescription);
        tvDescription.setTextColor(descriptionColor);
    }

    public void onClick(final AdapterView<?> parent, View view) {
        SettingDialog<T> dialog = getDialog(view);
        if (shouldRefreshList()) {
            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    ((SettingsListAdapter) parent.getAdapter()).notifyDataSetChanged();
                }
            });
        }
        dialog.show();

    }

    boolean shouldRefreshList() {
        return false;
    }

    protected abstract SettingDialog<T> getDialog(View view);

}

