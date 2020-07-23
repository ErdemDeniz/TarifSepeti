package com.yjs3408.tarifsepeti.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.yjs3408.tarifsepeti.R;
import com.yjs3408.tarifsepeti.dialog.WaitDialog;
import com.yjs3408.tarifsepeti.model.Recipe;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;

public class AddRecipeActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public static final String TAG = AddRecipeActivity.class.getName();
    public static final String EXTRA_USER = AddRecipeActivity.class.getName() + "_EXTRA_USER";
    //public static final String EXTRA_RECIPE = AddRecipeActivity.class.getName() + "_EXTRA_RECIPE";

    private final int SELECTED_PICTURE = 71;
    private Uri filePath;

    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    private TextInputLayout recipeNameTextInputLayout;
    private TextInputEditText recipeNameEditText;
    private Spinner spinner;
    private String spinnerText;
    private ImageView selectRecipeView;
    private TextInputLayout recipeIngredientsTextInputLayout;
    private TextInputEditText recipeIngredientsEditText;
    private TextInputLayout recipeDescriptionTextInputLayout;
    private TextInputEditText recipeDescriptionEditText;
    private Button saveButton;

    String recipeImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);
        initializeViews();
        registerViewEvents();
        selectSpinner();
    }

    private void initializeViews() {
        recipeNameTextInputLayout = findViewById(R.id.recipe_name_text_input_layout);
        recipeNameEditText = findViewById(R.id.recipe_name_edit_text);
        spinner = findViewById(R.id.category_spinner);
        selectRecipeView = findViewById(R.id.recipe_image_view);
        recipeIngredientsTextInputLayout = findViewById(R.id.recipe_ingredients_text_input_layout);
        recipeIngredientsEditText = findViewById(R.id.recipe_ingredients_edit_text);
        recipeDescriptionTextInputLayout = findViewById(R.id.recipe_description_text_input_layout);
        recipeDescriptionEditText = findViewById(R.id.recipe_description_edit_text);
        saveButton = findViewById(R.id.save_button);

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
    }

    private void registerViewEvents() {
        selectRecipeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean errorExists = false;
                if (recipeNameEditText.getText().toString().trim().length() == 0) {
                    recipeNameTextInputLayout.setError("Recipe name cannot be empty");
                    errorExists = true;
                } else {
                    recipeNameTextInputLayout.setError(null);
                }
                if (recipeIngredientsEditText.getText().toString().trim().length() == 0) {
                    recipeIngredientsTextInputLayout.setError("Ingredients cannot be empty");
                    errorExists = true;
                } else {
                    recipeIngredientsTextInputLayout.setError(null);
                }
                if (recipeDescriptionEditText.getText().toString().trim().length() == 0) {
                    recipeDescriptionTextInputLayout.setError("Description cannot be empty");
                    errorExists = true;
                } else {
                    recipeDescriptionTextInputLayout.setError(null);
                }
                if (Boolean.FALSE.equals(errorExists)) {
                    WaitDialog waitDialog = new WaitDialog(AddRecipeActivity.this);
                    waitDialog.show();
                    uploadImage();
                    String recipeName = recipeNameEditText.getText().toString();
                    String recipeIngredients = recipeIngredientsEditText.getText().toString();
                    String recipeDescription = recipeDescriptionEditText.getText().toString();

                    Recipe recipe = new Recipe(FirebaseAuth.getInstance().getUid(), recipeName, new Date(), spinnerText, recipeIngredients, recipeDescription, recipeImageUrl);
                    Log.d(TAG, recipeImageUrl);
                    FirebaseFirestore.getInstance()
                            .collection("recipes")
                            .document().set(recipe)
                            .addOnCompleteListener(task -> {
                                waitDialog.dismiss();
                                if (task.isSuccessful()){
                                    finish();
                                } else {
                                    Log.e(TAG, "onComplete: ", task.getException());
                                }
                    });
                }
            }
        });
    }

    private void uploadImage() {
        if (filePath != null){
            StorageReference reference = storageReference.child("recipeImages/"+ UUID.randomUUID().toString());
            reference.putFile(filePath);
            recipeImageUrl = String.valueOf(reference);
        }
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser((intent), "Select Picture"), SELECTED_PICTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECTED_PICTURE && resultCode == RESULT_OK && data != null && data.getData() != null){
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                selectRecipeView.setImageBitmap(bitmap);
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    private void selectSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.category, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        spinnerText = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
