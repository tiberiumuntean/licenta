package com.licenta;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SaveSharedPreference
{
    static final String PREF_USER_TOKEN = "user_token";
    static final String PREF_USER_ID = "user_id";
    static final String PREF_USER_EMAIL = "user_email";
    static final String PREF_CLUB_ID = "club_id";
    static final String PREF_CLIENT_ID = "client_id";
    static final String PREF_TRAINER_ID = "trainer_id";
    static final String PREF_IS_ADMIN = "is_admin";

    static SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public static void setIsAdmin(Context ctx, boolean val)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putBoolean(PREF_IS_ADMIN, val);
        editor.commit();
    }

    public static boolean getIsAdmin(Context ctx)
    {
        return getSharedPreferences(ctx).getBoolean(PREF_IS_ADMIN, false);
    }

    public static void setClientId(Context ctx, String id)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_CLIENT_ID, id);
        editor.commit();
    }

    public static String getClientId(Context ctx)
    {
        return getSharedPreferences(ctx).getString(PREF_CLIENT_ID, "");
    }

    public static void setTrainerId(Context ctx, String id)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_TRAINER_ID, id);
        editor.commit();
    }

    public static String getTrainerId(Context ctx)
    {
        return getSharedPreferences(ctx).getString(PREF_TRAINER_ID, "");
    }

    public static void setClubId(Context ctx, String id)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_CLUB_ID, id);
        editor.commit();
    }

    public static String getClubId(Context ctx)
    {
        return getSharedPreferences(ctx).getString(PREF_CLUB_ID, "");
    }

    public static void setId(Context ctx, String id)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_USER_ID, id);
        editor.commit();
    }

    public static String getId(Context ctx)
    {
        return getSharedPreferences(ctx).getString(PREF_USER_ID, "");
    }

    public static void setEmail(Context ctx, String email)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_USER_EMAIL, email);
        editor.commit();
    }

    public static String getEmail(Context ctx)
    {
        return getSharedPreferences(ctx).getString(PREF_USER_EMAIL, "");
    }

    public static void setToken(Context ctx, String token)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_USER_TOKEN, token);
        editor.commit();
    }

    public static String getToken(Context ctx)
    {
        return getSharedPreferences(ctx).getString(PREF_USER_TOKEN, "");
    }
}