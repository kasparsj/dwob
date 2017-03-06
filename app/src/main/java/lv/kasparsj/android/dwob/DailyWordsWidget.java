package lv.kasparsj.android.dwob;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.RemoteViews;
import android.widget.TextView;

import lv.kasparsj.android.dwob.app.AppService;
import lv.kasparsj.android.dwob.app.MainActivity;
import lv.kasparsj.android.dwob.model.DailyWords;
import lv.kasparsj.util.Objects;
import lv.kasparsj.android.widget.AutoResizeTextView;

public class DailyWordsWidget extends AppWidgetProvider
{
    public static final String OPTION_APPWIDGET_MIN_WIDTH = "appWidgetMinWidth";
    public static final String OPTION_APPWIDGET_MIN_HEIGHT = "appWidgetMinHeight";

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        context.startService(new Intent(context, AppService.class));
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        Class otherWidgetClass = getClass().equals(DailyWordsWidget.class) ? DailyWordsLargeWidget.class : DailyWordsWidget.class;
        ComponentName componentName = new ComponentName(context, otherWidgetClass);
        int[] ids = AppWidgetManager.getInstance(context).getAppWidgetIds(componentName);
        if (ids.length == 0) {
            context.stopService(new Intent(context, AppService.class));
        }
    }

    protected float getDefaultTextSize(int numLines) {
        switch (numLines) {
            case 1:
            case 2:
            case 3:
            case 4:
                return 13;
            case 5:
                return 10;
        }
        return 8;
    }

    protected int getMaxLines() {
        return 6;
    }

    protected int getMaxLines(Context context, Point widgetSize) {
        int largeMinHeight = (int) (context.getResources().getDimension(R.dimen.widget_large_min_height) / context.getResources().getDisplayMetrics().density);
        if (widgetSize.y < largeMinHeight) {
            return 6;
        }
        return 8;
    }

    private int countTextViewLines(TextView textView, String[] lines, float lineWidth, float density) {
        int numLines = lines.length;
        for (String line : lines) {
            if (textView.getPaint().measureText(line) * density > lineWidth) {
                numLines++;
            }
        }
        return numLines;
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager manager, int[] appWidgetIds) {
        DailyWords dailyWords = new DailyWords(context);
        Resources r = context.getResources();
        if (dailyWords.isOutdated()) {
            Intent intent = new Intent(context, AppService.class);
            intent.setAction(r.getString(R.string.action_update));
            context.startService(intent);
        }
        for (int appWidgetId : appWidgetIds) {
            Point size = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                size = getWidgetSize(manager, appWidgetId);
                if (size.x <= 0 || size.y <= 0) {
                    continue;
                }
            }
            RemoteViews updateViews = createUpdateViews(context, size);
            manager.updateAppWidget(appWidgetId, updateViews);
        }
    }

    protected RemoteViews createUpdateViews(Context context, Point widgetSize) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_words);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && widgetSize != null) {
            updateImageView(context, remoteViews, widgetSize);
        }
        else {
            updateTextView(context, remoteViews);
        }
        PendingIntent pendingIntent = getOnClickPendingIntent(context);
        remoteViews.setOnClickPendingIntent(R.id.words, pendingIntent);
        return remoteViews;
    }

    protected void updateImageView(Context context, RemoteViews remoteViews, Point widgetSize) {
        String text = getText(context);
        AutoResizeTextView autoFitTextView = new AutoResizeTextView(context);
        autoFitTextView.setMinTextSize(8);
        autoFitTextView.setGravity(Gravity.CENTER);
        autoFitTextView.setTextColor(Color.BLACK);
        autoFitTextView.setMaxLines(getMaxLines(context, widgetSize));
        autoFitTextView.setText(text);
        autoFitTextView.layout(0, 0, widgetSize.x, widgetSize.y);
        TextView textView = new TextView(context);
        textView.setText(text);
        textView.setTextSize(autoFitTextView.getTextSize());
        autoFitTextView.setTextSize(adjustTextSize(context, textView, widgetSize.x));
        autoFitTextView.resizeText();
        Bitmap bitmap = Bitmap.createBitmap(widgetSize.x, autoFitTextView.getTextHeight(), Bitmap.Config.ARGB_8888);
        autoFitTextView.draw(new Canvas(bitmap));
        remoteViews.setCharSequence(R.id.words, "setContentDescription", text);
        remoteViews.setImageViewBitmap(R.id.words, bitmap);
    }

    protected void updateTextView(Context context, RemoteViews remoteViews) {
        Resources r = context.getResources();
        String text = getText(context);
        String[] lines = text.split("\r\n|\r|\n");
        if (lines.length > getMaxLines() && text.contains("\n\n")) {
            String[] parts = text.split("\n\n");
            text = parts[0].trim()+"...";
        }
        TextView textView = new TextView(context);
        textView.setText(text);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getDefaultTextSize(lines.length));
        float widgetWidth = 80 * r.getDisplayMetrics().density * 4;
        remoteViews.setTextViewText(R.id.words, text);
        remoteViews.setFloat(R.id.words, "setTextSize", adjustTextSize(context, textView, widgetWidth));
    }

    protected float adjustTextSize(Context context, TextView textView, float widgetWidth) {
        Resources r = context.getResources();
        float padding = r.getDimension(R.dimen.widget_padding);
        float margin = r.getDimension(R.dimen.widget_margin);
        float lineWidth = (widgetWidth - padding * 2 - margin * 2);
        String[] lines = textView.getText().toString().split("\r\n|\r|\n");
        while (lines.length < countTextViewLines(textView, lines, lineWidth, r.getDisplayMetrics().density)) {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, (textView.getTextSize() - .5f));
        }
        return textView.getTextSize();
    }

    protected String getText(Context context) {
        DailyWords dailyWords = new DailyWords(context);
        String translation = dailyWords.getTranslated();
        if (translation.length() > 0) {
            return translation.trim();
        }
        return context.getResources().getString(R.string.widget_error);
    }

    protected PendingIntent getOnClickPendingIntent(Context context) {
        Intent defineIntent = new Intent(context, MainActivity.class);
        return PendingIntent.getActivity(context, 0, defineIntent, 0);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        // Handle TouchWiz
        if (Objects.equals(intent.getAction(), "com.sec.android.widgetapp.APPWIDGET_RESIZE")) {
            AppWidgetManager manager = AppWidgetManager.getInstance(context);
            int appWidgetId = intent.getIntExtra("widgetId", 0);
            int widgetSpanX = intent.getIntExtra("widgetspanx", 0);
            int widgetSpanY = intent.getIntExtra("widgetspany", 0);
            if (appWidgetId > 0 && widgetSpanX > 0 && widgetSpanY > 0) {
                Bundle newOptions = new Bundle();
                newOptions.putInt(OPTION_APPWIDGET_MIN_HEIGHT, widgetSpanY * 74);
                newOptions.putInt(OPTION_APPWIDGET_MIN_WIDTH, widgetSpanX * 74);
                onAppWidgetOptionsChanged(context, manager, appWidgetId, newOptions);
            }
        }
    }

    @Override
    public void onAppWidgetOptionsChanged (Context context, AppWidgetManager manager, int appWidgetId, Bundle newOptions) {
        int width = newOptions.getInt(OPTION_APPWIDGET_MIN_WIDTH);
        int height = newOptions.getInt(OPTION_APPWIDGET_MIN_HEIGHT);
        RemoteViews updateViews = createUpdateViews(context, new Point(width, height));
        manager.updateAppWidget(appWidgetId, updateViews);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    protected Point getWidgetSize(AppWidgetManager manager, int appWidgetId) {
        Bundle options = manager.getAppWidgetOptions(appWidgetId);
        int width = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
        int height = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT);
        return new Point(width, height);
    }
}
