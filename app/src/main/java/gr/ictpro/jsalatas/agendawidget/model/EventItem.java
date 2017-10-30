package gr.ictpro.jsalatas.agendawidget.model;

import java.util.Date;

public interface EventItem extends Comparable<EventItem> {
    Date getStartDate();
}
