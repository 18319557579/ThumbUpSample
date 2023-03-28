package net.arvin.thumbupsample.mine;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class MyTextView extends View {
    Paint mPaint = new Paint();
    float TEXT_DEFAULT_SIZE = 15;

    Rect rrr = new Rect();
    {


    }

    public MyTextView(Context context) {
        super(context);
    }

    public MyTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setTextSize(sp2px(TEXT_DEFAULT_SIZE));

        mPaint.setColor(getResources().getColor(android.R.color.holo_green_light));
//        canvas.drawRect(100, 100 - sp2px(TEXT_DEFAULT_SIZE), 2000, 100, mPaint);

        mPaint.setColor(0xFFFF0000);
        canvas.drawText("Aghijklmnopqrstuvwxyz123???我是黄绍飞", 100, 100, mPaint);

        Paint.FontMetrics fontM = mPaint.getFontMetrics();
        Log.d("Daisy", sp2px(TEXT_DEFAULT_SIZE) + "/" + fontM.ascent + "/" + fontM.bottom + "/" +
                fontM.descent + "/" + fontM.top + "/" + fontM.leading + "/" + (fontM.bottom - fontM.top));

//        mPaint.getTextBounds();

        String originStr = "Aghijklmnopqrstuvwxyz123???我是黄绍飞";
        mPaint.getTextBounds(originStr, 0, originStr.length(), rrr);

        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(rrr, mPaint);
    }

    private int sp2px(float spValue) {
        final float fontScale = getContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }
}
