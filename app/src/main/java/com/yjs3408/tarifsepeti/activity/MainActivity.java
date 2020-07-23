package com.yjs3408.tarifsepeti.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;
import com.yjs3408.tarifsepeti.R;
import com.yjs3408.tarifsepeti.dialog.CustomDialog;
import com.yjs3408.tarifsepeti.model.Recipe;
import com.yjs3408.tarifsepeti.model.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.yjs3408.tarifsepeti.dialog.CustomDialog.*;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getName();
    public static final String EXTRA_USER = MainActivity.class.getName() + "_EXTRA_USER";
    public static final String EXTRA_RECIPE = MainActivity.class.getName() + "_EXTRA_RECIPE";
    public static final String EXTRA_CATEGORY = MainActivity.class.getName() + "_EXTRA_CATEGORY";

    private User user;
    private ImageView accountImageView;
    private ImageView mainCourseImageView;
    private ImageView saladImageView;
    private ImageView sweetImageView;
    private ImageView soupImageView;
    private Recipe recipe;
    private Spinner spinner;

    List<String> recipeNameList;
    List<Recipe> recipeList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar(findViewById(R.id.activity_main_toolbar));
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        initializeViews();
        user = (User) getIntent().getSerializableExtra(EXTRA_USER);
        recipe = (Recipe) getIntent().getSerializableExtra(EXTRA_RECIPE);
    }

    private void initializeViews() {
        accountImageView = findViewById(R.id.activity_main_account_image_view);
        spinner = findViewById(R.id.searchable_spinner);
        mainCourseImageView = findViewById(R.id.main_cours_text_view);
        saladImageView = findViewById(R.id.salad_text_view);
        sweetImageView = findViewById(R.id.sweet_text_view);
        soupImageView = findViewById(R.id.soup_text_view);
    }

    @Override
    protected void onStart() {
        super.onStart();
//        ((TextView) findViewById(R.id.activity_main_toolbar_title_text_view)).setText(String.format("%s %s", user.getFirstName(), user.getLastName()));
        findViewById(R.id.activity_main_sign_out_image_button).setOnClickListener(v -> {
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                new CustomDialog(MainActivity.this, "Are you sure?", "Do you want to sign out?", true, true, (ActionListener.ActionResult result) -> {
                    if (result == ActionListener.ActionResult.OK) {
                        signOut();
                    }
                }).show();
            }
        });


        accountImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AccountActivity.class);
                intent.putExtra(AccountActivity.EXTRA_USER, user);
                startActivity(intent);
            }
        });

        mainCourseImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RecipesActivity.class);
                intent.putExtra(RecipesActivity.EXTRA_RECIPE, recipe);
                String categorySet = "Ana Yemek";
                intent.putExtra(MainActivity.EXTRA_CATEGORY, categorySet);
                startActivity(intent);
            }
        });

        saladImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RecipesActivity.class);
                intent.putExtra(RecipesActivity.EXTRA_RECIPE, recipe);
                String categorySet = "Salata";
                intent.putExtra(MainActivity.EXTRA_CATEGORY, categorySet);
                startActivity(intent);
            }
        });

        sweetImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RecipesActivity.class);
                intent.putExtra(RecipesActivity.EXTRA_RECIPE, recipe);
                String categorySet = "Tatlı";
                intent.putExtra(MainActivity.EXTRA_CATEGORY, categorySet);
                startActivity(intent);
            }
        });

        soupImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RecipesActivity.class);
                intent.putExtra(RecipesActivity.EXTRA_RECIPE, recipe);
                String categorySet = "Çorba";
                intent.putExtra(MainActivity.EXTRA_CATEGORY, categorySet);
                startActivity(intent);
            }
        });


        recipeNameList = new ArrayList<>();
        allRecipes();
        spinner.setAdapter(new ArrayAdapter<>(MainActivity.this,
                android.R.layout.simple_spinner_dropdown_item, recipeNameList));

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                for (int i = 0; i<recipeList.size(); i++){
                    recipe = recipeList.get(i);
                    if (recipe.getName().equals(parent.getItemAtPosition(position))){
                        Intent intent = new Intent(MainActivity.this, RecipeActivity.class);
                        intent.putExtra(MainActivity.EXTRA_RECIPE, recipe);
                        startActivity(intent);
                        finish();
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    private void allRecipes() {
        FirebaseFirestore.getInstance().collection("recipes").get()
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        List<DocumentSnapshot> documents = task.getResult().getDocuments();
                        for (DocumentSnapshot document : documents) {
                            recipeList.add(new Recipe(document.getId(),
                                document.getString("name"),
                                document.getDate("createdTime"),
                                document.getString("category"),
                                document.getString("ingredients"),
                                document.getString("description"),
                                document.getString("url")));
                        }

                        for (int i = 0; i < recipeList.size(); i++){
                            if (recipeNameList.contains(recipeList.get(i).getName())){
                            }else {
                                recipeNameList.add(recipeList.get(i).getName());
                            }
                        }
                    } else {
                        Log.e(TAG, "onComplete: ", task.getException());
                    }
                });
    }

    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void onBackPressed(){
        finish();
    }
}
