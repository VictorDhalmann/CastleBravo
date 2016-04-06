package API_Com.Modules.Wifi;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import API_Com.DialogAndAlert.AlertHandler;
import API_Com.DialogAndAlert.NoticeDialogListener;
import API_Com.Modules.AbstractComModule;
import API_Com.CommunicationManager.CommunicationManager;
import API_Com.Modules.ThreadMessage;
import fr.telecom_physique.castlebravo.R;


/**
 * Created by Guillaumee on 26/03/2016.
 */
public class Wifi extends AbstractComModule implements Handler.Callback, NoticeDialogListener {

    private int myId;
    private Socket mySocket;
    private WifiThread myThread;
    private int connectionTimeOut;
    private Handler myThreadHandler;
    private BR_WifiState myBR_State;
    private PrintWriter myPrintWriter;
    private SocketAddress theSocketAddress;
    private BufferedReader myBufferedReader;
    private AtomicBoolean isWifiConnected;
    private Activity anActivity;
    private AlertHandler myAlertWifiDisable;
    private WifiManager theWifiManager;
    private DialogScanResultWifi myDialogScanResultWifi;

    public enum SocketState {
        DISCONNECTED_FROM_SERVER, DISCONNECTED, CONNECTED, CONNECTION_FAILED
    }

    public enum SocketKey {
        CONNECTION_KEY, LISTEN_DATA_KEY
    }


    /**
     * Constructor
     *
     * @param instComManager
     */
    public Wifi(CommunicationManager instComManager, Activity aContext) {
        theComManager = instComManager;
        anActivity = aContext;
        myThreadHandler = new Handler(this);
        myIncomingDataBuffer = new ArrayList<String>();
        isWifiConnected = new AtomicBoolean(false);

        myAlertWifiDisable = AlertHandler.newInstance(bundleAlertWifiDisable());
        myAlertWifiDisable.setOnAlertListener(this);

        myDialogScanResultWifi = DialogScanResultWifi.newInstance();
        // myDialogScanResultWifi.display("Dialog_Wifi", anActivity);
        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        iFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);

        myBR_State = new BR_WifiState(this);
        anActivity.registerReceiver(myBR_State, iFilter);

