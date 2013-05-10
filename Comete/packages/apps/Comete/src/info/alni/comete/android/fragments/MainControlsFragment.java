package info.alni.comete.android.fragments;

import static alni.android.common.CommonUtils.showMsg;
import static info.alni.comete.android.Common.NUM_OF_ENGINES;
import static info.alni.comete.android.Common.fgfs;

import java.io.IOException;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.widget.Button;
import org.holoeverywhere.widget.LinearLayout;
import org.holoeverywhere.widget.SeekBar;
import org.holoeverywhere.widget.TextView;
import org.holoeverywhere.widget.ToggleButton;
import org.holoeverywhere.widget.SeekBar.OnSeekBarChangeListener;

import info.alni.comete.android.DigitalInstrument;
import info.alni.comete.android.Comete;
import info.alni.comete.android.R;
import alni.comete.android.widgets.gauges.NegPosAnalogGauge;
import alni.comete.android.widgets.gauges.VerticalNegPosAnalogGauge;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;

import com.tokaracamara.android.verticalslidebar.VerticalSeekBar;

public class MainControlsFragment extends Fragment implements
		View.OnTouchListener, VerticalSeekBar.OnSeekBarChangeListener,
		OnSeekBarChangeListener {

	private VerticalSeekBar mThrottle;
	private VerticalSeekBar mFlaps;
	private SeekBar mRudder;
	private ToggleButton mReverser;
	private Button buttonStart;
	private Comete mAct;
	private ToggleButton mBrake;
	private VerticalNegPosAnalogGauge mGElevator;
	private NegPosAnalogGauge mGAileron;
	private Button reconnectButton;
	private TextView reconnectTxt;
	private LinearLayout mActionbar;
	private Button fgViewButton;
	private DigitalInstrument airspeed;
	private DigitalInstrument direction;
	
	public MainControlsFragment() {
		
	}

	public VerticalNegPosAnalogGauge getGElevator() {
		return mGElevator;
	}

	public NegPosAnalogGauge getGAileron() {
		return mGAileron;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onResume()
	 */
	@Override
	public void onResume() {
		super.onResume();

		// sm.registerListener(sensorEventListener, mAccelSensor,
		// SensorManager.SENSOR_DELAY_GAME);
		// sm.registerListener(sensorEventListener, mOrienSensor,
		// SensorManager.SENSOR_DELAY_GAME);
		mHideControlsHandler.postDelayed(mHideControlsTask, 7500L);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		// return super.onCreateView(inflater, container,
		// savedInstanceState);

		View v = inflater.inflate(R.layout.main_controls, null);
		if (v != null) {
			v.setOnTouchListener(this);
			
			setFgViewButton((Button) v.findViewById(R.id.fgViewButton));
			
			setActionbar((LinearLayout) v.findViewById(R.id.actionbar));
			getActionbar().setOnTouchListener(this);
			
			setThrottle((VerticalSeekBar) v.findViewById(R.id.throttle));
			System.out.println(getThrottle());
			
			setFlaps((VerticalSeekBar) v.findViewById(R.id.flaps));
			setRudder((SeekBar) v.findViewById(R.id.rudder));

			setReverser((ToggleButton) v.findViewById(R.id.reverser));
			getReverser().setOnCheckedChangeListener(onCheckedListener);
			setBrake((ToggleButton) v.findViewById(R.id.brake));
			getBrake().setOnCheckedChangeListener(onCheckedListener);
			
			setAirspeed((DigitalInstrument) v.findViewById(R.id.txt_airspeed));
			setDirection((DigitalInstrument) v.findViewById(R.id.txt_direction));
			
			//
			// buttonStart = (Button) v.findViewById(R.id.buttonStart);
			// buttonStart.setOnTouchListener(new OnTouchListener() {
			// @Override
			// public boolean onTouch(View v, MotionEvent event) {
			// if (event.getAction() == MotionEvent.ACTION_DOWN) {
			// mAct.startEnginePressedReleased(true);
			// } else if (event.getAction() == MotionEvent.ACTION_UP) {
			// mAct.startEnginePressedReleased(false);
			// }
			// return true;
			// }
			// });

			getThrottle().setOnSeekBarChangeListener(this);
			getFlaps().setOnSeekBarChangeListener(this);
			getRudder().setOnSeekBarChangeListener(this);

			setGElevator((VerticalNegPosAnalogGauge) v
					.findViewById(R.id.gElevator));
			getGElevator().setRoundFactor(1);
			setGAileron((NegPosAnalogGauge) v.findViewById(R.id.gAileron));
			getGAileron().setRoundFactor(1);

			setReconnectButton((Button) v.findViewById(R.id.reconnectButton));
			setReconnectTxt((TextView) v
					.findViewById(R.id.textViewReconnect));
			if (mAct.getConnectionPrefs() != null && !mAct.getConnectionPrefs().getString("ipAddress", "").equals("")) {
				getReconnectTxt().setText(mAct.getConnectionPrefs().getString(
						"ipAddress", "")
						+ ":"
						+ mAct.getConnectionPrefs().getString("port", ""));
			} else {
				getReconnectButton().setVisibility(View.GONE);
				getReconnectTxt().setVisibility(View.GONE);

			}
		}
		return v;
	}

	public static MainControlsFragment newInstance(Comete act) {
		MainControlsFragment frg = new MainControlsFragment();
		frg.mAct = act;
		return frg;
	}

	public void touchHandler(MotionEvent event) {
		final int action = event.getAction();
		if (action == MotionEvent.ACTION_DOWN) {
			isDown = true;
			if (!isShowing) {
				isShowing = true;
				mShowControlsHandler.postDelayed(mShowControlsTask, 250L);
			}
		} else if (action == MotionEvent.ACTION_UP) {
			isDown = false;
			if (!isHiding) {
				isHiding = true;
				mHideControlsHandler.removeCallbacks(mHideControlsTask);
				mHideControlsHandler.postDelayed(mHideControlsTask, 7500);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.tokaracamara.android.verticalslidebar.VerticalSeekBar.
	 * OnSeekBarChangeListener
	 * #onProgressChanged(com.tokaracamara.android.verticalslidebar
	 * .VerticalSeekBar, int, boolean)
	 */
	@Override
	public void onProgressChanged(VerticalSeekBar seekBar, int progress,
			boolean fromUser) {
		System.out.println("PROGRESS="+progress);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.tokaracamara.android.verticalslidebar.VerticalSeekBar.
	 * OnSeekBarChangeListener
	 * #onStartTrackingTouch(com.tokaracamara.android.verticalslidebar
	 * .VerticalSeekBar)
	 */
	@Override
	public void onStartTrackingTouch(VerticalSeekBar seekBar) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.tokaracamara.android.verticalslidebar.VerticalSeekBar.
	 * OnSeekBarChangeListener
	 * #onStopTrackingTouch(com.tokaracamara.android.verticalslidebar
	 * .VerticalSeekBar)
	 */
	@Override
	public void onStopTrackingTouch(VerticalSeekBar seekBar) {
		mHideControlsHandler.removeCallbacks(mHideControlsTask);
		mHideControlsHandler.postDelayed(mHideControlsTask, 7500);
	}

	private boolean isDown = false;
	private boolean isHiding = false;
	private boolean isShowing = false;

	private Handler mShowControlsHandler = new Handler();
	private Handler mHideControlsHandler = new Handler();

	private Runnable mShowControlsTask = new Runnable() {

		@Override
		public void run() {
			final int hidden = View.INVISIBLE;
			if (getThrottle().getVisibility() == hidden
					|| getFlaps().getVisibility() == hidden
				 || getActionbar().getVisibility() == hidden) {
				Animation fadeIn = AnimationUtils.loadAnimation(mAct,
						android.R.anim.fade_in);
				getThrottle().setVisibility(View.VISIBLE);
				getFlaps().setVisibility(View.VISIBLE);
				 getActionbar().setVisibility(View.VISIBLE);
				getRudder().setVisibility(View.VISIBLE);
				// radioButton.setVisibility(View.VISIBLE);
				getFgViewButton().setVisibility(View.VISIBLE);
				//
				getThrottle().startAnimation(fadeIn);
				getFlaps().startAnimation(fadeIn);
				getActionbar().startAnimation(fadeIn);
				getRudder().startAnimation(fadeIn);
				// radioButton.startAnimation(fadeIn);
				getFgViewButton().startAnimation(fadeIn);
			}
			isShowing = false;
		}

	};
	private Runnable mHideControlsTask = new Runnable() {

		@Override
		public void run() {
			if (!getFlaps().isPressed() && !getThrottle().isPressed()
					&& !getBrake().isPressed() &&
					// !mGear.isPressed() &&
					!isDown
			//
			) {
				Animation fadeOut = AnimationUtils.loadAnimation(mAct,
						android.R.anim.fade_out);
				getThrottle().startAnimation(fadeOut);
				getFlaps().startAnimation(fadeOut);
				getActionbar().startAnimation(fadeOut);
				// radioButton.startAnimation(fadeOut);
				getFgViewButton().startAnimation(fadeOut);
				//
				getThrottle().setVisibility(View.INVISIBLE);
				getFlaps().setVisibility(View.INVISIBLE);
				getActionbar().setVisibility(View.INVISIBLE);
				getRudder().setVisibility(View.INVISIBLE);
				// radioButton.setVisibility(View.INVISIBLE);
				fgViewButton.setVisibility(View.INVISIBLE);
				isHiding = false;
			}
		}

	};

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		mHideControlsHandler.removeCallbacks(mHideControlsTask);
		mHideControlsHandler.postDelayed(mHideControlsTask, 7500);

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		touchHandler(event);
		return mAct.onTouchEvent(event);
	}
	
	public VerticalSeekBar getThrottle() {
		return mThrottle;
	}

	public void setThrottle(VerticalSeekBar mThrottle) {
		this.mThrottle = mThrottle;
	}

	public VerticalSeekBar getFlaps() {
		return mFlaps;
	}

	public void setFlaps(VerticalSeekBar mFlaps) {
		this.mFlaps = mFlaps;
	}

	public SeekBar getRudder() {
		return mRudder;
	}

	public void setRudder(SeekBar mRudder) {
		this.mRudder = mRudder;
	}

	public ToggleButton getReverser() {
		return mReverser;
	}

	public void setReverser(ToggleButton mReverser) {
		this.mReverser = mReverser;
	}

	public ToggleButton getBrake() {
		return mBrake;
	}

	public void setBrake(ToggleButton mBrake) {
		this.mBrake = mBrake;
	}

	public void setGElevator(VerticalNegPosAnalogGauge mGElevator) {
		this.mGElevator = mGElevator;
	}

	public void setGAileron(NegPosAnalogGauge mGAileron) {
		this.mGAileron = mGAileron;
	}

	public Button getReconnectButton() {
		return reconnectButton;
	}

	public void setReconnectButton(Button reconnectButton) {
		this.reconnectButton = reconnectButton;
	}

	public TextView getReconnectTxt() {
		return reconnectTxt;
	}

	public void setReconnectTxt(TextView reconnectTxt) {
		this.reconnectTxt = reconnectTxt;
	}

	public LinearLayout getActionbar() {
		return mActionbar;
	}

	public void setActionbar(LinearLayout mActionbar) {
		this.mActionbar = mActionbar;
	}

	public Button getFgViewButton() {
		return fgViewButton;
	}

	public void setFgViewButton(Button fgViewButton) {
		this.fgViewButton = fgViewButton;
	}

	public DigitalInstrument getAirspeed() {
		return airspeed;
	}

	public void setAirspeed(DigitalInstrument airspeed) {
		this.airspeed = airspeed;
	}

	public DigitalInstrument getDirection() {
		return direction;
	}

	public void setDirection(DigitalInstrument direction) {
		this.direction = direction;
	}

	public final CompoundButton.OnCheckedChangeListener onCheckedListener = new CompoundButton.OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			if (getBrake().equals(buttonView) && fgfs != null) {
				long delay = SystemClock.uptimeMillis();
				if (isChecked) {
					for (int i = 0; i < mBrakeSteps.length; i++) {
						delay += 500L;
						mBrakeStepHandler.postAtTime(new BrakeStepTask(i),
								delay);
					}

				} else {
					for (int i = mBrakeSteps.length - 1; i >= 0; i--) {
						delay += 500L;
						mBrakeStepHandler.postAtTime(new BrakeStepTask(i),
								delay);
					}
				}
			} else if (getReverser().equals(buttonView) && fgfs != null) {
				mSpoilerHandler.postDelayed(new SpoilerTask(isChecked), 500L);
			} else {
				// trueFalseHandler.postDelayed(new
				// TrueFalseTask(trueFalseChecks.get(buttonView),isChecked),
				// 500L);
			}

		}
	};

	private Handler mSpoilerHandler = new Handler();

	private class SpoilerTask implements Runnable {
		private boolean mOn;

		public SpoilerTask(boolean on) {
			mOn = on;
		}

		@Override
		public void run() {
			if (fgfs != null) {
				try {
					mAct.sendToMultiple(mAct.getReverserArr()[0], NUM_OF_ENGINES,
							mAct.getReverserArr()[1], mOn);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}

	}

	private float[] mBrakeSteps = { 0.0f, 0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f,
			0.7f, 0.8f, 0.9f, 1.0f };

	private Handler mBrakeStepHandler = new Handler();

	private class BrakeStepTask implements Runnable {

		private int mIndex;

		public BrakeStepTask(int index) {
			mIndex = index;
		}

		@Override
		public void run() {
			if (fgfs != null) {
				try {

					fgfs.setFloat(mAct.getBrakeLeftPropKey(), mBrakeSteps[mIndex]);
					fgfs.setFloat(mAct.getBrakeRightPropKey(), mBrakeSteps[mIndex]);
				} catch (IOException e) {
					showMsg(mAct, e.toString());
					e.printStackTrace();
				}
			}
		}

	}
}