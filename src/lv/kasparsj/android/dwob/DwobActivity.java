package lv.kasparsj.android.dwob;

import lv.kasparsj.android.dwob.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.util.Locale;

public class DwobActivity extends Activity implements SharedPreferences.OnSharedPreferenceChangeListener {
	
	private DwobApp app;
	private ProgressDialog progressDialog;
	private HelpDialog helpDialog;
	private boolean recreateOptionsMenu = true;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Log.i("test", "DwobActivity::onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        app = (DwobApp) getApplication();
        if (app.showHelpOnStart()) {
        	showHelp();
        }
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
    
    public void onStop() {
    	super.onStop();
    	
    	if (progressDialog != null)
    		progressDialog.cancel();
    	if (helpDialog != null)
    		helpDialog.cancel();
    }
    
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
    	Log.i("test", "DwobActivity::onSharedPreferenceChanged ("+key+(key.equals("loading") ? "="+prefs.getBoolean(key, false) : "")+")");
    	if (key == "loading") {
    		if (prefs.getBoolean("loading", false)) {
    			showProgress();
    		}
    		else {
    	    	if (progressDialog != null) {
    	    		progressDialog.cancel();
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
    	descrView.getSettings().setDefaultTextEncodingName("utf-8");
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
    	String head = "<head><style>@font-face {font-family: 'myface';src: url('Tahoma.ttf');}body {font-family: 'myface';}</style></head>";
    	String htmlData = "<html>" + head + "<body>" + app.getDescription() + "</body></html>";
    	descrView.loadDataWithBaseURL("file:///android_asset/", htmlData, "text/html", "UTF-8", null);
    }
    
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
    	if (recreateOptionsMenu) { 
    		menu.clear();
	        MenuInflater inflater = getMenuInflater();
	        inflater.inflate(R.menu.menu, menu);
	        MenuItem changeLang = menu.findItem(R.id.change_lang);
	        SubMenu langMenu = changeLang.getSubMenu();
	        if (app.getLanguage().equals(DwobLanguage.ES))
	        	langMenu.findItem(R.id.spanish).setChecked(true);
	        else if (app.getLanguage().equals(DwobLanguage.PT))
	        	langMenu.findItem(R.id.portuguese).setChecked(true);
	        else if (app.getLanguage().equals(DwobLanguage.IT))
	        	langMenu.findItem(R.id.italian).setChecked(true);
            else if (app.getLanguage().equals(DwobLanguage.ZH))
                langMenu.findItem(R.id.chinese).setChecked(true);
            else if (app.getLanguage().equals(DwobLanguage.FR))
                langMenu.findItem(R.id.french).setChecked(true);
	        else
	        	langMenu.findItem(R.id.english).setChecked(true);
    	}
        return super.onPrepareOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.english:
                app.setLanguage(DwobLanguage.EN);
                recreateOptionsMenu = true;
                return true;
            case R.id.spanish:
                app.setLanguage(DwobLanguage.ES);
            	recreateOptionsMenu = true;
            	return true;
            case R.id.portuguese:
                app.setLanguage(DwobLanguage.PT);
            	recreateOptionsMenu = true;
            	return true;
            case R.id.italian:
            	app.setLanguage(DwobLanguage.IT);
            	recreateOptionsMenu = true;
            	return true;
            case R.id.chinese:
                app.setLanguage(DwobLanguage.ZH);
                recreateOptionsMenu = true;
                return true;
            case R.id.french:
                app.setLanguage(DwobLanguage.FR);
                recreateOptionsMenu = true;
                return true;
            case R.id.help:
            	showHelp();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    public void showProgress() {
    	boolean restoreHelp = false;
    	if (helpDialog != null && helpDialog.isShowing()) {
    		helpDialog.cancel();
    		restoreHelp = true;
    	}
    	progressDialog = new ProgressDialog(this);
		progressDialog.setMessage(getString(R.string.widget_loading));
		progressDialog.setCancelable(false);
		progressDialog.show();
		if (restoreHelp)
			showHelp();
    }
    
    public void showHelp() {
    	helpDialog = new HelpDialog(this);
    	helpDialog.setTitle(getString(R.string.help));
    	helpDialog.setMessage(getString(R.string.help_msg));
    	helpDialog.setIcon(android.R.drawable.ic_menu_help);
    	helpDialog.show();
    }
    
    public class HelpDialog extends AlertDialog {
    	
    	public HelpDialog(Context context) {
    		super(context);
    	}
    	
    	@Override
    	public void dismiss() {
    		DwobApp app = (DwobApp) getContext().getApplicationContext();
    		if (app.showHelpOnStart())
    			app.dismissHelpOnStart();
    		super.dismiss();
    	}
    	
    	@Override
    	public boolean dispatchTouchEvent(MotionEvent ev) {
    		dismiss();
    	    return super.dispatchTouchEvent(ev);
    	}
    }
}