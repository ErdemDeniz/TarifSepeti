package com.yjs3408.tarifsepeti.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDialog;

import com.yjs3408.tarifsepeti.R;

public class CustomDialog extends AppCompatDialog {

    private String title;
    private String description;
    private boolean okEnabled;
    private boolean cancelEnabled;
    private ActionListener listener;

    public CustomDialog(Context context, String title, String description, boolean okEnabled, boolean cancelEnabled, ActionListener listener) {
        super(context);
        this.title = title;
        this.description = description;
        this.okEnabled = okEnabled;
        this.cancelEnabled = cancelEnabled;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_custom);
        setCancelable(false);
        ((TextView) findViewById(R.id.title_text_view)).setText(title);
        ((TextView) findViewById(R.id.desc_text_view)).setText(description);
        findViewById(R.id.ok_text_view).setVisibility(okEnabled ? View.VISIBLE : View.GONE);
        findViewById(R.id.cancel_text_view).setVisibility(cancelEnabled ? View.VISIBLE : View.GONE);
        findViewById(R.id.ok_text_view).setOnClickListener(v -> {
            dismiss();
            if (listener != null) listener.actionResult(ActionListener.ActionResult.OK);
        });
        findViewById(R.id.cancel_text_view).setOnClickListener(v -> {
            dismiss();
            if (listener != null) listener.actionResult(ActionListener.ActionResult.CANCEL);
        });
    }

    public interface ActionListener {
        enum ActionResult {
            OK, CANCEL
        }

        void actionResult(ActionResult result);
    }
}
