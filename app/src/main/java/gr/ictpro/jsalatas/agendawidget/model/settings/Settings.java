package gr.ictpro.jsalatas.agendawidget.model.settings;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.SwitchCompat;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import gr.ictpro.jsalatas.agendawidget.R;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

@Root(name = "settings")
public class Settings {
    @ElementList(inline = true, entry = "setting")
    private List<Setting> settings;

    private Context context;
    private int widgetId;

    public Settings() {
        // Used for deserialization
    }

    public Settings(Context context, int widgetId) {
        this.context = context;
        this.widgetId = widgetId;
        Serializer serializer = new Persister();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(this.context.getResources().openRawResource(R.raw.settings)));
            settings = serializer.read(Settings.class, br).getSettings();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<Setting> getSettings() {
        return settings;
    }

    public View getView(SettingTab tab) {
        LinearLayout root = new LinearLayout(context);
        root.setOrientation(LinearLayout.VERTICAL);
        int tabPadding = context.getResources().getDimensionPixelSize(R.dimen.settings_tab_padding);
        root.setPadding(tabPadding, tabPadding, tabPadding, tabPadding);
        LinearLayout.LayoutParams pv = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        root.setLayoutParams(pv);
        String category = "";
        int counter = 0;
        for (Setting setting : settings) {
            if (!category.equals(setting.getCategory())) {
                category = setting.getCategory();
                ViewGroup.LayoutParams pt = new ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                TextView tvCategory = new TextView(context);
                tvCategory.setLayoutParams(pt);
                tvCategory.setEnabled(false);
                tvCategory.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
                tvCategory.setText(getResourceString(category));
                tvCategory.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                tvCategory.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.settings_category_text_size));
                tvCategory.setTypeface(null, Typeface.BOLD);
                tvCategory.setPadding(0, counter == 0 ? 0 : context.getResources().getDimensionPixelSize(R.dimen.settings_category_padding_top), 0, context.getResources().getDimensionPixelSize(R.dimen.settings_category_padding_bottom));
                root.addView(tvCategory);

            }
            boolean showDivider = counter != settings.size() - 1 && settings.get(counter + 1).getCategory().equals(category);
            root.addView(getSettingView(setting, showDivider));
            counter++;
        }

        ScrollView v = new ScrollView(context);
        ViewGroup.LayoutParams ps = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        v.setLayoutParams(pv);
        v.setPadding(0, 0, 0, 0);
        v.addView(root);

        return v;
    }

    private View getSettingView(Setting setting, boolean showDivider) {
        LinearLayout root = new LinearLayout(context);
        root.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        root.setLayoutParams(p);
        ViewGroup.LayoutParams pt = new ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        TextView tvTitle = new TextView(context);
        tvTitle.setLayoutParams(pt);
        tvTitle.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        tvTitle.setEnabled(false);
        tvTitle.setText(getResourceString(setting.getTitle()));
        tvTitle.setTextColor(context.getResources().getColor(R.color.colorText));
        tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.settings_title_text_size));
        tvTitle.setPadding(0, 0, 0, context.getResources().getDimensionPixelSize(R.dimen.settings_text_padding));
        root.addView(tvTitle);

        TextView tvDescription = new TextView(context);
        tvDescription.setLayoutParams(pt);
        tvDescription.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        tvDescription.setEnabled(false);
        tvDescription.setText(getResourceString(setting.getDescription()));
        tvDescription.setTextColor(context.getResources().getColor(R.color.colorDescriptionText));
        tvDescription.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.settings_description_text_size));
        tvTitle.setPadding(0, context.getResources().getDimensionPixelSize(R.dimen.settings_text_padding), 0, 0);

        root.addView(tvDescription);

        if (setting.getType() == SettingType.BOOL) {
            LinearLayout l = root;
            LinearLayout.LayoutParams pl = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
            l.setLayoutParams(pl);

            LinearLayout inner = new LinearLayout(context);
            inner.setLayoutParams(p);
            root.setVerticalGravity(Gravity.CENTER_VERTICAL);

            inner.setOrientation(LinearLayout.HORIZONTAL);
            inner.addView(l);

            SwitchCompat s = new SwitchCompat(context);
            ViewGroup.LayoutParams ps = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            s.setLayoutParams(ps);
            s.setChecked(Boolean.parseBoolean(setting.getValue()));
            s.setTrackTintList(context.getResources().getColorStateList(R.drawable.switch_selector));

            inner.addView(s);

            root = new LinearLayout(context);
            root.setLayoutParams(p);
            root.setOrientation(LinearLayout.VERTICAL);
            root.addView(inner);

        } else {
        }

        root.setVerticalGravity(Gravity.CENTER_VERTICAL);
        root.setPadding(0, context.getResources().getDimensionPixelSize(R.dimen.settings_container_padding), 0, context.getResources().getDimensionPixelSize(R.dimen.settings_container_padding));

        if (showDivider) {
            root.setShowDividers(LinearLayout.SHOW_DIVIDER_END);
            root.setDividerDrawable(context.getResources().getDrawable(R.drawable.settings_divider));
        }

        return root;
    }

    private String getResourceString(String name) {
        int nameResourceID = context.getResources().getIdentifier(name, "string", context.getApplicationInfo().packageName);
        if (nameResourceID == 0) {
            throw new IllegalArgumentException("No resource string found with name " + name);
        } else {
            return context.getString(nameResourceID);
        }
    }
}
