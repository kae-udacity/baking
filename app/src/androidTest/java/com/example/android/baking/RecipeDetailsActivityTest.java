package com.example.android.baking;

import android.content.Context;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.android.baking.activity.MainActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withResourceName;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

/**
 * Tests the UI of the RecipeDetailsActivity.
 */

@RunWith(AndroidJUnit4.class)
public class RecipeDetailsActivityTest {

    private static final String BROWNIES = "Brownies";
    private static final String STEP_DESCRIPTION = "Recipe Introduction";

    @Rule
    public ActivityTestRule<MainActivity> activityTestRule =
            new ActivityTestRule<>(MainActivity.class);

    private Context context;

    @Before
    public void getContext() {
        context = activityTestRule.getActivity();
    }

    @Test
    public void clickOnStep_DisplaysCorrectStep() {
        onView(withId(R.id.recycler_view_recipes))
                .perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));

        onView(withId(R.id.recycler_view_steps))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        onView(allOf(isDescendantOfA(withResourceName(context.getString(R.string.action_bar))), withText(BROWNIES)))
                .check(matches(isDisplayed()));

        onView(withId(R.id.text_view_step_description)).check(matches(withText(STEP_DESCRIPTION)));
    }
}
