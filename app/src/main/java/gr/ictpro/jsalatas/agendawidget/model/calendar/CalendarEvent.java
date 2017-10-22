package gr.ictpro.jsalatas.agendawidget.model.calendar;

import android.support.annotation.ColorInt;

import java.util.Date;

public class CalendarEvent implements Comparable<CalendarEvent> {
    private final @ColorInt int color;

    private final String title;

    private final String location;

    private final String description;

    private final Date startDate;

    private final Date endDate;

    private final boolean allDay;

    CalendarEvent(int color, String title, String location, String description, Date startDate, Date endDate, boolean allDay) {
        this.color = color;
        this.title = title;
        this.location = location;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.allDay = allDay;
    }

    public @ColorInt int getColor() {
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
        return 0;
    }
}
