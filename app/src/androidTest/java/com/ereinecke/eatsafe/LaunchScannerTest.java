package com.ereinecke.eatsafe;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.ereinecke.eatsafe.util.Constants;
import com.google.zxing.client.android.Intents;
import com.google.zxing.integration.android.IntentIntegrator;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.app.Activity.RESULT_OK;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.intent.matcher.IntentMatchers.isInternal;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.ereinecke.eatsafe.util.Constants.TEST_BARCODE;
import static org.hamcrest.Matchers.not;

/**
 * Test scanner launch and stub return value
 */


@RunWith(AndroidJUnit4.class)
public class launchScannerTest {

    private static String LOG_TAG = launchScannerTest.class.getSimpleName();

    @Rule
    public IntentsTestRule<MainActivity> mActivityRule = new IntentsTestRule<>
            (MainActivity.class);

    @Before
    public void stubAllExternalIntents() {
        intending(not(isInternal())).respondWith(new Instrumentation
                .ActivityResult(RESULT_OK, null));
    }

    @Test
    public void clickScannerButtonTest() {
        // Click on Scan Barcode button
        onView((withId(R.id.scan_button))).perform(click());

        // Verify correct intent is sent to OpenFoodService
        intended(hasAction(Intents.Scan.ACTION));
    }

    /* Stub that returns test barcode */
    @Test
    public void scannerReturnsBarcodeTest() {
        // Stub all Intents to scanner activity to return TEST_BARCODE.
        final Intent resultData = new Intent();
        resultData.putExtra(Constants.BARCODE_KEY, TEST_BARCODE);
        resultData.putExtra("requestCode", IntentIntegrator.REQUEST_CODE);

        intending(hasAction(Intents.Scan.ACTION))
                .respondWith(new Instrumentation.ActivityResult(Activity.RESULT_OK,
                        resultData));

        // Check that the barcode displayed in the UI.
        // TODO: Fails. onView(withId) is not returning contents of TextView, rather all of its
        // properties.  Need an IdlingResource?
        onView(withId(R.id.barcode))
                .check(matches(withText(TEST_BARCODE)));

    }
}
