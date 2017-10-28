package gr.ictpro.jsalatas.agendawidget.model.calendar;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DayGroup implements EventItem  {
    private final Date startDate;

    DayGroup(Date startDate) {
        this.startDate = startDate;
    }

    @Override
    public Date getStartDate() {
        return startDate;
    }

    @Override
    public int compareTo(EventItem o) {
        Calendar startCalendarInstance = GregorianCalendar.getInstance();
        Calendar oStartCalendarInstance = GregorianCalendar.getInstance();
        startCalendarInstance.setTime(startDate);
        oStartCalendarInstance.setTime(o.getStartDate());

        if (startCalendarInstance.get(java.util.Calendar.YEAR) != oStartCalendarInstance.get(java.util.Calendar.YEAR) ||
                startCalendarInstance.get(java.util.Calendar.MONTH) != oStartCalendarInstance.get(java.util.Calendar.MONTH) ||
                startCalendarInstance.get(java.util.Calendar.DAY_OF_MONTH) != oStartCalendarInstance.get(Calendar.DAY_OF_MONTH)) {
            return startDate.compareTo(o.getStartDate());
        } else if(!(o instanceof DayGroup)) {
            return -1;
        }


        return startDate.compareTo(((DayGroup) o).startDate);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DayGroup dayGroup = (DayGroup) o;

        return startDate.equals(dayGroup.startDate);
    }

    @Override
    public int hashCode() {
        return startDate.hashCode();
    }
}
