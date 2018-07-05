package com.upday.shutterdemo.pickyup;

import android.content.Context;
import android.content.Intent;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import com.upday.shutterdemo.pickyup.ui.MainActivity;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasData;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.AllOf.allOf;

@RunWith(AndroidJUnit4.class)
public class MenuItemOpensWebActivityTest {

    @Rule
    public IntentsTestRule<MainActivity> intentsTestRule = new IntentsTestRule<>(MainActivity.class);

    private Context context = getInstrumentation().getTargetContext();

    @SuppressWarnings({"unchecked", "ResultOfMethodCallIgnored"})
    @Test
    public void menuItemOpensAnActivity() {
        onView(withId(R.id.vpImages)).perform(swipeLeft());

        onView(isRoot()).perform(waitFor(1500));

        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());

        onView(withText(R.string.report_title)).perform(click());

        intending(chooser(allOf(hasAction(Intent.ACTION_SENDTO), hasData(context.getString(R.string.mail_address)))));
    }

    // Check chooser
    public Matcher<Intent> chooser(Matcher<Intent> matcher) {
        return allOf(hasAction(Intent.ACTION_CHOOSER), hasData(context.getString(R.string.mail_address)), matcher);
    }

    // Wait for the swipe
    public static ViewAction waitFor(final long millis) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isRoot();
            }

            @Override
            public String getDescription() {
                return "Wait for " + millis + " milliseconds.";
            }

            @Override
            public void perform(UiController uiController, final View view) {
                uiController.loopMainThreadForAtLeast(millis);
            }
        };
    }
}