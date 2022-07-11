package com.entscz.krizovezasoby;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.entscz.krizovezasoby.TestUtils.randStr;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;

import com.entscz.krizovezasoby.activities.AddItemActivity;
import com.entscz.krizovezasoby.activities.BagInfoActivity;
import com.entscz.krizovezasoby.activities.BagsActivity;
import com.entscz.krizovezasoby.activities.DonateBagActivity;
import com.entscz.krizovezasoby.activities.EditItemActivity;
import com.entscz.krizovezasoby.activities.LoginActivity;
import com.entscz.krizovezasoby.activities.ScannerActivity;
import com.entscz.krizovezasoby.activities.SearchItemActivity;
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
public class SearchItemTest {

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

        onData(anything()).atPosition(0).perform(click());

    }

    @Test
    public void searchItem(){
        onView(withId(R.id.addItemFab)).perform(click());
        onView(withId(R.id.searchProductFab)).perform(click());

        onView(withId(R.id.searchText)).perform(typeText("test"));
        onData(anything()).inAdapterView(withId(R.id.itemSuggestions)).atPosition(0).perform(click());
        intended(hasComponent(AddItemActivity.class.getName()));
    }

    @After
    public void cleanup(){

        SharedPreferences prefs = context.getSharedPreferences("login", Context.MODE_PRIVATE);
        prefs.edit().clear().apply();

    }

}
