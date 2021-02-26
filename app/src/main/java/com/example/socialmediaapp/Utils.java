package com.example.socialmediaapp;

public class Utils
{
    private static long SECOND_MILLIS = 1000;
    private static long MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static long HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static long DAY_MILLIS = 24 * HOUR_MILLIS;

    public static String timeToDisplay(long time)
    {
        long currentTime = System.currentTimeMillis();
        long diff = currentTime - time;

        if(diff < MINUTE_MILLIS)
        {
            return "Just Now";
        }
        else if(diff < 2 * MINUTE_MILLIS)
        {
            return "A Minute Ago";
        }
        else if(diff < 5 * MINUTE_MILLIS)
        {
            return  "5 Minute Ago";
        }
        else if(diff < 50 * MINUTE_MILLIS)
        {
            return (diff / MINUTE_MILLIS) + "minutes ago";
        }
        else if(diff < 90 * MINUTE_MILLIS)
        {
            return "An Hour Ago";
        }
        else if(diff < 24 * HOUR_MILLIS)
        {
            return (diff / HOUR_MILLIS) + " Hours ago";
        }
        else if(diff < 48 * HOUR_MILLIS)
        {
            return "Yesterday";
        }

        return (diff / DAY_MILLIS) + "ago";
    }
}
