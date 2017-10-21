package gr.ictpro.jsalatas.agendawidget.ui.widgets;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.view.View;
import android.widget.TextView;
import gr.ictpro.jsalatas.agendawidget.R;
import gr.ictpro.jsalatas.agendawidget.model.settings.types.Setting;

abstract public class SettingDialog<T> extends Dialog {
    protected final Setting<T> setting;
    private @LayoutRes final int layoutResId;

    protected SettingDialog(Context context, Setting<T> setting, @LayoutRes int layoutResId) {
        super(context);
        this.layoutResId = layoutResId;
        this.setting = setting;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layoutResId);
        TextView tvOK = (TextView)findViewById(R.id.tvDialogOk);

        tvOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingDialog.this.setting.setValue(getSetting());
                SettingDialog.this.cancel();
            }
        });
    }

    abstract protected T getSetting();
}
