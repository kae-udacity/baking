package com.example.android.baking.util;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.example.android.baking.R;
import com.example.android.baking.data.Step;
import com.example.android.baking.fragment.StepDetailsFragment;
import com.example.android.baking.fragment.StepVideoFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kenneth on 08/12/2017.
 */

public class FragmentUtils {

    public static void addVideoFragment(AppCompatActivity appCompatActivity, String url) {
        FragmentManager fragmentManager = appCompatActivity.getSupportFragmentManager();
        StepVideoFragment stepVideoFragment = (StepVideoFragment) fragmentManager
                .findFragmentByTag(StepVideoFragment.class.getSimpleName());
        if (stepVideoFragment != null) {
            return;
        }

        Bundle bundle = new Bundle();
        bundle.putString(appCompatActivity.getString(R.string.video_url), url);

        fragmentManager.beginTransaction()
                .add(
                        R.id.container_step_video,
                        StepVideoFragment.newInstance(bundle),
                        StepVideoFragment.class.getSimpleName()
                )
                .commit();
    }

    public static void replaceVideoFragment(AppCompatActivity appCompatActivity, String url) {
        Bundle bundle = new Bundle();
        bundle.putString(appCompatActivity.getString(R.string.video_url), url);

        FragmentManager fragmentManager = appCompatActivity.getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(
                        R.id.container_step_video,
                        StepVideoFragment.newInstance(bundle),
                        StepVideoFragment.class.getSimpleName()
                )
                .commit();
    }

    public static void removeVideoFragment(AppCompatActivity appCompatActivity) {
        FragmentManager fragmentManager = appCompatActivity.getSupportFragmentManager();
        StepVideoFragment stepVideoFragment = (StepVideoFragment) fragmentManager
                .findFragmentByTag(StepVideoFragment.class.getSimpleName());
        if (stepVideoFragment != null) {
            fragmentManager.beginTransaction()
                    .remove(stepVideoFragment)
                    .commit();
        }
    }

    public static void addDetailsFragment(
            AppCompatActivity appCompatActivity,
            int position,
            List<Step> steps) {
        FragmentManager fragmentManager = appCompatActivity.getSupportFragmentManager();
        StepDetailsFragment stepDetailsFragment = (StepDetailsFragment) fragmentManager
                .findFragmentByTag(StepDetailsFragment.class.getSimpleName());
        if (stepDetailsFragment != null) {
            return;
        }

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(appCompatActivity.getString(R.string.steps), (ArrayList<Step>) steps);
        bundle.putInt(appCompatActivity.getString(R.string.step_position), position);

        fragmentManager.beginTransaction()
                .add(
                        R.id.container_step_details,
                        StepDetailsFragment.newInstance(bundle),
                        StepDetailsFragment.class.getSimpleName()
                )
                .commit();
    }

    public static void replaceDetailsFragment(
            AppCompatActivity appCompatActivity,
            int position,
            List<Step> steps) {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(
                appCompatActivity.getString(R.string.steps),
                (ArrayList<Step>) steps
        );
        bundle.putInt(appCompatActivity.getString(R.string.step_position), position);

        FragmentManager fragmentManager = appCompatActivity.getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(
                        R.id.container_step_details,
                        StepDetailsFragment.newInstance(bundle),
                        StepDetailsFragment.class.getSimpleName()
                )
                .commit();
    }

    public static void removeDetailsFragment(AppCompatActivity appCompatActivity) {
        FragmentManager fragmentManager = appCompatActivity.getSupportFragmentManager();
        StepDetailsFragment stepDetailsFragment = (StepDetailsFragment) fragmentManager
                .findFragmentByTag(StepDetailsFragment.class.getSimpleName());
        if (stepDetailsFragment != null) {
            fragmentManager.beginTransaction()
                    .remove(stepDetailsFragment)
                    .commit();
        }
    }
}
