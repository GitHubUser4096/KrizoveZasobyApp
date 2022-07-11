package com.entscz.krizovezasoby;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.init;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;

import android.app.Activity;
import android.app.backup.BackupAgent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;

import com.entscz.krizovezasoby.activities.BagsActivity;
import com.entscz.krizovezasoby.activities.ItemsActivity;
import com.entscz.krizovezasoby.activities.LoginActivity;
import com.entscz.krizovezasoby.activities.SettingsActivity;
import com.entscz.krizovezasoby.model.Bag;
import com.entscz.krizovezasoby.model.DataManager;
import com.entscz.krizovezasoby.util.Requests;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class BagsTest {

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
    public void addBag(){
        onView(withId(R.id.addBagFab)).perform(click());
        String bagName = TestUtils.randStr(6);
        onView(withId(R.id.bagName)).perform(typeText(bagName));
        onView(withId(android.R.id.button1)).perform(click());
        intended(hasComponent(ItemsActivity.class.getName()));
    }

    @Test
    public void addEmptyBag(){
        onView(withId(R.id.addBagFab)).perform(click());
        onView(withId(android.R.id.button1)).perform(click());
        onView(withText("Přidat tašku")).check(matches(isDisplayed()));
    }

    @Test
    public void addExistingBag(){
        String bagName = TestUtils.randStr(6);
        DataManager.bags.createBag(bagName);
        onView(withId(R.id.addBagFab)).perform(click());
        onView(withId(R.id.bagName)).perform(typeText(bagName));
        onView(withId(android.R.id.button1)).perform(click());
        onView(withText("Přidat tašku")).check(matches(isDisplayed()));
    }

    @Test
    public void pressBagItem(){
        DataManager.bags.createBag(TestUtils.randStr(6));
        onData(anything()).atPosition(0).perform(click());
        intended(hasComponent(ItemsActivity.class.getName()));
    }

    @Test
    public void pressSettings(){
        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().getTargetContext());
        onView(withText("Nastavení")).perform(click());
        intended(hasComponent(SettingsActivity.class.getName()));
    }

    @Test
    public void pressLogout(){
        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().getTargetContext());
        onView(withText("Odhlásit se")).perform(click());
        intended(hasComponent(LoginActivity.class.getName()));
    }

    @After
    public void cleanup(){

        SharedPreferences prefs = context.getSharedPreferences("login", Context.MODE_PRIVATE);
        prefs.edit().clear().apply();

    }

}
