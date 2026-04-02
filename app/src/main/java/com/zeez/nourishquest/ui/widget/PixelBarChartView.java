package com.zeez.nourishquest.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

// Custom Canvas bar chart used on the Insights screen.
// No third-party charting library — keeps the visual style consistent with the rest of the app
// and avoids unnecessary APK size increase.
//
// Bars grow upward from a baseline. Zero-value bars render as a small stub
// so the user can tell the difference between "no data" and a genuine zero.
public class PixelBarChartView extends View {

    private static final float GAP_DP = 4f;
    private static final float LABEL_TEXT_SIZE_DP = 9f;
    private static final float ZERO_STUB_DP = 2f;

    private final Paint barPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint emptyPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint labelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF barRect = new RectF();

    private float[] values = new float[0];
    private String[] xLabels = new String[0];
    private int barColour = 0xFF6457A6;   // grape default
    private int emptyColour = 0xFF241E42; // surface_card
    private float maxValue = 10f;
    private float gapPx;
    private float labelHeightPx;
    private float zeroStubPx;

    public PixelBarChartView(Context context) {
        super(context);
        init();
    }

    public PixelBarChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        float density = getResources().getDisplayMetrics().density;
        gapPx = GAP_DP * density;
        labelHeightPx = LABEL_TEXT_SIZE_DP * density * 2.5f;
        zeroStubPx = ZERO_STUB_DP * density;

        barPaint.setStyle(Paint.Style.FILL);
        emptyPaint.setStyle(Paint.Style.FILL);
        emptyPaint.setColor(emptyColour);

        labelPaint.setStyle(Paint.Style.FILL);
        labelPaint.setTextSize(LABEL_TEXT_SIZE_DP * density);
        labelPaint.setTextAlign(Paint.Align.CENTER);
        labelPaint.setTypeface(android.graphics.Typeface.MONOSPACE);
    }

    // Call this to provide data and trigger a redraw
    public void setData(float[] values, String[] xLabels, int barColour, float maxValue) {
        this.values = values;
        this.xLabels = xLabels;
        this.barColour = barColour;
        this.maxValue = maxValue;

        barPaint.setColor(barColour);
        labelPaint.setColor(barColour);

        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        // Fixed 120dp chart height plus space for x-axis labels
        int height = (int) ((120 * getResources().getDisplayMetrics().density) + labelHeightPx);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (values == null || values.length == 0) return;

        int count = values.length;
        float chartHeight = getHeight() - labelHeightPx;
        float barWidth = (getWidth() - gapPx * (count - 1)) / count;

        for (int i = 0; i < count; i++) {
            float left = i * (barWidth + gapPx);
            float right = left + barWidth;

            // Draw the empty background slot first
            barRect.set(left, 0, right, chartHeight);
            canvas.drawRect(barRect, emptyPaint);

            // Bar height proportional to value, minimum stub height for zero values
            float value = values[i];
            float barHeight = value > 0
                    ? Math.max(zeroStubPx, (value / maxValue) * chartHeight)
                    : zeroStubPx;

            // Bars grow upward from the bottom
            barRect.set(left, chartHeight - barHeight, right, chartHeight);
            canvas.drawRect(barRect, barPaint);

            // Draw x-axis label centred under the bar
            if (xLabels != null && i < xLabels.length) {
                canvas.drawText(xLabels[i],
                        left + barWidth / 2f,
                        chartHeight + labelHeightPx * 0.7f,
                        labelPaint);
            }
        }
    }
}
