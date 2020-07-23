package com.yjs3408.tarifsepeti.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatDialog;

import com.yjs3408.tarifsepeti.R;


public class WaitDialog extends AppCompatDialog {

    public WaitDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_wait);
        setCancelable(false);
        findViewById(R.id.logo_image_view).setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.bounce_animation));
    }
}
