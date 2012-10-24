package lv.kasparsj.android.dwob;

import java.util.Date;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class LoadFeedTask extends AsyncTask<String, Void, Boolean> {
	
	private Context context;
	private Resources r;
	private ProgressDialog dialog;
	private DwobActivity activity;
	
	public LoadFeedTask(Context context, DwobActivity activity) {
		this.context = context;
		this.r = context.getResources();
		this.activity = activity;
		if (this.activity != null) {
			dialog = new ProgressDialog(activity);
		}
	}
	
	protected void onPreExecute() {
		if (dialog != null) {
			dialog.setMessage(r.getString(R.string.widget_loading));
			dialog.show();
		}
    }
	
	 protected void onPostExecute(final Boolean success) {
		 if (activity != null) {
			 if (success) {
				 activity.updateView();
			 }
			 else {
				 CharSequence text = r.getString(R.string.widget_error);
				 Toast.makeText(context, text, Toast.LENGTH_LONG).show();
			 }
			 if (dialog.isShowing()) {
				 dialog.dismiss();
			 }
		 }
		 Intent broadcastIntent = new Intent(context, DwobWidget.class);
		 broadcastIntent.setAction(r.getString(R.string.action_refresh));
		 context.sendBroadcast(broadcastIntent);
	 }
	
	protected Boolean doInBackground(final String... args) {
    	try {
            // Try querying Pariyatti API for today's word
        	AndroidSaxFeedParser rssParser = new AndroidSaxFeedParser(r.getString(R.string.feed_url));
        	List<Message> messages = rssParser.parse();
        	String title = messages.get(0).getTitle();
        	String description = messages.get(0).getDescription().trim().replaceAll("^<br />", "").trim();
        	DwobApp app = (DwobApp) context.getApplicationContext();
        	// Update if changed
        	if (app.getTitle() != title || app.getDescription() != description) {
        		app.setTitle(title);
        		app.setDescription(description);
        		app.setUpdated(new Date().getTime());
        	}
        	return true;
        } catch (RuntimeException e) {
            Log.e(r.getString(R.string.app_name), e.getMessage(), e);
            return false;
        }
	}
}