package com.recorder.yma.audiorecorder.login;


import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.recorder.yma.audiorecorder.R;
import com.recorder.yma.audiorecorder.util.EspressoIdlingResource;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.pressImeActionButton;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;

@LargeTest
@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LoginActivityTest {

    @Rule
    public ActivityTestRule<LoginActivity> mActivityTestRule = new ActivityTestRule<>(LoginActivity.class);

    private IdlingResource mIdlingResource;
    @Before
    public void setup() {
        // To prove that the test fails, omit this call:
        Espresso.registerIdlingResources(EspressoIdlingResource.getIdlingResource());
    }

    @Test
    public void t1TestInserUser() {

        onView(withId(R.id.loginButton)).check(matches(not(isEnabled())));
        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.userEditText),
                        withParent(allOf(withId(R.id.activity_main),
                                withParent(withId(R.id.contentFrame)))),
                        isDisplayed()));
        appCompatEditText.perform(replaceText("teest"), closeSoftKeyboard());
        onView(withId(R.id.loginButton)).check(matches(not(isEnabled())));



    }

    @Test
    public void t2TestloginSuccess() {
        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.userEditText),
                        withParent(allOf(withId(R.id.activity_main),
                                withParent(withId(R.id.contentFrame)))),
                        isDisplayed()));
        appCompatEditText.perform(replaceText("teest"), closeSoftKeyboard());

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.passwordEditText),
                        withParent(allOf(withId(R.id.activity_main),
                                withParent(withId(R.id.contentFrame)))),
                        isDisplayed()));
        appCompatEditText2.perform(replaceText("ggxcvvvvvv"), closeSoftKeyboard());

        ViewInteraction appCompatEditText3 = onView(
                allOf(withId(R.id.passwordEditText), withText("ggxcvvvvvv"),
                        withParent(allOf(withId(R.id.activity_main),
                                withParent(withId(R.id.contentFrame)))),
                        isDisplayed()));
        appCompatEditText3.perform(pressImeActionButton());

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.loginButton), withText("Login"),
                        withParent(allOf(withId(R.id.activity_main),
                                withParent(withId(R.id.contentFrame)))),
                        isDisplayed()));
        appCompatButton.perform(click());

        ViewInteraction textView = onView(
                allOf(withText("RecordingsActivity"),
                        childAtPosition(
                                allOf(withId(R.id.toolbar),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                                0)),
                                0),
                        isDisplayed()));
        textView.check(matches(withText("RecordingsActivity")));

    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