        theWifiManager = (WifiManager) anActivity.getSystemService(Context.WIFI_SERVICE);

    }

    /**
     * Call on the UI thread. Launch the wifi Thread
     */
    @Override
    public void connect() {
        myThread = new WifiThread(this);
        myIncomingDataBuffer.clear(); /* No old data on a new connection */
        myThread.start();

    }

    /**
     * Call in our tread. Do the connection to the socket
     *
     * @throws Exception
     */
    @Override
    public void doConnection() throws Exception {
        mySocket = new Socket();
        mySocket.connect(theSocketAddress, connectionTimeOut);

        /*Create input reader and output buffer*/
        myPrintWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(mySocket.getOutputStream())), true);
        myBufferedReader = new BufferedReader(new InputStreamReader(mySocket.getInputStream()));

    }

    /**
     * Call on the UI thread. Launch the disconnection from the server
     */
    @Override
    public void disconnect() {
        try {
            mySocket.shutdownOutput();
            mySocket.shutdownInput();
            myThread.Run().set(false);
            callAComManagerListener().onDisconnection(myId, true, null);

        } catch (Exception e) {
            callAComManagerListener().onDisconnection(myId, true, null);
        }

    }

    /**
     * Send data to the server
     *
     * @param sToSend
     */
    @Override
    public void send(String sToSend) {
        /*if (myThread.getCurrentState() == SocketState.CONNECTED) {
            myPrintWriter.println(sToSend);
            myPrintWriter.flush();
        }*/

    }

    /**
     * Call in our thread. Catch incoming data from the server
     *
     * @return
     * @throws Exception
     */
    @Override
    public String listenForData() throws Exception {
        String theData = myBufferedReader.readLine();

        if (myBufferedReader.ready()) {
            //myThread.setCptForDisconnection(0);
            return theData;
        }

        return null;

    }

    /**
     * Check if the connection parameters are Ok
     *
     * @param sToAnalyze
     * @throws Exception
     */
    @Override
    public void parametersChecker(String sToAnalyze) throws Exception {
        syntaxAnalyzer(sToAnalyze);
        splitParam(sToAnalyze);
    }

    /**
     * Check the syntax of the parameters.
     *
     * @param sToAnalyze
     * @throws Exception
     */
    @Override
    protected void syntaxAnalyzer(String sToAnalyze) throws Exception {
        if (!sToAnalyze.contains("serverIp="))
            throw new Exception("WIFI configuration : sub parameter serverIp= missing");
        else if (!sToAnalyze.contains(",serverPort="))
            throw new Exception("WIFI configuration : sub parameter ,serverPort= missing");
        else if (!sToAnalyze.contains(",timeOut="))
            throw new Exception("WIFI configuration : sub parameter ,timeOut= missing");

    }

    /**
     * Split the connection parameters to fetch the Ip address, server port, and timeout
     *
     * @param sToSplit
     */
    @Override
    protected void splitParam(String sToSplit) {
        int posEqual;
        int posComma;
        int theServerPort;
        String theServerAddress;

        posEqual = sToSplit.indexOf("=", 0);
        posComma = sToSplit.indexOf(",");

        /*Fetch the ipAddress */
        theServerAddress = sToSplit.substring(posEqual + 1, posComma);

        posEqual = sToSplit.indexOf("=", posComma);
        posComma = sToSplit.indexOf(",", posEqual);

         /*Fetch the port id */
        theServerPort = Integer.parseInt(sToSplit.substring(posEqual + 1, posComma));

        posEqual = sToSplit.indexOf("=", posComma);

        /*Fetch the time out*/
        connectionTimeOut = Integer.parseInt(sToSplit.substring(posEqual + 1, sToSplit.length()));

         /*Build the socket address */
        theSocketAddress = new InetSocketAddress(theServerAddress, theServerPort);

    }

    /**
     * Handle the message from the thread
     *
     * @param msg
     * @return
     */
    @Override
    public boolean handleMessage(Message msg) {
        ThreadMessage aInterThreadMessage;

        /*Message about the connection*/
        if (msg.getData().containsKey(SocketKey.CONNECTION_KEY.name())) {

            aInterThreadMessage = msg.getData().getParcelable(SocketKey.CONNECTION_KEY.name());

            switch (aInterThreadMessage.getState()) {
                case CONNECTED:
                    callAComManagerListener().onConnection(myId, true, null);
                    break;

                case CONNECTION_FAILED:
                    onConnectionFailed(aInterThreadMessage.getException().getMessage());
                    break;

                case DISCONNECTED_FROM_SERVER:

                    break;

                case DISCONNECTED:
                    this.disconnect();
                    break;

            }

        }

        /*Message about the incoming data*/
        if (msg.getData().containsKey(SocketKey.LISTEN_DATA_KEY.name())) {
            aInterThreadMessage = msg.getData().getParcelable(SocketKey.LISTEN_DATA_KEY.name());

            switch (aInterThreadMessage.getState()) {
                case CONNECTED:
                    myIncomingDataBuffer.clear();
                    myIncomingDataBuffer.add(aInterThreadMessage.getString());
                    callAComManagerListener().onDataReceived(myId, myIncomingDataBuffer, null);
                    break;
            }
        }
        return true;
    }


    private void onConnectionFailed(String theReason) {
        anActivity.unregisterReceiver(myBR_State);
        callAComManagerListener().onConnection(myId, false, theReason);
        myThread.Run().set(false);
        theComManager.getOpenComList().remove(myId);

    }

    /**
     * Create the bundle for the alert dialog which will indicate that the wifi is disable
     *
     * @return
     */
    private Bundle bundleAlertWifiDisable() {
        Bundle theBundle = new Bundle();
        theBundle.putInt("Icon", R.drawable.api_wifi_no_signal);
        theBundle.putInt("Tittle", R.string.alert_no_wifi_tittle);
        theBundle.putInt("PositiveButton", R.string.alert_no_wifi_positive_button);
        theBundle.putInt("NegativeButton", R.string.alert_no_wifi_negative_button);
        theBundle.putInt("Message", R.string.alert_no_wifi_message);

        return theBundle;
    }

    /**
     * Handle pressed button of the disconnected from server alert dialog
     *
     * @param dialog
     * @param which
     */
    @Override
    public void onDialogNotification(DialogInterface dialog, int which) {
        /*Handle pressed button of the no wifi alert dialog*/
        if (dialog.equals(myAlertWifiDisable.getTheDialog())) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    if (!theWifiManager.isWifiEnabled()) {
                        theWifiManager.setWifiEnabled(true);
                    }
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    onConnectionFailed("Wifi is off");
                    break;

                case 1000: /*On fragment detach */
                    onConnectionFailed("Wifi is off");
                    break;
            }

        }
    }


    public int getMyId() {
        return myId;
    }

    public Handler getThreadHandler() {
        return myThreadHandler;
    }

    public WifiThread Thread() {
        return myThread;
    }

    public AlertHandler AlertWifiDisable() {
        return myAlertWifiDisable;
    }

    public DialogScanResultWifi DialogScanResultWifi() {
        return myDialogScanResultWifi;
    }


    public void setMyId(int myId) {
        this.myId = myId;
    }

    /*TODO Method for the on Pause method call, destroys Br */

}
