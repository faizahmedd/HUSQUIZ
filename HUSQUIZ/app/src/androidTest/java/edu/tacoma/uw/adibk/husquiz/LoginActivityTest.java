package edu.tacoma.uw.adibk.husquiz;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class LoginActivityTest
{
    @Rule
    public ActivityTestRule<LoginActivity> mActivityRule = new ActivityTestRule<>(
            LoginActivity.class);

    /**
     * Test the correct login
     */
    @Test
    public void testCorrectLogin() {
        String email = "keroadib@live.com";
        String password = "hello";
        // Type text and then press the button.
        onView(withId(R.id.edittext_email))
                .perform(typeText(email));
        onView(withId(R.id.edittext_password))
                .perform(typeText(password));
        closeSoftKeyboard();
        onView(withId(R.id.button_login))
                .perform(click());

        onView(withText("Courses"))
                .inRoot(withDecorView(not(is(
                        mActivityRule.getActivity()
                                .getWindow()
                                .getDecorView()))))
                .check(matches(isDisplayed()));
    }

    /**
     * Tests the Incorrect Login
     */
    @Test
    public void testIncorrectLogin() {
        String email = "keroadib@live.com";
        String password = "helloo";
        // Type text and then press the button.
        onView(withId(R.id.edittext_email))
                .perform(typeText(email));
        onView(withId(R.id.edittext_password))
                .perform(typeText(password));
        closeSoftKeyboard();
        onView(withId(R.id.button_login))
                .perform(click());

        onView(withText("Email or Password is Incorrect"))
                .inRoot(withDecorView(not(is(
                        mActivityRule.getActivity()
                                .getWindow()
                                .getDecorView()))))
                .check(matches(isDisplayed()));
    }

    /**
     * Tests the Register button
     */
    @Test
    public void testRegisterButton()
    {
        onView(withId(R.id.textview_register))
                .perform(click());
        onView(withText("REGISTER"))
                .inRoot(withDecorView(not(is(
                        mActivityRule.getActivity()
                                .getWindow()
                                .getDecorView()))))
                .check(matches(isDisplayed()));
    }
}