package com.yjs3408.tarifsepeti.activity;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.yjs3408.tarifsepeti.R;
import com.yjs3408.tarifsepeti.dialog.WaitDialog;
import com.yjs3408.tarifsepeti.model.User;

import java.util.Date;

public class SignUpActivity extends AppCompatActivity {

    public static final String TAG = SignUpActivity.class.getName();

    private TextInputLayout firstNameTextInputLayout;
    private TextInputEditText firstNameEditText;
    private TextInputLayout lastNameTextInputLayout;
    private TextInputEditText lastNameEditText;
    private TextInputLayout emailTextInputLayout;
    private TextInputEditText emailEditText;
    private TextInputLayout passwordTextInputLayout;
    private TextInputEditText passwordEditText;
    private Button signUpButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        initializeViews();
        registerViewEvents();
    }

    private void initializeViews() {
        firstNameTextInputLayout = findViewById(R.id.first_name_text_input_layout);
        firstNameEditText = findViewById(R.id.first_name_edit_text);
        lastNameTextInputLayout = findViewById(R.id.last_name_text_input_layout);
        lastNameEditText = findViewById(R.id.last_name_edit_text);
        emailTextInputLayout = findViewById(R.id.email_text_input_layout);
        emailEditText = findViewById(R.id.email_edit_text);
        passwordTextInputLayout = findViewById(R.id.password_text_input_layout);
        passwordEditText = findViewById(R.id.password_edit_text);
        signUpButton = findViewById(R.id.sign_up_button);
    }

    private void registerViewEvents() {
        signUpButton.setOnClickListener(v -> {
            boolean errorExists = false;
            if (firstNameEditText.getText().toString().trim().length() == 0) {
                firstNameTextInputLayout.setError("First name cannot be empty");
                errorExists = true;
            } else {
                firstNameTextInputLayout.setError(null);
            }
            if (lastNameEditText.getText().toString().trim().length() == 0) {
                lastNameTextInputLayout.setError("Last name cannot be empty");
                errorExists = true;
            } else {
                lastNameTextInputLayout.setError(null);
            }
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
                WaitDialog waitDialog = new WaitDialog(SignUpActivity.this);
                waitDialog.show();
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignUpActivity.this, task -> {
                            if (task.isSuccessful()) {
                                User user = new User(FirebaseAuth.getInstance().getUid(), firstNameEditText.getText().toString(), lastNameEditText.getText().toString(), new Date(), true);
                                FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .set(user)
                                        .addOnCompleteListener(task1 -> {
                                            waitDialog.dismiss();
                                            if (task1.isSuccessful()) {
                                                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                intent.putExtra(MainActivity.EXTRA_USER, user);
                                                startActivity(intent);
                                            } else {
                                                Toast.makeText(SignUpActivity.this, task1.getException().getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        });
                            } else {
                                waitDialog.dismiss();
                                Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });
    }
}
