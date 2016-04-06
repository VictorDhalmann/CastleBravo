package API_Com.CommunicationManager;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;

import java.util.ArrayList;

import API_Com.Modules.AbstractComModule;
import API_Com.Modules.Wifi.Wifi;


/**
 * Created by Guillaumee on 26/03/2016.
 */
public class CommunicationManager {

    private ArrayList<Object> openComList;
    private static Activity theCurrentActivity;
    private ComManagerListener comManagerListener;
    private static CommunicationManager ourInstance = new CommunicationManager();


    private CommunicationManager() {
        openComList = new ArrayList<Object>();
    }

    public int openCom(String sParam) throws Exception {
        try {
            return rootToModule(sParam);

        } catch (Exception e) {
            throw e;
        }
    }

    public void closeCom(int idToClose) {

        Object aObject;

        if (openComList.size() > 0) {
            aObject = openComList.get(idToClose);
            if (aObject instanceof Wifi) ((Wifi) aObject).disconnect();
            /*TODO Deco for bluetooth*/
            /*TODO Deco for Serial*/
            openComList.remove(idToClose);
        }

    }

    public void send(int id, String sToSend) throws Exception{

        Object aObject;
        if (openComList.size() > 0) {
            aObject = openComList.get(id);
            if (aObject instanceof Wifi) ((Wifi) aObject).send(sToSend);
        /*TODO send for bluetooth*/
        /*TODO send for serial*/
        }
    }

    /*TODO Method for enable/disable the buffer mode*/

    /*********************************************** CALLBACK  ***********************************************/


    public interface ComManagerListener{
        void onConnection(int id, Boolean isConnected, String reason);
        void onDisconnection(int id, boolean isDisconnected, Exception e);
        void onDataReceived(int moduleId, ArrayList<String> buffData, Exception e);
    }
    public void setOnComManagerListener(ComManagerListener listener){
        comManagerListener = listener;
    }


    /*********************************************** Root to Module ***********************************************/

    private int rootToModule(String sParam) throws Exception {

        try {
            if (sParam.startsWith("WIFI:")) return handleWifiRoot(sParam);
            /*TODO root for bluetooth*/
            /*TODO root for serial*/
            else throw new Exception("Root argument invalid");

        } catch (Exception e) {
            throw e;
        }

    }


    /*********************************************** WIFI ***********************************************/
    private int handleWifiRoot(String sParam) throws Exception {

        Wifi aWifiModule = new Wifi(this,theCurrentActivity);
        int id;

        try {
            if (listContainModule("WIFI"))
                throw new Exception("WIFI communication already open. Close it");
            else {
                aWifiModule.parametersChecker(sParam);
                aWifiModule.connect();
                openComList.add(aWifiModule);
                id = openComList.indexOf(aWifiModule);
                aWifiModule.setMyId(id);
                return id;
            }

        } catch (Exception e ) {
            throw e;
        }
    }

    /*********************************************** BLUETOOTH ***********************************************/

    private boolean listContainModule(String sModuleToCheck){

        Object aModule;

        for(int i = 0 ; i < openComList.size(); i++){
            aModule = openComList.get(i);

            switch (sModuleToCheck){
                case "WIFI" :
                    if(aModule instanceof  Wifi) return true;
                    break;

                case "BLUETOOTH" :
                    break;

                case "SERIAL" :
                    break;
            }

        }
        return false;
    }



    /*********************************************** GETTER ***********************************************/

    public static CommunicationManager getInstance(Activity aActivity){
        theCurrentActivity = aActivity;
        return ourInstance;
    }

    /*Callbacks */
    public ComManagerListener getComManagerListener() {
        return comManagerListener;
    }


    public ArrayList<Object> getOpenComList() { return openComList;}


    /*********************************************** SETTER ***********************************************/

    /*TODO activity onPause method handler*/

}
