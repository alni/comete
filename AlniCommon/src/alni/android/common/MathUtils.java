/**
 * Alni Common - Common utilities to be used with Android development
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

package alni.android.common;

import java.util.ArrayList;
import static java.lang.Math.*;
import android.content.Context;

public class MathUtils {
    public static float closestFloat(float value, ArrayList<Float> list) {
		float min = Float.MIN_VALUE;
		float closest = value;
	
		for (float v : list) {
		    final float diff = abs(v - value);
	
		    if (diff < min) {
			min = diff;
			closest = v;
		    }
		}
	
		return closest;

    }

    public static int dipToPixels(Context context, float dip) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dip * scale + 0.5f);
    }

    public static int flipValueInRange(int value, int max) {
    	return abs(value - max);
    }

    public static float flipValueInRange(float value, float max) {
    	return abs(value - max);
    }

    public static float valRangeToDec(float value, float max) {
    	return value / max;
    }

    public static float valToMinMax(float value) {
		if (value > 1.0f)
		    return 1.0f;
		else if (value < -1.0f)
		    return -1.0f;
		return value;
    }

    public static float valToMinMax(float value,float min,float max) {
		if (value > max)
		    return max;
		else if (value < min)
		    return min;
		return value;
    }
}
