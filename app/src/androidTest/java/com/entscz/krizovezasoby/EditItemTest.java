package com.entscz.krizovezasoby;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
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

import com.entscz.krizovezasoby.activities.BagsActivity;
import com.entscz.krizovezasoby.activities.LoginActivity;
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
public class EditItemTest {

    @Rule
    public IntentsTestRule<LoginActivity> intentsTestRule =
            new IntentsTestRule<>(LoginActivity.class);
    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule =
            new ActivityScenarioRule<>(LoginActivity.class);

    Context context;
    int bagId;

    @Before
    public void prepare(){

        context = InstrumentationRegistry.getInstrumentation().getTargetContext();

        Requests.GET(DataManager.BASE_URL+"/test/init.php").await();

        LoginManager.init(context);
        LoginManager.login("admin", "admin");
        Intent intent = new Intent(context, BagsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);

        bagId = DataManager.bags.createBag(randStr(6));

        Product product = DataManager.products.createProduct(
                "test",
                "test",
                "1",
                "g",
                "test",
                randStr(6),
                "test",
                "",
                null);

        Requests.POST(DataManager.API_URL + "/item/addItem.php?bagId=" + bagId, new Requests.Params()
                .add("productId", product.id)
                .add("count", 1)
                .add("expiration", "")
        ).await();

        onData(anything()).atPosition(0).perform(click());
        onData(anything()).inAdapterView(withId(R.id.itemList)).atPosition(0).perform(click());
        onData(anything()).inAdapterView(withId(R.id.itemList)).atPosition(0)
                .onChildView(withId(R.id.editBtn)).perform(click());

    }

    @Test
    public void editItem(){
        onView(withId(R.id.saveBtn)).perform(click());
        onView(withId(R.id.itemList)).check(matches(isDisplayed()));
    }

    @Test
    public void withDate(){
        onView(withId(R.id.dateDay)).perform(typeText("1"));
        onView(withId(R.id.dateMonth)).perform(typeText("12"));
        onView(withId(R.id.dateYear)).perform(typeText("2024"));
        closeSoftKeyboard();
        onView(withId(R.id.saveBtn)).perform(click());
        onView(withId(R.id.itemList)).check(matches(isDisplayed()));
    }

    @Test
    public void emptyCount(){
        onView(withId(R.id.countInput)).perform(replaceText(""));
        onView(withId(R.id.saveBtn)).perform(click());
        onView(withId(R.id.itemList)).check(doesNotExist());
    }

    @Test
    public void countTooLarge(){
        onView(withId(R.id.countInput)).perform(replaceText("100"));
        onView(withId(R.id.saveBtn)).perform(click());
        onView(withId(R.id.itemList)).check(doesNotExist());
    }

    @Test
    public void emptyDay(){
        onView(withId(R.id.dateDay)).perform(typeText("1"));
        onView(withId(R.id.dateYear)).perform(typeText("2024"));
        closeSoftKeyboard();
        onView(withId(R.id.saveBtn)).perform(click());
        onView(withId(R.id.itemList)).check(doesNotExist());
    }

    @Test
    public void dateOutOfRange(){
        onView(withId(R.id.dateDay)).perform(typeText("0"));
        onView(withId(R.id.dateMonth)).perform(typeText("1"));
        onView(withId(R.id.dateYear)).perform(typeText("2024"));
        closeSoftKeyboard();
        onView(withId(R.id.saveBtn)).perform(click());
        onView(withId(R.id.itemList)).check(doesNotExist());
    }

    @Test
    public void emptyMonth(){
        onView(withId(R.id.dateDay)).perform(typeText("1"));
        onView(withId(R.id.dateMonth)).perform(typeText("12"));
        onView(withId(R.id.dateYear)).perform(typeText("2024"));
        closeSoftKeyboard();
        onView(withId(R.id.saveBtn)).perform(click());
        onView(withId(R.id.itemList)).check(matches(isDisplayed()));
    }

    @After
    public void cleanup(){

        SharedPreferences prefs = context.getSharedPreferences("login", Context.MODE_PRIVATE);
        prefs.edit().clear().apply();

    }

}
