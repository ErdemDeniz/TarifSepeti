package com.yjs3408.tarifsepeti.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.yjs3408.tarifsepeti.R;
import com.yjs3408.tarifsepeti.model.Recipe;

public class RecipeActivity extends AppCompatActivity {

    public static final String TAG = RecipeActivity.class.getName();
    public static final String EXTRA_RECIPE = RecipeActivity.class.getName() + "_EXTRA_RECIPE";

    private Recipe recipeMain, recipe;

    private TextView titleTextView;
    private TextView categoryTextView;
    private TextView ingredientsTextView;
    private TextView descriptionTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
        initializeViews();
    }

    private void initializeViews() {
        titleTextView = findViewById(R.id.title_text_view);
        categoryTextView = findViewById(R.id.category_text_view);
        ingredientsTextView = findViewById(R.id.ingredients_text_view);
        descriptionTextView = findViewById(R.id.description_text_view);

        recipe = (Recipe) getIntent().getSerializableExtra(RecipesActivity.EXTRA_RECIPE);
        recipeMain = (Recipe) getIntent().getSerializableExtra(MainActivity.EXTRA_RECIPE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (recipeMain != null){
            titleTextView.setText(recipeMain.getName());
            categoryTextView.setText(String.valueOf(recipeMain.getCategory()));
            ingredientsTextView.setText(recipeMain.getIngredients());
            descriptionTextView.setText(recipeMain.getDescription());
        } else{
            titleTextView.setText(recipe.getName());
            categoryTextView.setText(String.valueOf(recipe.getCategory()));
            ingredientsTextView.setText(recipe.getIngredients());
            descriptionTextView.setText(recipe.getDescription());
        }
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(RecipeActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
