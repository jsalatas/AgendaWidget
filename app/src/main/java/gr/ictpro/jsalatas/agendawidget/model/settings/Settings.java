package gr.ictpro.jsalatas.agendawidget.model.settings;

import org.simpleframework.xml.ElementArray;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root
class Settings {
    @ElementList
    private List<Setting> settings;
}
