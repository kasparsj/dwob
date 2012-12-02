package org.pariyatti.dwob;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class DwobActivity extends Activity implements SharedPreferences.OnSharedPreferenceChangeListener {
	private ProgressDialog dialog;
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
    	updateView();
    }
    
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
    	if (key == "loading") {
    		if (prefs.getBoolean("loading", false)) {
    			dialog.setMessage(getString(R.string.widget_loading));
    			dialog.show();
    		}
    		else {
				 if (prefs.getBoolean("success", false)) {
					 updateView();
				 }
				 else {
					 CharSequence text = getString(R.string.widget_error);
					 Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
				 }
				 if (dialog.isShowing()) {
					 dialog.dismiss();
				 }
    		}
    	}
    }
    
    public void updateView() {
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