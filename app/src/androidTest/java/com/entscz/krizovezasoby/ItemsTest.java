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
import static androidx.test.espresso.intent.Intents.intending;
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
import android.provider.ContactsContract;

import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;

import com.entscz.krizovezasoby.activities.BagInfoActivity;
import com.entscz.krizovezasoby.activities.BagsActivity;
import com.entscz.krizovezasoby.activities.DonateBagActivity;
import com.entscz.krizovezasoby.activities.EditItemActivity;
import com.entscz.krizovezasoby.activities.ItemsActivity;
import com.entscz.krizovezasoby.activities.LoginActivity;
import com.entscz.krizovezasoby.activities.ScannerActivity;
import com.entscz.krizovezasoby.activities.SearchItemActivity;
import com.entscz.krizovezasoby.activities.SettingsActivity;
import com.entscz.krizovezasoby.model.Bag;
import com.entscz.krizovezasoby.model.DataManager;
import com.entscz.krizovezasoby.model.Item;
import com.entscz.krizovezasoby.model.Product;
import com.entscz.krizovezasoby.util.Requests;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ItemsTest {

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
        DataManager.bags.createBag(randStr(6));

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

//        onView(withId(R.id.addBagFab)).perform(click());
//        String bagName = TestUtils.randStr(6);
//        onView(withId(R.id.bagName)).perform(typeText(bagName));
//        onView(withId(android.R.id.button1)).perform(click());

    }

    @Test
    public void openBagInfo(){
        onView(withId(R.id.editBag)).perform(click());
        intended(hasComponent(BagInfoActivity.class.getName()));
    }

    @Test
    public void openCharities(){
        openActionBarOverflowOrOptionsMenu(context);
        onView(withText("Odevzdat tašku")).perform(click());
        intended(hasComponent(DonateBagActivity.class.getName()));
    }

    @Test
    public void openEditItem(){

        onData(anything()).inAdapterView(withId(R.id.itemList)).atPosition(0).perform(click());
//        onView(withId(R.id.editBtn)).check(matches(isDisplayed()));
//        onView(withId(R.id.itemList))
        onData(anything()).inAdapterView(withId(R.id.itemList)).atPosition(0)
                .onChildView(withId(R.id.editBtn)).perform(click());
        intended(hasComponent(EditItemActivity.class.getName()));

    }

    @Test
    public void useItem(){
        onData(anything()).inAdapterView(withId(R.id.itemList)).atPosition(0).perform(click());
        onData(anything()).inAdapterView(withId(R.id.itemList)).atPosition(0)
                .onChildView(withId(R.id.useBtn)).perform(click());
        onView(withId(R.id.useItemDialog)).check(matches(isDisplayed()));
        onView(withId(android.R.id.button1)).perform(click());
        onView(withId(R.id.useItemDialog)).check(doesNotExist());
    }

    @Test
    public void useItemEmpty(){
        onData(anything()).inAdapterView(withId(R.id.itemList)).atPosition(0).perform(click());
        onData(anything()).inAdapterView(withId(R.id.itemList)).atPosition(0)
                .onChildView(withId(R.id.useBtn)).perform(click());
        onView(withId(R.id.countInput)).perform(replaceText(""));
        onView(withId(android.R.id.button1)).perform(click());
        onView(withId(R.id.useItemDialog)).check(matches(isDisplayed()));
    }

    @Test
    public void useItemTooLarge(){
        onData(anything()).inAdapterView(withId(R.id.itemList)).atPosition(0).perform(click());
        onData(anything()).inAdapterView(withId(R.id.itemList)).atPosition(0)
                .onChildView(withId(R.id.useBtn)).perform(click());
        onView(withId(R.id.countInput)).perform(replaceText("99"));
        onView(withId(android.R.id.button1)).perform(click());
        onView(withId(R.id.useItemDialog)).check(matches(isDisplayed()));
    }

    @Test
    public void moveItem(){
        onData(anything()).inAdapterView(withId(R.id.itemList)).atPosition(0).perform(click());
        onData(anything()).inAdapterView(withId(R.id.itemList)).atPosition(0)
                .onChildView(withId(R.id.moveBtn)).perform(click());
        onView(withId(R.id.moveItemDialog)).check(matches(isDisplayed()));
        onView(withId(android.R.id.button1)).perform(click());
        onView(withId(R.id.moveItemDialog)).check(doesNotExist());
    }

    @Test
    public void moveItemEmpty(){
        onData(anything()).inAdapterView(withId(R.id.itemList)).atPosition(0).perform(click());
        onData(anything()).inAdapterView(withId(R.id.itemList)).atPosition(0)
                .onChildView(withId(R.id.moveBtn)).perform(click());
        onView(withId(R.id.countInput)).perform(replaceText(""));
        onView(withId(android.R.id.button1)).perform(click());
        onView(withId(R.id.moveItemDialog)).check(matches(isDisplayed()));
    }

    @Test
    public void moveItemTooLarge(){
        onData(anything()).inAdapterView(withId(R.id.itemList)).atPosition(0).perform(click());
        onData(anything()).inAdapterView(withId(R.id.itemList)).atPosition(0)
                .onChildView(withId(R.id.moveBtn)).perform(click());
        onView(withId(R.id.countInput)).perform(replaceText("99"));
        onView(withId(android.R.id.button1)).perform(click());
        onView(withId(R.id.moveItemDialog)).check(matches(isDisplayed()));
    }

    @Test
    public void deleteItem(){
        onData(anything()).inAdapterView(withId(R.id.itemList)).atPosition(0).perform(click());
        onData(anything()).inAdapterView(withId(R.id.itemList)).atPosition(0)
                .onChildView(withId(R.id.deleteBtn)).perform(click());
        onView(withText("Odstranit položku?")).check(matches(isDisplayed()));
        onView(withId(android.R.id.button1)).perform(click());
        onView(withText("Odstranit položku?")).check(doesNotExist());
    }

    @Test
    public void searchItem(){
        onView(withId(R.id.addItemFab)).perform(click());
        onView(withId(R.id.searchProductFab)).perform(click());
        intended(hasComponent(SearchItemActivity.class.getName()));
    }

    @Test
    public void scanItem(){
        onView(withId(R.id.addItemFab)).perform(click());
        onView(withId(R.id.scanCodeFab)).perform(click());
        intended(hasComponent(ScannerActivity.class.getName()));
    }

    @Test
    public void testDisplay(){
        onView(withId(R.id.displaySpinner)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Typ • Značka"))).perform(click());
        onView(withId(R.id.displaySpinner)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Značka • Typ"))).perform(click());
        // TODO should anything else be tested here? (other than no crash)
    }

    @Test
    public void testSort(){
        onView(withId(R.id.sortSpinner)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Název"))).perform(click());
        onView(withId(R.id.sortSpinner)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Datum"))).perform(click());
        // TODO should anything else be tested here? (other than no crash)
    }

    @After
    public void cleanup(){

        SharedPreferences prefs = context.getSharedPreferences("login", Context.MODE_PRIVATE);
        prefs.edit().clear().apply();

    }

}
