package com.lucideye.lucideye;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.lucideye.R;

public class CircleView extends View {
    private int animValue;
    private int strokeWidth = 35;

    public CircleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        animValue = 0;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(strokeWidth);
        paint.setColor(Color.WHITE);
        RectF rectF = new RectF();
        rectF.set(strokeWidth, strokeWidth, getWidth() - strokeWidth, getWidth() - strokeWidth);
        canvas.drawArc(rectF, 0, 360, false, paint);
        paint.setColor(getResources().getColor(R.color.green));
        canvas.drawArc(rectF, animValue, 80, false, paint);
    }

    public void setValue(int animatedValue) {
        animValue = animatedValue;
        invalidate();
    }
}
