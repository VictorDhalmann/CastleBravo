package API_Com.Modules.Wifi;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import API_Com.DialogAndAlert.AlertDialogSc;
import fr.telecom_physique.castlebravo.R;

/**
 * Created by Guillaumee on 06/04/2016.
 */
public class DialogConnectNetwork extends AlertDialogSc {

    private MyScanResult theScanResultToHandle;

    public static DialogConnectNetwork newInstance(MyScanResult theScanResult) {
        DialogConnectNetwork fragment = new DialogConnectNetwork();
        fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);
        Bundle myBundle = new Bundle();

        myBundle.putParcelable("ScanResult",theScanResult);
        fragment.setArguments(myBundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        Bundle aBundle = getArguments();
        theScanResultToHandle = aBundle.getParcelable("ScanResult");

        final View view = inflater.inflate(R.layout.api_wifi_dialog_connect_wifi, container, false);
        getDialog().setTitle(theScanResultToHandle.getDataNetwork().SSID);

        return view;

    }

}
