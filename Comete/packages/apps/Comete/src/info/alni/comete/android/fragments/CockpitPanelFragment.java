package info.alni.comete.android.fragments;

import java.util.HashMap;

import com.tokaracamara.android.verticalslidebar.VerticalSeekBar;

import info.alni.comete.android.FGToggleButton;
import info.alni.comete.android.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

public class CockpitPanelFragment extends Fragment {

	private VerticalSeekBar mMixture;
	private VerticalSeekBar mPropeller;
	private FGToggleButton mGear;
	private FGToggleButton mAutoCoordination;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		// return super.onCreateView(inflater, container,
		// savedInstanceState);

		View v = inflater.inflate(R.layout.cockpit_panel, null);
		if (v != null) {
			setMixture((VerticalSeekBar) v.findViewById(R.id.mixture));
			setPropeller((VerticalSeekBar) v.findViewById(R.id.propeller));
			setGear((FGToggleButton) v.findViewById(R.id.gear2));
			setAutoCoordination((FGToggleButton) v.findViewById(R.id.autoCoordination));
			getGear().setBoolean(false);
			getAutoCoordination().setBoolean(false);
		}
		return v;
	}

	public static CockpitPanelFragment newInstance() {
		return new CockpitPanelFragment();
	}

	public VerticalSeekBar getMixture() {
		return mMixture;
	}

	public void setMixture(VerticalSeekBar mMixture) {
		this.mMixture = mMixture;
	}

	public VerticalSeekBar getPropeller() {
		return mPropeller;
	}

	public void setPropeller(VerticalSeekBar mPropeller) {
		this.mPropeller = mPropeller;
	}

	public FGToggleButton getGear() {
		return mGear;
	}

	public void setGear(FGToggleButton mGear) {
		this.mGear = mGear;
	}

	public FGToggleButton getAutoCoordination() {
		return mAutoCoordination;
	}

	public void setAutoCoordination(FGToggleButton mAutoCoordination) {
		this.mAutoCoordination = mAutoCoordination;
	}
}