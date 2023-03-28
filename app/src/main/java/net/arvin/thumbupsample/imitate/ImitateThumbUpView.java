package net.arvin.thumbupsample.imitate;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import net.arvin.thumbupsample.OldThumbUpView;
import net.arvin.thumbupsample.R;
import net.arvin.thumbupsample.changed.TuvUtils;

public class ImitateThumbUpView extends View implements View.OnClickListener {
    //文本颜色
    private static final int TEXT_DEFAULT_COLOR = Color.parseColor("#cccccc");
    private static final int TEXT_DEFAULT_END_COLOR = Color.parseColor("#00cccccc");

    private static final int SCALE_DURING = 150;
    private static final int RADIUS_DURING = 250;
    private static final float SCALE_MIN = 0.9f;  //拇指缩放的比例
    private static final float SCALE_MAX = 1f;  //拇指原比例

    private float RADIUS_MIN;
    private float RADIUS_MAX;

    private static final int START_COLOR = Color.parseColor("#00e24d3d");
    private static final int END_COLOR = Color.parseColor("#88e24d3d");

    int count;

    private Paint mBitmapPaint;  //用来画拇指和三点
    private Paint mCirclePaint;  //用来画圆圈
    private Paint mTextPaint;  //用来写字

    //三张图片
    private Bitmap thumbUp;
    private Bitmap notThumbUp;
    private Bitmap shining;

    private int circleWidth;
    private int shiningOffset;
    private int thumbOffset;

    private float mScale = SCALE_MAX;  //拇指缩放比例

    //为了保证居中绘制，这是绘制的起点坐标，减去这个值则为以原点为坐标开始绘制的
    private int startX;
    private int startY;

    private String[] nums;//num[0]是不变的部分，nums[1]原来的部分，nums[2]变化后的部分

    private long lastClickTime;  //上次点击的时间
    public boolean isThumbUp = false;  //当前是否是点赞

    private float mCircleX;
    private float mCircleY;

    private float mCircleRadius;

    private Path mClipPath;

    private boolean toBigger;

    private int textHeight;
    private float textTranslate;

    private Animator currentAnim;

    public ImitateThumbUpView(Context context) {
        this(context, null);
    }

