package API_Com.DialogAndAlert;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by Guillaumee on 04/04/2016.
 */
public class AlertHandler extends AlertDialogSc  {

    private Bundle paramBundle;

    private AlertDialog theDialog;
    private Boolean isABtnPressed;

    public static AlertHandler newInstance(Bundle aBundle) {
        AlertHandler frag = new AlertHandler();
        frag.setArguments(aBundle);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        isABtnPressed =false;
        paramBundle = getArguments();
        theDialog =  builtAlertDialog(paramBundle);

        return theDialog;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        myAlertListener.onDialogNotification(theDialog, 1000); /*TODO Make it Better if time*/
    }


    private AlertDialog builtAlertDialog(Bundle theBundleOfParam){

        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

        if(theBundleOfParam.getInt("Tittle") > 0 ){
            alert.setTitle(theBundleOfParam.getInt("Tittle"));
        }

        if(theBundleOfParam.getInt("Icon") > 0 ){
            alert.setIcon(theBundleOfParam.getInt("Icon"));
        }

        if(theBundleOfParam.getInt("Message") > 0 ){
            alert.setMessage(theBundleOfParam.getInt("Message"));
        }

        if(theBundleOfParam.getInt("PositiveButton") > 0 ){
            alert.setPositiveButton(theBundleOfParam.getInt("PositiveButton"), this);
        }

        if(theBundleOfParam.getInt("NegativeButton") > 0 ){
            alert.setNegativeButton(theBundleOfParam.getInt("NegativeButton"), this);
        }

        return alert.create();

    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        myAlertListener.onDialogNotification(dialog, which);
        isABtnPressed = true;

    }


    public AlertDialog getTheDialog() {
        return theDialog;
    }
}
