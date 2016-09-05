package stabs.com.pro_fi;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class add1_activity extends AppCompatActivity {
    WifiManager wifi;
    List<WifiConfiguration> wifis;
   List<String> names=new ArrayList <String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add1_activity_layout);
        wifi=(WifiManager) getSystemService(Context.WIFI_SERVICE);
        wifis=wifi.getConfiguredNetworks();
        WifiConfiguration [] array= new WifiConfiguration[wifis.size()];
        wifis.toArray(array);
        names.add("");
        for(int i=0;i<wifis.size();i++)
        {
            names.add(array[i].SSID.replace("\"", ""));
        }
        Spinner spin =(Spinner)findViewById(R.id.spinner);
        ArrayAdapter<String> adapter= new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_dropdown_item, names);
        spin.setAdapter(adapter);
    }
}
