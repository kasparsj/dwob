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
	
	private ProgressDialog dialog;
	private Boolean isShowing = false;
	private Boolean isLoading = false;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        dialog = new ProgressDialog(this);
        
        SharedPreferences prefs = ((DwobApp) getApplication()).getSharedPreferences();
        prefs.registerOnSharedPreferenceChangeListener(this);
    }
    
    public void onResume() {
    	super.onResume();
    	isShowing = true;
   		showOrHideDialog();
    	updateView();
    	((DwobApp) getApplication()).update();
    }
    
    public void onPause() {
    	isShowing = false;
    	super.onPause();
    }
    
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
    	Log.i("test", "DwobActivity::onSharedPreferenceChanged");
    	if (key == "loading") {
    		isLoading = prefs.getBoolean("loading", false);
    		showOrHideDialog();
    		if (isShowing && !isLoading) {
				 if (prefs.getBoolean("success", false)) {
					 updateView();
				 }
				 else {
					 DwobApp app = ((DwobApp) getApplication());
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
    
    public void showOrHideDialog() {
    	if (isShowing) {
	    	if (isLoading) {
	    		dialog.setMessage(getString(R.string.widget_loading));
	    		dialog.show();
	    	}
	    	else if (dialog.isShowing()) {
				dialog.dismiss();
			}
    	}
    }
    
    public void updateView() {
    	Log.i("test", "DwobActivity::updateView");
    	DwobApp app = ((DwobApp) getApplication());
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