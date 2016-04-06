package API_Com.Modules.Wifi;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import API_Com.DialogAndAlert.AlertDialogSc;
import fr.telecom_physique.castlebravo.R;

/**
 * Created by Guillaume.Ebert on 30/03/2016.
 */
public class DialogScanResultWifi extends AlertDialogSc {


    private RelativeLayout myProgressBarLayout;
    private RelativeLayout myListViewLayout;
    private ListView myScanResultListView;
    private List<MyScanResult> arrL_MyScanResult;
    private BR_WifiScan theScanBR;
    private MyListAdapter myAdapter;
    private WifiManager theWifiManager;

    public static DialogScanResultWifi newInstance() {

        DialogScanResultWifi fragment = new DialogScanResultWifi();
        fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.api_wifi_dialog_scan_result, container, false);
        getDialog().setTitle("WI-FI");

        theWifiManager = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);

        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.layout_progress_bar);
        myProgressBarLayout = (RelativeLayout) view.findViewById(R.id.progressBarLayout);

        myListViewLayout = (RelativeLayout) view.findViewById(R.id.layout_lv_scan_result);
        myListViewLayout.setVisibility(View.GONE);

        myScanResultListView = (ListView) view.findViewById(R.id.list_scan_result);

        arrL_MyScanResult = new ArrayList<MyScanResult>();

        myAdapter = new MyListAdapter(getActivity(),R.layout.api_wifi_list_view_scan_result, arrL_MyScanResult);
        myScanResultListView.setAdapter(myAdapter);

        theScanBR = new BR_WifiScan();
        getActivity().registerReceiver(theScanBR, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        theWifiManager.startScan();

        return view;
    }


    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        getActivity().unregisterReceiver(theScanBR);
    }

    @Override
    public void onStart() {
        super.onStart();

        myScanResultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               dismiss();
            }
        });

    }


    /**
     * Display the fragment.
     * @param key
     * @param aContext
     */
    public void display(String key, Context aContext) {
        Activity theActivity = (Activity) aContext;
        FragmentTransaction ft = theActivity.getFragmentManager().beginTransaction();
        Fragment prev = theActivity.getFragmentManager().findFragmentByTag(key);

        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        this.show(ft, key);
    }

    /**
     * Sort the List<ScanResult>, the wifi with the most signal are shown at the top the double are skipped
     */
    private void sortListScanResult(List<ScanResult> aScanResultList){

        int  numLevels = 5;
        int signalLevel = 0;
        ScanResult aScanResult;

        for(int i= numLevels -1; i>= 0; i--){

            for(int y= 0; y < aScanResultList.size(); y++){ /*Get through the Scan list result */
                aScanResult = aScanResultList.get(y);
                signalLevel = theWifiManager.calculateSignalLevel(aScanResult.level,numLevels);

                if( signalLevel == i){
                    if(!containSSID(aScanResult)){
                        arrL_MyScanResult.add(new MyScanResult(aScanResult, signalLevel));
                    }
                }

            }
        }
    }

    /**
     * Check if the SSID is already in the list
     * @param aScanResultToCompare
     * @return
     *      @true if the SSID is already present
     *      @false if not 
     */
    private boolean containSSID(ScanResult aScanResultToCompare){
        for(int i = 0; i< arrL_MyScanResult.size(); i++){
            if(arrL_MyScanResult.get(i).getDataNetwork().SSID.equals(aScanResultToCompare.SSID)){
                return true;
            }
        }

        return false;
    }

    /*********************************************** PRIVATE CLASS ***********************************************/
    /**
     * Broadcast when a scan result is available
     */
    private class BR_WifiScan extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            List<ScanResult> theResultList;
            ScanResult aScanResult;

            if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {

                if(myProgressBarLayout.getVisibility() == View.VISIBLE) {
                    myProgressBarLayout.setVisibility(View.GONE);
                    myListViewLayout.setVisibility(View.VISIBLE);
                }
                
                /*Empty list*/
                arrL_MyScanResult.clear();

                sortListScanResult(theWifiManager.getScanResults());
                
                myAdapter.notifyDataSetChanged();
                
            }
        }
    }

    /**
     * Adapter for the list view. Inflate the layout api_wifi_list_view_scan_result.
     */
    private class MyListAdapter extends ArrayAdapter<MyScanResult> {

        private int resource;
        private LayoutInflater mLayoutInflater;

        public MyListAdapter ( Context ctx, int resourceId, List<MyScanResult> objects) {

            super( ctx, resourceId, objects );
            resource = resourceId;
            mLayoutInflater = LayoutInflater.from(ctx);
        }

        @Override
        public View getView ( int position, View convertView, ViewGroup parent ) {

            convertView = (RelativeLayout) mLayoutInflater.inflate( resource, null );

            MyScanResult aScanResult = (MyScanResult) getItem(position);

            TextView tvSSID = (TextView) convertView.findViewById(R.id.tv_ssid);
            TextView tvBSSID = (TextView) convertView.findViewById(R.id.tv_bssid);
            ImageView IvIcon = (ImageView) convertView.findViewById(R.id.iv_wifi_icon);

            tvSSID.setText(aScanResult.getDataNetwork().SSID);
            tvBSSID.setText(Integer.toString(aScanResult.getSignalLevel()) + " signal level");

            if(!aScanResult.getDataNetwork().capabilities.contains("WEP") && !aScanResult.getDataNetwork().capabilities.contains("WPA") ){
                IvIcon.setImageResource(R.drawable.api_wifi_wifi_4_bar);
            }

            /*TODO set the correct icon */

            return convertView;
        }


    }

}