    public ImitateThumbUpView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImitateThumbUpView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ImitateThumbUpView);
        count = typedArray.getInteger(R.styleable.ImitateThumbUpView_imitate_tuv, 0);
        typedArray.recycle();
        init();
    }

    private void init() {
        nums = new String[3];
        calculateChangeNum(0);

//        circleWidth = TuvUtils.dip2px(getContext(), 2);
        shiningOffset = TuvUtils.dip2px(getContext(), 2);
        thumbOffset = TuvUtils.dip2px(getContext(), 8);

        mBitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setStrokeWidth(TuvUtils.dip2px(getContext(), 2));
        mCirclePaint.setStyle(Paint.Style.STROKE);  //设置为线条模式
        mCirclePaint.setColor(START_COLOR);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextSize(TuvUtils.sp2px(getContext(), 15));
        mTextPaint.setColor(TEXT_DEFAULT_COLOR);
        Paint.FontMetricsInt fontMetrics = mTextPaint.getFontMetricsInt();
        textHeight = fontMetrics.descent - fontMetrics.ascent;

        thumbUp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_messages_like_selected);
        notThumbUp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_messages_like_unselected);
        shining = BitmapFactory.decodeResource(getResources(), R.drawable.ic_messages_like_selected_shining);

        mCircleX = thumbUp.getWidth() / 2f;
        mCircleY = thumbOffset + thumbUp.getHeight() / 2f;

        RADIUS_MIN = 0;
        RADIUS_MAX = mCircleY;

        mClipPath = new Path();

        setOnClickListener(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int www = getHandledWidth(widthMeasureSpec);
        int hhh = getHandleHeight(heightMeasureSpec);
        Log.d("Daisy", "测量结果" + www + " / " + hhh);
        setMeasuredDimension(www, hhh);
    }

    private int getHandledWidth(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        switch (specMode) {
            case MeasureSpec.UNSPECIFIED:
            case MeasureSpec.EXACTLY:
                result = Math.max(getContentWidth(), specSize);  //如果限制确定值，则取限制值和自己计算值中的较大值
                break;
            case MeasureSpec.AT_MOST:
                result = getContentWidth();
                break;
        }
        return result;
    }

    private int getHandleHeight(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        switch (specMode) {
            case MeasureSpec.UNSPECIFIED:
            case MeasureSpec.EXACTLY:
                result = Math.max(getContentHeight(), specSize);  //如果限制确定值，则取限制值和自己计算值中的较大值
                break;
            case MeasureSpec.AT_MOST:
                result = getContentHeight();
                break;
        }
        return result;
    }

    //获得实际内容的宽度
    private int getContentWidth() {
        return (int) (thumbUp.getWidth() + mTextPaint.measureText(String.valueOf(count)));
    }

    //获得实际内容的高度
    private int getContentHeight() {
        return Math.max(thumbUp.getHeight() + thumbOffset, textHeight);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        Log.d("Daisy", "尺寸发生了变化" + w + " / " +  h + " / " + oldw + " / " + oldh);

        startX = (int)( ( w - getContentWidth() ) / 2 );
        startY = (int)( ( h - getContentHeight() ) / 2 );
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        Log.d("Daisy", "布局" + changed + " / " +  left + " / " + top + " / " + right + " / " + bottom);
    }

    //todo 这两个动画可以合二为一
    public void setNotThumbUpScale(float scale) {
        mScale = scale;
        invalidate();
    }
    public float getNotThumbUpScale() {
        return mScale;
    }

    public void setThumbUpScale(float scale) {
        mScale = scale;
        invalidate();
    }
    public float getThumbUpScale() {
        return mScale;
    }
    public void setCircleScale(float radius) {
        mCircleRadius = radius;

        float fraction = (RADIUS_MAX - radius) / (RADIUS_MAX - RADIUS_MIN);  //求出现在扩散的比例
        mCirclePaint.setColor((int) TuvUtils.evaluate(fraction, START_COLOR, END_COLOR));  //根据比例设定现在颜色

        mClipPath.reset();
        mClipPath.addCircle(mCircleX, mCircleY, mCircleRadius, Path.Direction.CW);

        postInvalidate();
    }

    public void setTextOffsetY(float offsetY) {
        textTranslate = offsetY;
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.translate(startX, startY);

        drawIcon(canvas);
        drawText(canvas);

        canvas.restore();

        Log.d("Daisy", "当前是否是点赞 " + isThumbUp);
    }

    private void drawIcon(Canvas canvas) {
        if (isThumbUp) {
//            canvas.drawBitmap(shining, shiningOffset, 0, mBitmapPaint);

            int stretchEffect = canvas.save();
            canvas.scale(mScale, mScale, 0, thumbOffset);
            canvas.drawBitmap(thumbUp, 0, thumbOffset, mBitmapPaint);
            canvas.restoreToCount(stretchEffect);

            int pathEffect = canvas.save();
            canvas.clipPath(mClipPath);
            canvas.drawBitmap(shining, shiningOffset, 0, mBitmapPaint);  //画一部分被遮住了的闪光
            canvas.restoreToCount(pathEffect);

            canvas.drawCircle(mCircleX, mCircleY, mCircleRadius, mCirclePaint);  //画圆

            Log.d("Daisy", "最终的颜色" + Integer.toHexString(mCirclePaint.getColor()));
        } else {
            int shrinkEffect = canvas.save();
            canvas.scale(mScale, mScale, 0, thumbOffset);
            canvas.drawBitmap(notThumbUp, 0, thumbOffset, mBitmapPaint);
            canvas.restoreToCount(shrinkEffect);
        }
    }

    private void drawText(Canvas canvas) {
        Paint.FontMetricsInt fontMetrics = mTextPaint.getFontMetricsInt();
        float textBaseLine = getContentHeight() - fontMetrics.descent;

        //画固定位置
        if (! nums[0].equals("")) {
            mTextPaint.setColor(TEXT_DEFAULT_COLOR);
            canvas.drawText(String.valueOf(nums[0]), thumbUp.getWidth(), textBaseLine, mTextPaint);
        }


        String text = String.valueOf(count);
        float textWidth = mTextPaint.measureText(text) / text.length();

        //画上半部分
        String upperPart = toBigger ? nums[1] : nums[2];
        mTextPaint.setColor((Integer) TuvUtils.evaluate(textTranslate, TEXT_DEFAULT_COLOR, TEXT_DEFAULT_END_COLOR));
        canvas.drawText(upperPart, thumbUp.getWidth() + textWidth * nums[0].length(),
                textBaseLine - textTranslate * textHeight, mTextPaint);

        //画下半部分
        String lowerPart = toBigger ? nums[2] : nums[1];
            mTextPaint.setColor((Integer) TuvUtils.evaluate(textTranslate, TEXT_DEFAULT_END_COLOR, TEXT_DEFAULT_COLOR));
            canvas.drawText(lowerPart, thumbUp.getWidth() + textWidth * nums[0].length(),
                    textBaseLine + textHeight - textTranslate * textHeight, mTextPaint);

    }

    private void showThumbUpAnim() {
        ObjectAnimator notThumbUpScale = ObjectAnimator.ofFloat(this, "notThumbUpScale", SCALE_MAX, SCALE_MIN);
        notThumbUpScale.setDuration(SCALE_DURING);
        notThumbUpScale.addListener(new ClickAnimatorListener() {
            @Override
            public void onAnimRealEnd(Animator animation) {
                isThumbUp = true;
                Log.d("Daisy", "点赞 变小动画已结束");
            }
        });

        ObjectAnimator textOffsetY = ObjectAnimator.ofFloat(this, "textOffsetY", 0, 1);
        textOffsetY.setDuration(SCALE_DURING + RADIUS_DURING);

        ObjectAnimator thumbUpScale = ObjectAnimator.ofFloat(this, "thumbUpScale", SCALE_MIN, SCALE_MAX);
        thumbUpScale.setDuration(SCALE_DURING);
        thumbUpScale.setInterpolator(new OvershootInterpolator());

        ObjectAnimator circleScale = ObjectAnimator.ofFloat(this, "circleScale", RADIUS_MIN, RADIUS_MAX);
        circleScale.setDuration(RADIUS_DURING);

        AnimatorSet set = new AnimatorSet();
        set.play(notThumbUpScale).with(textOffsetY);
        set.play(thumbUpScale).with(circleScale);
        set.play(notThumbUpScale).before(thumbUpScale);
        set.start();

        currentAnim = set;
    }

    private void showThumbDownAnim() {
        ObjectAnimator thumbUpScale = ObjectAnimator.ofFloat(this, "thumbUpScale", SCALE_MAX, SCALE_MIN);
        thumbUpScale.setDuration(SCALE_DURING);
        thumbUpScale.addListener(new ClickAnimatorListener() {
            @Override
            public void onAnimRealEnd(Animator animation) {
                isThumbUp = false;
                Log.d("Daisy", "取消点赞 变小动画已结束");
            }
        });

        ObjectAnimator textOffsetY = ObjectAnimator.ofFloat(this, "textOffsetY", 1, 0);
        textOffsetY.setDuration(SCALE_DURING + RADIUS_DURING);

        ObjectAnimator notThumbUpScale = ObjectAnimator.ofFloat(this, "notThumbUpScale", SCALE_MIN, SCALE_MAX);
        thumbUpScale.setDuration(SCALE_DURING);

        AnimatorSet set = new AnimatorSet();
        set.play(thumbUpScale).with(textOffsetY);
        set.play(thumbUpScale).before(notThumbUpScale);
        set.start();

        currentAnim = set;
    }

    @Override
    public void onClick(View v) {
        if (currentAnim != null && currentAnim.isRunning()) {
            Log.d("Daisy", "当前动画正在运行中");
            return;
        }

        if (isThumbUp) {
            calculateChangeNum(-1);
            count--;
            showThumbDownAnim();
        } else {
            calculateChangeNum(1);
            count++;
            showThumbUpAnim();
        }
    }

    private abstract class ClickAnimatorListener extends AnimatorListenerAdapter {
        @Override
        public void onAnimationStart(Animator animation) {
            super.onAnimationStart(animation);
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            onAnimRealEnd(animation);
        }

        @Override
        public void onAnimationCancel(Animator animation) {
            super.onAnimationCancel(animation);
        }

        public abstract void onAnimRealEnd(Animator animation);
    }

    //计算数字不变、变化前、变化后
    private void calculateChangeNum(int change) {
        Log.d("Daisy", "开始计算了" + change);
        if (change == 0) {
            nums[0] = String.valueOf(count);
            nums[1] = "";
            nums[2] = "";
            return;
        }
        toBigger = change > 0;
        String oldNum = String.valueOf(count);
        String newNum = String.valueOf(count + change);

        for (int i = 0; i < oldNum.length(); i++) {
            char oldC1 = oldNum.charAt(i);
            char newC1 = newNum.charAt(i);
            if (oldC1 != newC1) {
                nums[0] = i == 0 ? "" : newNum.substring(0, i);
                nums[1] = oldNum.substring(i);
                nums[2] = newNum.substring(i);
                break;
            }
        }
    }

    //手动设置数字
    public void setCount(int count) {
        this.count = count;
        calculateChangeNum(0);
        requestLayout();
    }

    //手动设置是否点赞
    public void setThumbUp(boolean isThumbUp) {
        this.isThumbUp = isThumbUp;
        postInvalidate();
    }

}
