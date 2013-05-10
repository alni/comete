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

package alni.comete.android.widgets.gauges.ai;

import alni.android.common.MathUtils;
import alni.comete.android.widgets.RotatingImageView;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

public class AttitudeIndicator extends RotatingImageView {
	// private float tx = 0f;
	private float ty = 0f;

	public AttitudeIndicator(Context context) {
		super(context);
	}

	public AttitudeIndicator(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public AttitudeIndicator(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View#onDraw(android.graphics.Canvas)
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		canvas.save();

		canvas.restore();
		canvas.save();
		canvas.rotate(getAngle(), getWidth() / 2f, getHeight() / 2f);
		canvas.restore();

		canvas.save();
		float h = getHeight();
		float dx = 0f;

		float m = h * 0.20f;
		float dy = MathUtils.valToMinMax(ty, -20f * m, 20f * m);
		canvas.translate(dx, Math.min(dy, h));
		super.onDraw(canvas);
		canvas.restore();
	}

	// public void setTx(float tx) {
	// this.tx = tx;
	// }

	public void setTy(float ty) {
		this.ty = ty;
	}

}
