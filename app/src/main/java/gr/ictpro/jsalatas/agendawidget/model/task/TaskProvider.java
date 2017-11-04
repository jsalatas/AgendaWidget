package gr.ictpro.jsalatas.agendawidget.model.task;

import gr.ictpro.jsalatas.agendawidget.R;
import gr.ictpro.jsalatas.agendawidget.application.AgendaWidgetApplication;

import java.util.ArrayList;
import java.util.List;

public class TaskProvider {
    private static List<TaskContract> providers;

    public synchronized static List<TaskContract> getProviders() {
        if(providers == null) {
            String[] providersList = AgendaWidgetApplication.getContext().getResources().getStringArray(R.array.task_providers);
            providers = new ArrayList<>();
            providers.add(getTaskContract("gr.ictpro.jsalatas.agendawidget.model.task.providers.NoTaskProvider"));
            for(String provider: providersList) {
                providers.add(getTaskContract(provider));
            }
        }
        return providers;
    }


    public static TaskContract getTaskContract(String className) {
        Class c;
        try {
            c = Class.forName(className);
            Object o = c.newInstance();
            if(o instanceof TaskContract) {
                return (TaskContract) o;
            }
            return null;
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException  e) {
            return null;
        }
    }
}
