package nemo.wifum;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.Time;
import android.view.View;
import android.widget.ListView;
import android.widget.ToggleButton;

public class LocationActivity extends Activity {
	LocationManager locationManager ;
	String locProvider_GPS;
	String locProvider_Net;
	List<Location> gpsLocList;
	List<Location> netLocList;
	LocationAdapter gpsLocAdapter;
	LocationAdapter netLocAdapter;
	boolean	isLoc = false;
	boolean isLog = false;
	File	logFile = null;
	OutputStream out = null;
	DataOutputStream dos = null;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loc);
        
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        locProvider_Net = LocationManager.NETWORK_PROVIDER;
        locProvider_GPS = LocationManager.GPS_PROVIDER;
        
        ListView listView1 = (ListView)findViewById(R.id.locList1);
        ListView listView2 = (ListView)findViewById(R.id.locList2);
        netLocList = new ArrayList<Location>();
        netLocAdapter = new LocationAdapter(this, netLocList);
        listView1.setAdapter(netLocAdapter);
        gpsLocList = new ArrayList<Location>();
        gpsLocAdapter = new LocationAdapter(this, gpsLocList);
        listView2.setAdapter(gpsLocAdapter);
        
        
     // Define a listener that responds to location updates
        final LocationListener netLocListener = new LocationListener() {
            public void onLocationChanged(Location location) {
              // Called when a new location is found by the network location provider.
            	netLocList.add(location);
            	netLocAdapter.setLocList(netLocList);     
            	
            	if(isLog)
            	{
            		long mtime = location.getTime();
            		
            		double lat = location.getLatitude();
            		double lon = location.getLongitude();
            		double alt = location.getAltitude();
            		String gps = String.format("%f,%f,%f", lat,lon,alt);
            		
            		float acc = location.getAccuracy();
            		float speed = location.getSpeed();
            		
            		String str = String.format("<net_provider time='%d' gps='%s' accuracy=%f speed=%f >",mtime,gps,acc,speed);
            		try {
            			dos.writeBytes(str);
						dos.writeBytes("</net_provider>\n");
						dos.flush();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

            	}
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
          };
          
         final LocationListener gpsLocListener = new LocationListener() {
              public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
            	  gpsLocList.add(location);
            	  gpsLocAdapter.setLocList(gpsLocList);   
            	  
            	  if(isLog)
              	{
              		long mtime = location.getTime();
              		
              		double lat = location.getLatitude();
              		double lon = location.getLongitude();
              		double alt = location.getAltitude();
              		String gps = String.format("%f,%f,%f", lat,lon,alt);
              		
              		float acc = location.getAccuracy();
              		float speed = location.getSpeed();
              		
              		String str = String.format("<gps_provider time='%d' gps='%s' accuracy=%f speed=%f >",mtime,gps,acc,speed);
              		try {
              			dos.writeBytes(str);
  						dos.writeBytes("</gps_provider>\n");
  						dos.flush();
  					} catch (IOException e) {
  						// TODO Auto-generated catch block
  						e.printStackTrace();
  					}

              	}
              }

              public void onStatusChanged(String provider, int status, Bundle extras) {}

              public void onProviderEnabled(String provider) {}

              public void onProviderDisabled(String provider) {}
            };
          final ToggleButton togglebutton = (ToggleButton) findViewById(R.id.onLoc);
          togglebutton.setOnClickListener(new View.OnClickListener() {
              public void onClick(View v) {
                  // Perform action on clicks
                  if (togglebutton.isChecked()) {
                	//  if (!locationManager.isProviderEnabled(locProvider_Net))
                	      
                	  isLoc= true;
                	  // Register the listener with the Location Manager to receive location updates
                	  locationManager.requestLocationUpdates(locProvider_Net, 0, 0, netLocListener);
                	  locationManager.requestLocationUpdates(locProvider_GPS, 0, 0, gpsLocListener);
                  } 
                  else {
                  	isLoc = false;
                  	locationManager.removeUpdates(netLocListener);
                  	locationManager.removeUpdates(gpsLocListener);
                  }
              }
          });
          
          final ToggleButton logButton = (ToggleButton) findViewById(R.id.onLog);
          logButton.setOnClickListener(new View.OnClickListener() {
              public void onClick(View v) {
                  // Perform action on clicks
                  if (logButton.isChecked()) {                	      
                	  	isLog= true;
                	  	File wiFumdir = new File(Environment.getExternalStorageDirectory(), "/WiFum");
                        if (!wiFumdir.exists())
                        	wiFumdir.mkdir();
                        
                	  	Date currentdate = new Date();
                	  	long mtime = currentdate.getTime();
              			Time t = new Time();
              			t.set(mtime);
              			String fname = t.format("Wifum_loc_%Y%m%d%H%M%S.xml").toString();
              			logFile = new File(wiFumdir,fname);
              			try {
                		     out = new BufferedOutputStream(new FileOutputStream(logFile));
                		     dos = new DataOutputStream(out);
              			} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}     
                		try {
							dos.writeBytes("<?xml version='1.0' encoding='utf-8'?>\n");
							dos.flush();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                		 
                	  
                  } 
                  else {
                  	isLog = false;
                  	if(out!=null){
						try {
							out.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                  	}
                  	if(dos!=null){
						try {
							dos.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                  	}
                  }
              }
          });
       

	}
	
	protected void onDestroy()
	{
		super.onDestroy();
		if(out!=null){
			try {
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
      	}
      	if(dos!=null){
			try {
				dos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
      	}
		
	}
}
