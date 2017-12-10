package com.example.android.baking.activity;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.example.android.baking.R;
import com.example.android.baking.data.Recipe;
import com.example.android.baking.data.Step;
import com.example.android.baking.fragment.RecipeDetailsFragment;
import com.example.android.baking.fragment.StepDetailsFragment;
import com.example.android.baking.util.DataUtils;
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
                    Step currentStep = null;
                    if (position != -1) {
                        currentStep = steps.get(position);
                    }

                    String url = DataUtils.getUrl(currentStep);
                    if (!TextUtils.isEmpty(url)) {
                        FragmentUtils.addVideoFragment(this, url);
                    }
                    FragmentUtils.addDetailsFragment(this, position, steps);
                }
            }
        }
    }

    @Override
    public void onStepSelected(int position) {
        if (getResources().getBoolean(R.bool.isTablet)) {
            List<Step> steps = recipe.getSteps();
            if (steps != null) {
                Step currentStep = null;
                this.position = position;
                if (this.position != -1) {
                    currentStep = steps.get(this.position);
                }

                String url = DataUtils.getUrl(currentStep);
                if (!TextUtils.isEmpty(url)) {
                    FragmentUtils.replaceVideoFragment(this, url);
                } else {
                    FragmentUtils.removeVideoFragment(this);
                }
                FragmentUtils.replaceDetailsFragment(this, this.position, steps);
            }
        } else {
            List<Step> steps = recipe.getSteps();

            Intent intent = new Intent(this, StepActivity.class);
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