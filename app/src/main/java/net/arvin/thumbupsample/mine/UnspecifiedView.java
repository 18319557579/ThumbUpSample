package net.arvin.thumbupsample.mine;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import net.arvin.thumbupsample.R;

public class UnspecifiedView extends View {

    private Bitmap thumbUp;
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public UnspecifiedView(Context context) {
        this(context, null);
    }

    public UnspecifiedView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UnspecifiedView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        thumbUp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_messages_like_selected);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        getWidth(widthMeasureSpec);
        getHeight(heightMeasureSpec);
    }



    private void getWidth(int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        switch (specMode) {
            case MeasureSpec.UNSPECIFIED:
                Log.d("Daisy", "宽度 无限制");
                break;
            case MeasureSpec.AT_MOST:
                Log.d("Daisy", "宽度 上限");
                break;
            case MeasureSpec.EXACTLY:
                Log.d("Daisy", "宽度 确定值");
                break;
        }
        Log.d("Daisy", "宽度传值" + specSize);
    }

    private void getHeight(int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        switch (specMode) {
            case MeasureSpec.UNSPECIFIED:
                Log.d("Daisy", "高度 无限制");
                break;
            case MeasureSpec.AT_MOST:
                Log.d("Daisy", "高度 上限");
                break;
            case MeasureSpec.EXACTLY:
                Log.d("Daisy", "高度 确定值");
                break;
        }
        Log.d("Daisy", "高度传值" + specSize);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.d("Daisy", "回调 onSizeChanged");
        Log.d("Daisy", "获得图片的宽度 " + thumbUp.getWidth() + " / " + thumbUp.getHeight());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d("Daisy", "onDraw 获得图片的宽度 " + thumbUp.getWidth() + " / " + thumbUp.getHeight());

        canvas.drawBitmap(thumbUp, 0, 0, paint);

        paint.setColor(0xFFFF00FF);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(0, 100, thumbUp.getWidth(), 100+ thumbUp.getHeight(), paint);
    }
}
