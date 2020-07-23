package com.yjs3408.tarifsepeti.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.yjs3408.tarifsepeti.R;
import com.yjs3408.tarifsepeti.dialog.WaitDialog;
import com.yjs3408.tarifsepeti.model.Recipe;
import com.yjs3408.tarifsepeti.model.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RecipesActivity extends AppCompatActivity {
    public static final String TAG = RecipesActivity.class.getName();
    public static final String EXTRA_USER = RecipesActivity.class.getName() + "_EXTRA_USER";
    public static final String EXTRA_RECIPE = RecipesActivity.class.getName() + "_EXTRA_RECIPE";
    public static final String EXTRA_CATEGORY = RecipesActivity.class.getName() + "_EXTRA_CATEGORY";

    private RecyclerView recipesRecyclerView;
    private User user;
    private Recipe recipe;
    private String categorySet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipes);
        setSupportActionBar(findViewById(R.id.activity_recipes_toolbar));
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        recipesRecyclerView = findViewById(R.id.recipes_recycler_view);
        recipesRecyclerView.setHasFixedSize(true);
        recipesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        recipesRecyclerView.setItemAnimator(new DefaultItemAnimator());

        user = (User) getIntent().getSerializableExtra(EXTRA_USER);
        recipe = (Recipe) getIntent().getSerializableExtra(EXTRA_RECIPE);

    }

    @Override
    protected void onStart() {
        super.onStart();

        WaitDialog waitDialog = new WaitDialog(this);
        waitDialog.show();
        FirebaseFirestore.getInstance().collection("recipes").get()
                .addOnCompleteListener(this, task -> {
                    waitDialog.dismiss();
                    if (task.isSuccessful()) {
                        List<Recipe> recipeList = new ArrayList<>();
                        List<DocumentSnapshot> documents = task.getResult().getDocuments();
                        List<Recipe> recipeListSort = new ArrayList<>();
                        if ((categorySet = getIntent().getStringExtra(MainActivity.EXTRA_CATEGORY)).equals(categorySet)){
                            for (DocumentSnapshot document : documents) {
                                recipeListSort.add(new Recipe(document.getId(),
                                        document.getString("name"),
                                        document.getDate("createdTime"),
                                        document.getString("category"),
                                        document.getString("ingredients"),
                                        document.getString("description"),
                                        document.getString("url")));
                            }
                            for (int i = 0; i<recipeListSort.size(); i++){
                                recipe = recipeListSort.get(i);
                                if (recipe.getCategory().equals(categorySet)){
                                    recipeList.add(recipe);
                                }
                            }
                            Collections.sort(recipeList, (first, second) -> first.getName().compareTo(second.getName()));
                            recipesRecyclerView.setAdapter(new RecyclerViewAdapter(RecipesActivity.this, recipeList, recipe -> {
                                Intent intent = new Intent(RecipesActivity.this, RecipeActivity.class);
                                intent.putExtra(RecipesActivity.EXTRA_RECIPE, recipe);
                                startActivity(intent);
                                finish();
                            }));
                        }

                    } else {
                        Log.e(TAG, "onComplete: ", task.getException());
                    }
                });
        recipesRecyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: " + v);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ((RecyclerViewAdapter) recipesRecyclerView.getAdapter()).getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }

    private static class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RowHolder> implements Filterable {

        private Context context;
        private List<Recipe> recipeList;
        private List<Recipe> recipeSearchList;
        private OnItemClickListener listener;

        public RecyclerViewAdapter(Context context, List<Recipe> recipeList, OnItemClickListener listener) {
            this.context = context;
            this.recipeList = recipeList;
            this.listener = listener;
            recipeSearchList = new ArrayList<>(recipeList);
        }

        @Override
        public Filter getFilter() {
            return recipeListFilter;
        }

        private Filter recipeListFilter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<Recipe> filteredList = new ArrayList<>();
                if (constraint == null || constraint.length() == 0) {
                    filteredList.addAll(recipeSearchList);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    for (Recipe recipe : recipeSearchList) {
                        if (recipe.getName().toLowerCase().contains(filterPattern)) {
                            filteredList.add(recipe);
                        }
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                recipeList.clear();
                recipeList.addAll((List) results.values);
                notifyDataSetChanged();
            }
        };

        @NonNull
        @Override
        public RowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new RowHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_recipe, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull RowHolder holder, int position) {
            Recipe recipe = recipeList.get(position);
            holder.bind(context, recipe, listener);
        }

        @Override
        public int getItemCount() {
            return recipeList.size();
        }


        public static class RowHolder extends RecyclerView.ViewHolder {

            ImageView iconImageView;
            TextView nameTextView;
            TextView createdTimeTextView;
            TextView categoryTextView;

            public RowHolder(@NonNull View itemView) {
                super(itemView);
                iconImageView = itemView.findViewById(R.id.icon_image_view);
                nameTextView = itemView.findViewById(R.id.name_text_view);
                createdTimeTextView = itemView.findViewById(R.id.created_time_text_view);
                categoryTextView = itemView.findViewById(R.id.category_text_view);
            }

            public void bind(Context context, Recipe recipe, OnItemClickListener listener) {
                nameTextView.setText(recipe.getName());
                createdTimeTextView.setText(new SimpleDateFormat("dd-MM-yyyy").format(recipe.getCreatedTime()));
                Glide.with(context).load(recipe.getUrl()).into(iconImageView);
                categoryTextView.setText(String.valueOf(recipe.getCategory()));
                itemView.setOnClickListener(v -> listener.onItemClick(recipe));
            }
        }

        public interface OnItemClickListener {
            void onItemClick(Recipe recipe);
        }
    }
}
