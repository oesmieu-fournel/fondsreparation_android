package com.ecosystem.mobile.reparation.app

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.filters.LargeTest
import androidx.test.runner.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import com.ecosystem.mobile.reparation.ui.WelcomeActivity

@RunWith(AndroidJUnit4::class)
@LargeTest
class WelcomeActivityTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(WelcomeActivity::class.java)

    @Test
    fun test() {
        onView(withText("GET STARTED")).check(matches(isDisplayed()))
    }
}
