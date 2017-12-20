package com.example.android.baking.activity;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.example.android.baking.R;
import com.example.android.baking.data.Recipe;
import com.example.android.baking.data.Step;
import com.example.android.baking.fragment.RecipeDetailsFragment;
import com.example.android.baking.fragment.StepDetailsFragment;
import com.example.android.baking.util.FragmentUtils;

import java.util.ArrayList;
import java.util.List;

public class RecipeDetailsActivity extends AppCompatActivity implements
        RecipeDetailsFragment.RecipeDetailsOnClickListener,
        StepDetailsFragment.StepDetailsOnClickListener {

    private Recipe recipe;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);

        Intent intent = getIntent();
        if (intent != null) {
            recipe = intent.getParcelableExtra(getString(R.string.recipe));
        }

        if (recipe != null) {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle(recipe.getName());
            }

            FragmentManager fragmentManager = getSupportFragmentManager();
            if (fragmentManager.findFragmentByTag(RecipeDetailsFragment.class.getSimpleName()) != null) {
                return;
            }
            Bundle bundle = new Bundle();
            bundle.putParcelable(getString(R.string.recipe), recipe);

            fragmentManager.beginTransaction()
                    .add(
                            R.id.container_recipe_details,
                            RecipeDetailsFragment.newInstance(bundle),
                            RecipeDetailsFragment.class.getSimpleName()
                    )
                    .commit();

            if (getResources().getBoolean(R.bool.isTablet)) {
                List<Step> steps = recipe.getSteps();
                if (steps != null ) {
                    bundle = new Bundle();
                    bundle.putParcelableArrayList(getString(R.string.steps), (ArrayList<Step>) steps);
                    bundle.putInt(getString(R.string.step_position), position);
                    FragmentUtils.addDetailsFragment(this, bundle);
                }
            }
        }
    }

    @Override
    public void onStepSelected(int position) {
        if (getResources().getBoolean(R.bool.isTablet)) {
            List<Step> steps = recipe.getSteps();
            if (steps != null) {
                this.position = position;

                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList(getString(R.string.steps), (ArrayList<Step>) steps);
                bundle.putInt(getString(R.string.step_position), position);
                FragmentUtils.replaceDetailsFragment(this, bundle);
            }
        } else {
            List<Step> steps = recipe.getSteps();

            Intent intent = new Intent(this, StepDetailsActivity.class);
            intent.putParcelableArrayListExtra(getString(R.string.steps), (ArrayList<Step>) steps);
            intent.putExtra(getString(R.string.step_position), position);

            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                intent.putExtra(getString(R.string.name), actionBar.getTitle());
            }
            startActivity(intent);
        }
    }
}