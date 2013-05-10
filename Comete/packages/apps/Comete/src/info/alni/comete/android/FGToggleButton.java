package info.alni.comete.android;
import static info.alni.comete.android.Common.fgfs;

import java.io.IOException;


import android.content.Context;
import android.util.AttributeSet;
import android.widget.ToggleButton;


public class FGToggleButton extends ToggleButton {

	@Override
	public boolean performClick() {
		boolean res=super.performClick();
		new Thread(new TrueFalseTask(fgProp,isChecked())).start();
		
		return res;
	}

	String fgProp;
	
	public FGToggleButton(Context context) {
		super(context);
	}

	public FGToggleButton(Context context,AttributeSet attrs) {
		super(context,attrs);
	}
	
	public String getFgProp() {
		return fgProp;
	}

	public void setFgProp(String fgProp) {
		this.fgProp = fgProp;
	}
	
	private boolean isBoolean=true;
	
	public boolean isBoolean() {
		return isBoolean;
	}

	public void setBoolean(boolean isBoolean) {
		this.isBoolean = isBoolean;
	}

	

	private class TrueFalseTask implements Runnable {
    	private boolean mOn;
    	private String prop;

    	public TrueFalseTask(String prop,boolean on) {
    	    mOn = on;
    	    this.prop=prop;
    	}

    	@Override
    	public void run() {
    	    if (fgfs != null) {    		
    	    	try {
    	    		if (isBoolean()) {
    	    			fgfs.setBoolean(prop, mOn);    	    			
    	    		} else {
    	    			if (mOn) {
    	    				fgfs.setFloat(prop, 1);
    	    			} else {
    	    				fgfs.setFloat(prop, 0);
    	    			}
    	    		}
    		    } catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		    }
    		}

    	}

    }
}
