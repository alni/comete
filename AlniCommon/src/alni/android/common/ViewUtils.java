package alni.android.common;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;

public class ViewUtils {
    public static Bitmap clipBitmapCircular(Bitmap sourceBitmap, float w, float h) {
	Bitmap targetBitmap = Bitmap.createBitmap((int)w, (int)h, Bitmap.Config.ARGB_8888);
	Canvas canvas = new Canvas(targetBitmap);
	Path path = new Path();
	path.addCircle((w - 1) / 2f, (h - 1) / 2f, Math.min(w, h)/2f, Path.Direction.CCW);
	canvas.clipPath(path);
	canvas.drawBitmap(sourceBitmap, 
		new Rect(0, 0, sourceBitmap.getWidth(), sourceBitmap.getHeight()), 
		new Rect(0, 0, (int)w, (int)h),
		null);
	return targetBitmap;
    }
}
