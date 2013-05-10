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

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;

public class VerticalNegPosAnalogGauge extends NegPosAnalogGauge {

    public VerticalNegPosAnalogGauge(Context context) {
	super(context);
	// TODO Auto-generated constructor stub
    }

    public VerticalNegPosAnalogGauge(Context context, AttributeSet attrs) {
	super(context, attrs);
	// TODO Auto-generated constructor stub
    }

    public VerticalNegPosAnalogGauge(Context context, AttributeSet attrs,
	    int defStyle) {
	super(context, attrs, defStyle);
	// TODO Auto-generated constructor stub
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.alni.android.widgets.gauges.NegPosAnalogGauge#onDraw(android.graphics
     * .Canvas)
     */
    @Override
    protected void onDraw(Canvas canvas) {
	// TODO Auto-generated method stub
	// progress = -progress;
	super.onDraw(canvas);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.alni.android.widgets.gauges.NegPosAnalogGauge#drawCircles(android
     * .graphics.Canvas)
     */
    @Override
    protected void drawCircles(Canvas canvas) {
	// TODO Auto-generated method stub
	// super.drawCircles(canvas);
	final float w = getWidth();
	final float h = getHeight();
	final float wo = (w / 100f) * 10f;
	final float ho = (h / 100f) * 10f;
	canvas.drawArc(new RectF(0f, 0f, w, h), 180f + 90f, 180f, false, mPaint);
	canvas.drawArc(new RectF(wo, ho, w - wo, h - ho), 180f + 90f, 180f,
		false, mPaint);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.alni.android.widgets.gauges.NegPosAnalogGauge#drawMinMax(android.
     * graphics.Canvas)
     */
    @Override
    protected void drawMinMax(Canvas canvas) {
	// TODO Auto-generated method stub
	// super.drawMinMax(canvas);

	final float w = getWidth();
	final float h = getHeight();
	final float cx = w / 2f;
	float th = (w / 100f) * 10f;

	mPaint.setTextSize(th);
	canvas.drawText("" + min, 0f + cx - th, h, mPaint);
	canvas.drawText("" + max, 0f + cx - th, 0f + th, mPaint);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.alni.android.widgets.gauges.NegPosAnalogGauge#drawProgress(android
     * .graphics.Canvas)
     */
    @Override
    protected void drawProgress(Canvas canvas) {
	// TODO Auto-generated method stub
	// super.drawProgress(canvas);

	final float w = getWidth();
	final float h = getHeight();
	final float cx = w / 2f;
	final float cy = h / 2f;

	final float wo = (w / 100f) * 10f;
	final float ho = (h / 100f) * 10f;
	final float th = (w / 100f) * 25f;
	mPaintFont.setTextSize(th);
	
	mPaintFill.setStrokeWidth(wo);
	setFillColor();
	canvas.drawArc(
		new RectF(wo / 2f, ho / 2f, w - (wo / 2f), h - (ho / 2f)),
		270f + 90f, (90f / max) * -progress, false, mPaintFill);
	drawProgressText(canvas, cx /*+ th*/, cy + th / 2f);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alni.android.widgets.gauges.AnalogGauge#initPaint()
     */
    @Override
    protected void initPaint() {
	// TODO Auto-generated method stub
	super.initPaint();

	mPaint.setTextAlign(Paint.Align.RIGHT);
    }

}
