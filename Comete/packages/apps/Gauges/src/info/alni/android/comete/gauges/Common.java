package info.alni.android.comete.gauges;

import org.apache.commons.math.ArgumentOutsideDomainException;
import org.apache.commons.math.MathException;
import org.apache.commons.math.analysis.interpolation.LoessInterpolator;
import org.apache.commons.math.analysis.polynomials.PolynomialSplineFunction;

import alni.android.common.MathUtils;

public class Common {
    public static class Properties {
		public static final String BASE = "/instrumentation";
	
		public static final String AI_BASE = BASE + "/attitude-indicator";
		public static final String AI_PITCH = AI_BASE + "/indicated-pitch-deg";
		public static final String AI_ROLL = AI_BASE + "/indicated-roll-deg";
	
		public static final String ASI_BASE = BASE + "/airspeed-indicator";
		public static final String ASI_IAS_KT = ASI_BASE
			+ "/indicated-speed-kt";
	
		public static final String HDG_BASE = BASE + "/heading-indicator";
		public static final String HDG_DEG = HDG_BASE
			+ "/indicated-heading-deg";
	
		public static final String ALT_BASE = BASE + "/altimeter";
		public static final String ALT_FT = ALT_BASE + "/indicated-altitude-ft";
	
		public static final String VSI_BASE = BASE
			+ "/vertical-speed-indicator";
		public static final String VSI_VS_FPM = VSI_BASE
			+ "/indicated-speed-fpm";
    }

    public static class Values {
		public static float pitch = 0f, roll = 0f, alt = 0f, heading = 0f;
		public static double ias = 0d, vs = 0d;
		
		private static double limit(double val, double[] ind) {
			return MathUtils.valToMinMax(val, ind[0], ind[ind.length-1]);
		}
	
		public static class ASI {
		    public static final double[] INTERPOLATION_IND = { 0, 10, 40, 60,
			    80, 100, 120, 140, 160, 180, 200, 210 };
		    public static final double[] INTERPOLATION_DEP = { 0.0, 3.0, 31.1,
			    71.7, 118.0, 165.3, 206.6, 238.3, 267.7, 293.1, 319.2,
			    331.6 };
	
		    public static PolynomialSplineFunction interpolation;
		    public static double angle = 0d;
	
		    public static void setAngle() throws ArgumentOutsideDomainException {
		    	angle = interpolation.value(limit(ias, INTERPOLATION_IND));
		    }
	
		    private static void init() {
				try {
				    interpolation = new LoessInterpolator().interpolate(
					    INTERPOLATION_IND, INTERPOLATION_DEP);
				} catch (MathException e) {
				    // TODO Auto-generated catch block
				    e.printStackTrace();
				}
		    }
	
		    static {
			init();
		    }
		}
	
		public static class VSI {
		    public static final double[] INTERPOLATION_IND = { -2000.0,
			    -1500.0, -1000.0, -500.0, 0.0, 500.0, 1000.0, 1500.0,
			    2000.0 };
		    public static final double[] INTERPOLATION_DEP = { -173.5, -131.5,
			    -81.5, -35.3, 0.0, 35.7, 81.5, 131, 172.9 };
	
		    public static PolynomialSplineFunction interpolation;
	
		    public static double angle = 0d;
	
		    public static void setAngle() throws ArgumentOutsideDomainException {
		    	
		    	angle = interpolation.value(limit(vs, INTERPOLATION_IND));
		    }
	
		    private static void init() {
				try {
				    interpolation = new LoessInterpolator().interpolate(
					    INTERPOLATION_IND, INTERPOLATION_DEP);
				} catch (MathException e) {
				    // TODO Auto-generated catch block
				    e.printStackTrace();
				}
		    }
	
		    static {
		    	init();
		    }
		}
    }
}
