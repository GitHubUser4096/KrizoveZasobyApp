package com.entscz.krizovezasoby;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.entscz.krizovezasoby.TestUtils.randStr;
import static org.hamcrest.CoreMatchers.anything;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;

import com.entscz.krizovezasoby.activities.AddItemActivity;
import com.entscz.krizovezasoby.activities.BagsActivity;
import com.entscz.krizovezasoby.activities.LoginActivity;
import com.entscz.krizovezasoby.activities.SettingsActivity;
import com.entscz.krizovezasoby.activities.TakePictureActivity;
import com.entscz.krizovezasoby.model.DataManager;
import com.entscz.krizovezasoby.model.Product;
import com.entscz.krizovezasoby.util.Requests;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class AddProductTest {

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

        DataManager.bags.createBag(randStr(6));

        onData(anything()).atPosition(0).perform(click());
        onView(withId(R.id.addItemFab)).perform(click());
        onView(withId(R.id.searchProductFab)).perform(click());
        onView(withId(R.id.addProduct)).perform(click());

    }

    @Test
    public void addProduct(){

        onView(withId(R.id.brandInput)).perform(typeText(randStr(6)));
        onView(withId(R.id.typeInput)).perform(typeText(randStr(6)));
        onView(withId(R.id.shortDescInput)).perform(typeText(randStr(6)));
        onView(withId(R.id.codeInput)).perform(typeText(randStr(6)));
        closeSoftKeyboard();
        onView(withId(R.id.submitBtn)).perform(click());

        intended(hasComponent(AddItemActivity.class.getName()));

    }

    @Test
    public void withMoreInfo(){

        onView(withId(R.id.brandInput)).perform(typeText(randStr(6)));
        onView(withId(R.id.typeInput)).perform(typeText(randStr(6)));
        onView(withId(R.id.shortDescInput)).perform(typeText(randStr(6)));
        onView(withId(R.id.codeInput)).perform(typeText(randStr(6)));
        onView(withId(R.id.amountValueInput)).perform(typeText("100"));
        onView(withId(R.id.packageTypeInput)).perform(typeText(randStr(6)));
        onView(withId(R.id.descriptionInput)).perform(scrollTo()).perform(typeText(randStr(6)));
        closeSoftKeyboard();
        onView(withId(R.id.submitBtn)).perform(click());

        intended(hasComponent(AddItemActivity.class.getName()));

    }

    @Test
    public void invalidAmount(){

        onView(withId(R.id.brandInput)).perform(typeText(randStr(6)));
        onView(withId(R.id.typeInput)).perform(typeText(randStr(6)));
        onView(withId(R.id.shortDescInput)).perform(typeText(randStr(6)));
        onView(withId(R.id.codeInput)).perform(typeText(randStr(6)));
        onView(withId(R.id.amountValueInput)).perform(typeText("0"));
        onView(withId(R.id.packageTypeInput)).perform(typeText(randStr(6)));
        onView(withId(R.id.descriptionInput)).perform(scrollTo()).perform(typeText(randStr(6)));
        closeSoftKeyboard();
        onView(withId(R.id.submitBtn)).perform(click());

        onView(withText("Přidat produkt")).check(matches(isDisplayed()));

    }

    @Test
    public void emptyField(){

        onView(withId(R.id.typeInput)).perform(typeText(randStr(6)));
        onView(withId(R.id.shortDescInput)).perform(typeText(randStr(6)));
        onView(withId(R.id.codeInput)).perform(typeText(randStr(6)));
        closeSoftKeyboard();
        onView(withId(R.id.submitBtn)).perform(click());

        onView(withText("Přidat produkt")).check(matches(isDisplayed()));

    }

    @Test
    public void takePicture(){

        onView(withId(R.id.takePicBtn)).perform(click());
        intended(hasComponent(TakePictureActivity.class.getName()));

    }

    @Test
    public void selectImage(){

        onView(withId(R.id.selectImageBtn)).perform(click());
        intending(hasAction(Intent.ACTION_GET_CONTENT));

    }

    @After
    public void cleanup(){

        SharedPreferences prefs = context.getSharedPreferences("login", Context.MODE_PRIVATE);
        prefs.edit().clear().apply();

    }

}
