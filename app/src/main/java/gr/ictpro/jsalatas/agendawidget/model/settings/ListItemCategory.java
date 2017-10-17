package gr.ictpro.jsalatas.agendawidget.model.settings;

class ListItemCategory extends ListItem {
    private final String category;

    ListItemCategory(String category) {
        this.category = category;
    }

    String getCategory() {
        return category;
    }
}
