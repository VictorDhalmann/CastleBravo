package API_Com.Modules;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;

import API_Com.Modules.Wifi.Wifi;
import API_Com.Modules.Wifi.Wifi.SocketKey;
import API_Com.Modules.Wifi.Wifi.SocketState;


public class ThreadMessage implements Parcelable {

    private Exception anException;
    private SocketState aState;
    private String sString;


    public ThreadMessage (SocketState aState){
        this.aState = aState;
    }

    public ThreadMessage (SocketState aState, Exception e){
        this.aState = aState;
        this.anException = e;
    }

    public ThreadMessage(SocketState aState, String aString) {
        this.aState = aState;
        sString = aString;
    }


    /*********************************************** GETTER ***********************************************/
    public Exception getException() {
        return anException;
    }

    public SocketState getState() {
        return aState;
    }

    public String getString() {
        return sString;
    }



    /*********************************************** PARCELABLE ***********************************************/
    protected ThreadMessage(Parcel in) {
        sString = in.readString();
    }

    public static final Creator<ThreadMessage> CREATOR = new Creator<ThreadMessage>() {
        @Override
        public ThreadMessage createFromParcel(Parcel in) {
            return new ThreadMessage(in);
        }

        @Override
        public ThreadMessage[] newArray(int size) {
            return new ThreadMessage[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(sString);
    }
}

