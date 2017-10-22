package gr.ictpro.jsalatas.agendawidget.utils;

public class TimePeriod {

    private final String timeUnit;
    private final int timeUnitIndex;
    private final long value;

    TimePeriod(String timeUnit, long value, int timeUnitIndex) {
        this.timeUnit = timeUnit;
        this.timeUnitIndex = timeUnitIndex;
        this.value = value;
    }

    public String getTimeUnit() {
        return timeUnit;
    }

    public long getValue() {
        return value;
    }

    public int getTimeUnitIndex() {
        return timeUnitIndex;
    }
}
