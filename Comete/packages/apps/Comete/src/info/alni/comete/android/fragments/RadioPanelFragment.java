package info.alni.comete.android.fragments;

import static alni.android.common.CommonUtils.showMsg;
import static info.alni.comete.android.Common.fgfs;

import java.io.IOException;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.widget.EditText;
import org.holoeverywhere.widget.SeekBar;
import org.holoeverywhere.widget.SeekBar.OnSeekBarChangeListener;

import info.alni.comete.android.Comete;
import info.alni.comete.android.Common;
import info.alni.comete.android.MSFSRequestTask;
import info.alni.comete.android.R;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

public class RadioPanelFragment extends Fragment implements
		OnSeekBarChangeListener {

	private EditText nav1Text;
	private SeekBar nav1RadText;
	private EditText nav2Text;
	private SeekBar nav2RadText;
	private EditText adf1Text;
	private EditText com1Text;
	private Comete mAct;

	public RadioPanelFragment() {

	}
	
	public void setNav1Freq(View view) {
		if (fgfs != null) {
			double v = Double.parseDouble(
					getNav1Text().getText().toString());
			try {
				fgfs.setDouble(mAct.nav1FreqPropKey, v);
			} catch (IOException e) {
				showMsg(mAct, e.toString());
			}
		} else if (Common.msfs != null) {
			String str = 
					getNav1Text().getText().toString();
			new MSFSRequestTask().execute("set NAV1 " + str);
		}
	}

	public void setNav2Freq(View view) {
		if (fgfs != null) {
			double v = Double.parseDouble(
					getNav2Text().getText().toString());
			try {
				fgfs.setDouble(mAct.nav2FreqPropKey, v);
			} catch (IOException e) {
				showMsg(mAct, e.toString());
			}
		} else if (Common.msfs != null) {
			String str = 
					getNav2Text().getText().toString();
			new MSFSRequestTask().execute("set NAV2 " + str);
		}
	}

	public void setAdf1Freq(View view) {
		if (fgfs != null) {
			double v = Double.parseDouble(
					getAdf1Text().getText().toString());
			try {
				fgfs.setDouble(mAct.adf1FreqPropKey, v);
			} catch (IOException e) {
				showMsg(mAct, e.toString());
			}
		} else if (Common.msfs != null) {
			String str = 
					getAdf1Text().getText().toString();
			new MSFSRequestTask().execute("set ADF1 " + str);
		}
	}

	public void setCom1Freq(View view) {
		if (fgfs != null) {
			double v = Double.parseDouble(
					getCom1Text().getText().toString());
			try {
				fgfs.setDouble(mAct.com1FreqPropKey, v);
			} catch (IOException e) {
				showMsg(mAct, e.toString());
			}
		} else if (Common.msfs != null) {
			String str = 
					getCom1Text().getText().toString();
			new MSFSRequestTask().execute("set COM1 " + str);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		// return super.onCreateView(inflater, container,
		// savedInstanceState);

		View v = inflater.inflate(R.layout.nav_panel, null);
		if (v != null) {
			setNav1Text((EditText) v.findViewById(R.id.nav1Freq));
			setNav1RadText((SeekBar) v.findViewById(R.id.nav1Rad));

			setNav2Text((EditText) v.findViewById(R.id.nav2Freq));
			setNav2RadText((SeekBar) v.findViewById(R.id.nav2Rad));

			setAdf1Text((EditText) v.findViewById(R.id.adf1Freq));

			setCom1Text((EditText) v.findViewById(R.id.com1Freq));

			getNav1RadText().setOnSeekBarChangeListener(this);
			getNav2RadText().setOnSeekBarChangeListener(this);
		}
		return v;
	}

	public static RadioPanelFragment newInstance(Comete act) {
		RadioPanelFragment frg = new RadioPanelFragment();
		frg.mAct = act;
		return frg;
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		if (fromUser && fgfs != null) {
			try {
				if (seekBar == getNav1RadText()) {
					fgfs.setDouble(mAct.getNav1RadPropKey(), progress);
				}
				if (seekBar == getNav2RadText()) {
					fgfs.setDouble(mAct.getNav2RadPropKey(), progress);
				}
			} catch (IOException e) {
				showMsg(mAct, e.toString());
			}
		}

	}

	@Override
	public void onStartTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStopTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub

	}

	public EditText getNav1Text() {
		return nav1Text;
	}

	public void setNav1Text(EditText nav1Text) {
		this.nav1Text = nav1Text;
	}

	public EditText getNav2Text() {
		return nav2Text;
	}

	public void setNav2Text(EditText nav2Text) {
		this.nav2Text = nav2Text;
	}

	public EditText getAdf1Text() {
		return adf1Text;
	}

	public void setAdf1Text(EditText adf1Text) {
		this.adf1Text = adf1Text;
	}

	public EditText getCom1Text() {
		return com1Text;
	}

	public void setCom1Text(EditText com1Text) {
		this.com1Text = com1Text;
	}

	public SeekBar getNav1RadText() {
		return nav1RadText;
	}

	public void setNav1RadText(SeekBar nav1RadText) {
		this.nav1RadText = nav1RadText;
	}

	public SeekBar getNav2RadText() {
		return nav2RadText;
	}

	public void setNav2RadText(SeekBar nav2RadText) {
		this.nav2RadText = nav2RadText;
	}
}