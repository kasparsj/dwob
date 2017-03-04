package lv.kasparsj.android.dwob.app;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;

import net.hockeyapp.android.CrashManager;

import java.util.ArrayList;

import lv.kasparsj.android.dwob.R;
import lv.kasparsj.android.dwob.model.DailyWords;
import lv.kasparsj.android.dwob.model.DhammaVerses;
import lv.kasparsj.android.dwob.model.Languages;
import lv.kasparsj.android.dwob.model.PaliWord;

public class MainActivity extends ActionBarActivity implements ActionBar.TabListener {

    ViewPager viewPager;
    AppFragmentsPagerAdapter appFragmentsPagerAdapter;

    private ProgressDialog progressDialog;
    private ArrayList<String> progressStack = new ArrayList<String>();
    private DailyWords dailyWords;
    private PaliWord paliWord;
    private DhammaVerses dhammaVerses;
	private HelpDialog helpDialog;
    private WhatsNewDialog whatsNewDialog;
	private boolean recreateOptionsMenu = true;
    private ViewPager.SimpleOnPageChangeListener onPageChangeListener;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CrashManager.execute(getApplicationContext(), null);

        setContentView(R.layout.activity_main);

        dailyWords = new DailyWords(this);
        if (dailyWords.isLoaded()) {
            dailyWords.updateWidgets();
        }
        paliWord = new PaliWord(this);
        dhammaVerses = new DhammaVerses(this);

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
        onPageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // When swiping between different app sections, select the corresponding tab.
                // We can also use ActionBar.Tab#select() to do this if we have a reference to the
                // Tab.
                actionBar.setSelectedNavigationItem(position);
            }
        };
        viewPager.addOnPageChangeListener(onPageChangeListener);

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

        if (dailyWords.showHelpOnStart()) {
        	showHelp();
        }
        else if (dailyWords.showWhatsNewOnStart()) {
            showWhatsNew();
        }
    }

    public DailyWords getDailyWords() {
        return dailyWords;
    }

    public PaliWord getPaliWord() {
        return paliWord;
    }

    public DhammaVerses getDhammaVerses() {
        return dhammaVerses;
    }

    @Override
    public void onStop() {
    	super.onStop();

        viewPager.removeOnPageChangeListener(onPageChangeListener);

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
            switch (dailyWords.getLanguage()) {
                case Languages.ES:
                    langMenu.findItem(R.id.spanish).setChecked(true);
                    break;
                case Languages.PT:
                    langMenu.findItem(R.id.portuguese).setChecked(true);
                    break;
                case Languages.IT:
                    langMenu.findItem(R.id.italian).setChecked(true);
                    break;
                case Languages.ZH:
                    langMenu.findItem(R.id.chinese).setChecked(true);
                    break;
                case Languages.FR:
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
                dailyWords.setLanguage(Languages.EN);
                recreateOptionsMenu = true;
                return true;
            case R.id.spanish:
                dailyWords.setLanguage(Languages.ES);
            	recreateOptionsMenu = true;
            	return true;
            case R.id.portuguese:
                dailyWords.setLanguage(Languages.PT);
            	recreateOptionsMenu = true;
            	return true;
            case R.id.italian:
                dailyWords.setLanguage(Languages.IT);
            	recreateOptionsMenu = true;
            	return true;
            case R.id.chinese:
                dailyWords.setLanguage(Languages.ZH);
                recreateOptionsMenu = true;
                return true;
            case R.id.french:
                dailyWords.setLanguage(Languages.FR);
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
}