package com.yjs3408.tarifsepeti.activity;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.animation.AnimationUtils;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.yjs3408.tarifsepeti.R;
import com.yjs3408.tarifsepeti.dialog.CustomDialog;
import com.yjs3408.tarifsepeti.model.User;

public class SplashActivity extends AppCompatActivity {

    public static final String TAG = SplashActivity.class.getName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        findViewById(R.id.sheff_image_view).startAnimation(AnimationUtils.loadAnimation(this, R.anim.bounce_animation));
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Handler().postDelayed(() -> {
            switch (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(SplashActivity.this)) {
                case ConnectionResult.SUCCESS:
                    if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                        FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .get()
                                .addOnCompleteListener(task1 -> {
                                    DocumentSnapshot documentSnapshot = task1.getResult();
                                    User user = new User(
                                            FirebaseAuth.getInstance().getCurrentUser().getUid(),
                                            documentSnapshot.getString("firstName"),
                                            documentSnapshot.getString("lastName"),
                                            documentSnapshot.getDate("joinDate"),
                                            documentSnapshot.getBoolean("enabled")
                                    );
                                    if (task1.isSuccessful()) {
                                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        intent.putExtra(MainActivity.EXTRA_USER, user);
                                        startActivity(intent);
                                    } else {
                                        Log.e(TAG, "onResume: ", task1.getException());
                                    }
                                });
                    } else {
                        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                    break;
                case ConnectionResult.SERVICE_MISSING:
                    new CustomDialog(SplashActivity.this, "Error", "Google Play Services missing. Please install Google Play Services", true, false, result -> {
                        if (result == CustomDialog.ActionListener.ActionResult.OK) finish();
                    }).show();
                    break;
                case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
                    new CustomDialog(SplashActivity.this, "Error", "Google Play Services update required", true, false, result -> {
                        if (result == CustomDialog.ActionListener.ActionResult.OK) finish();
                    }).show();
                    break;
                case ConnectionResult.SERVICE_DISABLED:
                    new CustomDialog(SplashActivity.this, "Error", "Google Play Services disabled. Please enable Google Play Services", true, false, result -> {
                        if (result == CustomDialog.ActionListener.ActionResult.OK) finish();
                    }).show();
                    break;
                case ConnectionResult.SERVICE_INVALID:
                    new CustomDialog(SplashActivity.this, "Error", "Invalid Google Play Services. Please check Google Play Services", true, false, result -> {
                        if (result == CustomDialog.ActionListener.ActionResult.OK) finish();
                    }).show();
                    break;
            }
        }, 1000);
    }
}
