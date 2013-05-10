package info.alni.comete.android.fragments;

import com.tokaracamara.android.verticalslidebar.VerticalSeekBar;

import info.alni.comete.android.R;
import info.alni.comete.android.R.layout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class CockpitPanelFragment extends Fragment {

	private VerticalSeekBar mMixture;
	private VerticalSeekBar mPropeller;

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
}