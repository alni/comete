package info.alni.comete.android;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;
import info.alni.comete.android.R;

public class DigitalInstrument extends LinearLayout {	
	private static final String brisans="http://brisa.brisa";
	
	private TextView textName;
	private TextView textValue;
	private TextView textUnit;
	
	String name=null;
	String value=null;
	String unit=null;
	
	public DigitalInstrument(Context context) {
		super(context);
	}

	public DigitalInstrument(Context context,AttributeSet attr) {
		super(context,attr);		
		
		if (attr.getAttributeValue(brisans, "name")!=null) {
			name=attr.getAttributeValue(brisans, "name");
		}
		if (attr.getAttributeValue(brisans, "value")!=null) {
			value=attr.getAttributeValue(brisans, "value");
		}
		if (attr.getAttributeValue(brisans, "unit")!=null) {
			unit=attr.getAttributeValue(brisans, "unit");
		}
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		
		if (isInEditMode()) {
			LayoutInflater.from(getContext()).inflate(R.layout.digital_instrument, this);
		} else {
			((Activity)getContext()).getLayoutInflater().inflate(R.layout.digital_instrument, this);	
		}
		
		
		
		
		
		setupViewItems();
	}
	
	private void setupViewItems() {
		textName = (TextView) findViewById(R.id.di_name);
		if (name!=null) {
			textName.setText(name);	
		}
		
		textValue = (TextView) findViewById(R.id.di_value);		
		if (value!=null) {
			textValue .setText(value);
		}
				
		textUnit= (TextView) findViewById(R.id.di_unit);
		if (value!=null) {
			textUnit.setText(unit);
		}
		
		if (isInEditMode()) {
			//Typeface font = Typeface.createFromAsset(getContext().getAssets(), "fonts/Crysta.ttf");  
			//textValue.setTypeface(font);  		
			
		} else {
			Typeface font = Typeface.createFromAsset(((Activity)getContext()).getAssets(), "fonts/Crysta.ttf");  
		    textValue.setTypeface(font);  		
		}

	}
 
	public void setName(String text) {
		textName.setText(text);
		textName.invalidate();
	}
 
	public void setValue(String text) {
		textValue.setText(text);
		textValue.invalidate();
	}

	public void setUnit(String text) {
		textUnit.setText(text);
		textUnit.invalidate();
	}
}
