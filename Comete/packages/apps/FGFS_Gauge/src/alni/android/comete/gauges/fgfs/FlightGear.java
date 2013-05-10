package alni.android.comete.gauges.fgfs;

import static alni.android.common.CommonUtils.showMsg;

import java.io.IOException;
import java.util.Locale;

import alni.android.comete.gauges.fgfs.R;
import org.apache.commons.math.ArgumentOutsideDomainException;
import org.flightgear.fgfsclient.FGFSConnection;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.app.Dialog;
import org.holoeverywhere.app.ProgressDialog;
import org.holoeverywhere.widget.EditText;
import org.holoeverywhere.widget.LinearLayout;
import org.holoeverywhere.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import alni.android.common.StringUtils;
import alni.comete.android.widgets.*;
import alni.comete.android.widgets.gauges.ai.AttitudeIndicator;

public class FlightGear extends Activity {
	private static final int DIALOG_CONNECT_ID = 0;
	private static final int DIALOG_HELP_ID = 1;

	private long mStartTime = 0L;

	private TextView diag;
	private RotatingImageView ati_background, ati_roll, asi, hdg, alt_needle,
			alt_needle_1k, vsi;
	private AttitudeIndicator ati_pitch;

	private FGFSConnection fgfs;
	private SharedPreferences connectionPrefs;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		connectionPrefs = getSharedPreferences("connection", MODE_PRIVATE);

		setContentView(R.layout.main);
		diag = (TextView) findViewById(R.id.diag);
		ati_background = (RotatingImageView) findViewById(R.id.ati_background);
		ati_pitch = (AttitudeIndicator) findViewById(R.id.ati_pitch);
		ati_roll = (RotatingImageView) findViewById(R.id.ati_roll);
		asi = (RotatingImageView) findViewById(R.id.asi);
		hdg = (RotatingImageView) findViewById(R.id.hdg);

		alt_needle = (RotatingImageView) findViewById(R.id.alt_needle);
		alt_needle_1k = (RotatingImageView) findViewById(R.id.alt_needle_1k);

		vsi = (RotatingImageView) findViewById(R.id.vsi);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		// mGLView.onPause();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onStop()
	 */
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub

		super.onStop();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onStart()
	 */
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onPrepareOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.findItem(R.id.connect).setEnabled(fgfs == null);
		menu.findItem(R.id.disconnect).setEnabled(fgfs != null);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
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
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (id == R.id.help) {
			showDialog(DIALOG_HELP_ID);
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
			// return createHelpDialog();
		}

