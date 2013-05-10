/**
 * Comete Widgets - Common Widgets Library for Comete
 * Copyright (C) 2011-2013  Alexander Nilsen
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * @author Alexander Nilsen
 *
 */

package alni.comete.android.widgets.gauges;

import java.util.ArrayList;

import alni.comete.android.widgets.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * This widget is an analog gauge that displays a value from 0 to
 * {@link getMax()}. This widget is the base of other analog gauges
 * @author Alexander Nilsen
 *
 */

public class AnalogGauge extends View {
    protected int roundFactor = 2;
    protected float start;// = 120f;
    protected float length;// = 275f;
    
    protected float min;// = 0;
    protected float max;//= 100;
    
    protected float okValue, warningValue, criticalValue;
    
    protected int okColor, warningColor, criticalColor;
    
    protected float rotation;
    
    protected float progress = 50;
    
    protected int color, negativeColor, positiveColor, maxColor;
    
    protected Bitmap okBitmap, warningBitmap, maxBitmap;

    protected Paint mPaint, mPaintFill, mPaintFont;
    
    protected ArrayList<Color> okColorRange, warningColorRange, maxColorRange;
    public AnalogGauge(Context context) {
	super(context);
	initPaint();
    }

    public AnalogGauge(Context context, AttributeSet attrs) {
	super(context, attrs);
	initAttrs(context, attrs);
	initPaint();
    }

    public AnalogGauge(Context context, AttributeSet attrs, int defStyle) {
	super(context, attrs, defStyle);
	initAttrs(context, attrs);
	initPaint();
	
    }
    
    private void initAttrs(Context context, AttributeSet attrs) {
	TypedArray a = context.obtainStyledAttributes(attrs,
		R.styleable.AnalogGauge);
	start = a.getFloat(R.styleable.AnalogGauge_start, 120f);
	length = a.getFloat(R.styleable.AnalogGauge_length, 275f);
	//min = a.getInteger(R.styleable.AnalogGauge_min, 0);
	//max = a.getInteger(R.styleable.AnalogGauge_max, 100);
	min = a.getFloat(R.styleable.AnalogGauge_minF, 0f);
	max = a.getFloat(R.styleable.AnalogGauge_maxF, 100f);
	color = a.getColor(R.styleable.AnalogGauge_color, Color.GREEN);
	positiveColor = a.getColor(R.styleable.AnalogGauge_positiveColor, color);
	negativeColor = a.getColor(R.styleable.AnalogGauge_negativeColor, color);
	maxColor = a.getColor(R.styleable.AnalogGauge_maxColor, color);
	rotation = a.getFloat(R.styleable.AnalogGauge_rotation, 0);
	
	//okValue = a.getInteger(R.styleable.AnalogGauge_okValue, 0);
	//warningValue = a.getInteger(R.styleable.AnalogGauge_warningValue, 
	//	(int)(max*0.70d));
	//maxValue = a.getInteger(R.styleable.AnalogGauge_maxValue, 
	//	(int)(max*0.85d));
	okValue = a.getFloat(R.styleable.AnalogGauge_okValue, 0f);
	warningValue = a.getFloat(R.styleable.AnalogGauge_warningValue, max*0.70F);
	criticalValue = a.getFloat(R.styleable.AnalogGauge_criticalValue, max*0.85f);
	
	
	okColor = a.getColor(R.styleable.AnalogGauge_okColor, color);
	warningColor = a.getColor(R.styleable.AnalogGauge_warningColor, color);
	criticalColor = a.getColor(R.styleable.AnalogGauge_criticalColor, color);
	
	try {
	okBitmap = Bitmap.createBitmap(new int[] { okColor }, 1, 1, Bitmap.Config.RGB_565);
	warningBitmap = Bitmap.createBitmap( new int[] { warningColor }, 1, 1, Bitmap.Config.RGB_565);
	maxBitmap = Bitmap.createBitmap(new int[] { criticalColor }, 1, 1, Bitmap.Config.RGB_565);
	} catch (java.lang.UnsupportedOperationException e) {
		
	}
    }

