package gr.ictpro.jsalatas.agendawidget.model.settings;

class ListItemCategory extends ListItem {
    private String category;

    ListItemCategory(String category) {
        this.category = category;
    }

    String getCategory() {
        return category;
    }
}
