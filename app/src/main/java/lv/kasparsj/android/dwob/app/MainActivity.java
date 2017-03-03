package lv.kasparsj.android.dwob.app;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
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

import java.util.ArrayList;

import lv.kasparsj.android.app.AppFragment;
import lv.kasparsj.android.dwob.R;
import lv.kasparsj.android.dwob.model.DwobLanguage;

public class MainActivity extends ActionBarActivity implements ActionBar.TabListener {

    ViewPager viewPager;
    AppFragmentsPagerAdapter appFragmentsPagerAdapter;

	private App app;
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
        if (progressDialog == null) {
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
                progressDialog = null;
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