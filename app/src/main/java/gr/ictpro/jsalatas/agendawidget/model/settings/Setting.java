package gr.ictpro.jsalatas.agendawidget.model.settings;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root
public class Setting {
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

    public String getName() {
        return name;
    }

    public SettingTab getTab() {
        return tab;
    }

    public String getCategory() {
        return category;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public SettingType getType() {
        return type;
    }
}

