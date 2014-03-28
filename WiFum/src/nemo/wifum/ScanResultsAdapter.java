package nemo.wifum;

import java.util.List;

import android.content.Context;
import android.database.DataSetObserver;
import android.net.wifi.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ScanResultsAdapter extends BaseAdapter {
	List<ScanResult> wifiList;
	Context _context;
	
	public ScanResultsAdapter(Context paramContext, List<ScanResult> paramList)
	{
		_context = paramContext;
		wifiList = paramList;
	}
	
	@Override
	public int getCount() {
		if(wifiList != null)
		{
			return wifiList.size();
		}
		else return 0;
	}

	@Override
	public Object getItem(int arg0) {
		return wifiList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int paramInt, View paramView, ViewGroup arg2) {
		if(paramView == null)
		{
			paramView = ((LayoutInflater)this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.wifirow,null);
		}
		ScanResult localScanResult = (ScanResult)getItem(paramInt);
		
		TextView ssidTextView = (TextView)paramView.findViewById(R.id.ssid);
		ssidTextView.setText(localScanResult.SSID.toString());
		TextView bssidTextView = (TextView)paramView.findViewById(R.id.bssid);
		bssidTextView.setText(localScanResult.BSSID.toString());
		TextView signalTextView = (TextView)paramView.findViewById(R.id.signal);
		signalTextView.setText(Integer.toString(localScanResult.level));
		TextView channelTextView = (TextView)paramView.findViewById(R.id.channel);
		channelTextView.setText(Integer.toString(localScanResult.frequency));
		TextView securityTextView = (TextView)paramView.findViewById(R.id.security);
		securityTextView.setText(localScanResult.capabilities.toString());
		
		return paramView;
	}

	@Override
    public void registerDataSetObserver(DataSetObserver observer) {
        super.registerDataSetObserver(observer);
	}
	
	public void setWifiList(List<ScanResult> wl){
		this.wifiList = wl;
		notifyDataSetChanged();
	}
}
