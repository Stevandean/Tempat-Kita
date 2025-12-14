package com.example.tempatkita.api;

import android.content.Context;

import com.cloudinary.android.MediaManager;

import java.util.HashMap;
import java.util.Map;

public class CloudinaryConfig {

    private static Map config = new HashMap();
    private static boolean isInitialized = false;

    public static void init(Context context) {
        if (!isInitialized) {
            config.put("cloud_name", "drpkfitqa");
            config.put("api_key", "823497143386254");
            config.put("api_secret", "5ubJkrGp5DwIGcrn8TQqX1D1XOI");

            MediaManager.init(context, config);
            isInitialized = true;
        }
    }
}
