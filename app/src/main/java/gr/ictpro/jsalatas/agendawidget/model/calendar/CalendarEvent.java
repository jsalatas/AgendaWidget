package gr.ictpro.jsalatas.agendawidget.model.calendar;

import android.support.annotation.ColorInt;
import gr.ictpro.jsalatas.agendawidget.utils.DateUtils;

import java.util.*;
import java.util.Calendar;

public class CalendarEvent implements Comparable<CalendarEvent> {
    private final long id;

    private final @ColorInt
    int color;

    private final String title;

    private final String location;

    private final String description;

    private final Date startDate;

    private final Date endDate;

    private final boolean allDay;

    CalendarEvent(long id, int color, String title, String location, String description, Date startDate, Date endDate, boolean allDay) {
        this.id = id;
        this.color = color;
        this.title = title;
        this.location = location;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.allDay = allDay;
    }

    public long getId() {
        return id;
    }

    public @ColorInt
    int getColor() {
        return color;
    }

    public String getTitle() {
        return title;
    }

    public String getLocation() {
        return location;
    }

    public String getDescription() {
        return description;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public boolean isAllDay() {
        return allDay;
    }

    @SuppressWarnings("SimplifiableIfStatement")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CalendarEvent that = (CalendarEvent) o;

        if (allDay != that.allDay) return false;
        if (!title.equals(that.title)) return false;
        if (location != null ? !location.equals(that.location) : that.location != null) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (!startDate.equals(that.startDate)) return false;
        return endDate.equals(that.endDate);
    }

    @Override
    public int hashCode() {
        int result = title.hashCode();
        result = 31 * result + (location != null ? location.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + startDate.hashCode();
        result = 31 * result + endDate.hashCode();
        result = 31 * result + (allDay ? 1 : 0);
        return result;
    }

    @Override
    public int compareTo(CalendarEvent o) {
        Calendar startCalendarInstance = GregorianCalendar.getInstance();
        Calendar oStartCalendarInstance = GregorianCalendar.getInstance();
        startCalendarInstance.setTime(startDate);
        oStartCalendarInstance.setTime(o.startDate);

        // Check date part
        if (startCalendarInstance.get(Calendar.YEAR) != oStartCalendarInstance.get(Calendar.YEAR) ||
                startCalendarInstance.get(Calendar.MONTH) != oStartCalendarInstance.get(Calendar.MONTH) ||
                startCalendarInstance.get(Calendar.DAY_OF_MONTH) != oStartCalendarInstance.get(Calendar.DAY_OF_MONTH)) {
            return startDate.compareTo(o.startDate);
        }

        // All day events come first
        if(allDay != o.allDay) {
            return allDay ? -1 : 1;
        }

        if (startCalendarInstance.get(Calendar.HOUR_OF_DAY) != oStartCalendarInstance.get(Calendar.HOUR_OF_DAY) ||
                startCalendarInstance.get(Calendar.MINUTE) != oStartCalendarInstance.get(Calendar.MINUTE)) {
            return startDate.compareTo(o.startDate);
        }

        if(endDate.getTime() % (1000L * 60)  != o.endDate.getTime() % (1000L * 60)) {
            return endDate.compareTo(o.endDate);
        }

        return title.compareTo(o.title);
    }

    public boolean isMultiDay() {
        Date now = GregorianCalendar.getInstance().getTime();
        Date currentStart;

        if(now.compareTo(startDate) > 0 && !DateUtils.isInSameDay(now, startDate)) {
            currentStart = DateUtils.dayFloor(now);
        } else {
            currentStart = startDate;
        }

        int days = DateUtils.daysBetween(currentStart, endDate);

        return days > 1;
    }

    public List<CalendarEvent> getMultidayEventsList(Date until) {
        List<CalendarEvent> res = new ArrayList<>();

        Date now = GregorianCalendar.getInstance().getTime();
        Date currentStart, currentEnd;
        if(now.compareTo(startDate) > 0 && !DateUtils.isInSameDay(now, startDate)) {
            currentStart = DateUtils.dayFloor(now);
        } else {
            currentStart = startDate;
        }
        currentEnd = DateUtils.dayCeil(currentStart);

        int dateDiff = DateUtils.daysBetween(startDate, endDate);


        for (int i=0; i<dateDiff; i++) {
            CalendarEvent e = new CalendarEvent(id, color, title, location, description, currentStart, currentEnd,DateUtils. isAllDay(currentStart, currentEnd));
            res.add(e);
            currentStart = DateUtils.nextDay(DateUtils.dayFloor(currentStart));
            currentEnd = DateUtils.nextDay(DateUtils.dayFloor(currentEnd));
        }
        if(until.compareTo(endDate) <= 0 && !DateUtils.isInSameDay(until, endDate)) {
            currentEnd = until;
        } else {
            currentEnd = endDate;
        }
        CalendarEvent e = new CalendarEvent(id, color, title, location, description, currentStart, currentEnd, allDay);
        res.add(e);

        return res;
    }

// Debug
//    static SimpleDateFormat df = new SimpleDateFormat("YYYY/MM/dd HH:mma z");
//
//    public String toString(Date from, Date to) {
//        return "from=" + df.format(from) +
//                " to=" + df.format(to) +
//                ":::: startDate=" + df.format(startDate) +
//                ", endDate=" + df.format(endDate) +
//                ", title='" + title + '\'' +
//                ", allDay=" + allDay;
//    }
}
