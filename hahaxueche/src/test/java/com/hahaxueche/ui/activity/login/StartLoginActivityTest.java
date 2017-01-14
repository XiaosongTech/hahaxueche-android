package com.hahaxueche.ui.activity.login;

import android.content.Intent;

import com.hahaxueche.R;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.robolectric.Shadows.shadowOf;

/**
 * Created by wangshirui on 2017/1/14.
 */
@RunWith(RobolectricTestRunner.class)
public class StartLoginActivityTest {
    @Test
    public void clickingLogin_shouldStartLoginActivity() {
        StartLoginActivity activity = Robolectric.setupActivity(StartLoginActivity.class);
        activity.findViewById(R.id.tv_start_login).performClick();

        Intent expectedIntent = new Intent(activity, LoginActivity.class);
        assertThat(shadowOf(activity).getNextStartedActivity(),equalTo(expectedIntent));
    }
}
