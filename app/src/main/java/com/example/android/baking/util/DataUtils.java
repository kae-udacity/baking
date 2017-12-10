package com.example.android.baking.util;

import com.example.android.baking.data.Step;

/**
 * Created by Kenneth on 05/12/2017.
 */

public final class DataUtils {

    private DataUtils() {

    }

    public static String getUrl(Step step) {
        if (step == null) {
            return null;
        }

        String url = step.getVideoUrl();
        if (url == null) {
            url = step.getThumbnailUrl();
        }
        return url;
    }
}
