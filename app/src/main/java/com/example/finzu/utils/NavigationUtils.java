package com.example.finzu.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

public class NavigationUtils {
    public void changeActivity(Context context, Class<?> activityClass) {
        Intent intent = new Intent(context, activityClass);
        context.startActivity(intent);
    }

    public void changeActivityAndFinish(Activity activity, Class<?> activityClass) {
        Intent intent = new Intent(activity, activityClass);
        activity.startActivity(intent);
        activity.finish();
    }
}
