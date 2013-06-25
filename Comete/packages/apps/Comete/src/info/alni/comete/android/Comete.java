/**
 * Comete - Control Comete with an Android device
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

package info.alni.comete.android;

import java.io.IOException;
import java.util.Locale;

import static alni.android.common.CommonUtils.showMsg;
import static info.alni.comete.android.Common.NUM_OF_ENGINES;
import static info.alni.comete.android.Common.URL_BASE;
import static info.alni.comete.android.Common.URL_HELP_MAIN;
import static info.alni.comete.android.Common.fgfs;
import static java.lang.Math.abs;

import org.flightgear.fgfsclient.FGFSConnection;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.widget.Spinner;

import com.tokaracamara.android.verticalslidebar.VerticalSeekBar;

import alni.android.common.CommonUtils;
import alni.android.common.MathUtils;
import alni.android.common.StringUtils;
import alni.comete.android.widgets.gauges.NegPosAnalogGauge;
import alni.comete.android.widgets.gauges.VerticalNegPosAnalogGauge;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.ActionBar.TabListener;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class Comete extends Activity implements View.OnTouchListener {

	private static final int DIALOG_CONNECT_ID = 0;
	private static final int DIALOG_HELP_ID = 1;

	private SharedPreferences connectionPrefs;

	/*
	 * Sensor data vars
	 */
	private float[] mGData = new float[3];
	private float[] mMData = new float[3];
	private float[] mR = new float[16];
	private float[] mI = new float[16];

	private float[] mOrient = new float[3];

	/*
	 * Property data
	 */
	// private ArrayList<Float> flaps = new ArrayList<Float>();

	String airspeedKey = "";
	String directionKey = "";
	String nav1FreqKey = "";
	String nav1RadKey = "";
	String nav2FreqKey = "";
	String nav2RadKey = "";
	String adf1FreqKey = "";
	String com1FreqKey = "";

	String fgViewKey = "";

	@Override
	protected void onStart() {
		super.onStart();

		getPrefs();
	}

	private SensorManager sm;

	private Sensor mAccelSensor;
	private Sensor mOrienSensor;

	// private TextView mTvDiag;
	private TextView reconnectTxt;
	private LinearLayout mActionbar;

	private Button buttonStart, reconnectButton, radioButton, fgViewButton;

	private ViewPager mPager;
	private Menu mMenu;

	// private HashMap<CompoundButton, String> trueFalseChecks =new
	// HashMap<CompoundButton, String>();

	PropertyGetterThread thrGetAirspeed;
	private PanelsAdapter mAdapter;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_views);

		setConnectionPrefs(getSharedPreferences("connection", MODE_PRIVATE));

		sm = (SensorManager) getSystemService(SENSOR_SERVICE);
		mAccelSensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mOrienSensor = sm.getDefaultSensor(Sensor.TYPE_ORIENTATION);

		mPager = (ViewPager) findViewById(R.id.pager);
		mPager.setOnTouchListener(this);
		mAdapter = new PanelsAdapter(getSupportFragmentManager(), this);
		mPager.setOffscreenPageLimit(mAdapter.getCount() - 1);
		mPager.setAdapter(mAdapter);

		// mTvDiag = (TextView) findViewById(R.id.tvDiag);

		//

		// ((FGToggleButton)
		// findViewById(R.id.gear)).setFgProp("/controls/gear/gear-down");

		// trueFalseChecks.put((FGToggleButton)
		// findViewById(R.id.gear),"/controls/gear/gear-down");
		// trueFalseChecks.put((FGToggleButton)
		// findViewById(R.id.gear2),"/controls/gear/gear-down");
		// trueFalseChecks.put((FGToggleButton)
		// findViewById(R.id.autoCoordination),"/sim/auto-coordination");

		// for (CompoundButton c:trueFalseChecks.keySet() ) {
		// c.setOnCheckedChangeListener(onCheckedListener);
		// }

		//

		// radioButton = (Button) findViewById(R.id.radioButton);

		//
		//

		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());

		Handler handler = new MyHandler();
		thrGetAirspeed = new PropertyGetterThread(handler);

		airspeedKey = sharedPreferences.getString("airspeedKey",
				getString(R.string.prop_airspeed));
		directionKey = sharedPreferences.getString("directionKey",
				getString(R.string.prop_direction));
		nav1FreqKey = sharedPreferences.getString("nav1FreqKey",
				getString(R.string.prop_nav1Freq));
		nav1RadKey = sharedPreferences.getString("nav1RadKey",
				getString(R.string.prop_nav1Rad));
		nav2FreqKey = sharedPreferences.getString("nav2FreqKey",
				getString(R.string.prop_nav2Freq));
		nav2RadKey = sharedPreferences.getString("nav2RadKey",
				getString(R.string.prop_nav2Rad));
		adf1FreqKey = sharedPreferences.getString("adf1FreqKey",
				getString(R.string.prop_adf1Freq));
		com1FreqKey = sharedPreferences.getString("com1FreqKey",
				getString(R.string.prop_com1Freq));

		fgViewKey = sharedPreferences.getString("fgViewKey",
				getString(R.string.prop_fgView));

		thrGetAirspeed.addProp(airspeedKey,
				getString(R.string.prop_airspeed_format));
		thrGetAirspeed.addProp(directionKey,
				getString(R.string.prop_direction_format));
		
		thrGetAirspeed.addProp("tas",
				getString(R.string.prop_airspeed_format));
		thrGetAirspeed.addProp("hdg",
				getString(R.string.prop_direction_format));

		thrGetAirspeed.setEnabled(sharedPreferences.getBoolean("enableGetting",
				true));
		thrGetAirspeed.start();

	}

	@Override
	public void onBackPressed() {

		// MyViewFlipper flipper = (MyViewFlipper) findViewById(R.id.flipper);
		//
		// if (flipper.getCurrentView().getId() == R.id.second) {
		// onGoToMainView(null);
		// return;
		//
		// }

		// Ask the user if they want to quit
		new AlertDialog.Builder(this)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setTitle(R.string.quit)
				.setMessage(R.string.really_quit)
				.setPositiveButton(R.string.yes,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {

								// Stop the activity
								Comete.super.onBackPressed();
							}

						}).setNegativeButton(R.string.no, null).show();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		super.onPause();
		sm.unregisterListener(sensorEventListener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();

		sm.registerListener(sensorEventListener, mAccelSensor,
				SensorManager.SENSOR_DELAY_GAME);
		sm.registerListener(sensorEventListener, mOrienSensor,
				SensorManager.SENSOR_DELAY_GAME);
		// mHideControlsHandler.postDelayed(mHideControlsTask, 7500L);
	}

	/**
	 * @deprecated Moved to Fragment based ViewPager
	 * @param view
	 */
	public void onGoToCockpit(View view) {
		// create the ViewSwitcher in the current context
		MyViewFlipper flipper = (MyViewFlipper) findViewById(R.id.flipper);
		View engineV = (View) findViewById(R.id.second);
		flipper.setDisplayedChild(flipper.indexOfChild(engineV));
	}

	/**
	 * @deprecated Moved to Fragment based ViewPager
	 * @param view
	 */
	public void onGoToRadio(View view) {
		// create the ViewSwitcher in the current context
		MyViewFlipper flipper = (MyViewFlipper) findViewById(R.id.flipper);
		// View radioV=(View) findViewById(R.id.third);
		// flipper.setDisplayedChild(flipper.indexOfChild(radioV));
	}

	/**
	 * @deprecated Moved to Fragment based ViewPager
	 * @param view
	 */
	public void onGoToMainView(View view) {
		// create the ViewSwitcher in the current context
		MyViewFlipper flipper = (MyViewFlipper) findViewById(R.id.flipper);
		View firstV = (View) findViewById(R.id.first);
		flipper.setDisplayedChild(flipper.indexOfChild(firstV));
	}

	public void setNav1Freq(View view) {
		if (fgfs != null) {
			double v = Double.parseDouble(mAdapter.getRadioPanelFragment()
					.getNav1Text().getText().toString());
			try {
				fgfs.setDouble(nav1FreqPropKey, v);
			} catch (IOException e) {
				showMsg(Comete.this, e.toString());
			}
		} else if (Common.msfs != null) {
			String str = mAdapter.getRadioPanelFragment()
					.getNav1Text().getText().toString();
			new MSFSRequestTask().execute("set NAV1 " + str);
		}
	}

	public void setNav2Freq(View view) {
		if (fgfs != null) {
			double v = Double.parseDouble(mAdapter.getRadioPanelFragment()
					.getNav2Text().getText().toString());
			try {
				fgfs.setDouble(nav2FreqPropKey, v);
			} catch (IOException e) {
				showMsg(Comete.this, e.toString());
			}
		} else if (Common.msfs != null) {
			String str = mAdapter.getRadioPanelFragment()
					.getNav2Text().getText().toString();
			new MSFSRequestTask().execute("set NAV2 " + str);
		}
	}

	public void setAdf1Freq(View view) {
		if (fgfs != null) {
			double v = Double.parseDouble(mAdapter.getRadioPanelFragment()
					.getAdf1Text().getText().toString());
			try {
				fgfs.setDouble(adf1FreqPropKey, v);
			} catch (IOException e) {
				showMsg(Comete.this, e.toString());
			}
		}
	}

	public void setCom1Freq(View view) {
		if (fgfs != null) {
			double v = Double.parseDouble(mAdapter.getRadioPanelFragment()
					.getCom1Text().getText().toString());
			try {
				fgfs.setDouble(com1FreqPropKey, v);
			} catch (IOException e) {
				showMsg(Comete.this, e.toString());
			}
		} else if (Common.msfs != null) {
			String str = mAdapter.getRadioPanelFragment()
					.getCom1Text().getText().toString();
			new MSFSRequestTask().execute("set COM1 " + str);
		}
	}

	private int fgView = 0;

	public void onChangeFGView(View view) {
		if (fgfs != null) {
			try {
				fgView = 1 - fgView;
				fgfs.setInt(fgViewPropKey, fgView);
			} catch (IOException e) {
				showMsg(Comete.this, e.toString());
			} catch (Exception e) {
				showMsg(Comete.this, e.toString());
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onTouchEvent(android.view.MotionEvent)
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// touchHandler(event);
		try {
			mAdapter.getMainControlsFragment().touchHandler(event);
		} catch (Exception ex) {

		}
		return super.onTouchEvent(event);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnTouchListener#onTouch(android.view.View,
	 * android.view.MotionEvent)
	 */
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// touchHandler(event);
		try {
			mAdapter.getMainControlsFragment().touchHandler(event);
		} catch (Exception ex) {

		}
		return onTouchEvent(event);
	}

	// /*
	// * (non-Javadoc)
	// *
	// * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	// */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.main, menu);
		mMenu = menu;
		return true;
	}

	//
	// /*
	// * (non-Javadoc)
	// *
	// * @see android.app.Activity#onPrepareOptionsMenu(android.view.Menu)
	// */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.findItem(R.id.connect).setEnabled(fgfs == null && Common.msfs == null);
		menu.findItem(R.id.disconnect).setEnabled(fgfs != null || Common.msfs != null);
		return true;
	}

	//
	// /*
	// * (non-Javadoc)
	// *
	// * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	// */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		final int id = item.getItemId();
		if (id == R.id.connect) {
			showDialog(DIALOG_CONNECT_ID);
		} else if (id == R.id.disconnect) {
			try {
				if (fgfs != null)
					fgfs.close();
				fgfs = null;
				if (Common.msfs != null)
					Common.msfs.close();
				Common.msfs = null;
				onPrepareOptionsMenu(mMenu);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (id == R.id.help) {
			showDialog(DIALOG_HELP_ID);
		} else if (id == R.id.menuProps) {

			Intent i = new Intent(Comete.this, Preferences.class);
			startActivity(i);
			// Some feedback to the user
			Toast.makeText(Comete.this,
					"Here you can enter your user credentials.",
					Toast.LENGTH_LONG).show();

		} else if (id == R.id.loadDefaultsButton) {

			Intent i = new Intent(Comete.this, Preferences.class);
			i.setAction("reset");
			startActivity(i);

		}
		return super.onOptionsItemSelected(item);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateDialog(int)
	 */
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_CONNECT_ID:
			return createConnectDialog();
		case DIALOG_HELP_ID:
			return createHelpDialog();
		}

		return super.onCreateDialog(id);
	}
	
	public String mIpAddress = null;
	public int mPort;

	private AlertDialog createConnectDialog() {
		final View ll = getLayoutInflater().inflate(
				R.layout.connect_dialog, null);
		final EditText etIpAddress = (EditText) ll.findViewById(R.id.ipAddress);
		final EditText etPort = (EditText) ll.findViewById(R.id.port);
		final Spinner sSimType = (Spinner) ll.findViewById(R.id.sim_type);
		etIpAddress.setText(getConnectionPrefs().getString("ipAddress", ""));
		etPort.setText(getConnectionPrefs().getString("port", ""));
		return new AlertDialog.Builder(this)
				.setTitle("Enter host IP and port ")
				.setView(ll)
				.setPositiveButton("Connect",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								String ipAddress = etIpAddress.getText()
										.toString();
								String port = etPort.getText().toString();
								int simType = sSimType.getSelectedItemPosition();
								if (StringUtils.validateIPAddress(ipAddress)
										&& StringUtils.validatePort(port)) {
									Editor editor = getConnectionPrefs().edit();
									editor.putString("ipAddress", ipAddress);
									editor.putString("port", port);
									editor.putInt("simType", simType);
									editor.commit();

									try {
										mConnectingDialog = ProgressDialog
												.show(Comete.this,
														"",
														"Connecting. Please wait...",
														true);
//										Comete.this.mIpAddress = ipAddress;
//										Comete.this.mPort = Integer.parseInt(port);
										Thread t = new Thread(new ConnectTask(
												ipAddress, Integer
														.parseInt(port), simType));
										
										t.start();
									} catch (NumberFormatException e) {
										showMsg(Comete.this, e.toString());
										e.printStackTrace();
									}
								}
							}
						})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();

							}
						}).create();
	}

	private Handler mConnectHandler = new Handler() {

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.Handler#handleMessage(android.os.Message)
		 */
		@Override
		public void handleMessage(Message msg) {
			
			if (mMenu != null) {
				Comete.this.onPrepareOptionsMenu(mMenu);
			}

			mConnectingDialog.dismiss();
			if (msg != null && msg.obj != null) {
				showMsg(Comete.this, (String) msg.obj);
				return;
			}
			if (fgfs == null && Common.msfs == null)
				return;
			
			

			mAdapter.getMainControlsFragment().getReconnectButton().setVisibility(View.GONE);
			mAdapter.getMainControlsFragment().getReconnectTxt().setVisibility(View.GONE);

			try {
				fgfs.setBoolean("/sim/auto-coordination", true);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	};
	private ProgressDialog mConnectingDialog;

	private class ConnectTask implements Runnable {
		public static final int SIM_TYPE_FGFS = 0;
		public static final int SIM_TYPE_MSFS = 1;
		private String ip;
		private int port;
		private int simType;

		public ConnectTask(String ip, int port, int simType) {
			this.ip = ip;
			this.port = port;
			this.simType = simType;
		}

		@Override
		public void run() {
			try {

				lastThrottleSent = -1;
				if (simType == SIM_TYPE_FGFS) {
					fgfs = new FGFSConnection(ip, port);
				} else if (simType == SIM_TYPE_MSFS) {
					Common.msfs = new MSFSConnection(ip, port);
				}
			} catch (IOException e) {
				Message message = new Message();
				message.obj = e.toString();
				//showMsg(Comete.this, e.toString());
				mConnectHandler.sendMessage(message);
				e.printStackTrace();
			} finally {
				mConnectHandler.sendEmptyMessage(0);

			}

		}

	}

	private class MyHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
//			if (Common.msfs != null) {
//				MSFSConnection.Controls controls = Common.msfs.getControls();
//				if (controls != null) {
//					mAdapter.getMainControlsFragment().getAirspeed().setValue(String.format(Locale.ENGLISH, 
//							getString(R.string.prop_airspeed_format), controls.getTas()));
//					mAdapter.getMainControlsFragment().getDirection().setValue(String.format(Locale.ENGLISH, 
//							getString(R.string.prop_direction_format), controls.getHdg()));
//				}
//			} else {
				Bundle bundle = msg.getData();
				if (bundle.containsKey("tas")) {
					mAdapter.getMainControlsFragment().getAirspeed().setValue(String.format(Locale.ENGLISH, 
							getString(R.string.prop_airspeed_format), bundle.getFloat("tas")));
				} else if (bundle.containsKey("hdg")) {
					mAdapter.getMainControlsFragment().getDirection().setValue(String.format(Locale.ENGLISH, 
							getString(R.string.prop_direction_format), bundle.getFloat("hdg")));
				}
				
				if (bundle.containsKey(airspeedKey)) {
					String value = bundle.getString(airspeedKey);
					mAdapter.getMainControlsFragment().getAirspeed()
							.setValue(value);
					mAdapter.getMainControlsFragment().getAirspeed().invalidate();
				} else if (bundle.containsKey(directionKey)) {
					String value = bundle.getString(directionKey);
					mAdapter.getMainControlsFragment().getDirection()
							.setValue(value);
					mAdapter.getMainControlsFragment().getDirection().invalidate();
				} else if (bundle.containsKey(nav1FreqKey)) {
					String value = bundle.getString(nav1FreqKey);
					mAdapter.getRadioPanelFragment().getNav1Text().setText(value);
				} else if (bundle.containsKey(nav2FreqKey)) {
					String value = bundle.getString(nav2FreqKey);
					mAdapter.getRadioPanelFragment().getNav2Text().setText(value);
				} else if (bundle.containsKey(nav1RadKey)) {
					String value = bundle.getString(nav1RadKey);
					mAdapter.getRadioPanelFragment().getNav1RadText()
							.setProgress((int) Double.parseDouble(value));
				} else if (bundle.containsKey(nav2RadKey)) {
					String value = bundle.getString(nav2RadKey);
					mAdapter.getRadioPanelFragment().getNav2RadText()
							.setProgress((int) Double.parseDouble(value));
				} else if (bundle.containsKey(adf1FreqKey)) {
					String value = bundle.getString(adf1FreqKey);
					mAdapter.getRadioPanelFragment().getAdf1Text().setText(value);
				} else if (bundle.containsKey(com1FreqKey)) {
					String value = bundle.getString(com1FreqKey);
					mAdapter.getRadioPanelFragment().getCom1Text().setText(value);
				}
//			}
		}
	}

	private AlertDialog createHelpDialog() {
		final WebView wv = new WebView(this);
		wv.setTag("help");
		wv.getSettings().setJavaScriptEnabled(true);
		wv.setWebViewClient(internalWebViewClient);
		wv.loadUrl(URL_HELP_MAIN);
		return new AlertDialog.Builder(this)
				.setTitle("Help")
				.setView(wv)
				.setPositiveButton("Close",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						}).create();
	}

	private WebViewClient internalWebViewClient = new WebViewClient() {

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.webkit.WebViewClient#shouldOverrideUrlLoading(android.webkit
		 * .WebView, java.lang.String)
		 */
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			if (!url.contains(URL_BASE)) {
				view.loadUrl(URL_BASE + url);
			} else {
				view.loadUrl(url);
			}

			return true;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.webkit.WebViewClient#onReceivedError(android.webkit.WebView,
		 * int, java.lang.String, java.lang.String)
		 */
		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			view.loadUrl(Common.URL_HELP_MAIN);
			// super.onReceivedError(view, errorCode, description, failingUrl);
		}
	};

	public void reconnect(View view) {
		try {
			mConnectingDialog = ProgressDialog.show(Comete.this, "",
					"Connecting. Please wait...", true);

			Thread t = new Thread(
					new ConnectTask(
							getConnectionPrefs().getString("ipAddress", ""), 
							Integer.parseInt(getConnectionPrefs().getString("port", "")), 
							getConnectionPrefs().getInt("simType", 0)
					)
			);
			t.start();
		} catch (NumberFormatException e) {
			showMsg(Comete.this, e.toString());
			e.printStackTrace();
		}
	}

	private float aileron_ = 0;
	private float elevator_ = 0;
	private float aileronOffset_ = 0;
	private float elevatorOffset_ = 0;

	private SensorEventListener sensorEventListener = new SensorEventListener() {

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {

		}

		@Override
		public void onSensorChanged(SensorEvent event) {
			final int type = event.sensor.getType();
			float[] data;
			if (type == Sensor.TYPE_ACCELEROMETER) {
				data = mGData;
			} else if (type == Sensor.TYPE_MAGNETIC_FIELD) {
				data = mMData;
			} else {
				return;
			}
			for (int i = 0; i < 3; i++)
				data[i] = event.values[i];

			SensorManager.getRotationMatrix(mR, mI, mGData, mMData);
			SensorManager.getOrientation(mR, mOrient);

			if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
				aileron_ = tweakAileronValue(mGData[0]);
			} else {
				aileron_ = tweakAileronValue(mGData[1]);
			}
			
			elevator_ = tweakElevatorValue(mGData[2]);

			float aileron = aileron_ - aileronOffset_;
			float elevator = elevator_ - elevatorOffset_;
			
			try {

			float throttle = tweakThrottleValue(mAdapter
					.getMainControlsFragment().getThrottle().getProgress(),
					mAdapter.getMainControlsFragment().getThrottle().getMax());
			float mixture = tweakThrottleValue(mAdapter
					.getCockpitPanelFragment().getMixture().getProgress(),
					mAdapter.getCockpitPanelFragment().getMixture().getMax());
			float propeller_pitch = tweakThrottleValue(mAdapter
					.getCockpitPanelFragment().getPropeller().getProgress(),
					mAdapter.getCockpitPanelFragment().getPropeller().getMax());
			float flaps = tweakFlapValue(mAdapter.getMainControlsFragment()
					.getFlaps().getProgress(), mAdapter
					.getMainControlsFragment().getFlaps().getMax());
			float rudder = (tweakThrottleValue(mAdapter
					.getMainControlsFragment().getRudder().getProgress(),
					mAdapter.getMainControlsFragment().getRudder().getMax()) - 0.5f)
					* rudderMoltDefault;

			
				// mTvDiag.setText(String.format(Locale.ENGLISH,
				// " aileron:%1$+.1f0" + "\n" + "elevator:%2$+.1f0" + "\n"
				// + "throttle: %3$.2f " + "\n"
				// + "   flaps: %4$.2f ", aileron, elevator,
				// throttle, flaps));
				// mGElevator.setProgress((int) ((elevator * 100)/10)*10);
				mAdapter.getMainControlsFragment().getGElevator()
						.setProgress(elevator);
				// mGAileron.setProgress((int) ((aileron * 100)/10)*10);
				mAdapter.getMainControlsFragment().getGAileron()
						.setProgress(aileron);
				
//				if (mIpAddress != null) {
//					new MSFSRequestTask().execute(String.format(Locale.ENGLISH,
//							"http://%s:%s/SET/ELEVATOR/%.2f", 
//							mIpAddress, mPort + "", elevator));
//					new MSFSRequestTask().execute(String.format(Locale.ENGLISH,
//							"http://%s:%s/SET/AILERON/%.2f", 
//							mIpAddress, mPort + "", aileron));
//				}
				
				if (Common.msfs != null) {
					try {
						MSFSConnection.Controls controls = new MSFSConnection.Controls();
						float ele = Common.msfs.getLastSent().getEle();
						float ail = Common.msfs.getLastSent().getAil();
						float thr = Common.msfs.getLastSent().getThr();
						if (MathUtils.floatMatches(ele, elevator, 2)) {
							elevator = -100;
						}
						controls.setEle(elevator);
						if (MathUtils.floatMatches(ail, aileron, 2)) {
							aileron = -100;
						}
						controls.setAil(aileron);
						if (MathUtils.floatMatches(thr, throttle, 2)) {
							throttle = -100;
						}
						controls.setThr(throttle);
						new MSFSRequestTask().execute(controls);
//						new MSFSRequestTask().execute(String.format(Locale.ENGLISH,
//								"pch=%.2f,bnk=%.2f,thr=%.2f", elevator*360, aileron*360, throttle));
					} catch (/*IO*/Exception e) {
						e.printStackTrace();
					}
				} else if (fgfs != null) {
					try {
						fgfs.setFloat(aileronPropKey, aileron);
						fgfs.setFloat(elevatorPropKey, elevator);
						setAllThrottles(throttle);
						setAllMix(mixture);
						setAllPropeller(propeller_pitch);
						fgfs.setFloat(flapPropKey, flaps);
						fgfs.setFloat(rudderPropKey, rudder);
					} catch (IOException e) {
						showMsg(Comete.this, e.toString());
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				showMsg(Comete.this, ex.toString());
			}
		}

	};

	private void sendToMultiple(String preProp, int max, String postProp,
			float value) throws IOException {
		for (int i = 0; i < max; i++) {
			String prop = preProp + i + postProp;
			fgfs.setFloat(prop, value);
		}
	}

	public void sendToMultiple(String preProp, int max, String postProp,
			boolean value) throws IOException {
		if (fgfs != null) {
			for (int i = 0; i < max; i++) {
				String prop = preProp + i + postProp;
				fgfs.setBoolean(prop, value);
			}
		}
	}

	String throttleArr[] = new String[] { "/controls/engines/engine[",
			"]/throttle" };
	String mixtureArr[] = new String[] { "/controls/engines/engine[",
			"]/mixture" };
	String propellerArr[] = new String[] { "/controls/engines/engine[",
			"]/propeller-pitch" };
	String magnetosArr[] = new String[] { "/controls/engines/engine[",
			"]/magnetos" };
	String starterArr[] = new String[] { "/controls/engines/engine[",
			"]/starter" };
	String startSwitchsArr[] = new String[] { "/controls/engines/engine[",
			"]/starter-switch" };
	private String reverserArr[] = new String[] { "/controls/engines/engine[",
			"]/reverser" };
	String flapPropKey = "";
	String rudderPropKey = "";
	String aileronPropKey = "";
	String elevatorPropKey = "";
	private String brakeLeftPropKey = "";
	private String brakeRightPropKey = "";
	String starter2PropKey = "";
	public String nav1FreqPropKey = "";
	private String nav1RadPropKey = "";
	public String nav2FreqPropKey = "";
	private String nav2RadPropKey = "";
	public String adf1FreqPropKey = "";
	public String com1FreqPropKey = "";
	String fgViewPropKey = "";                   

	float aileronMoltDefault = 2;
	float elevatorMoltDefault = 2;
	float elevatorSubDefault = 0.25f;
	float rudderMoltDefault = 2;

	private float lastThrottleSent = -1;

	private void setAllThrottles(float throttle) throws IOException {
		if (throttle != lastThrottleSent) {
			sendToMultiple(throttleArr[0], NUM_OF_ENGINES, throttleArr[1],
					throttle);

			lastThrottleSent = throttle;
		}
	}

	private float lastMixSent = -1;

	private void setAllMix(float mixture) throws IOException {
		if (mixture != lastMixSent) {
			sendToMultiple(mixtureArr[0], NUM_OF_ENGINES, mixtureArr[1],
					mixture);
			lastMixSent = mixture;
		}
	}

	private float lastPropSent = -1;

	private void setAllPropeller(float propeller_pitch) throws IOException {
		if (propeller_pitch != lastPropSent) {
			sendToMultiple(propellerArr[0], NUM_OF_ENGINES, propellerArr[1],
					propeller_pitch);
			lastPropSent = propeller_pitch;
		}
	}

	private void setMagnetos(float value) {
		try {
			sendToMultiple(magnetosArr[0], NUM_OF_ENGINES, magnetosArr[1],
					value);
		} catch (IOException e) {
			showMsg(Comete.this, e.toString());
		}
	}

	public void setMagZero(View view) {
		setMagnetos(0);
	}

	public void setMagLeft(View view) {
		setMagnetos(1);
	}

	public void setMagRight(View view) {
		setMagnetos(2);
	}

	public void setMagBoth(View view) {
		setMagnetos(3);
	}

	public final void startEnginePressedReleased(boolean value) {
		try {
			sendToMultiple(starterArr[0], NUM_OF_ENGINES, starterArr[1], value);
			sendToMultiple(startSwitchsArr[0], NUM_OF_ENGINES,
					startSwitchsArr[1], value);
			fgfs.setFloat(starter2PropKey, 1);
		} catch (IOException e) {
			showMsg(Comete.this, e.toString());
		}
	}

	public void calibrate(View view) {
		aileronOffset_ = aileron_;
		elevatorOffset_ = elevator_;
	}

	private static float valToMinMax(float value) {
		if (value > 1.0f)
			return 1.0f;
		else if (value < -1.0f)
			return -1.0f;
		return value;
	}

	private static float valRangeToDec(float value, float max) {
		return value / max;
	}

	private static float flipValueInRange(float value, float max) {
		return abs(value - max);
	}

	private float tweakAileronValue(float aileron) {
		
		aileron /= SensorManager.GRAVITY_EARTH;
//		System.out.println("AILERON=" + aileron);
//		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
//			aileron = aileron - (25f/32f); //0.78125
//		}
		aileron *= aileronMoltDefault;
		
		
		return valToMinMax(aileron);
		/*
		 * if (aileron > 1.0f) aileron = 1.0f; else if (aileron < -1.0f) aileron
		 * = -1.0f; return aileron;
		 */
	}

	private float tweakElevatorValue(float elevator) {
		elevator /= SensorManager.GRAVITY_EARTH;
		elevator -= elevatorSubDefault;
		elevator *= elevatorMoltDefault;
		return valToMinMax(elevator);
		/*
		 * if (elevator > 1.0f) elevator = 1.0f; else if (elevator < -1.0f)
		 * elevator = -1.0f; return elevator;
		 */
	}

	private float tweakThrottleValue(int current, int max) {
		return valRangeToDec((float) current, (float) max);
		// return (float) (current / 100.0f);
	}

	private float tweakFlapValue(int current, int max) {
		return valRangeToDec(flipValueInRange((float) current, (float) max),
				(float) max);
		// return (float) Math.abs(current - max) / (float) max;
	}

	/**
	 * Attempt (not currently working) to work around this bug:
	 * http://code.google.com/p/android/issues/detail?id=6191 work.
	 */
	@Override
	public void onDetachedFromWindow() {
		// Log.d("Dash","OnDetachedFromWindow()");

		try {
			super.onDetachedFromWindow();
		} catch (Exception e) {
			MyViewFlipper v = (MyViewFlipper) findViewById(R.id.flipper);
			if (v != null) {
				// Log.d("Dash","De-Bug hit. e=" + e.getMessage());
				v.stopFlipping();
			}
		}
	}

	// Changed the "[%d]" in the strings to "[$d]" as Lint was complaining
	private void getPrefs() {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());

		throttleArr = sharedPreferences.getString("throttleKey",
				getString(R.string.prop_throttle)).split("$d");

		mixtureArr = sharedPreferences.getString("mixtureKey",
				getString(R.string.prop_mixture)).split("$d");
		propellerArr = sharedPreferences.getString("propellerKey",
				getString(R.string.prop_propeller)).split("$d");
		magnetosArr = sharedPreferences.getString("magnetosKey",
				getString(R.string.prop_magnetos)).split("$d");
		starterArr = sharedPreferences.getString("starterKey",
				getString(R.string.prop_starter)).split("$d");
		startSwitchsArr = sharedPreferences.getString("starterSwitchKey",
				getString(R.string.prop_starter_switch)).split("$d");
		setReverserArr(sharedPreferences.getString("reverserKey",
				getString(R.string.prop_reverser)).split("$d"));

		flapPropKey = sharedPreferences.getString("flapKey",
				getString(R.string.prop_flap));
		rudderPropKey = sharedPreferences.getString("rudderKey",
				getString(R.string.prop_rudder));
		aileronPropKey = sharedPreferences.getString("aileronKey",
				getString(R.string.prop_aileron));
		elevatorPropKey = sharedPreferences.getString("elevatorKey",
				getString(R.string.prop_elevator));
		setBrakeLeftPropKey(sharedPreferences.getString("brakeLeftKey",
				getString(R.string.prop_brakeLeft)));
		setBrakeRightPropKey(sharedPreferences.getString("brakeRightKey",
				getString(R.string.prop_brakeRight)));
		starter2PropKey = sharedPreferences.getString("starter2Key",
				getString(R.string.prop_starter2));
		nav1FreqPropKey = sharedPreferences.getString("nav1FreqKey",
				getString(R.string.prop_nav1Freq));
		setNav1RadPropKey(sharedPreferences.getString("nav1RadKey",
				getString(R.string.prop_nav1Rad)));
		nav2FreqPropKey = sharedPreferences.getString("nav2FreqKey",
				getString(R.string.prop_nav2Freq));
		setNav2RadPropKey(sharedPreferences.getString("nav2RadKey",
				getString(R.string.prop_nav2Rad)));
		adf1FreqPropKey = sharedPreferences.getString("adf1FreqKey",
				getString(R.string.prop_adf1Freq));
		com1FreqPropKey = sharedPreferences.getString("com1FreqKey",
				getString(R.string.prop_com1Freq));
		fgViewPropKey = sharedPreferences.getString("fgViewKey",
				getString(R.string.prop_fgView));

		((FGToggleButton) findViewById(R.id.gear2)).setFgProp(sharedPreferences
				.getString("gearDownKey", getString(R.string.prop_gearDown)));
		((FGToggleButton) findViewById(R.id.autoCoordination))
				.setFgProp(sharedPreferences.getString("autoCoordinationKey",
						getString(R.string.prop_autoCoordination)));
		((FGToggleButton) findViewById(R.id.parking_brake))
				.setFgProp(sharedPreferences.getString("parkingBrakeKey",
						getString(R.string.prop_parkingBrake)));
		// this will be (0 or 1) not (true or false)
		((FGToggleButton) findViewById(R.id.parking_brake)).setBoolean(false);

		try {
			aileronMoltDefault = sharedPreferences.getFloat(
					"aileronMoltDefaultKey",
					Float.parseFloat(getString(R.string.aileronMoltDefault)));
			elevatorMoltDefault = sharedPreferences.getFloat(
					"elevatorMoltDefaultKey",
					Float.parseFloat(getString(R.string.elevatorMoltDefault)));
			elevatorSubDefault = sharedPreferences.getFloat(
					"elevatorSubDefaultKey",
					Float.parseFloat(getString(R.string.elevatorSubDefault)));
			rudderMoltDefault = sharedPreferences.getFloat(
					"rudderMoltDefaultKey",
					Float.parseFloat(getString(R.string.rudderMoltDefault)));
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (sharedPreferences.getBoolean("orientationLandscape", false)) {

			/* First, get the Display from the WindowManager */
			Display display = ((WindowManager) getSystemService(WINDOW_SERVICE))
					.getDefaultDisplay();

			/* Now we can retrieve all display-related infos */
			// int width = display.getWidth();
			// int height = display.getHeight();
			int orientation = display.getOrientation();

			if (orientation != Configuration.ORIENTATION_LANDSCAPE) {
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			}
		} else {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
		}

		thrGetAirspeed.getProp().clear();
		thrGetAirspeed.addProp(sharedPreferences.getString("airspeedKey",
				getString(R.string.prop_airspeed)),
				getString(R.string.prop_airspeed_format));
		thrGetAirspeed.addProp(sharedPreferences.getString("directionKey",
				getString(R.string.prop_direction)),
				getString(R.string.prop_direction_format));

		thrGetAirspeed.addProp(sharedPreferences.getString("nav1FreqKey",
				getString(R.string.prop_nav1Freq)),
				getString(R.string.prop_nav1Freq_format), true);
		thrGetAirspeed.addProp(sharedPreferences.getString("nav1RadKey",
				getString(R.string.prop_nav1Rad)),
				getString(R.string.prop_nav1Rad_format), true);
		thrGetAirspeed.addProp(sharedPreferences.getString("nav2FreqKey",
				getString(R.string.prop_nav2Freq)),
				getString(R.string.prop_nav2Freq_format), true);
		thrGetAirspeed.addProp(sharedPreferences.getString("nav2RadKey",
				getString(R.string.prop_nav2Rad)),
				getString(R.string.prop_nav2Rad_format), true);
		thrGetAirspeed.addProp(sharedPreferences.getString("adf1FreqKey",
				getString(R.string.prop_adf1Freq)),
				getString(R.string.prop_adf1Freq_format), true);
		thrGetAirspeed.addProp(sharedPreferences.getString("com1FreqKey",
				getString(R.string.prop_com1Freq)),
				getString(R.string.prop_com1Freq_format), true);

		thrGetAirspeed.setEnabled(sharedPreferences.getBoolean("enableGetting",
				true));

		// }

	}

	public String getNav1RadPropKey() {
		return nav1RadPropKey;
	}

	public void setNav1RadPropKey(String nav1RadPropKey) {
		this.nav1RadPropKey = nav1RadPropKey;
	}

	public String getNav2RadPropKey() {
		return nav2RadPropKey;
	}

	public void setNav2RadPropKey(String nav2RadPropKey) {
		this.nav2RadPropKey = nav2RadPropKey;
	}

	public String getBrakeLeftPropKey() {
		return brakeLeftPropKey;
	}

	public void setBrakeLeftPropKey(String brakeLeftPropKey) {
		this.brakeLeftPropKey = brakeLeftPropKey;
	}

	public String getBrakeRightPropKey() {
		return brakeRightPropKey;
	}

	public void setBrakeRightPropKey(String brakeRightPropKey) {
		this.brakeRightPropKey = brakeRightPropKey;
	}

	public String[] getReverserArr() {
		return reverserArr;
	}

	public void setReverserArr(String reverserArr[]) {
		this.reverserArr = reverserArr;
	}

	public SharedPreferences getConnectionPrefs() {
		return connectionPrefs;
	}

	public void setConnectionPrefs(SharedPreferences connectionPrefs) {
		this.connectionPrefs = connectionPrefs;
	}
}