    /* (non-Javadoc)
     * @see android.view.View#onDraw(android.graphics.Canvas)
     */
    @Override
    protected void onDraw(Canvas canvas) {
	//super.onDraw(canvas);
	
	canvas.rotate(rotation);
	drawCircles(canvas);
	drawProgress(canvas);
	drawMaxMin(canvas);
    }
    
    
    
    /* (non-Javadoc)
     * @see android.view.View#onMeasure(int, int)
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), 
		View.MeasureSpec.getSize(heightMeasureSpec));
    }

    private void drawCircles(Canvas canvas) {
	final float w = getWidth();
	final float h = getHeight();
	final float wo = (w/100f)*10f;
	final float ho = (h/100f)*10f;
	canvas.drawArc( new RectF(0f, 0f, w, h), 
		start, length, false, mPaint);
	canvas.drawArc( new RectF(wo, ho, w-wo, h-ho), 
		start, length, false, mPaint);
    }
    
    private void drawMaxMin(Canvas canvas) {
	final float w = getWidth();
	final float h = getHeight();
	final float cx = w/2f;
	final float cy = h/2f;
	float tw = mPaint.measureText("" + min);
	float th = (w/100f)*10f;
	
	mPaint.setTextSize(th);
	canvas.drawText("" + min, 
		cx*((float)Math.cos(Math.toRadians(start))) + cx+tw, 
		cy*((float)Math.sin(Math.toRadians(start))) + cy+(th/2f), 
		mPaint);
	float end = length + start;
	tw = mPaint.measureText("" + max);
	canvas.drawText("" + max, 
		cx*((float)Math.cos(Math.toRadians(end))) + cx-tw, 
		cy*((float)Math.sin(Math.toRadians(end))) + cy+(th/2f), 
		mPaint);
    }
    
    private void drawProgress(Canvas canvas) {
	final float w = getWidth();
	final float h = getHeight();
	final float cx = w/2f;
	final float cy = h/2f;
	
	final float wo = (w/100f)*10f;
	final float ho = (h/100f)*10f;
	mPaint.setTextSize((w/100f)*25f);
	mPaintFill.setStrokeWidth(wo);
	if (progress >= criticalValue) {
	    mPaintFill.setColor(criticalColor);
	} else if (progress >= warningValue) {
	    mPaintFill.setColor(warningColor);
	} else if (progress >= okColor) {
	    mPaintFill.setColor(okColor);
	}
	canvas.drawArc( new RectF(wo/2f, ho/2f, w-(wo/2f), h-(ho/2f)), 
		start, (length/max)*progress, false, mPaintFill);
	canvas.drawText("" + progress, cx, cy, mPaint);
    }
    
    protected void initPaint() {
	mPaint = new Paint();
	mPaint.setStyle(Paint.Style.STROKE);
	mPaint.setColor(color);
	mPaint.setStrokeWidth(2.0f);
	mPaint.setTextAlign(Paint.Align.CENTER);
	mPaint.setAntiAlias(true);
	mPaintFill = new Paint(mPaint);
	
	mPaintFont = new Paint(mPaint);
	mPaintFont.setStyle(Paint.Style.FILL);
    }
    
    public void setMin(float min) {
	this.min = min;
    }
    
    public void setMax(float max) {
	this.max = max;
    }
    
    public void setProgress(float progress) {
	this.progress = progress;
	this.invalidate();
    }
    
    public void setRoundFactor(int roundFactor) {
	this.roundFactor = roundFactor;
    }
    
    public float getMin() {
	return this.min;
    }
    
    public float getMax() {
	return this.max;
    }
    
    public float getProgress() {
	return this.progress;
    }
    
    public int getRoundFactor() {
	return this.roundFactor;
    }
    
    public Paint getPaintFill() {
	return this.mPaintFill;
    }
    
    public Paint getPaint() {
	return this.mPaint;
    }
    
    public Paint getPaintFont() {
	return this.mPaintFont;
    }
}
