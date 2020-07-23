package com.yjs3408.tarifsepeti.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.yjs3408.tarifsepeti.R;
import com.yjs3408.tarifsepeti.dialog.WaitDialog;
import com.yjs3408.tarifsepeti.model.User;

public class LoginActivity extends AppCompatActivity {
    private TextInputLayout emailTextInputLayout;
    private TextInputEditText emailEditText;
    private TextInputLayout passwordTextInputLayout;
    private TextInputEditText passwordEditText;
    private Button loginButton;
    private TextView signUpTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initializeViews();
        registerViewEvents();
    }

    private void initializeViews() {
        emailTextInputLayout = findViewById(R.id.email_text_input_layout);
        emailEditText = findViewById(R.id.username_edit_text);
        passwordTextInputLayout = findViewById(R.id.password_text_input_layout);
        passwordEditText = findViewById(R.id.password_edit_text);
        loginButton = findViewById(R.id.login_button);
        signUpTextView = findViewById(R.id.sign_up_text_view);
    }

    private void registerViewEvents() {
        loginButton.setOnClickListener(v -> {
            boolean errorExists = false;
            if (emailEditText.getText().toString().trim().length() == 0) {
                emailTextInputLayout.setError("Username cannot be empty");
                errorExists = true;
            } else {
                emailTextInputLayout.setError(null);
            }
            if (passwordEditText.getText().toString().trim().length() == 0) {
                passwordTextInputLayout.setError("Password cannot be empty");
                errorExists = true;
            } else {
                passwordTextInputLayout.setError(null);
            }
            if (Boolean.FALSE.equals(errorExists)) {
                WaitDialog waitDialog = new WaitDialog(LoginActivity.this);
                waitDialog.show();
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .get()
                                        .addOnCompleteListener(task1 -> {
                                            waitDialog.dismiss();
                                            DocumentSnapshot documentSnapshot = task1.getResult();
                                            User user = new User(
                                                    FirebaseAuth.getInstance().getCurrentUser().getUid(),
                                                    documentSnapshot.getString("firstName"),
                                                    documentSnapshot.getString("lastName"),
                                                    documentSnapshot.getDate("joinDate"),
                                                    documentSnapshot.getBoolean("enabled")
                                            );
                                            if (task1.isSuccessful()) {
                                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                intent.putExtra(MainActivity.EXTRA_USER, user);
                                                startActivity(intent);
                                            } else {
                                                Toast.makeText(LoginActivity.this, task1.getException().getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        });
                            } else {
                                Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });
        signUpTextView.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, SignUpActivity.class)));
    }
}
