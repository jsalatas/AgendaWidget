package gr.ictpro.jsalatas.agendawidget.model.settings;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

@Root
class Setting {
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
    private SettingType type;
    @Attribute
    private String defaultValue;

    private String value;

    public String getName() {
        return name;
    }

    SettingTab getTab() {
        return tab;
    }

    String getCategory() {
        return category;
    }

    String getTitle() {
        return title;
    }

    String getDescription() {
        return description;
    }

    SettingType getType() {
        return type;
    }

    String getValue() {
        return value != null? value : defaultValue;
    }

    void setValue(String value) {
        this.value = value;
    }
}

