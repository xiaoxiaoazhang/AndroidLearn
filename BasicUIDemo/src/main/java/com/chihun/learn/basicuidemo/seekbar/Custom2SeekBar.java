package com.chihun.learn.basicuidemo.seekbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.chihun.learn.basicuidemo.R;

import java.util.List;

/**
 * File description
 * <p>
 * Author: wzd
 * Date: 2017/10/14.
 */

public class Custom2SeekBar extends View {

    private List<String> data;
    private float[] positions;

    private Bitmap lineBtp;
    private Bitmap bigBtp;
    private Bitmap smallBtp;

    private float focusTextSize;
    private int focusTextColor;
    private float textSize;
    private int textColor;
    private float textMove;//字与下方点的距离，因为字体字体是40px，再加上10的间隔

    private Paint mPaint;
    private Paint mTextPaint;
    private float bigBtpWidth;
    private float smallBtpWidth;

    private float lineHeight;
    private int selectPosition;
    private int width;
    private int height;

    private float perWidth;

    private ResponseOnTouch responseOnTouch;

    public Custom2SeekBar(Context context) {
        super(context);
    }

    public Custom2SeekBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Custom2SeekBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.Custom2SeekBar);
        focusTextSize = typedArray.getDimension(R.styleable.Custom2SeekBar_focus_text_size, 10);
        focusTextColor = typedArray.getColor(R.styleable.Custom2SeekBar_focus_text_color, Color.rgb(0x21, 0x21, 0x21));
        textSize = typedArray.getDimension(R.styleable.Custom2SeekBar_text_size, 10);
        textColor = typedArray.getColor(R.styleable.Custom2SeekBar_text_color, Color.rgb(0x75, 0x75, 0x75));
        textMove = typedArray.getDimension(R.styleable.Custom2SeekBar_text_move, 10);

        Drawable focusDrawable = typedArray.getDrawable(R.styleable.Custom2SeekBar_focus_src);
        bigBtp = drawableToBitmap(focusDrawable);
        Drawable drawable = typedArray.getDrawable(R.styleable.Custom2SeekBar_src);
        smallBtp = drawableToBitmap(drawable);
        Drawable lineDrawable = typedArray.getDrawable(R.styleable.Custom2SeekBar_line);
        lineBtp = drawableToBitmap(lineDrawable);
        typedArray.recycle();

//        bigBtp = BitmapFactory.decodeResource(getResources(), R.drawable.dot_big_org);
//        smallBtp = BitmapFactory.decodeResource(getResources(),R.drawable.dot_small_org);
//        lineBtp = BitmapFactory.decodeResource(getResources(),R.drawable.dash_org);
        bigBtpWidth = bigBtp.getWidth() / 2;
        smallBtpWidth = smallBtp.getWidth() / 2;
        lineHeight = lineBtp.getHeight();

        mPaint = new Paint(Paint.DITHER_FLAG);
        mPaint.setAntiAlias(true);//锯齿不显示
        mPaint.setStrokeWidth(3);
        mTextPaint = new Paint(Paint.DITHER_FLAG);
        mTextPaint.setAntiAlias(true);//锯齿不显示
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setAntiAlias(true);
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(
                 drawable.getIntrinsicWidth(),
                 drawable.getIntrinsicHeight(),
                 drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public void setData(List<String> data) {
        if (data == null || data.size() < 2) {
            throw new IllegalArgumentException("list must contain at least two data");
        }
        this.data = data;
        positions = new float[data.size()];
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        width = widthSize;
        height = bigBtp.getHeight() + (int) (textMove + textSize + 5);
        setMeasuredDimension(getDefaultSize(width, widthMeasureSpec), getDefaultSize(height, heightMeasureSpec));
        if (data != null && !data.isEmpty()) {
            perWidth = (width - bigBtpWidth * 2) / (data.size() - 1);
            for (int i = 0; i < data.size(); i++) {
                if (i == 0) {
                    positions[i] = bigBtpWidth;
                } else if (i == data.size() - 1) {
                    positions[i] = width - bigBtpWidth;
                } else {
                    positions[i] = positions[i - 1] + perWidth;
                }
            }
        }
    }

    public static int getDefaultSize(int size, int measureSpec) {
        int result = size;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        switch (specMode) {
            case MeasureSpec.UNSPECIFIED:
            case MeasureSpec.AT_MOST:
                result = size;
                break;
            case MeasureSpec.EXACTLY:
                result = specSize;
                break;
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAlpha(255);
        if (data != null && data.size() > 1) {
            Rect line = new Rect((int)positions[0], (int)(bigBtpWidth - lineHeight / 2),(int) positions[positions.length - 1], (int)(bigBtpWidth + lineHeight / 2));
            canvas.drawBitmap(lineBtp, line , line, mPaint);
            for (int i = 0; i < data.size(); i ++) {
                String text = data.get(i);
                if (i == selectPosition) {
                    canvas.drawBitmap(bigBtp, positions[i] - bigBtpWidth, 0, mPaint);
                    mTextPaint.setTextSize(focusTextSize);
                    mTextPaint.setColor(focusTextColor);
                    Paint.FontMetricsInt fontMetrics = mTextPaint.getFontMetricsInt();
                    float baseline = (bigBtp.getHeight() - fontMetrics.bottom - fontMetrics.top) / 2;
                    canvas.drawText(text, positions[i], baseline, mTextPaint);
                } else {
                    canvas.drawBitmap(smallBtp, positions[i] - smallBtpWidth, bigBtpWidth - smallBtpWidth, mPaint);
                    mTextPaint.setTextSize(textSize);
                    mTextPaint.setColor(textColor);
                    canvas.drawText(text, positions[i], bigBtpWidth * 2 + textMove, mTextPaint);
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                responseTouch((int) event.getX(), (int) event.getY());
                break;
        }
        return true;
    }
    private void responseTouch(int x, int y){
        if (data != null && data.size() > 1) {
            if (bigBtpWidth - smallBtpWidth <= y && y <= bigBtpWidth + smallBtpWidth) {
                for (int i = 0; i < data.size(); i++) {
                    if (positions[i] - smallBtpWidth <= x && x <= positions[i] + smallBtpWidth ) {
                        selectPosition = i;
                        break;
                    }
                }
                responseOnTouch.onTouchResponse(selectPosition);
                invalidate();
            }
        }
    }

    //设置监听
    public void setResponseOnTouch(ResponseOnTouch response){
        responseOnTouch = response;
    }
}
