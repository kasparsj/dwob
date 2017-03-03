package lv.kasparsj.android.dwob.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import lv.kasparsj.android.app.AppFragment;
import lv.kasparsj.android.dwob.R;
import lv.kasparsj.android.dwob.model.BaseModel;
import lv.kasparsj.android.widget.ZoomWebView;

abstract public class BaseFragment extends AppFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    ZoomWebView descrView;
    protected App app;
    protected MainActivity mainActivity;
    protected Button zoomIn;
    protected Button zoomOut;
    protected BaseModel model;
    protected Handler handler = new Handler();

    public BaseFragment(int layoutId) {
        super(layoutId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        descrView = (ZoomWebView) view.findViewById(R.id.description);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences prefs = app.getSharedPreferences();
        prefs.registerOnSharedPreferenceChangeListener(this);

        if (model.isOutdated()) {
            model.update();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        SharedPreferences prefs = app.getSharedPreferences();
        prefs.unregisterOnSharedPreferenceChangeListener(this);

        hideZoom(0);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        app = (App) mainActivity.getApplication();
        zoomIn = (Button) getActivity().findViewById(R.id.zoom_in);
        zoomOut = (Button) getActivity().findViewById(R.id.zoom_out);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        if (key.equals(model.getNSKey("loading"))) {
            if (prefs.getBoolean(model.getNSKey("loading"), false)) {
                if (mainActivity != null) {
                    mainActivity.pushProgress(this.getClass().getName());
                }
            }
            else {
                if (mainActivity != null) {
                    mainActivity.popProgress(this.getClass().getName());
                }
                if (prefs.getBoolean(model.getNSKey("success"), false)) {
                    updateView();
                }
                else {
                    if (!model.isLoaded()) {
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
        descrView.persistTo(app.getSharedPreferences(), getClass().getSimpleName());
        descrView.getSettings().setDefaultTextEncodingName("utf-8");
        final Point touchDown = new Point();
        descrView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url)  {
                handler.removeCallbacks(showZoomRunnable);
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
        descrView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    touchDown.set((int) event.getX(), (int) event.getY());
                }
                else if (event.getAction() == MotionEvent.ACTION_UP &&
                        touchDown.equals((int) event.getX(), (int) event.getY()) &&
                        ZoomWebView.isTextZoomSupported() &&
                        zoomIn != null && zoomOut != null) {
                    showZoom();
                }
                return false;
            }
        });
        String htmlData = "<html>" + buildHeadHtml() + "<body>" + buildBodyHtml() + "</body></html>";
        descrView.loadDataWithBaseURL("file:///android_asset/", htmlData, "text/html", "UTF-8", null);
    }

    public String buildHeadHtml() {
        return "<head>" +
                "<style>@font-face {font-family: 'myface';src: url('Tahoma.ttf');}body {font-family: 'myface';}</style>" +
                "</head>";
    }

    public String buildBodyHtml() {
        return model.getHtml();
    }

    private View.OnClickListener getZoomClickListener(final float delta) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                descrView.setCurrentZoom(descrView.getCurrentZoom() + delta);
                hideZoom(3000);
            }
        };
    }

    private void showZoom() {
        handler.removeCallbacks(showZoomRunnable);
        handler.postDelayed(showZoomRunnable, 500);
    }

    private void hideZoom(int delay) {
        handler.removeCallbacks(hideZoomRunnable);
        if (delay > 0) {
            handler.postDelayed(hideZoomRunnable, delay);
        }
        else {
            handler.post(hideZoomRunnable);
        }
    }

    private Runnable showZoomRunnable = new Runnable() {
        @Override
        public void run() {
            zoomIn.setVisibility(View.VISIBLE);
            zoomOut.setVisibility(View.VISIBLE);
            zoomIn.setOnClickListener(getZoomClickListener(0.25f));
            zoomOut.setOnClickListener(getZoomClickListener(-0.25f));
            hideZoom(3000);
        }
    };

    private Runnable hideZoomRunnable = new Runnable() {
        @Override
        public void run() {
            zoomIn.setVisibility(View.GONE);
            zoomOut.setVisibility(View.GONE);
        }
    };
}