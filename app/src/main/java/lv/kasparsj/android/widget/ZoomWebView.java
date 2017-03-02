package lv.kasparsj.android.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.webkit.WebView;

public class ZoomWebView extends WebView
{
    private float zoomLimit = 3.0f;
    private float scaleFactor = 1.f;
    private int defaultZoom;
    private ScaleGestureDetector scaleDetector;
    private SharedPreferences sharedPreferences;
    private String persistKey;

    public ZoomWebView(Context context) {
        super(context);
        initialize();
    }

    public ZoomWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public ZoomWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    public static boolean isSupported() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    }

    private void initialize() {
        if (isSupported()) {
            defaultZoom = getSettings().getTextZoom();
            scaleDetector = new ScaleGestureDetector(getContext(), new ScaleListener());
        }
    }

    public void persistTo(SharedPreferences sharedPreferences, String persistKey) {
        this.sharedPreferences = sharedPreferences;
        this.persistKey = persistKey;
        float zoom = sharedPreferences.getFloat(getSaveKey(), getZoom());
        scaleFactor = zoom / defaultZoom;
        setZoom(zoom);
    }

    private String getSaveKey() {
        return getClass().getName() + "." + persistKey;
    }

    public float getZoomLimit() {
        return zoomLimit;
    }

    public void setZoomLimit(float zoomLimit) {
        this.zoomLimit = zoomLimit;
    }

    public float getZoom() {
        if (isSupported()) {
            return getSettings().getTextZoom();
        }
        return scaleFactor;
    }

    public void setZoom(float zoom) {
        if (isSupported()) {
            getSettings().setTextZoom((int) zoom);
            if (sharedPreferences != null) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putFloat(getSaveKey(), zoom);
                editor.commit();
            }
        }
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent ev) {
        super.onTouchEvent(ev);
        if (isSupported()) {
            scaleDetector.onTouchEvent(ev);
        }
        return true;
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scaleFactor *= detector.getScaleFactor();
            scaleFactor = Math.max(1.0f, Math.min(scaleFactor, zoomLimit));
            setZoom(defaultZoom * scaleFactor);
            return true;
        }
    }
}
