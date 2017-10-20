package gr.ictpro.jsalatas.agendawidget.model.settings.types;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import gr.ictpro.jsalatas.agendawidget.R;
import gr.ictpro.jsalatas.agendawidget.application.AgendaWidgetApplication;
import gr.ictpro.jsalatas.agendawidget.model.settings.SettingTab;
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

    public String getStringValue() {return value != null? value : defaultValue;}

    public void setStringValue(String value) {
        this.value = value;
    }

    protected abstract T getValue();

    protected abstract void setValue(T value);

    public View getView(Context context) {
        View v = View.inflate(context, R.layout.settings_list_item, null);

        TextView tvTitle = (TextView) v.findViewById(R.id.tvTitle);
        tvTitle.setText(AgendaWidgetApplication.getResourceString(getTitle()));

        TextView tvDescription = (TextView) v.findViewById(R.id.tvDescription);
        tvDescription.setText(AgendaWidgetApplication.getResourceString(getDescription()));

        return v;
    }

    public abstract void onClick(final AdapterView<?> parent, View view);
}

