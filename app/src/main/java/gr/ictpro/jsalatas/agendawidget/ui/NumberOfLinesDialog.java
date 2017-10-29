package gr.ictpro.jsalatas.agendawidget.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputFilter;
import android.widget.*;
import gr.ictpro.jsalatas.agendawidget.R;
import gr.ictpro.jsalatas.agendawidget.model.settings.types.Setting;
import gr.ictpro.jsalatas.agendawidget.ui.widgets.SettingDialog;
import gr.ictpro.jsalatas.agendawidget.utils.EmptyTextWatcherWithCheckBox;
import gr.ictpro.jsalatas.agendawidget.utils.Ime;
import gr.ictpro.jsalatas.agendawidget.utils.InputFilterMinMax;

public class NumberOfLinesDialog extends SettingDialog<Integer> implements Dialog.OnShowListener {

    public NumberOfLinesDialog(Activity activity, Setting<Integer> setting) {
        super(activity, setting, R.layout.dialog_number_of_lines);

        setOnShowListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final EditText editNumberOfLines = (EditText) findViewById(R.id.editNumberOfLines);
        editNumberOfLines.setFilters(new InputFilter[]{ new InputFilterMinMax("1", "5")});

        CheckBox chkUnlimited = (CheckBox) findViewById(R.id.chkUnlimited);
        TextView tvOK = (TextView) findViewById(R.id.tvDialogOk);
        editNumberOfLines.addTextChangedListener(new EmptyTextWatcherWithCheckBox(chkUnlimited, tvOK));

        chkUnlimited.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    editNumberOfLines.setText("");
                    editNumberOfLines.setEnabled(false);
                } else {
                    editNumberOfLines.setText("1");
                    editNumberOfLines.setEnabled(true);
                }
            }
        });
        if(setting.getValue() == Integer.MAX_VALUE) {
            chkUnlimited.setChecked(true);
        } else {
            chkUnlimited.setChecked(false);
            editNumberOfLines.setText(setting.getStringValue());
        }
    }

    @Override
    public void onShow(DialogInterface dialog) {
        EditText editNumberOfLines = (EditText) findViewById(R.id.editNumberOfLines);

        Ime.show(editNumberOfLines);
    }

    @Override
    protected Integer getSetting() {
        CheckBox chkUnlimited = (CheckBox) findViewById(R.id.chkUnlimited);
        if(chkUnlimited.isChecked()) {
            return Integer.MAX_VALUE;
        }

        EditText editNumberOfLines = (EditText) findViewById(R.id.editNumberOfLines);
        return Integer.parseInt(editNumberOfLines.getText().toString());
    }
}
