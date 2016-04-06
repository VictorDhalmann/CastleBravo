package API_Com.Modules.Wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

/**
 * Created by Guillaumee on 04/04/2016.
 */
public class BR_WifiState extends BroadcastReceiver {

    private Wifi myWifiModule;

    public BR_WifiState(Wifi aModule){
        myWifiModule = aModule;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {

            int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);

            if (wifiState == WifiManager.WIFI_STATE_DISABLED) {
                Log.d("BR_WIFI_STATE", "WIFI IS DISABLE");
                myWifiModule.Thread().Pause().set(true);
                myWifiModule.AlertWifiDisable().display("Alert_Wifi",context);

            }
            if (wifiState == WifiManager.WIFI_STATE_ENABLED) {
                Log.d("BR_WIFI_STATE", "WIFI IS ENABLE");
            }

        }

        if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {

            NetworkInfo aNetworkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);

            if (aNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {

                if (aNetworkInfo.getDetailedState() == NetworkInfo.DetailedState.SCANNING) {
                    Log.d("BR_WIFI_STATE", "WIFI IS SCANNING");
                    myWifiModule.DialogScanResultWifi().display("Dialog_Wifi",context);

                }
                else if(aNetworkInfo.getState() == NetworkInfo.State.CONNECTED){
                    Log.d("BR_WIFI_STATE", "WIFI IS CONNECTED");
                    myWifiModule.Thread().Pause().set(false);

                }
                else if(aNetworkInfo.getState() == NetworkInfo.State.DISCONNECTED){
                    Log.d("BR_WIFI_STATE", "WIFI IS DISCONNECTED");
                    myWifiModule.Thread().Pause().set(true);

                }
            }

        }

    }
}
