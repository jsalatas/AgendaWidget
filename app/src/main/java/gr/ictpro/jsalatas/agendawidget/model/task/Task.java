package gr.ictpro.jsalatas.agendawidget.model.task;

import gr.ictpro.jsalatas.agendawidget.model.calendar.Calendar;

public class Task extends Calendar {
    Task(Long id, String accountName, String name, int color) {
        super(id, accountName, name, color);
    }
}
