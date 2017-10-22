package gr.ictpro.jsalatas.agendawidget.utils;

public class TimePeriodUtils {

    private final String[] values;
    private Class<? extends TimePeriodEnumInterface> valuesEnum;

    public <T extends Enum & TimePeriodEnumInterface> TimePeriodUtils(String[] values, Class<? extends T> valuesEnum) {
        this.values = values;
        this.valuesEnum = valuesEnum;
    }

    public long getBase(String item) {
        int index = ArrayUtils.indexOf(values, item);

        if(index != -1) {
            return valuesEnum.getEnumConstants()[index].interval();
        }
        return -1;
    }

    public TimePeriod getTimePeriod(long value) {
        String timeUnit = "";
        int timeUnitIndex = 0;
        long v = 0;
        for(int i=values.length - 1 ; i>=0 ; i--) {
            if((value % valuesEnum.getEnumConstants()[i].interval()) == 0) {
                v = value / valuesEnum.getEnumConstants()[i].interval();
                timeUnit = values[i];
                timeUnitIndex = i;
                break;
            }
        }

        return new TimePeriod(timeUnit, v, timeUnitIndex);
    }


}
