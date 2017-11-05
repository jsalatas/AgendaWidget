package gr.ictpro.jsalatas.agendawidget.model.task;

import gr.ictpro.jsalatas.agendawidget.model.calendar.Calendar;

public class Task extends Calendar {
    public Task(Long id, String accountName, String name, Integer color) {
        super(id, accountName, name, color);
    }
}
