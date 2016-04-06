package API_Com.DialogAndAlert;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by Guillaumee on 06/04/2016.
 */
public class AlertDialogSc extends DialogFragment implements DialogInterface.OnClickListener {

    protected NoticeDialogListener myAlertListener;

    public void setOnAlertListener(NoticeDialogListener listener){
        myAlertListener = listener;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

    }

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
}


