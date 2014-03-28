package nemo.wifum;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

public class NetworksActivity extends Activity {
	WifiManager 	wifiManager;
	WifiReceiver 	receiverWifi;
	List<ScanResult> wifiList;
	boolean			isScan = false;	
	ScanResultsAdapter 	srAdapter;
	boolean isLog = false;
	File	logFile = null;
	OutputStream out = null;
	DataOutputStream dos = null;
	
	private SensorManager mSensorManager;
	private SensorListener sensorListener;
	
	//private Sensor mRotationMeter;
	//private float[] mRotation;
	
	private Sensor mOrientationMeter;
	private float azimuth=0;
	public TextView tvOrientation;
	
	private Sensor mAccelerometer;
	private Sensor mMagnetometer;
	private Sensor mGyroscope;
	private float[] mGravity;
	private float[] mGeomagentic;
	//private float[] mGyro;
	private float compass=0;
	//private long startts;
	//public TextView tvZGyro;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scanwifi);    
   
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        receiverWifi = new WifiReceiver();
        registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        
        ListView listView = (ListView)findViewById(R.id.listview);
        srAdapter = new ScanResultsAdapter(this, wifiList);
        listView.setAdapter(srAdapter);
        
        final ToggleButton togglebutton = (ToggleButton) findViewById(R.id.scan);
        togglebutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on clicks
                if (togglebutton.isChecked()) {
                	if(wifiManager == null)
                	{
                		wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                		registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
                	}
                	wifiManager.startScan(); 
                   isScan = true;
                } 
                else {
                	isScan = false;
                }
            }
        });
        
        final ToggleButton logButton = (ToggleButton) findViewById(R.id.onAPLog);
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
            			String fname = t.format("Wifum_ap_%Y%m%d%H%M%S.xml").toString();
            			logFile = new File(wiFumdir,fname);
            			try {
              		     out = new BufferedOutputStream(new FileOutputStream(logFile));
              		     dos = new DataOutputStream(out);
            			} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}   
            			/****
              		try {
							dos.writeBytes("<?xml version='1.0' encoding='utf-8'?>\n");
							dos.flush();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
              		 ***/
              	  
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
        
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
       // mRotationMeter = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        
        //mOrientationMeter = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        tvOrientation = (TextView)findViewById(R.id.tvSensor_orientation);
		//mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
		//mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		//Log.e("NetworksActivity", "Gyroscope size:" +Integer.toString(mSensorManager.getSensorList(Sensor.TYPE_GYROSCOPE).size()));
		mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
		//tvZGyro = (TextView) findViewById(R.id.tvSensor_zgyro);
        
		sensorListener = new SensorListener();
	}
	
	protected void onResume() {
	    registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
	  //  mSensorManager.registerListener(sensorListener, mRotationMeter,SensorManager.SENSOR_DELAY_UI);
	    //mSensorManager.registerListener(sensorListener,mOrientationMeter,SensorManager.SENSOR_DELAY_GAME);
	    //mSensorManager.registerListener(sensorListener, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
	    //mSensorManager.registerListener(sensorListener, mMagnetometer, SensorManager.SENSOR_DELAY_UI);
	    mSensorManager.registerListener(sensorListener, mGyroscope, SensorManager.SENSOR_DELAY_NORMAL);
	 //   startts = 0;
	    super.onResume();
	}
	
	protected void onPause() {
	    unregisterReceiver(receiverWifi);
	    mSensorManager.unregisterListener(sensorListener);
	    super.onPause();
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
			srAdapter.setWifiList(wifiList);
		    
			if(isLog)
			{
				ScanResult sr;
				Date currentdate = new Date();
          	  	long mtime = currentdate.getTime();
          	  	float currentOrientation = azimuth;
				for(int i=0;i<wifiList.size();i++)
				{
					sr = wifiList.get(i);
					//String str = String.format("<ap time='%d' orientation='%.2f' ssid='%s' bssid='%s' signal=%d frequency=%d security='%s' >",mtime,currentOrientation,sr.SSID.toString(),sr.BSSID.toString(),sr.level,sr.frequency,sr.capabilities.toString());
					String str = String.format("%d,%.2f,%s,%s,%d,%d,'%s'\n",mtime,currentOrientation,sr.SSID.toString(),sr.BSSID.toString(),sr.level,sr.frequency,sr.capabilities.toString());
					try {
						dos.writeBytes(str);
						//dos.writeBytes("</ap>\n");
						dos.flush();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				
			}
			if(isScan){
				this.mHandler.removeCallbacks(this.mUpdateTimeTask);
		        this.mHandler.postDelayed(this.mUpdateTimeTask, 500);			
			}
			else {
				this.mHandler.removeCallbacks(this.mUpdateTimeTask);			
			}
	        
		}

	}
	
	public class SensorListener implements SensorEventListener {
		private static final float NS2S = 1.0f / 1000000000.0f;
		private final float[] deltaRotationVector = new float[4];
	    private double gyroDeltaTime ;
	    private float gyroTimeStamp ;
	    private static final float EPSILON = 1.0f / 1000000000.0f;
	    private float []rotAngle = new float[3];
	    
		@Override
		public void onAccuracyChanged(Sensor arg0, int arg1) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onSensorChanged(SensorEvent event) {
			if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
				if (gyroDeltaTime != 0) {
					double dT = (event.timestamp - gyroDeltaTime) * NS2S;
				    //default set counter-clockwise as positive
					//for my usage: set clockwise as positive
				    float axisX = - event.values[0]; 
				    float axisY = - event.values[1];
				    float axisZ = - event.values[2];
				    float angleX = (float) (axisX*dT);
				    float angleY = (float) (axisY*dT);
				    float angleZ = (float) (axisZ*dT);
				    
				    float rad2deg = (float)(180.0/ Math.PI);
				    rotAngle[0] = (rotAngle[0] + angleX*rad2deg)%360;
				    rotAngle[1] = (rotAngle[1] + angleY*rad2deg)%360;
				    rotAngle[2] = (rotAngle[2] + angleZ*rad2deg)%360;
				    azimuth = rotAngle[2];
				   // Log.e("Sensor timestamp:",Double.toHexString(event.timestamp) + " " +String.format("%f,%f,%f" ,axisX,axisY,axisZ));
				    Log.e("Sensor rotation angle:",String.format("%f,%f,%f",rotAngle[0],rotAngle[1],rotAngle[2]));
				    tvOrientation.setText(Float.toString(rotAngle[2]));
				}
				gyroDeltaTime = event.timestamp;
			}
		}
		
	};
	
}
