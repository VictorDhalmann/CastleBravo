package API_Com.Modules;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import API_Com.CommunicationManager.CommunicationManager;

/**
 * Created by Guillaumee on 26/03/2016.
 */
public abstract class AbstractComModule   {

    protected CommunicationManager theComManager;
    protected ArrayList<String> myIncomingDataBuffer;

    public abstract void connect();
    public abstract void doConnection() throws Exception;
    public abstract void disconnect();
    public abstract void send(String sToSend);
    public abstract String listenForData() throws Exception;
    public abstract void parametersChecker(String sToAnalyze)throws Exception;
    protected abstract void syntaxAnalyzer(String sToAnalyze) throws Exception;
    protected  abstract void splitParam(String sToSplit);


    protected CommunicationManager.ComManagerListener callAComManagerListener() {return theComManager.getComManagerListener();}

}
