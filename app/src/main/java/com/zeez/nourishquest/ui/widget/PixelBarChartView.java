package com.zeez.nourishquest.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

// Custom Canvas bar chart used on the Insights screen.
// Bars grow upward from a baseline. A Y axis on the left shows the scale (0-10).
public class PixelBarChartView extends View {

    // Layout constants in dp — converted to px in init()
    private static final float GAP_DP           = 4f;   // gap between bars
    private static final float LABEL_TEXT_SIZE_DP = 9f;  // x-axis label size
    private static final float ZERO_STUB_DP     = 2f;   // minimum bar height for zero values
    private static final float Y_LABEL_WIDTH_DP = 20f;  // space reserved for Y axis labels
    private static final float TOP_PADDING_DP   = 10f;  // padding above chart so the 10 label is not clipped

    // Paint objects — created once and reused to avoid allocations during onDraw
    private final Paint barPaint    = new Paint(Paint.ANTI_ALIAS_FLAG); // filled bar colour
    private final Paint emptyPaint  = new Paint(Paint.ANTI_ALIAS_FLAG); // empty bar slot background
    private final Paint labelPaint  = new Paint(Paint.ANTI_ALIAS_FLAG); // x-axis day labels
    private final Paint yLabelPaint = new Paint(Paint.ANTI_ALIAS_FLAG); // y-axis value labels
    private final Paint gridPaint   = new Paint(Paint.ANTI_ALIAS_FLAG); // horizontal grid lines
    private final RectF barRect     = new RectF();                       // reusable rect for bar drawing

    // Data
    private float[]  values   = new float[0];
    private String[] xLabels  = new String[0];
    private int      barColour  = 0xFF6457A6; // grape default
    private int      emptyColour = 0xFF241E42; // surface_card dark
    private float    maxValue   = 10f;

    // Pixel values calculated in init()
    private float gapPx;
    private float labelHeightPx;
    private float zeroStubPx;
    private float yLabelWidthPx;
    private float topPaddingPx;

    public PixelBarChartView(Context context) {
        super(context);
        init();
    }

    public PixelBarChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    // Convert dp constants to px and configure paint objects
    private void init() {
        float density  = getResources().getDisplayMetrics().density;
        gapPx         = GAP_DP            * density;
        labelHeightPx = LABEL_TEXT_SIZE_DP * density * 2.5f;
        zeroStubPx    = ZERO_STUB_DP      * density;
        yLabelWidthPx = Y_LABEL_WIDTH_DP  * density;
        topPaddingPx  = TOP_PADDING_DP    * density;

        barPaint.setStyle(Paint.Style.FILL);

        emptyPaint.setStyle(Paint.Style.FILL);
        emptyPaint.setColor(emptyColour);

        labelPaint.setStyle(Paint.Style.FILL);
        labelPaint.setTextSize(LABEL_TEXT_SIZE_DP * density);
        labelPaint.setTextAlign(Paint.Align.CENTER);
        labelPaint.setTypeface(android.graphics.Typeface.MONOSPACE);

        yLabelPaint.setStyle(Paint.Style.FILL);
        yLabelPaint.setTextSize(LABEL_TEXT_SIZE_DP * density * 0.9f);
        yLabelPaint.setTextAlign(Paint.Align.RIGHT);
        yLabelPaint.setTypeface(android.graphics.Typeface.MONOSPACE);
        yLabelPaint.setColor(0xFF8888AA); // muted so it does not compete with bars

        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setStrokeWidth(1f);
        gridPaint.setColor(0x22FFFFFF); // very subtle white line
    }

    // Supply new data and trigger a redraw
    public void setData(float[] values, String[] xLabels, int barColour, float maxValue) {
        this.values    = values;
        this.xLabels   = xLabels;
        this.barColour = barColour;
        this.maxValue  = maxValue;
        barPaint.setColor(barColour);
        labelPaint.setColor(barColour);
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width  = MeasureSpec.getSize(widthMeasureSpec);
        // 120dp chart area + room for x labels below + top padding above
        int height = (int) ((120 * getResources().getDisplayMetrics().density) + labelHeightPx + topPaddingPx);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (values == null || values.length == 0) return;

        int   count       = values.length;
        float chartTop    = topPaddingPx;                  // top of chart area
        float chartBottom = getHeight() - labelHeightPx;   // bottom of chart area (above x labels)
        float chartHeight = chartBottom - chartTop;
        float chartLeft   = yLabelWidthPx + gapPx;         // chart starts after Y axis labels
        float chartWidth  = getWidth() - chartLeft;
        float barWidth    = (chartWidth - gapPx * (count - 1)) / count;

        // ── Y axis: grid lines and value labels at 0, 2, 4, 6, 8, 10 ───
        int[] yTicks = {0, 2, 4, 6, 8, 10};
        for (int tick : yTicks) {
            float fraction = tick / maxValue;
            float y = chartBottom - fraction * chartHeight;
            canvas.drawLine(chartLeft, y, getWidth(), y, gridPaint);
            canvas.drawText(String.valueOf(tick),
                    yLabelWidthPx,
                    y + yLabelPaint.getTextSize() / 3f,
                    yLabelPaint);
        }

        // ── Bars ─────────────────────────────────────────────────────────
        for (int i = 0; i < count; i++) {
            float left  = chartLeft + i * (barWidth + gapPx);
            float right = left + barWidth;

            // Draw empty background slot first
            barRect.set(left, chartTop, right, chartBottom);
            canvas.drawRect(barRect, emptyPaint);

            // Draw filled bar — minimum stub height so zero values are visible
            float value     = values[i];
            float barHeight = value > 0
                    ? Math.max(zeroStubPx, (value / maxValue) * chartHeight)
                    : zeroStubPx;
            barRect.set(left, chartBottom - barHeight, right, chartBottom);
            canvas.drawRect(barRect, barPaint);

            // Draw x-axis day label centred under the bar
            if (xLabels != null && i < xLabels.length) {
                canvas.drawText(xLabels[i],
                        left + barWidth / 2f,
                        chartBottom + labelHeightPx * 0.7f,
                        labelPaint);
            }
        }
    }
}