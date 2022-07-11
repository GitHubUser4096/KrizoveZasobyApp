package com.entscz.krizovezasoby;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;

import com.entscz.krizovezasoby.activities.BagsActivity;
import com.entscz.krizovezasoby.activities.LoginActivity;
import com.entscz.krizovezasoby.activities.SettingsActivity;
import com.entscz.krizovezasoby.model.DataManager;
import com.entscz.krizovezasoby.util.Requests;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class SettingsTest {

    @Rule
    public IntentsTestRule<LoginActivity> intentsTestRule =
            new IntentsTestRule<>(LoginActivity.class);
    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule =
            new ActivityScenarioRule<>(LoginActivity.class);

    Context context;

    @Before
    public void prepare(){

        context = InstrumentationRegistry.getInstrumentation().getTargetContext();

        Requests.GET(DataManager.BASE_URL+"/test/init.php").await();

        LoginManager.init(context);
        LoginManager.login("admin", "admin");
        Intent intent = new Intent(context, BagsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);

    }

    @Test
    public void saveSettings(){
        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().getTargetContext());
        onView(withText("Nastavení")).perform(click());

        onView(withId(R.id.saveBtn)).perform(click());
        onView(withId(R.id.bagList)).check(matches(isDisplayed()));
    }

    @Test
    public void emptyField(){
        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().getTargetContext());
        onView(withText("Nastavení")).perform(click());

        onView(withId(R.id.criticalValue)).perform(replaceText(""));

        onView(withId(R.id.saveBtn)).perform(click());
        onView(withId(R.id.bagList)).check(doesNotExist());
    }

    @Test
    public void tooLargeField(){
        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().getTargetContext());
        onView(withText("Nastavení")).perform(click());

        onView(withId(R.id.criticalValue)).perform(replaceText("99"));

        onView(withId(R.id.saveBtn)).perform(click());
        onView(withId(R.id.bagList)).check(doesNotExist());
    }

    @After
    public void cleanup(){

        SharedPreferences prefs = context.getSharedPreferences("login", Context.MODE_PRIVATE);
        prefs.edit().clear().apply();

    }

}
