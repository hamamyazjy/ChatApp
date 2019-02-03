package com.example.hamam.chatapp;

import android.content.Context;

public class LastSeenTime {

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;



    public static String getTimeAgo(Long time ,Context cxt) {
         if (time < 1000000000000L) {
            time *= 1000;
        }

        long now = System.currentTimeMillis();
        if (time > now || time <= 0) {
            return null;
        }

        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "active few seconds ago";
        }
        else if (diff < 2 * MINUTE_MILLIS) {
            return "a minute ago";
        }
        else if (diff < 60 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " minutes ago";
        }
        else if (diff < 2 * HOUR_MILLIS) {
            return "an hour ago";
        }
        else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " hours ago";
        }
        else if (diff < 48 * HOUR_MILLIS) {
            return "yesterday";
        }
        else {
            return diff / DAY_MILLIS + " days ago";
        }
    }
}
