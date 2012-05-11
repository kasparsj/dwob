package lv.kasparsj.android.dwob;

import java.util.Date;

import lv.kasparsj.android.dwob.R;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.widget.TextView;

public class DwobActivity extends Activity {
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        updateView();
        
        DwobApp app = ((DwobApp) getApplication());
        if (new Date().getTime() - app.getUpdated() > DwobApp.UPDATE_INTERVAL)
        	new LoadFeedTask(getApplicationContext(), this).execute();
    }
    
    public void updateView() {
    	DwobApp app = ((DwobApp) getApplication());
		setTitle(app.getTitle());
		
    	TextView transView = (TextView) findViewById(R.id.translation);
        transView.setText(Html.fromHtml(TextUtils.join("\n<br />\n", app.getTranslation().toArray()).trim().replaceAll("^<br />", "").trim()));
        
    	TextView originalView = (TextView) findViewById(R.id.original);
    	originalView.setText(Html.fromHtml(TextUtils.join("\n<br />\n", app.getOriginal().toArray()).trim().replaceAll("^<br />", "").trim()));
        
        TextView sourceView = (TextView) findViewById(R.id.source);
        sourceView.setText(Html.fromHtml(app.getSource().trim().replaceAll("^<br />", "").trim()));
    }
}