		// return super.onCreateDialog(id);
		return null;
	}

	private AlertDialog createConnectDialog() {
		final LinearLayout ll = (LinearLayout) getLayoutInflater().inflate(
				R.layout.connect_dialog, null);
		final EditText etIpAddress = (EditText) ll.findViewById(R.id.ipAddress);
		final EditText etPort = (EditText) ll.findViewById(R.id.port);
		etIpAddress.setText(connectionPrefs.getString("ipAddress", ""));
		etPort.setText(connectionPrefs.getString("port", ""));
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
								if (StringUtils.validateIPAddress(ipAddress)
										&& StringUtils.validatePort(port)) {
									Editor editor = connectionPrefs.edit();
									editor.putString("ipAddress", ipAddress);
									editor.putString("port", port);
									editor.commit();
									try {
										mConnectingDialog = ProgressDialog
												.show(FlightGear.this,
														"",
														"Connecting. Please wait...",
														true);

										Thread t = new Thread(new ConnectTask(
												ipAddress, Integer
														.parseInt(port)));
										t.start();
									} catch (NumberFormatException e) {
										showMsg(FlightGear.this, e.toString());
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

			mConnectingDialog.dismiss();
			if (msg != null && msg.obj != null) {
				showMsg(FlightGear.this, (String) msg.obj);
				return;
			}
			if (fgfs == null)
				return;
			try {
				if (mStartTime == 0L) {
					mStartTime = System.currentTimeMillis();
					mUpdateHandler.removeCallbacks(mUpdateTimerTask);
					mUpdateHandler.postDelayed(mUpdateTimerTask, 100);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	};
	private ProgressDialog mConnectingDialog;

	private class ConnectTask implements Runnable {
		private String ip;
		private int port;

		public ConnectTask(String ip, int port) {
			this.ip = ip;
			this.port = port;
		}

		@Override
		public void run() {
			try {

				fgfs = new FGFSConnection(ip, port);
			} catch (IOException e) {
				Bundle extras = new Bundle();
				Message message = new Message();
				message.obj = e.toString();
				mConnectHandler.sendMessage(message);
				e.printStackTrace();
			} finally {
				mConnectHandler.sendEmptyMessage(0);
			}

		}

	}

	private Handler mUpdateHandler = new Handler();

	class UpdateThread extends Thread {
		public UpdateThread() {
			super(new Runnable() {

				@Override
				public void run() {
					if (fgfs == null)
						return;
					setAti();
					setAsi();
					setHdg();
					setAlt();
					setVsi();
				}

			});
		}
	}

	UpdateThread mUpdateThread = null;

	private void setAti() {
		if (fgfs == null)
			return;
		try {
			Common.Values.pitch = fgfs.getFloat(Common.Properties.AI_PITCH);
			Common.Values.roll = fgfs.getFloat(Common.Properties.AI_ROLL);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException e) {

		} finally {
			FlightGear.this.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					ati_background.setAngle(-Common.Values.roll);
					ati_background.invalidate();

					ati_pitch.setAngle(-Common.Values.roll);
					ati_pitch.setTy(Common.Values.pitch * (4.5f / 2f));
					ati_pitch.invalidate();
					ati_roll.setAngle(-Common.Values.roll);
					ati_roll.invalidate();
				}

			});
		}
	}

	private void setAsi() {
		if (fgfs == null)
			return;
		try {
			Common.Values.ias = fgfs.getDouble(Common.Properties.ASI_IAS_KT);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			FlightGear.this.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					try {
						Common.Values.ASI.setAngle();
					} catch (ArgumentOutsideDomainException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} finally {
						asi.setAngle((float) Common.Values.ASI.angle);
						asi.invalidate();
					}
				}

			});
		}
	}

	private void setHdg() {
		if (fgfs == null)
			return;
		try {
			Common.Values.heading = fgfs.getFloat(Common.Properties.HDG_DEG);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			FlightGear.this.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					hdg.setAngle(-Common.Values.heading);
					hdg.invalidate();
				}

			});
		}
	}

	private void setAlt() {
		if (fgfs == null)
			return;
		try {
			Common.Values.alt = fgfs.getFloat(Common.Properties.ALT_FT);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			FlightGear.this.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					alt_needle.setAngle(Common.Values.alt * 0.36f);
					alt_needle.invalidate();

					alt_needle_1k.setAngle(Common.Values.alt * 0.036f);
					alt_needle_1k.invalidate();
				}

			});
		}
	}

	private void setVsi() {
		if (fgfs == null)
			return;
		try {
			Common.Values.vs = fgfs.getDouble(Common.Properties.VSI_VS_FPM);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			FlightGear.this.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					try {
						Common.Values.VSI.setAngle();
					} catch (ArgumentOutsideDomainException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} finally {
						vsi.setAngle((float) Common.Values.VSI.angle);
						vsi.invalidate();
					}
				}

			});
		}
	}

	private Runnable mUpdateTimerTask = new Runnable() {

		@Override
		public void run() {

			if (fgfs != null) {
				if (mUpdateThread != null && mUpdateThread.isAlive()) {
					mUpdateThread.interrupt();
				}
				if (mUpdateThread == null || !mUpdateThread.isInterrupted()) {
					mUpdateThread = new UpdateThread();
					mUpdateThread.start();
					diag.setText(String.format(Locale.ENGLISH, "[ias: %1$.0f]"
							+ "[[pitch: %2$+.2f][roll: %3$+.2f]]"
							+ "[alt: %4$.0f]" + "[heading: %5$.0f]"
							+ "[vs: %6$+.0f]", Common.Values.ias,
							Common.Values.pitch, Common.Values.roll,
							Common.Values.alt, Common.Values.heading,
							Common.Values.vs));

				}
				mUpdateHandler.postDelayed(this, 16L);
			}

		}

	};
}