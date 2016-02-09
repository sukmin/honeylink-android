package kr.netty.honeylink.manager;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.SystemClock;

public class NetworkManager {

    private static Context context;

    public static void init(Context context){
        NetworkManager.context = context;
    }

    public static boolean isAvailable(){

        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        // 비행기모드인 경우 NULL이고, 가끔 정상인 경우에도 null인 경우가 있어 3회 재시도
        if (activeNetworkInfo == null) {


            int retryCount = 0;
            while (true) {

                // 커넥티비니매니져가 변화되도록 일정시간 기다림
                SystemClock.sleep(100);


                activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                if (activeNetworkInfo == null) {
                    retryCount = retryCount + 1;
                    if (retryCount >= 2) {
                        return false;
                    } else {
                        continue;
                    }
                } else {
                    break;
                }
            }
        }

        NetworkInfo.State networkStatus = activeNetworkInfo.getState();

        return networkStatus == NetworkInfo.State.CONNECTED && activeNetworkInfo.isAvailable();
    }

    public static boolean isNotAvailable(){
        return !isAvailable();
    }

}
