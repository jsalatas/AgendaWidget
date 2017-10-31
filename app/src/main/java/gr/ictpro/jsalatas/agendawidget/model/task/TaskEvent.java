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
    public int compareTo(EventItem o) {
        if(o instanceof TaskEvent) {
            int otherPriority = ((TaskEvent)o).priority;
            if (this.priority != 0 && otherPriority != 0) {
                return this.priority - otherPriority;
            } else if (this.priority != otherPriority) {
                return otherPriority - this.priority;
            }
        } else {
            return -1;
        }
        return super.compareTo(o);
    }

    @Override
    public boolean isMultiDay() {
        return false;
    }

    @Override
    public List<CalendarEvent> getMultidayEventsList(Date until) {
        return new ArrayList<>();
    }

    public @ColorInt int getPriorityColor() {
        String coloStr = "#888888";
        if(priority > 5) {
            // low
            coloStr = "#00AA00";
        } else if (priority == 5) {
            // medium
            coloStr = "#FF8000";
        } else if (priority> 0 && priority < 5) {
            // high
            coloStr = "#FF0000";
        }

        return Color.parseColor(coloStr);
    }

}
