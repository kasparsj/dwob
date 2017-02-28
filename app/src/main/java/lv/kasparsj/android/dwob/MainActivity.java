package lv.kasparsj.android.dwob;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.util.ArrayList;

import lv.kasparsj.android.dwob.model.BaseModel;
import lv.kasparsj.android.dwob.model.DailyWords;
import lv.kasparsj.android.dwob.model.DhammaVerses;
import lv.kasparsj.android.dwob.model.DwobLanguage;
import lv.kasparsj.android.dwob.model.PaliWord;
import lv.kasparsj.android.util.Strings;

public class MainActivity extends ActionBarActivity implements ActionBar.TabListener {

    ViewPager viewPager;
    AppFragmentsPagerAdapter appFragmentsPagerAdapter;

	static private App app;
    private ProgressDialog progressDialog;
    private ArrayList<String> progressStack = new ArrayList<String>();
	private HelpDialog helpDialog;
    private WhatsNewDialog whatsNewDialog;
	private boolean recreateOptionsMenu = true;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appFragmentsPagerAdapter = new AppFragmentsPagerAdapter(this, getSupportFragmentManager());

        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();

        // Specify that the Home/Up button should not be enabled, since there is no hierarchical
        // parent.
        actionBar.setHomeButtonEnabled(false);

        // Specify that we will be displaying tabs in the action bar.
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Set up the ViewPager, attaching the adapter and setting up a listener for when the
        // user swipes between sections.
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(appFragmentsPagerAdapter);
        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // When swiping between different app sections, select the corresponding tab.
                // We can also use ActionBar.Tab#select() to do this if we have a reference to the
                // Tab.
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < appFragmentsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by the adapter.
            // Also specify this Activity object, which implements the TabListener interface, as the
            // listener for when this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(appFragmentsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }

