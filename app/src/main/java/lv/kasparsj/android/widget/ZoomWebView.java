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
    private float minZoom = 1.0f;
    private float maxZoom = 3.0f;
    private float currentZoom = 1.f;
    private int defaultTextZoom;
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
            defaultTextZoom = getSettings().getTextZoom();
            scaleDetector = new ScaleGestureDetector(getContext(), new ScaleListener());
        }
    }

    public void persistTo(SharedPreferences sharedPreferences, String persistKey) {
        this.sharedPreferences = sharedPreferences;
        this.persistKey = persistKey;
        if (isSupported()) {
            float textZoom = sharedPreferences.getFloat(getSaveKey(), defaultTextZoom);
            setCurrentZoom(textZoom / defaultTextZoom);
        }
    }

    private String getSaveKey() {
        return getClass().getName() + "." + persistKey;
    }

    public float getMinZoom() {
        return minZoom;
    }

    public void setMinZoom(float value) {
        if (value <= 0) {
            throw new RuntimeException("minZoom must be greater than 0");
        }
        minZoom = value;
    }

    public float getMaxZoom() {
        return maxZoom;
    }

    public void setMaxZoom(float value) {
        if (value < minZoom) {
            throw new RuntimeException("maxZoom must be greater than or equal to minZoom");
        }
        maxZoom = value;
    }

    public float getCurrentZoom() {
        return currentZoom;
    }

    public void setCurrentZoom(float value) {
        if (value <= 0) {
            throw new RuntimeException("zoom must be greater than 0");
        }
        currentZoom = Math.max(minZoom, Math.min(value, maxZoom));
        if (isSupported()) {
            int textZoom = (int) (defaultTextZoom * currentZoom);
            getSettings().setTextZoom(textZoom);
            if (sharedPreferences != null) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putFloat(getSaveKey(), textZoom);
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
            currentZoom *= detector.getScaleFactor();
            setCurrentZoom(currentZoom);
            return true;
        }
    }
}
