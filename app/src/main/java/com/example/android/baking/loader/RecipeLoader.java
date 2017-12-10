package com.example.android.baking.loader;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.example.android.baking.data.Recipe;
import com.example.android.baking.util.NetworkUtils;

import java.util.List;

/**
 * Created by Kenneth on 04/12/2017.
 */

public class RecipeLoader extends AsyncTaskLoader<List<Recipe>> {

    private List<Recipe> recipes;

    public RecipeLoader(Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        if (recipes != null) {
            deliverResult(recipes);
        } else {
            forceLoad();
        }
    }

    @Override
    public List<Recipe> loadInBackground() {
        return NetworkUtils.fetchRecipes(getContext());
    }

    @Override
    public void deliverResult(List<Recipe> recipes) {
        this.recipes = recipes;
        super.deliverResult(recipes);
    }
}
