package gr.ictpro.jsalatas.agendawidget.model.calendar;

import java.util.Date;

public interface EventItem extends Comparable<EventItem> {
    Date getStartDate();
}
