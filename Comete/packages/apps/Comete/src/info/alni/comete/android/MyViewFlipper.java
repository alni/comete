package info.alni.comete.android;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ViewFlipper;

public class MyViewFlipper extends ViewFlipper {

    public MyViewFlipper(Context context) {
        super(context);
    }
    public MyViewFlipper(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDetachedFromWindow() {
        try{
            super.onDetachedFromWindow();

             //getContext().unregisterReceiver(mReceiver);             
        } catch (IllegalArgumentException e) {
			// Call stopFlipping() in order to kick off updateRunning()
			stopFlipping();
        } catch(Exception e) {
            Log.d("MyViewFlipper","Stopped a viewflipper crash");
            stopFlipping();
        }
    }


}
