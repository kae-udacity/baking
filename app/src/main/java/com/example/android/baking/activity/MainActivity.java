package com.example.android.baking.activity;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.baking.R;
import com.example.android.baking.adapter.RecipeAdapter;
import com.example.android.baking.data.Recipe;
import com.example.android.baking.loader.RecipeLoader;
import com.example.android.baking.util.NetworkUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements
        RecipeAdapter.RecipeOnClickHandler {

    @BindView(R.id.recycler_view_recipes)
    RecyclerView recyclerViewRecipes;

    @BindView(R.id.empty_view)
    TextView emptyView;

    @BindView(R.id.loading_indicator)
    ProgressBar loadingIndicator;

    private static final int RECIPE_LOADER_ID = 0;
    private RecipeAdapter recipeAdapter;

    private LoaderManager.LoaderCallbacks<List<Recipe>> recipeLoaderCallbacks = new LoaderManager.LoaderCallbacks<List<Recipe>>() {

        @Override
        public RecipeLoader onCreateLoader(int i, Bundle bundle) {
            loadingIndicator.setVisibility(View.VISIBLE);
            return new RecipeLoader(MainActivity.this);
        }

        @Override
        public void onLoadFinished(Loader<List<Recipe>> loader, List<Recipe> recipes) {
            loadingIndicator.setVisibility(View.GONE);
            recipeAdapter.clear();
            recipeAdapter.setRecipes(recipes);
            recipeAdapter.notifyDataSetChanged();
        }

        @Override
        public void onLoaderReset(Loader<List<Recipe>> loader) {
            recipeAdapter.clear();
            recipeAdapter.notifyDataSetChanged();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        recipeAdapter = new RecipeAdapter(this, this);
        recyclerViewRecipes.setAdapter(recipeAdapter);

        int spanCount = 1;
        if (getResources().getBoolean(R.bool.isTablet)) {
            spanCount = 3;
        }

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, spanCount);
        recyclerViewRecipes.setLayoutManager(layoutManager);

        if (NetworkUtils.isOnline(this)) {
            getLoaderManager().initLoader(RECIPE_LOADER_ID, null, recipeLoaderCallbacks);
        } else {
            loadingIndicator.setVisibility(View.GONE);
            recyclerViewRecipes.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
            emptyView.setText(R.string.no_internet_connection);
        }
    }

    @Override
    public void onClick(Recipe recipe) {
        Intent intent = new Intent(this, RecipeDetailsActivity.class);
        intent.putExtra("recipe", recipe);
        startActivity(intent);
    }
}