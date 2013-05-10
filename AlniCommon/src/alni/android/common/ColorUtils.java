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

import static android.graphics.Color.*;

public class ColorUtils {
    public static int getColor(int c0, int c1, float p) {
	int a = ave(alpha(c0), alpha(c1), p);
	int r = ave(red(c0), red(c1), p);
	int g = ave(green(c0), green(c1), p);
	int b = ave(blue(c0), blue(c1), p);
	return argb(a, r, g, b);
    }

    public static int getColorHSV(int c0, int c1, float p) {
	float[] hsv0 = new float[3];
	float[] hsv1 = new float[3];
	colorToHSV(c0, hsv0);
	colorToHSV(c1, hsv1);
	float h = ave(hsv0[0], hsv1[0], p);
	return HSVToColor(new float[] { h, hsv0[1], hsv1[2] });
    }

    /**
     * Linearly interpolates between two colors based on n. If n is 0, this
     * returns a Color equal to c2. If n is 1, you get c1 instead. A c value of
     * .5 will return the average, and so forth.
     * 
     * @param c1
     *            the first Color
     * @param c2
     *            the second Color
     * @param n
     *            the ratio of c2 to use
     * @return the interpolated color.
     */
    public static int interpolate(int c1, int c2, float n) {
	float m = 1 - n;
	return rgb((int) (n * red(c1) + m * red(c2)), (int) (n * green(c1) + m
		* green(c2)), (int) (n * blue(c1) + m * blue(c2)));
    }

    public static float ave(float src, float dst, float p) {
	return src + (p * (dst - src));
    }

    private static int ave(int src, int dst, float p) {
	return src + Math.round(p * (dst - src));
    }
}
