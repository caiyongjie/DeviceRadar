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
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.format.Time;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

public class ConfigActivity extends Activity {
	WifiManager 	wifiManager;
	WifiReceiver 	receiverWifi;
	List<ScanResult> wifiList;
	
	LocationManager locationManager ;
	String locProvider_GPS;
	String locProvider_Net;
	List<Location> gpsLocList;
	List<Location> netLocList;
	LocationAdapter gpsLocAdapter;
	LocationAdapter netLocAdapter;
	Location cur_gpsLoc= null;
	Location cur_netLoc = null;
	
	boolean	isScan = false;
	boolean isLog = false;
	File	logFile = null;
	OutputStream outLog = null;
	DataOutputStream dosLog = null; 
	
	File	apFile = null;
	OutputStream outAp = null;
	DataOutputStream dosAp = null; 
	
	File	locFile = null;
	OutputStream outLoc = null;
	DataOutputStream dosLoc = null; 
	
	File wiFumdir = null;
	int		logNum = 0;
	
	TextView logTV;
	TextView apTV ;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logging);
        
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        receiverWifi = new WifiReceiver();
        registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        locProvider_Net = LocationManager.NETWORK_PROVIDER;
        locProvider_GPS = LocationManager.GPS_PROVIDER;
        
        netLocList = new ArrayList<Location>();
        gpsLocList = new ArrayList<Location>();
        
        wiFumdir = new File(Environment.getExternalStorageDirectory(), "/WiFum");
        if (!wiFumdir.exists())
        	wiFumdir.mkdir();
        
        logTV = (TextView) findViewById(R.id.logNum);
        apTV = (TextView) findViewById(R.id.apNum);
        final TextView netTV = (TextView) findViewById(R.id.gpsNet);
        final TextView gpsTV = (TextView) findViewById(R.id.gpsAssit);
     // Define a listener that responds to location updates
        final LocationListener netLocListener = new LocationListener() {
            public void onLocationChanged(Location location) {
              // Called when a new location is found by the network location provider.
            	netLocList.add(location);  
            	cur_netLoc = location;
            	
            	long mtime = location.getTime();
        		
        		double lat = location.getLatitude();
        		double lon = location.getLongitude();
        		double alt = location.getAltitude();
        		String gps = String.format("%f,%f,%f", lat,lon,alt);
        		netTV.setText(gps);
        		
            	if(isLog)
            	{
            		float acc = location.getAccuracy();
            		float speed = location.getSpeed();
            		
            		String str = String.format("<net_provider time='%d' gps='%s' accuracy=%f speed=%f >",mtime,gps,acc,speed);
            		try {
            			dosLoc.writeBytes(str);
						dosLoc.writeBytes("</net_provider>\n");
						dosLoc.flush();
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
            	  cur_gpsLoc = location;
            	  
            	  double lat = location.getLatitude();
            	  double lon = location.getLongitude();
            	  double alt = location.getAltitude();
            	  String gps = String.format("%f,%f,%f", lat,lon,alt);
            	  gpsTV.setText(gps);
            	  
            	  if(isLog)
              	{
              		long mtime = location.getTime();
            		
              		float acc = location.getAccuracy();
              		float speed = location.getSpeed();
              		
              		String str = String.format("<gps_provider time='%d' gps='%s' accuracy=%f speed=%f >",mtime,gps,acc,speed);
              		try {
              			dosLoc.writeBytes(str);
  						dosLoc.writeBytes("</gps_provider>\n");
  						dosLoc.flush();
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
            
        final ToggleButton togglebutton = (ToggleButton) findViewById(R.id.isScan_conf);
        togglebutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on clicks
                if (togglebutton.isChecked()) {
                	isScan = true;
                	if(wifiManager == null)
                	{
                		wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                		registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
                	}
                	wifiManager.startScan();
                	
                	locationManager.requestLocationUpdates(locProvider_Net, 0, 0, netLocListener);
              	  	locationManager.requestLocationUpdates(locProvider_GPS, 0, 0, gpsLocListener);
                   
                } 
                else {
                	isScan = false;
                	locationManager.removeUpdates(netLocListener);
                  	locationManager.removeUpdates(gpsLocListener);
                }
            }
        });
        
        final ToggleButton logButton = (ToggleButton) findViewById(R.id.isLog_conf);
        logButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on clicks
                if (logButton.isChecked()) {                	      
              	  	isLog= true;
              	      
              	  	Date currentdate = new Date();
              	  	long mtime = currentdate.getTime();
            		Time t = new Time();
            		t.set(mtime);
            		String fname = t.format("Wifum_log_%Y%m%d%H%M%S.xml").toString();
            		logFile = new File(wiFumdir,fname);
            		fname = t.format("Wifum_loc_%Y%m%d%H%M%S.xml").toString();
            		locFile = new File(wiFumdir,fname);
            		fname = t.format("Wifum_ap_%Y%m%d%H%M%S.xml").toString();
            		apFile = new File(wiFumdir,fname);
            			try {
              		     outLog = new BufferedOutputStream(new FileOutputStream(logFile));
              		     dosLog = new DataOutputStream(outLog);
              		     
              		     outLoc = new BufferedOutputStream(new FileOutputStream(locFile));
            		     dosLoc = new DataOutputStream(outLoc);
            		     
            		     outAp = new BufferedOutputStream(new FileOutputStream(apFile));
              		     dosAp = new DataOutputStream(outAp);
            			} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}     
              		try {
							dosLog.writeBytes("<?xml version='1.0' encoding='utf-8'?>\n");
							dosLog.flush();
							
							dosLoc.writeBytes("<?xml version='1.0' encoding='utf-8'?>\n");
							dosLoc.flush();
							
							dosAp.writeBytes("<?xml version='1.0' encoding='utf-8'?>\n");
							dosAp.flush();
							
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
              		 
              	  
                } 
                else {
                	isLog = false;
                	if(outLog!=null){
						try {
							outLog.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                	}
                	if(dosLog!=null){
						try {
							dosLog.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                	}
                	
                	if(outLoc!=null){
						try {
							outLoc.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                	}
                	if(dosLoc!=null){
						try {
							dosLoc.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                	}
                	
                	if(outAp!=null){
						try {
							outAp.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                	}
                	if(dosAp!=null){
						try {
							dosAp.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                	}
                }
            }
        });
        
        
	}
	
	
	public class WifiReceiver extends BroadcastReceiver {
		private Handler mHandler = new Handler();
	    private Runnable mUpdateTimeTask = new Runnable()
	    {
	        public void run()
	        {
	          wifiManager.startScan();
	        }
	    };
	    
		@Override
		public void onReceive(Context arg0, Intent arg1) {
			wifiList = wifiManager.getScanResults();
			String str = String.format("%d aps",wifiList.size());
			apTV.setText(str);
			if(isLog)
			{
				ScanResult sr;
				Date currentdate = new Date();
          	  	long mtime = currentdate.getTime();
          	  	
          	  	str = String.format("<log time='%d'>\n", mtime);
          	  	try {
					dosLog.writeBytes(str);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
          	  	if(cur_gpsLoc!=null){
	          	  	long ltime = cur_gpsLoc.getTime();
	          		
	          		double lat = cur_gpsLoc.getLatitude();
	          		double lon = cur_gpsLoc.getLongitude();
	          		double alt = cur_gpsLoc.getAltitude();
	          		String gps = String.format("%f,%f,%f", lat,lon,alt);
	          		
	          		float acc = cur_gpsLoc.getAccuracy();
	          		float speed = cur_gpsLoc.getSpeed();
	          		
	          		str = String.format("<gps_provider time='%d' gps='%s' accuracy=%f speed=%f >",ltime,gps,acc,speed);
	          		try {
	          			dosLog.writeBytes(str);
						dosLog.writeBytes("</gps_provider>\n");
					} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
					}
          	  	}
          	  if(cur_netLoc!=null){
	          	  	long ltime = cur_netLoc.getTime();
	          		
	          		double lat = cur_netLoc.getLatitude();
	          		double lon = cur_netLoc.getLongitude();
	          		double alt = cur_netLoc.getAltitude();
	          		String gps = String.format("%f,%f,%f", lat,lon,alt);
	          		
	          		float acc = cur_netLoc.getAccuracy();
	          		float speed = cur_netLoc.getSpeed();
	          		
	          		str = String.format("<net_provider time='%d' gps='%s' accuracy=%f speed=%f >",ltime,gps,acc,speed);
	          		try {
	          			dosLog.writeBytes(str);
						dosLog.writeBytes("</gps_provider>\n");
					} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
					}
        	  	}
          	  
				for(int i=0;i<wifiList.size();i++)
				{
					sr = wifiList.get(i);
					str = String.format("<ap time='%d' ssid='%s' bssid='%s' signal=%d frequency=%d security='%s' >",mtime,sr.SSID.toString(),sr.BSSID.toString(),sr.level,sr.frequency,sr.capabilities.toString());
					try {
						dosAp.writeBytes(str);
						dosAp.writeBytes("</ap>\n");
						dosAp.flush();
						
						dosLog.writeBytes(str);
						dosLog.writeBytes("</ap>\n");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				try {
					dosLog.writeBytes("</log>\n");
					dosLog.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				logNum +=1;
				str = String.format("%d logs",logNum);
				logTV.setText(str);			
				
			}
			if(isScan){
				this.mHandler.removeCallbacks(this.mUpdateTimeTask);
		        this.mHandler.postDelayed(this.mUpdateTimeTask, 2000);			
			}
			else {
				this.mHandler.removeCallbacks(this.mUpdateTimeTask);			
			}
	        
		}

	}
}
