package nemo.wifum;

import java.io.File;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.widget.TabHost;

public class WiFumActivity extends TabActivity {
    /** Called when the activity is first created. */
	static File wiFumdir = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        File wiFumdir = new File(Environment.getExternalStorageDirectory(), "/WiFum");
        if (!wiFumdir.exists())
        	wiFumdir.mkdir();
        Resources res = getResources(); // Resource object to get Drawables
        TabHost tabHost = getTabHost();  // The activity TabHost
        TabHost.TabSpec spec;  // Resusable TabSpec for each tab
        Intent intent;  // Reusable Intent for each tab

        // Create an Intent to launch an Activity for the tab (to be reused)
        intent = new Intent().setClass(this, NetworksActivity.class);

        // Initialize a TabSpec for each tab and add it to the TabHost
        spec = tabHost.newTabSpec("ap").setIndicator("AP",
                          res.getDrawable(R.drawable.ic_tab_networks))
                      .setContent(intent);
        tabHost.addTab(spec);

        // Do the same for the other tabs
        intent = new Intent().setClass(this, ConfigActivity.class);
        spec = tabHost.newTabSpec("config").setIndicator("Config",
                          res.getDrawable(R.drawable.ic_tab_logging))
                      .setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent().setClass(this, LocationActivity.class);
        spec = tabHost.newTabSpec("loc").setIndicator("GLoc",
                          res.getDrawable(R.drawable.ic_tab_map))
                      .setContent(intent);
        tabHost.addTab(spec);

        tabHost.setCurrentTab(0);
    }
}