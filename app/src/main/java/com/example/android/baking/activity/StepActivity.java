package com.example.android.baking.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;

import com.example.android.baking.util.DataUtils;
import com.example.android.baking.R;
import com.example.android.baking.data.Step;
import com.example.android.baking.fragment.StepDetailsFragment;
import com.example.android.baking.util.FragmentUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Kenneth on 05/12/2017.
 */

public class StepActivity extends AppCompatActivity implements
        StepDetailsFragment.StepDetailsOnClickListener {

    @BindView(R.id.container_step_video)
    FrameLayout frameLayoutContainerStepVideo;

    @BindView(R.id.container_step_details)
    FrameLayout frameLayoutContainerStepDetails;

    private List<Step> steps;
    private String recipeName;
    private int position = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra(getString(R.string.steps))) {
                steps = intent.getParcelableArrayListExtra(getString(R.string.steps));
            }

            if (savedInstanceState != null) {
                position = savedInstanceState.getInt(getString(R.string.step_position));
            } else if (intent.hasExtra(getString(R.string.step_position))) {
                position = intent.getIntExtra(getString(R.string.step_position), position);
            }

            recipeName = intent.getStringExtra(getString(R.string.name));
        }

        if (steps != null ) {
            Step currentStep = null;
            if (position != -1) {
                currentStep = steps.get(position);
            }

            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle(recipeName);
            }

            Configuration configuration = getResources().getConfiguration();
            String url = DataUtils.getUrl(currentStep);
            if (!TextUtils.isEmpty(url)
                    && configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                frameLayoutContainerStepVideo.setVisibility(View.VISIBLE);
                frameLayoutContainerStepDetails.setVisibility(View.VISIBLE);
                FragmentUtils.addVideoFragment(this, url);
                FragmentUtils.addDetailsFragment(this, position, steps);
            } else if (!TextUtils.isEmpty(url)
                && configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                frameLayoutContainerStepVideo.setVisibility(View.VISIBLE);
                frameLayoutContainerStepDetails.setVisibility(View.GONE);
                FragmentUtils.addVideoFragment(this, url);
            } else if (TextUtils.isEmpty(url)) {
                frameLayoutContainerStepVideo.setVisibility(View.GONE);
                frameLayoutContainerStepDetails.setVisibility(View.VISIBLE);
                FragmentUtils.addDetailsFragment(this, position, steps);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(getString(R.string.step_position), position);
    }

    @Override
    public void onStepSelected(int position) {
        if (steps != null ) {
            Step currentStep = null;
            this.position = position;
            if (this.position != -1) {
                currentStep = steps.get(this.position);
            }

            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null && !TextUtils.isEmpty(recipeName)) {
                actionBar.setTitle(recipeName);
            }

            Configuration configuration = getResources().getConfiguration();
            String url = DataUtils.getUrl(currentStep);
            if (!TextUtils.isEmpty(url)
                    && configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                frameLayoutContainerStepVideo.setVisibility(View.VISIBLE);
                frameLayoutContainerStepDetails.setVisibility(View.VISIBLE);
                FragmentUtils.replaceVideoFragment(this, url);
                FragmentUtils.replaceDetailsFragment(this, this.position, steps);
            } else if (!TextUtils.isEmpty(url)
                    && configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                frameLayoutContainerStepVideo.setVisibility(View.VISIBLE);
                frameLayoutContainerStepDetails.setVisibility(View.GONE);
                FragmentUtils.removeDetailsFragment(this);
                FragmentUtils.replaceVideoFragment(this, url);
            } else if (TextUtils.isEmpty(url)) {
                frameLayoutContainerStepVideo.setVisibility(View.GONE);
                frameLayoutContainerStepDetails.setVisibility(View.VISIBLE);
                FragmentUtils.replaceDetailsFragment(this, this.position, steps);
                FragmentUtils.removeVideoFragment(this);
            }
        }
    }
}
