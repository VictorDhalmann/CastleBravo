package API_Com.Modules.Wifi;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.concurrent.atomic.AtomicBoolean;

import API_Com.Modules.ThreadMessage;
import API_Com.Modules.Wifi.Wifi.SocketState;

/**
 * Created by Guillaumee on 26/03/2016.
 */
public class WifiThread extends Thread {

    private Wifi myWifiToRun;
    private AtomicBoolean canRun;
    private AtomicBoolean isPaused;

    public WifiThread(Wifi aWifiModule) {
        myWifiToRun = aWifiModule;
        canRun = new AtomicBoolean(true);
        isPaused = new AtomicBoolean(false);
    }


    @Override
    public void run() {
        super.run();

       try{

           while(canRun.get()){
                Log.d("Thread","Main Loop");
               if(isPaused.get()){
                    Wait();
               }
               sleep(10000);
           }

       } catch (Exception e) {

       }

        Log.d("Thread","Run over");

    }

    private void sendAInterThreadMessage(String key, ThreadMessage theMessage) {
        Bundle messageBundle = new Bundle();
        Message myMessage;
        Handler theThreadHandler;

        theThreadHandler = myWifiToRun.getThreadHandler();
        myMessage = theThreadHandler.obtainMessage();

        messageBundle.putParcelable(key, theMessage);
        myMessage.setData(messageBundle);
        theThreadHandler.sendMessage(myMessage);

    }

    private void Wait() throws Exception{

        Log.d("WifiThread","Pause");
        while(canRun.get() && isPaused.get() ){
            sleep(250);
        }
        Log.d("WifiThread","Pause finish");
    }



    public AtomicBoolean Run(){return canRun;}

    public AtomicBoolean Pause() {return isPaused;}

}
