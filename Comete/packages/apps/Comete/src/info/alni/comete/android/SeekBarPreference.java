package info.alni.comete.android;

import info.alni.comete.android.R;
import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.preference.Preference;
import org.holoeverywhere.widget.SeekBar;
import org.holoeverywhere.widget.SeekBar.OnSeekBarChangeListener;
import org.holoeverywhere.widget.TextView;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public class SeekBarPreference extends Preference implements OnSeekBarChangeListener {
	
	private final String TAG = getClass().getName();
	
	//private static final String androidns="http://schemas.android.com/apk/res/android";
	private static final String robobunnyns="http://robobunny.com";
	private static final float DEFAULT_VALUE = 0.5f;
	
	private float mMaxValue     = 1;
	private float mMinValue     = 0;
	private float mInterval     = 0.01f;
	private float mCurrentValue;
	private String mUnits     = "";
	
	private TextView mStatusText;

	public SeekBarPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		setValuesFromXml(attrs);
	}

	public SeekBarPreference(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setValuesFromXml(attrs);
	}

	private void setValuesFromXml(AttributeSet attrs) {
		
		if (attrs.getAttributeValue(robobunnyns, "max")!=null) {
			mMaxValue = Float.parseFloat(attrs.getAttributeValue(robobunnyns, "max"));			
		}
		if (attrs.getAttributeValue(robobunnyns, "min")!=null) {
			mMinValue = Float.parseFloat(attrs.getAttributeValue(robobunnyns, "min"));
		}
		mUnits = attrs.getAttributeValue(robobunnyns, "units");
		try {
			String newInterval = attrs.getAttributeValue(robobunnyns, "interval");
			if(newInterval != null)
				mInterval = Float.parseFloat(newInterval);
		}
		catch(Exception e) {
			Log.e(TAG, "Invalid interval value", e);
		}
		
	}
	
	@Override
	protected View onCreateView(ViewGroup parent){
		
		RelativeLayout layout =  null;
				
		try {
			LayoutInflater mInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			layout = (RelativeLayout)mInflater.inflate(R.layout.seek_bar_preference, parent, false);
			
			TextView title = (TextView)layout.findViewById(R.id.seekBarPrefTitle);
			title.setText(getTitle());
			
			TextView summary = (TextView)layout.findViewById(R.id.seekBarPrefSummary);
			summary.setText(getSummary());
			
			SeekBar seekBar = (SeekBar)layout.findViewById(R.id.seekBarPrefBar);
			seekBar.setMax((int) (Math.abs(mMaxValue - mMinValue)*100));							
			seekBar.setProgress((int) (( Math.abs(mCurrentValue-mMinValue)/(mMaxValue-mMinValue) )*seekBar.getMax()));
			seekBar.setOnSeekBarChangeListener(this);

			mStatusText = (TextView)layout.findViewById(R.id.seekBarPrefValue);
			mStatusText.setText(String.valueOf(mCurrentValue));
			mStatusText.setMinimumWidth(30);
			
			TextView units = (TextView)layout.findViewById(R.id.seekBarPrefUnits);
			units.setText(mUnits);
			
		}
		catch(Exception e) {
			Log.e(TAG, "Error building seek bar preference", e);
		}

		return layout; 
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

		float newValue = ((float) progress)/100 + mMinValue;
		
		if(newValue > mMaxValue)
			newValue = mMaxValue;
		else if(newValue < mMinValue)
			newValue = mMinValue;
		else if(mInterval != 1 && newValue % mInterval != 0) {
			if (mInterval>1) {
				newValue = Math.round(((float)newValue)/mInterval)*mInterval;  				
			} else {
				newValue = Math.round(newValue/mInterval)*mInterval;
			}
		}
		// change rejected, revert to the previous value
		int v=(int) (
				( Math.abs(newValue-mMinValue)/(mMaxValue-mMinValue) )*seekBar.getMax()
		);
		if(! callChangeListener(v)){
			seekBar.setProgress((int) (( Math.abs(mCurrentValue-mMinValue)/(mMaxValue-mMinValue) )*seekBar.getMax())); 
			return; 
		}

		// change accepted, store it
		mCurrentValue = newValue;
		mStatusText.setText(String.valueOf(newValue));
		persistFloat(newValue);
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		notifyChanged();
	}


	@Override 
	protected Object onGetDefaultValue(TypedArray ta, int index){
		
		float defaultValue = ta.getFloat(index, DEFAULT_VALUE);
		return defaultValue;
		
	}

	@Override
	protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {

		if(restoreValue) {
			try {
				mCurrentValue = getPersistedFloat(mCurrentValue);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else {
			float temp = (Float)defaultValue;
			persistFloat(temp);
			mCurrentValue = temp;
		}
		
	}

}
