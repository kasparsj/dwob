package lv.kasparsj.android.dwob;

import java.util.Date;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class LoadFeedTask extends AsyncTask<String, Void, Boolean> {
	
	public static final String FEED_URL = "http://feeds.pariyatti.org/dwob";
	public static final String LOADING_TXT = "Loading Daily Words of the Buddha";
	public static final String ACTION_REFRESH = "lv.kasparsj.android.dwob.REFRESH";
	
	private Context context;
	private ProgressDialog dialog;
	private DwobActivity activity;
	
	public LoadFeedTask(Context context, DwobActivity activity) {
		this.context = context;
		this.activity = activity;
		if (this.activity != null) {
			dialog = new ProgressDialog(activity);
		}
	}
	
	protected void onPreExecute() {
		if (dialog != null) {
			dialog.setMessage(LOADING_TXT);
			dialog.show();
		}
    }
	
	 protected void onPostExecute(final Boolean success) {
		 if (activity != null) {
			 if (success) {
				 activity.updateView();
			 }
			 else {
				 CharSequence text = "Failed loading Daily Words of Buddha";
				 Toast.makeText(context, text, Toast.LENGTH_LONG).show();
			 }
			 if (dialog.isShowing()) {
				 dialog.dismiss();
			 }
		 }
		 Intent broadcastIntent = new Intent(context, DwobWidget.class);
		 broadcastIntent.setAction(ACTION_REFRESH);
		 context.sendBroadcast(broadcastIntent);
	 }
	
	protected Boolean doInBackground(final String... args) {
    	try {
            // Try querying Pariyatti API for today's word
    		Log.i("Daily words of the Buddha", "will load feed");
        	AndroidSaxFeedParser rssParser = new AndroidSaxFeedParser(FEED_URL);
        	List<Message> messages = rssParser.parse();
        	DwobApp app = (DwobApp) context.getApplicationContext();
        	app.setTitle(messages.get(0).getTitle());
        	app.setDescription(messages.get(0).getDescription());
        	app.setUpdated(new Date().getTime());
        	return true;
        } catch (RuntimeException e) {
            Log.e("Daily words of the Buddha", e.getMessage(), e);
            return false;
        }
	}
}