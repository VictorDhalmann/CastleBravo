package API_Com.DialogAndAlert;

import android.app.DialogFragment;

/**
 * Created by Guillaumee on 06/04/2016.
 */
public class MyAlertHandler extends DialogFragment  {

    private NoticeDialogListener myAlertListener;

    public void setOnAlertListener(NoticeDialogListener listener){
        myAlertListener = listener;
    }
}
