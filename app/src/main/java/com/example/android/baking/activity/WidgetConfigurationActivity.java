package com.example.android.baking.activity;

import android.app.LoaderManager;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
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
import com.example.android.baking.widget.RecipeWidgetProvider;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WidgetConfigurationActivity extends AppCompatActivity implements
        RecipeAdapter.RecipeOnClickHandler {

    private static final int WIDGET_RECIPE_LOADER_ID = 0;

    @BindView(R.id.widget_recycler_view_recipes)
    RecyclerView recyclerViewRecipes;

    @BindView(R.id.widget_empty_view)
    TextView emptyView;

    @BindView(R.id.widget_loading_indicator)
    ProgressBar loadingIndicator;

    private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private RecipeAdapter recipeAdapter;

    private LoaderManager.LoaderCallbacks<List<Recipe>> recipeLoaderCallbacks =
            new LoaderManager.LoaderCallbacks<List<Recipe>>() {

        @Override
        public RecipeLoader onCreateLoader(int i, Bundle bundle) {
            loadingIndicator.setVisibility(View.VISIBLE);
            return new RecipeLoader(WidgetConfigurationActivity.this);
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
        setContentView(R.layout.activity_widget_configuration);
        ButterKnife.bind(this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Choose a recipe");
        }

        setResult(RESULT_CANCELED);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            appWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        recipeAdapter = new RecipeAdapter(this, this);
        recyclerViewRecipes.setAdapter(recipeAdapter);

        int spanCount = 1;
        if (getResources().getBoolean(R.bool.isTablet)) {
            spanCount = 3;
        }

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, spanCount);
        recyclerViewRecipes.setLayoutManager(layoutManager);
        if (NetworkUtils.isOnline(this)) {
            getLoaderManager().initLoader(WIDGET_RECIPE_LOADER_ID, null, recipeLoaderCallbacks);
        } else {
            loadingIndicator.setVisibility(View.GONE);
            recyclerViewRecipes.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
            emptyView.setText(R.string.no_internet_connection);
        }
    }

    @Override
    public void onClick(Recipe recipe) {
        if (recipe == null) {
            return;
        }

        SharedPreferences sharedPreferences = getSharedPreferences(
                getString(R.string.baking_preferences),
                0
        );
        String serializedRecipe = recipe.serialize();
        sharedPreferences
                .edit()
                .putString(getString(R.string.serialized_recipe), serializedRecipe)
                .apply();

        Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE, null, this, RecipeWidgetProvider.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, new int[]{appWidgetId});
        setResult(RESULT_OK, intent);
        sendBroadcast(intent);
        finish();
    }
}
