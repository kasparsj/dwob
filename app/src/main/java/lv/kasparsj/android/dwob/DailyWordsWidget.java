package lv.kasparsj.android.dwob;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.RemoteViews;
import android.widget.TextView;

import lv.kasparsj.android.dwob.model.DailyWords;
import lv.kasparsj.android.widget.AutoFitTextView;

public class DailyWordsWidget extends AppWidgetProvider {

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        context.startService(new Intent(context, AppService.class));
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        context.stopService(new Intent(context, AppService.class));
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            return 8;
        }
        return 6;
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
        DailyWords dailyWords = DailyWords.getInstance();
        Resources r = context.getResources();
        String translation = dailyWords.getTranslated();
        if (dailyWords.isOutdated()) {
            Intent intent = new Intent(context, AppService.class);
            intent.setAction(r.getString(R.string.action_update));
            context.startService(intent);
        }
        else if (translation.length() > 0) {
            for (int appWidgetId : appWidgetIds) {
                update(context, manager, appWidgetId);
            }
        }
    }

    protected void update(Context context, AppWidgetManager manager, int appWidgetId) {
        // Retrieve latest translation
        DailyWords dailyWords = DailyWords.getInstance();
        Resources r = context.getResources();
        String text = r.getString(R.string.widget_error);
        String translation = dailyWords.getTranslated();
        if (translation.length() > 0) {
            text = translation.trim();
        }
        // Build an update that holds the updated widget contents
        RemoteViews updateViews;
        updateViews = new RemoteViews(context.getPackageName(), R.layout.widget_words);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            Bundle options = manager.getAppWidgetOptions(appWidgetId);
            int width = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
            int height = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT);
            AutoFitTextView autoFitTextView = new AutoFitTextView(context);
            autoFitTextView.measure(width, height);
            autoFitTextView.layout(0, 0, width, height);
            autoFitTextView.setMaxLines(getMaxLines());
            autoFitTextView.setText(text);
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            autoFitTextView.draw(new Canvas(bitmap));
            updateViews.setImageViewBitmap(R.id.words, bitmap);
        }
        else {
            // Detect numLines to display
            String[] lines = text.split("\r\n|\r|\n");
            if (lines.length > getMaxLines() && text.contains("\n\n")) {
                String[] parts = text.split("\n\n");
                text = parts[0].trim()+"...";
                lines = text.split("\r\n|\r|\n");
            }
            // Measure text width, and alter numLines accordingly
            TextView textView = new TextView(context);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getDefaultTextSize(lines.length));
            float width = 80 * r.getDisplayMetrics().density * 4;
            float padding = r.getDimension(R.dimen.widget_padding);
            float margin = r.getDimension(R.dimen.widget_margin);
            float lineWidth = (width - padding*2 - margin*2);
            while (lines.length < countTextViewLines(textView, lines, lineWidth, r.getDisplayMetrics().density)) {
                textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, (textView.getTextSize()-.5f));
            }
            updateViews.setTextViewText(R.id.words, text);
            updateViews.setFloat(R.id.words, "setTextSize", textView.getTextSize());
        }
        // setOnClickPendingIntent
        Intent defineIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, defineIntent, 0);
        updateViews.setOnClickPendingIntent(R.id.words, pendingIntent);
        manager.updateAppWidget(appWidgetId, updateViews);
    }
}
