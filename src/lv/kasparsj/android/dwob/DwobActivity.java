package lv.kasparsj.android.dwob;

import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class DwobActivity extends Activity {
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        updateView();
        
        DwobApp app = ((DwobApp) getApplication());
        if (new Date().getTime() - app.getUpdated() > R.integer.update_period)
        	new LoadFeedTask(getApplicationContext(), this).execute();
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