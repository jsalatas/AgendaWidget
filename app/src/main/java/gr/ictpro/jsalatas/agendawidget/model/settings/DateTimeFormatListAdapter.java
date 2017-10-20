package gr.ictpro.jsalatas.agendawidget.model.settings;

import android.content.Context;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import gr.ictpro.jsalatas.agendawidget.R;
import gr.ictpro.jsalatas.agendawidget.application.AgendaWidgetApplication;
import gr.ictpro.jsalatas.agendawidget.model.settings.types.SettingDateLong;
import gr.ictpro.jsalatas.agendawidget.model.settings.types.SettingDateShort;
import gr.ictpro.jsalatas.agendawidget.model.settings.types.SettingTime;
import gr.ictpro.jsalatas.agendawidget.ui.DateFormatDialog;

import java.util.Calendar;
import java.util.Date;

public class DateTimeFormatListAdapter extends ArrayAdapter<String> {
    private final LayoutInflater inflater;
    private final Setting setting;

    public DateTimeFormatListAdapter(Context context, Setting setting) {
        super(context, 0);
        this.setting = setting;

        fillItems();
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String item = getItem(position);
        final View v = inflater.inflate(R.layout.datetime_format_list_item, parent, false);
        TextView tvTitle = (TextView) v.findViewById(R.id.tvTitle);
        tvTitle.setText(AgendaWidgetApplication.getResourceString(item));
        Date currentTime = Calendar.getInstance().getTime();

        final TextView tvDescription = (TextView) v.findViewById(R.id.tvDescription);
        final View dialog = (View) parent.getParent();
        String dateFormatExample = "";
        if (setting instanceof SettingDateLong || setting instanceof SettingDateShort) {
            dateFormatExample = Settings.formatDate(DateFormatDialog.getDateTimeFormat(dialog, item), currentTime);
        } else if (setting instanceof SettingTime) {
            dateFormatExample = Settings.formatTime(DateFormatDialog.getDateTimeFormat(dialog, item), currentTime);
        }

        TextView tvOK = (TextView) dialog.findViewById(R.id.tvDateTimeFormatOk);
        final ListView lv = (ListView) parent;

        if (!dateFormatExample.isEmpty()) {
            tvDescription.setText(dateFormatExample);
            tvDescription.setTextColor(getContext().getResources().getColor(R.color.colorDescriptionText));
            tvOK.setClickable(true);
            tvOK.setTextColor(getContext().getResources().getColor(R.color.colorPrimary));
        } else {
            if (lv.getCheckedItemPosition() == position) {
                tvDescription.setText(R.string.custom_format_error);
                tvDescription.setTextColor(getContext().getResources().getColor(android.R.color.holo_red_dark));
                tvOK.setClickable(false);
                tvOK.setTextColor(getContext().getResources().getColor(R.color.colorGray));
            } else {
                tvDescription.setText("");
                tvDescription.setTextColor(getContext().getResources().getColor(R.color.colorDescriptionText));
                tvOK.setClickable(true);
                tvOK.setTextColor(getContext().getResources().getColor(R.color.colorPrimary));
            }
        }


        if (item.equals(getContext().getString(R.string.custom_format))) {
            RadioButton r = (RadioButton) v.findViewById(R.id.compound_button);
            final EditText editor = (EditText) dialog.findViewById(R.id.edtCustomFormat);
            r.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    editor.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                    editor.setCursorVisible(isChecked);
                    TextView tvCustomFormatHelp = (TextView) dialog.findViewById(R.id.tvDateTimeFormatHelp);
                    tvCustomFormatHelp.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                    if (isChecked) {
                        String html = getContext().getString(setting instanceof SettingTime ? R.string.custom_time_format_help : R.string.custom_date_format_help);
                        tvCustomFormatHelp.setText(Html.fromHtml(html));
                    }
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (isChecked) {
                        editor.requestFocus();
                        imm.showSoftInput(editor, InputMethodManager.SHOW_IMPLICIT);
                        DateTimeFormatListAdapter.this.notifyDataSetChanged();
                    } else {
                        editor.setText("");
                        imm.hideSoftInputFromWindow(editor.getWindowToken(), 0);
                    }
                }
            });

            editor.addTextChangedListener(new TextWatcher() {

                @Override
                public void afterTextChanged(Editable s) {
                    DateTimeFormatListAdapter.this.notifyDataSetChanged();
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }
            });
        }


        return v;
    }

    private void fillItems() {
        if (setting instanceof SettingDateLong) {
            super.add(getContext().getString(R.string.full_format));
            super.add(getContext().getString(R.string.long_format));
            super.add(getContext().getString(R.string.medium_format));
        } else if (setting instanceof SettingDateShort) {
            super.add(getContext().getString(R.string.medium_format));
            super.add(getContext().getString(R.string.short_format));
        } else if (setting instanceof SettingTime) {
            super.add(getContext().getString(R.string.short_format));
        }

        super.add(getContext().getString(R.string.custom_format));
    }
}
