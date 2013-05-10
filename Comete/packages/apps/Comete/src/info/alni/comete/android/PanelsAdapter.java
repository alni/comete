/**
 * Comete for Comete - Control Comete with an Android device
 * Copyright (C) 2013  Alexander Nilsen
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



import info.alni.comete.android.fragments.CockpitPanelFragment;
import info.alni.comete.android.fragments.MainControlsFragment;
import info.alni.comete.android.fragments.RadioPanelFragment;
import android.hardware.SensorManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View.OnTouchListener;

public class PanelsAdapter extends FragmentPagerAdapter {
	private static final int MAIN_CONTROLS_INDEX = 0;
	private static final int COCKPIT_PANEL_INDEX = 1;
	private static final int RADIO_PANEL_INDEX = 2;

	private static final String[] PAGE_TITLES = new String[] { "Main Controls",
			"Engine Panel", "Radio Panel" };

	public static final int SIZE = PAGE_TITLES.length;
	private Comete mAct;
	private CockpitPanelFragment mCockpitPanelFragment;
	private RadioPanelFragment mRadioPanelFragment;
	private MainControlsFragment mMainControlsFragment;

	public CockpitPanelFragment getCockpitPanelFragment() {
		return mCockpitPanelFragment;
	}

	public RadioPanelFragment getRadioPanelFragment() {
		return mRadioPanelFragment;
	}

	public MainControlsFragment getMainControlsFragment() {
		return mMainControlsFragment;
	}

	public PanelsAdapter(FragmentManager fm, Comete act) {
		super(fm);

		mAct = act;

		mMainControlsFragment = MainControlsFragment.newInstance(mAct);

		mCockpitPanelFragment = CockpitPanelFragment.newInstance();

		mRadioPanelFragment = RadioPanelFragment.newInstance(mAct);
	}

	@Override
	public Fragment getItem(int pos) {
		switch (pos) {
		case RADIO_PANEL_INDEX:
			return mRadioPanelFragment;
		case COCKPIT_PANEL_INDEX:
			return mCockpitPanelFragment;
		case MAIN_CONTROLS_INDEX:
		default:
			return mMainControlsFragment;
		}
		// TODO Auto-generated method stub
		// return null;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return SIZE;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return PAGE_TITLES[position];
		// return super.getPageTitle(position);
	}

}
