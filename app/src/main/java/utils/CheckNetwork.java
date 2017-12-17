package utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Li Wenzhao on 2017/12/13.
 */

public class CheckNetwork {
    public static boolean CheckNetState(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        Boolean isWifiConn = networkInfo.isConnected();
        networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        Boolean isMobileConn = networkInfo.isConnected();

        if(isWifiConn ||isMobileConn )
            return true;
        return false;
    }
}
