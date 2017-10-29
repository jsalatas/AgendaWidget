package gr.ictpro.jsalatas.agendawidget.utils;

import android.text.InputFilter;
import android.text.Spanned;
import android.widget.EditText;

public class InputFilterMinMax implements InputFilter {

    private final int min, max;

    public InputFilterMinMax(String min, String max) {
        this.min = Integer.parseInt(min);
        this.max = Integer.parseInt(max);
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        String destination = dest.toString();
        try {
            StringBuilder sb = new StringBuilder();
            if(dstart > 0) {
                sb.append(destination.substring(0, dstart));
            }
            sb.append(source);
            if(dend < destination.length()) {
                sb.append(destination.substring(dend));
            }

            int input = Integer.parseInt(sb.toString());
            if (isInRange(min, max, input))
                return null;
        } catch (NumberFormatException nfe) {
            // do nothing
        }
        return destination.substring(dstart, dend);
    }

    private boolean isInRange(int a, int b, int c) {
        return b > a ? c >= a && c <= b : c >= b && c <= a;
    }
}

