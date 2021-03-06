package gr.ictpro.jsalatas.agendawidget.model.task;

import android.graphics.Color;
import android.support.annotation.ColorInt;
import gr.ictpro.jsalatas.agendawidget.model.calendar.CalendarEvent;
import gr.ictpro.jsalatas.agendawidget.model.EventItem;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TaskEvent extends CalendarEvent {

    private final int priority;


    public TaskEvent(long id, int color, String title, String location, String description, Date startDate, Date endDate, boolean allDay, int priority) {
        super(id, color, title, location, description, startDate, endDate, allDay);
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        TaskEvent taskEvent = (TaskEvent) o;

        return priority == taskEvent.priority;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + priority;
        return result;
    }

    @Override
    public boolean isMultiDay() {
        return false;
    }

    @Override
    public List<CalendarEvent> getMultidayEventsList(Date until) {
        return new ArrayList<>();
    }
}
