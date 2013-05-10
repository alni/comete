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

import alni.android.common.ColorUtils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.AttributeSet;

public class NegPosAnalogGauge extends AnalogGauge {

    public NegPosAnalogGauge(Context context) {
	super(context);
	// TODO Auto-generated constructor stub
    }

    public NegPosAnalogGauge(Context context, AttributeSet attrs) {
	super(context, attrs);
	// TODO Auto-generated constructor stub
    }

    public NegPosAnalogGauge(Context context, AttributeSet attrs, int defStyle) {
	super(context, attrs, defStyle);
	// TODO Auto-generated constructor stub
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.alni.android.widgets.gauges.AnalogGauge#onDraw(android.graphics.Canvas
     * )
     */
    @Override
    protected void onDraw(Canvas canvas) {
	// TODO Auto-generated method stub
	// super.onDraw(canvas);
	drawCircles(canvas);
	drawProgress(canvas);

	drawMinMax(canvas);
    }

    protected void drawCircles(Canvas canvas) {
	final float w = getWidth();
	final float h = getHeight();
	final float wo = (w / 100f) * 10f;
	final float ho = (h / 100f) * 10f;
	canvas.drawArc(new RectF(0f, 0f, w, h), 180f, 180f, false, mPaint);
	canvas.drawArc(new RectF(wo, ho, w - wo, h - ho), 180f, 180f, false,
		mPaint);
    }

    protected void drawMinMax(Canvas canvas) {
	final float w = getWidth();
	final float h = getHeight();
	final float cy = h / 2f;
	float th = (w / 100f) * 10f;

	mPaint.setTextSize(th);
	canvas.drawText("" + min, th, cy + th, mPaint);
	canvas.drawText("" + max, w - th, cy + th, mPaint);
    }

    protected void drawProgress(Canvas canvas) {
	final float w = getWidth();
	final float h = getHeight();
	final float cx = w / 2f;
	final float cy = h / 2f;

	final float wo = (w / 100f) * 10f;
	final float ho = (h / 100f) * 10f;
	mPaintFont.setTextSize((w / 100f) * 25f);
	mPaintFill.setStrokeWidth(wo);
	setFillColor();
	canvas.drawArc(
		new RectF(wo / 2f, ho / 2f, w - (wo / 2f), h - (ho / 2f)),
		180f + 90f, (90f / max) * progress, false, mPaintFill);
	drawProgressText(canvas, cx, cy);
    }

    protected void drawProgressText(Canvas canvas, float cx, float cy) {
	String format = "%1$.2f";
	if (roundFactor < 1) {
	    format = "%1$.0f";
	} else if (roundFactor < 2) {
	    format = "%1$.1f0";
	}
	canvas.drawText(String.format(format, progress), cx, cy, mPaintFont);
    }

    protected void setFillColor() {
	mPaintFill.setColor(progress >= 0 ? positiveColor : negativeColor);
	float fMax = Math.abs(max);
	float fProgress = (float) Math.abs(progress);
	float fCriticalValue = (float) Math.abs(criticalValue);
	float fWarningValue = (float) Math.abs(warningValue);
	float fOkValue = (float) Math.abs(okValue);
	if (fProgress <= 0.05f) {
	    int c = color;
	    mPaintFill.setColor(c);
	    mPaintFont.setColor(c);
	} else if (fProgress <= fOkValue) {
	    float p = fWarningValue - fOkValue;
	    p = p / fProgress;
	    int c = ColorUtils.getColorHSV(okColor, color, p);
	    mPaintFill.setColor(c);
	    mPaintFont.setColor(c);
	} else if (fProgress <= fWarningValue) {
	    float p = fWarningValue - fOkValue;
	    p = p / fProgress;
	    int c = ColorUtils.getColorHSV(warningColor, okColor, p);
	    mPaintFill.setColor(c);
	    mPaintFont.setColor(c);
	} else if (fProgress <= fCriticalValue) {
	    float p = fCriticalValue - fWarningValue;
	    p = p / fProgress;
	    int c = ColorUtils.getColorHSV(criticalColor, warningColor, p);
	    mPaintFill.setColor(c);
	    mPaintFont.setColor(c);
	} else if (fProgress < fMax) {
	    float p = fCriticalValue - fWarningValue;
	    p = p / fProgress;
	    int c = ColorUtils.getColorHSV(maxColor, criticalColor, p);
	    mPaintFill.setColor(c);
	    mPaintFont.setColor(c);
	} else {
	    int c = maxColor;
	    mPaintFill.setColor(c);
	    mPaintFont.setColor(c);
	}
	/*
	 * if (fProgress >= fMaxValue) { float p = fMaxValue - fWarningValue; p
	 * = p / fProgress; mPaintFill.setColor(ColorUtils.getColorHSV(maxColor,
	 * warningColor, p)); } else if (fProgress >= fWarningValue) { float p =
	 * fMaxValue - fWarningValue; p = p / fProgress;
	 * mPaintFill.setColor(ColorUtils.getColorHSV(maxColor, warningColor,
	 * p)); } else if (fProgress >= fOkValue) { float p = fWarningValue -
	 * fOkValue; p = p / fProgress; mPaintFill.setColor(ColorUtils
	 * .getColorHSV(warningColor, okColor, p)); }
	 */
    }
}