        app = (App) getApplication();
        if (app.showHelpOnStart()) {
        	showHelp();
        }
        else if (app.showWhatsNewOnStart()) {
            showWhatsNew();
        }
    }
    
    public void onStop() {
    	super.onStop();

        if (progressDialog != null) {
            progressDialog.cancel();
        }
    	if (helpDialog != null) {
            helpDialog.cancel();
        }
        if (whatsNewDialog != null) {
            whatsNewDialog.cancel();
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
    	if (recreateOptionsMenu) {
    		menu.clear();
	        MenuInflater inflater = getMenuInflater();
	        inflater.inflate(R.menu.menu, menu);
	        MenuItem changeLang = menu.findItem(R.id.change_lang);
	        SubMenu langMenu = changeLang.getSubMenu();
            switch (app.getLanguage()) {
                case DwobLanguage.ES:
                    langMenu.findItem(R.id.spanish).setChecked(true);
                    break;
                case DwobLanguage.PT:
                    langMenu.findItem(R.id.portuguese).setChecked(true);
                    break;
                case DwobLanguage.IT:
                    langMenu.findItem(R.id.italian).setChecked(true);
                    break;
                case DwobLanguage.ZH:
                    langMenu.findItem(R.id.chinese).setChecked(true);
                    break;
                case DwobLanguage.FR:
                    langMenu.findItem(R.id.french).setChecked(true);
                    break;
                default:
                    langMenu.findItem(R.id.english).setChecked(true);
                    break;
            }
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
            case R.id.whats_new:
                showWhatsNew();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void pushProgress(String target) {
        if (progressDialog != null) {
            boolean restoreHelp = false;
            boolean restoreWhatsNew = false;
            if (helpDialog != null && helpDialog.isShowing()) {
                closeHelp();
                restoreHelp = true;
            }
            else if (whatsNewDialog != null && whatsNewDialog.isShowing()) {
                closeWhatsNew();
                restoreWhatsNew = true;
            }
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage(getString(R.string.widget_loading));
            progressDialog.setCancelable(false);
            progressDialog.show();
            if (restoreHelp) {
                showHelp();
            }
            else if (restoreWhatsNew) {
                showWhatsNew();
            }
        }
        progressStack.add(target);
    }

    public void popProgress(String target) {
        int targetIndex = progressStack.indexOf(target);
        if (targetIndex > -1) {
            progressStack.remove(targetIndex);
            if (progressDialog != null && progressStack.isEmpty()) {
                progressDialog.cancel();
            }
        }
    }

    public void showHelp() {
    	helpDialog = new HelpDialog(this);
    	helpDialog.setTitle(getString(R.string.help));
    	helpDialog.setMessage(getString(R.string.help_msg));
    	helpDialog.setIcon(android.R.drawable.ic_menu_help);
    	helpDialog.show();
    }

    public void closeHelp() {
        helpDialog.cancel();
    }

    public void showWhatsNew() {
        whatsNewDialog = new WhatsNewDialog(this);
        whatsNewDialog.setTitle(getString(R.string.whats_new));
        whatsNewDialog.setMessage(getString(R.string.whats_new_msg));
        whatsNewDialog.setIcon(android.R.drawable.ic_menu_help);
        whatsNewDialog.show();
    }

    public void closeWhatsNew() {
        whatsNewDialog.cancel();
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction fragmentTransaction) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction fragmentTransaction) {

    }

    public static class AppFragmentsPagerAdapter extends FragmentPagerAdapter {

        private Context context;
        private AppFragment dailyWordsFragment;
        private AppFragment paliWordFragment;
        private AppFragment dhammaVersesFragment;

        public AppFragmentsPagerAdapter(Context context, FragmentManager fm) {
            super(fm);
            this.context = context;
        }

        @Override
        public Fragment getItem(int i) {
            switch (i) {
                case 0:
                    dailyWordsFragment = new DailyWordsFragment();
                    return dailyWordsFragment;
                case 1:
                    paliWordFragment = new PaliWordFragment();
                    return paliWordFragment;
                case 2:
                    dhammaVersesFragment = new DhammaVersesFragment();
                    return dhammaVersesFragment;
                default:
                    throw new RuntimeException("Invalid tab requested");
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return context.getResources().getString(R.string.tab_dwob);
                case 1:
                    return context.getResources().getString(R.string.tab_pali);
                case 2:
                    return context.getResources().getString(R.string.tab_goenka);
                default:
                    throw new RuntimeException("Invalid tab requested");
            }
        }
    }

    abstract public static class BaseFragment extends AppFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

        protected BaseModel model;

        public BaseFragment(int layout_id) {
            super(layout_id);
        }

        public void onResume() {
            super.onResume();

            SharedPreferences prefs = app.getSharedPreferences();
            prefs.registerOnSharedPreferenceChangeListener(this);

            if (model.isOutdated()) {
                model.update();
            }
        }

        public void onPause() {
            super.onPause();

            SharedPreferences prefs = app.getSharedPreferences();
            prefs.unregisterOnSharedPreferenceChangeListener(this);
        }

        public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
            if (key.equals(model.getNSKey("loading"))) {
                if (prefs.getBoolean(model.getNSKey("loading"), false)) {
                    ((MainActivity) getActivity()).pushProgress(this.getClass().getName());
                }
                else {
                    ((MainActivity) getActivity()).popProgress(this.getClass().getName());
                    if (prefs.getBoolean(model.getNSKey("success"), false)) {
                        updateView();
                    }
                    else {
                        if (!model.isLoaded()) {
                            WebView descrView = (WebView) getView().findViewById(R.id.description);
                            descrView.loadDataWithBaseURL(null, getString(R.string.activity_error), "text/html", "UTF-8", null);
                        }
                        CharSequence text = getString(R.string.widget_error);
                        Toast.makeText(app, text, Toast.LENGTH_LONG).show();
                    }
                }
            }
        }

        @Override
        public void updateView() {
            WebView descrView = (WebView) getView().findViewById(R.id.description);
            descrView.getSettings().setBuiltInZoomControls(true);
            descrView.getSettings().setSupportZoom(true);
            descrView.getSettings().setDefaultTextEncodingName("utf-8");
            descrView.setWebViewClient(new WebViewClient() {
                public boolean shouldOverrideUrlLoading(WebView view, String url)  {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    Uri data = Uri.parse(url);
                    Context context = view.getContext();
                    if (url.endsWith(".mp3")) {
                        intent.setDataAndType(data, "audio/mp3");
                    }
                    else {
                        intent.setData(data);
                    }
                    context.startActivity(intent);
                    return true;
                }
            });
            String head = "<head><style>@font-face {font-family: 'myface';src: url('Tahoma.ttf');}body {font-family: 'myface';}</style></head>";
            String htmlData = "<html>" + head + "<body>" + buildBodyHtml() + "</body></html>";
            descrView.loadDataWithBaseURL("file:///android_asset/", htmlData, "text/html", "UTF-8", null);
        }

        public String buildBodyHtml() {
            return model.getHtml();
        }
    }

    public static class DailyWordsFragment extends BaseFragment {

        public DailyWordsFragment() {
            super(R.layout.fragment_dwob);
            model = DailyWords.getInstance();
        }

        @Override
        public String buildBodyHtml() {
            DailyWords dailyWords = (DailyWords) model;
            return dailyWords.getHtml() + "<br><br>" +
                    (!Strings.isEmpty(dailyWords.getSource()) ? dailyWords.getSource() + "<br><br>" : "") +
                    (!Strings.isEmpty(dailyWords.getTranslator()) ? dailyWords.getTranslator() + "<br><br>" : "") +
                    (!Strings.isEmpty(dailyWords.getListenLink()) ? "<a href='" + dailyWords.getListenLink() + "'>Listen to Pāli</a><br><br>" : "") +
                    (!Strings.isEmpty(dailyWords.getBookLink()) ? "<a href='" + dailyWords.getBookLink() + "'>View Book<br><br>" : "") +
                    (!Strings.isEmpty(dailyWords.getTipitakaLink()) ? "<a href='" + dailyWords.getTipitakaLink() + "'>View Pāli in Tipitaka</a><br><br>" : "");
        }
    }

    public static class PaliWordFragment extends BaseFragment {

        public PaliWordFragment() {
            super(R.layout.fragment_pali_word);
            model = PaliWord.getInstance();
        }
    }

    public static class DhammaVersesFragment extends BaseFragment {

        public DhammaVersesFragment() {
            super(R.layout.fragment_dhamma_verses);
            model = DhammaVerses.getInstance();
        }
    }

    public static class HelpDialog extends AlertDialog {
    	
    	public HelpDialog(Context context) {
    		super(context);
    	}
    	
    	@Override
    	public void dismiss() {
    		App app = (App) getContext().getApplicationContext();
    		if (app.showHelpOnStart()) {
                app.dismissHelpOnStart();
            }
    		super.dismiss();
    	}
    	
    	@Override
    	public boolean dispatchTouchEvent(MotionEvent ev) {
    		dismiss();
    	    return super.dispatchTouchEvent(ev);
    	}
    }

    public static class WhatsNewDialog extends AlertDialog {

        public WhatsNewDialog(Context context) {
            super(context);
        }

        @Override
        public void dismiss() {
            App app = (App) getContext().getApplicationContext();
            if (app.showWhatsNewOnStart()) {
                app.dismissWhatsNewOnStart();
            }
            super.dismiss();
        }

        @Override
        public boolean dispatchTouchEvent(MotionEvent ev) {
            dismiss();
            return super.dispatchTouchEvent(ev);
        }
    }
}