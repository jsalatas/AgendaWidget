package gr.ictpro.jsalatas.agendawidget.utils;

class ArrayUtils {
    static int indexOf(String[] array, String search) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(search)) {
                return i;
            }
        }
        return -1;
    }


}
