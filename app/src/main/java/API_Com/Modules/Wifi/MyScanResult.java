package API_Com.Modules.Wifi;

import android.net.wifi.ScanResult;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Guillaumee on 06/04/2016.
 */
public class MyScanResult implements Parcelable {

    private ScanResult myScanResult;
    private int mySignalLevel;

    public MyScanResult(ScanResult aScanResult, int aSignalLvl){
        myScanResult = aScanResult;
        mySignalLevel = aSignalLvl;
    }

    protected MyScanResult(Parcel in) {
        myScanResult = in.readParcelable(ScanResult.class.getClassLoader());
        mySignalLevel = in.readInt();
    }

    /*********************************************** GETTER ***********************************************/
    public ScanResult getDataNetwork() {return myScanResult;}

    public int getSignalLevel(){
        return mySignalLevel;
    }

    /*********************************************** PARCELABLE ***********************************************/
    public static final Creator<MyScanResult> CREATOR = new Creator<MyScanResult>() {
        @Override
        public MyScanResult createFromParcel(Parcel in) {
            return new MyScanResult(in);
        }

        @Override
        public MyScanResult[] newArray(int size) {
            return new MyScanResult[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(myScanResult, flags);
        dest.writeInt(mySignalLevel);
    }
}
