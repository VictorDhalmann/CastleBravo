package fr.telecom_physique.castlebravo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;

import API_Com.CommunicationManager.CommunicationManager;
import fr.telecom_physique.castlebravo.ActivitiesForDemo.WifiActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private CommunicationManager theComManager = CommunicationManager.getInstance(this);
    private int id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ImageButton imgBtn_Wifi = (ImageButton) findViewById(R.id.ImgBtn_Wifi);

        imgBtn_Wifi.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
    /* react on a click */
        switch (v.getId()) {

            case R.id.ImgBtn_Wifi:
                Intent intent = new Intent(MainActivity.this, WifiActivity.class);
                startActivity(intent);
                break;
        }
    }


}
