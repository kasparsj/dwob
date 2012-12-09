package lv.kasparsj.android.dwob;

import lv.kasparsj.android.dwob.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class DwobActivity extends Activity implements SharedPreferences.OnSharedPreferenceChangeListener {
	
	private DwobApp app;
	private ProgressDialog dialog;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Log.i("test", "DwobActivity::onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        app = (DwobApp) getApplication();
        dialog = new ProgressDialog(this);
    }
    
    public void onResume() {
    	super.onResume();
    	
        SharedPreferences prefs = app.getSharedPreferences();
        prefs.registerOnSharedPreferenceChangeListener(this);
    	
    	updateView();
    	if (app.isOutdated())
    		app.update();
    }
    
    public void onPause() {
    	super.onPause();
    	
    	SharedPreferences prefs = app.getSharedPreferences();
    	prefs.unregisterOnSharedPreferenceChangeListener(this);
    }
    
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
    	Log.i("test", "DwobActivity::onSharedPreferenceChanged ("+key+(key.equals("loading") ? "="+prefs.getBoolean(key, false) : "")+")");
    	if (key == "loading") {
    		if (prefs.getBoolean("loading", false)) {
        		dialog.setMessage(getString(R.string.widget_loading));
        		dialog.setCancelable(false);
        		dialog.show();
    		}
    		else {
    	    	if (dialog.isShowing()) {
    				dialog.dismiss();
    	    	}
				if (prefs.getBoolean("success", false)) {
					updateView();
				}
				else {
					if (app.getDescription().length() == 0) {
						WebView descrView = (WebView) findViewById(R.id.description);
						descrView.loadDataWithBaseURL(null, getString(R.string.activity_error), "text/html", "UTF-8", null);
					}
					CharSequence text = getString(R.string.widget_error);
					Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
				}
    		}
    	}
    }
    
    public void updateView() {
    	Log.i("test", "DwobActivity::updateView");
		setTitle(app.getTitle());
		
    	WebView descrView = (WebView) findViewById(R.id.description);
    	descrView.setWebViewClient(new WebViewClient() {  
		    public boolean shouldOverrideUrlLoading(WebView view, String url)  {
		    	Intent intent = new Intent("android.intent.action.VIEW");
		    	Uri data = Uri.parse(url);
		    	if (url.endsWith(".mp3"))
		    		intent.setDataAndType(data, "audio/mp3");
		    	else
		    		intent.setData(data);
		    	view.getContext().startActivity(intent);
		    	return true;
            }
        });  
        descrView.loadDataWithBaseURL(null, app.getDescription(), "text/html", "UTF-8", null);
    }
}