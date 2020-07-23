package com.yjs3408.tarifsepeti.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.yjs3408.tarifsepeti.R;
import com.yjs3408.tarifsepeti.model.User;

public class AccountActivity extends AppCompatActivity {

    public static final String TAG = AccountActivity.class.getName();
    public static final String EXTRA_USER = AccountActivity.class.getName() + "_EXTRA_USER";

    private User user;
    private Button addRecipeButton;
    private TextView firstNameTextView;
    private TextView lastNameTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        user = (User) getIntent().getSerializableExtra(EXTRA_USER);
        addRecipeButton = findViewById(R.id.add_recipe_button);
        firstNameTextView = findViewById(R.id.first_name_text_view);
        lastNameTextView = findViewById(R.id.last_name_text_view);
    }

    @Override
    protected void onStart() {
        super.onStart();
        firstNameTextView.setText(String.format("%s", user.getFirstName()));
        lastNameTextView.setText(String.format("%s", user.getLastName()));
        addRecipeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountActivity.this, AddRecipeActivity.class);
                intent.putExtra(AddRecipeActivity.EXTRA_USER, user);
                startActivity(intent);
            }
        });
    }
}
