package kr.netty.honeylink.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class HoneylinkPreferenceManager {

    private static final String LAST_NOTICE_SEQUENCE = "lastNoticeSequence";

    private static SharedPreferences sharedPreference;

    public static void init(Context context){
        sharedPreference = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static boolean isOverSavedNoticeSequence(long id) {
        return id > sharedPreference.getLong(LAST_NOTICE_SEQUENCE, 0);
    }

    public static long getLastNoticeSequence(){
        return sharedPreference.getLong(LAST_NOTICE_SEQUENCE, 0);
    }

    public static void setLastNoticeSequence(long id) {
        SharedPreferences.Editor editor = sharedPreference.edit();
        editor.putLong(LAST_NOTICE_SEQUENCE, id);
        editor.apply();
    }

}
