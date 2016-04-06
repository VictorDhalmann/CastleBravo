package fr.telecom_physique.castlebravo.ActivitiesForDemo;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import API_Com.CommunicationManager.CommunicationManager;
import fr.telecom_physique.castlebravo.R;

public class WifiActivity extends AppCompatActivity {

    private CommunicationManager theComManager;
    private int myId;
    private boolean wasOnPause = false;
    private ArrayList<String> _lElementForList;
    private CustomAdapter _adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_wifi);
        Toolbar _toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(_toolbar);
        setTitle("Wifi");
        _toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        _toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        _lElementForList = new ArrayList<String>();
        ListView _listView = (ListView) findViewById(R.id.list);

        _adapter = new CustomAdapter(this, _lElementForList);
        _listView.setAdapter(_adapter);

        theComManager = CommunicationManager.getInstance(this);

        try {

            myId = theComManager.openCom("WIFI:serverIp=192.168.237.50,serverPort=21,timeOut=1000");

        } catch (Exception e) {

            Log.e("OpenCom", "exception:", e);
            finish();
        }

    }


    @Override
    protected void onResume() {
        super.onResume();

        theComManager.setOnComManagerListener(new CommunicationManager.ComManagerListener() {
            @Override
            public void onConnection(int id, Boolean isConnected, String reason) {
                if (isConnected)
                    Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_LONG).show();
                else {
                    Toast.makeText(getApplicationContext(), "Connection failed : " + reason, Toast.LENGTH_LONG).show();
                    finish();
                }

            }

            @Override
            public void onDisconnection(int id, boolean isDisconnected, Exception e) {
                if (isDisconnected && e == null){
                    Toast.makeText(getApplicationContext(), "Disconnected", Toast.LENGTH_LONG).show();
                    finish();
                }
                else {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    finish();
                }

            }

            @Override
            public void onDataReceived(int moduleId, ArrayList<String> buffData, Exception e) {
                if (buffData != null) {

                    for (int i = 0; i < buffData.size(); i++) {

                        if (buffData.get(i) != null) {

                            _lElementForList.add("Server : " + buffData.get(i));
                            _adapter.notifyDataSetChanged();
                        }
                    }
                }
                if (buffData == null && e != null) {

                    Log.e("ASYNC_EXCEPTION", "Error : ", e);
                    Toast.makeText(getApplicationContext(),"Connection lost", Toast.LENGTH_LONG).show();
                    finish();
                }
            }


        });

        Button Btn_send = (Button) findViewById(R.id.send_button);

        Btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText editText = (EditText) findViewById(R.id.editText);
                String dataToSend = editText.getText().toString();

                try {

                    theComManager.send(myId, dataToSend);

                    _lElementForList.add("Client : " + dataToSend);
                    _adapter.notifyDataSetChanged();
                    editText.setText(""); /* Clear the editText from the data send to the server*/

                } catch (Exception e) {

                    _lElementForList.add("Exception: " + e.getMessage());
                    Log.e("TCP", "S: Error", e);
                    editText.setText(""); /* Clear the editText from the data send to the server*/
                }
            }
        });

    }



    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {

            theComManager.closeCom(myId);

        } catch (Exception e) {

            Log.e("onDestroy", "EXCEPTION:", e);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
    }




}

