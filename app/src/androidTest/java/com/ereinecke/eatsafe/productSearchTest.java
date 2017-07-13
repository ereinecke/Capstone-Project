package com.ereinecke.eatsafe;

import android.app.Activity;
import android.app.Instrumentation;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.ereinecke.eatsafe.util.Constants;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.ComponentNameMatchers.hasShortClassName;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.intent.matcher.IntentMatchers.isInternal;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.ereinecke.eatsafe.util.Constants.TEST_BARCODE;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;

/**
 * Test product search cycle
 */

@RunWith(AndroidJUnit4.class)
public class productSearchTest {

    private static String LOG_TAG = productSearchTest.class.getSimpleName();

    @Rule
    public IntentsTestRule<MainActivity> mActivityRule = new IntentsTestRule<MainActivity>
            (MainActivity.class);

    @Before
    public void stubAllExternalIntents() {
        intending(not(isInternal())).respondWith(new Instrumentation
                .ActivityResult(Activity.RESULT_OK, null));
    }

    // TODO: this test fails because the service appears to not be stubbed out
    @Test
    public void typeNumber_ValidInput_InitiatesSearch() {
        // Type a barcode into the search field and press the search button.
        onView((withId(R.id.barcode))).perform(typeText(TEST_BARCODE), closeSoftKeyboard());
        onView((withId(R.id.search_button))).perform(click());

        // Verify correct intent is sent to OpenFoodService
        intended(allOf(
                hasComponent(hasShortClassName(".OpenFoodService")),
                hasAction(Constants.ACTION_FETCH_PRODUCT),
                hasExtra(Constants.BARCODE_KEY, TEST_BARCODE)));
    }

    /* Test product search intent */
    @Test
    public void productSearch_displays_productInfo() {

        /* Stub intent to OpenFoodService to return product found result
        intending(hasComponent(hasShortClassName(".OpenFoodService")))
                .respondWith(new Instrumentation.ActivityResult()

        */
    }
}

