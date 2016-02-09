package kr.netty.honeylink;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import kr.netty.honeylink.manager.HoneylinkPreferenceManager;
import kr.netty.honeylink.manager.NetworkManager;

public class HoneylinkApplication extends Application {


    public static String appVersionName;


    @Override
    public void onCreate() {
        super.onCreate();

        appVersionName = getAppVersionName(this);
        HoneylinkPreferenceManager.init(this);
        NetworkManager.init(this);

    }

    public static String getAppVersionName(Context context) {

        // application version
        String versionName = "1.0";
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            versionName = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return versionName;
        }

        return versionName;
    }

}
