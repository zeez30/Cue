package com.zeez.nourishquest.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.zeez.nourishquest.util.HungerScaleHelper;

// Custom View that draws the 10-block hunger/fullness scale on a Canvas.
// Used on the hunger check-in screen.
//
// Why a custom View instead of 10 separate Views in a LinearLayout?
// - One Canvas draw call is much cheaper than 10 measure/layout/draw passes
// - Touch handling maps X position to scale level via simple arithmetic — more reliable
//   than routing click events through 10 separate child Views
// - Can be reused anywhere in the app at any size
public class PixelHungerScaleView extends View {

    private static final int BLOCK_COUNT = 10;
    private static final float GAP_DP = 3f;

    private final Paint filledPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint dimPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF blockRect = new RectF();

    private int selectedLevel = 5;
    private float blockWidth;
    private float gapPx;

    // Callback fired when the user taps a new level
    public interface OnLevelSelectedListener {
        void onLevelSelected(int level);
    }

    private OnLevelSelectedListener listener;

    public PixelHungerScaleView(Context context) {
        super(context);
        init();
    }

    public PixelHungerScaleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PixelHungerScaleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        gapPx = GAP_DP * getResources().getDisplayMetrics().density;
        setClickable(true);
        setFocusable(true);
    }

    public void setSelectedLevel(int level) {
        if (level < 1) level = 1;
        if (level > BLOCK_COUNT) level = BLOCK_COUNT;
        if (this.selectedLevel != level) {
            this.selectedLevel = level;
            invalidate(); // triggers a redraw
        }
    }

    public int getSelectedLevel() { return selectedLevel; }

    public void setOnLevelSelectedListener(OnLevelSelectedListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        // Recalculate block width whenever the view resizes
        float totalGaps = gapPx * (BLOCK_COUNT - 1);
        blockWidth = (w - totalGaps) / BLOCK_COUNT;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        // Height = block width (square blocks), capped at 36dp
        float desiredBlockWidth = (width - gapPx * (BLOCK_COUNT - 1)) / BLOCK_COUNT;
        int height = (int) Math.min(desiredBlockWidth,
                36 * getResources().getDisplayMetrics().density);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float height = getHeight();

        for (int level = 1; level <= BLOCK_COUNT; level++) {
            float left = (level - 1) * (blockWidth + gapPx);
            float right = left + blockWidth;
            blockRect.set(left, 0, right, height);

            // Blocks at or below selected level are bright, above are dimmed
            if (level <= selectedLevel) {
                filledPaint.setColor(HungerScaleHelper.getColour(level));
                canvas.drawRect(blockRect, filledPaint);
            } else {
                dimPaint.setColor(HungerScaleHelper.getDimColour(level));
                canvas.drawRect(blockRect, dimPaint);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN
                || event.getAction() == MotionEvent.ACTION_MOVE) {

            // Clamp X to view bounds then map to a scale level
            float x = Math.max(0, Math.min(event.getX(), getWidth() - 1));
            int tappedLevel = Math.max(1, Math.min(
                    (int) (x / (blockWidth + gapPx)) + 1, BLOCK_COUNT));

            if (tappedLevel != selectedLevel) {
                setSelectedLevel(tappedLevel);
                if (listener != null) {
                    listener.onLevelSelected(tappedLevel);
                }
            }
            return true;
        }
        return super.onTouchEvent(event);
    }
}
