package gr.ictpro.jsalatas.agendawidget.utils;

public interface TimePeriodEnumInterface {
    long interval();
    int ord();
    TimePeriodEnumInterface getValue(int ordinal);
}

