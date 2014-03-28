package nemo.wifum;

import java.util.List;

import nemo.wifum.R;

import android.content.Context;
import android.location.Location;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


public class LocationAdapter extends BaseAdapter {
	List<Location> locList;
	Context _context;
	
	public LocationAdapter(Context paramContext, List<Location> paramList)
	{
		_context = paramContext;
		locList = paramList;
	}
	
	@Override
	public int getCount() {
		if(locList != null)
		{
			return locList.size();
		}
		else return 0;
	}

	@Override
	public Object getItem(int arg0) {
		return locList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int paramInt, View paramView, ViewGroup arg2) {
		if(paramView == null)
		{
			paramView = ((LayoutInflater)this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.locrow,null);
		}
		Location localLoc = (Location)getItem(paramInt);
		
		long mtime = localLoc.getTime();
		Time t = new Time();
		t.set(mtime);
		String st = t.format("%Y-%m-%d %H:%M:%S").toString();
		double lat = localLoc.getLatitude();
		double lon = localLoc.getLongitude();
		double alt = localLoc.getAltitude();
		String gps = String.format("%f,%f,%f", lat,lon,alt);
		
		float acc = localLoc.getAccuracy();
		float speed = localLoc.getSpeed();
		
		TextView timeTextView = (TextView)paramView.findViewById(R.id.timestamp);
		timeTextView.setText(st);
		TextView bssidTextView = (TextView)paramView.findViewById(R.id.gps);
		bssidTextView.setText(gps.toString());
		TextView signalTextView = (TextView)paramView.findViewById(R.id.accurancy);
		signalTextView.setText(Float.toString(acc));
		TextView channelTextView = (TextView)paramView.findViewById(R.id.speed);
		channelTextView.setText(Float.toString(speed));
		
		return paramView;
	}

	public void setLocList(List<Location> ll){
		this.locList = ll;
		notifyDataSetChanged();
	}
}
