package comete.android.common;

public class MSFSControls {
	private float lat, lon, alt, pch, bnk, hdg, tas, ias, vs, ele, ail, thr, ai_bnk, ai_pch;
	
	private String com1, nav1, nav2, adf1;
	
	public MSFSControls() {
		
	}
	
	public float getLatitude() {
		return this.lat;
	}
	
	public float getLongitude() {
		return this.lon;
	}
	
	public float getAltitude() {
		return this.alt;
	}
	
	public float getPitch() {
		return this.pch;
	}
	
	public float getBank() {
		return this.bnk;
	}
	
	public float getHeading() {
		return this.hdg;
	}
	
	public float getTAS() {
		return this.tas;
	}
	
	public float getIAS() {
		return this.ias;
	}
	
	public float getVS() {
		return this.vs;
	}
	
	public float getElevator() {
		return this.ele;
	}
	
	public float getAileron() {
		return this.ail;
	}
	
	public float getThrottle() {
		return this.thr;
	}
	
	public float getAttitudePitch() {
		return this.ai_pch;
	}
	
	public float getAttitudeBank() {
		return this.ai_bnk;
	}
	
	public String getCOM1() {
		return com1;
	}
	
	public String getNAV1() {
		return nav1;
	}
	
	public String getNAV2() {
		return nav2;
	}
	
	public String getADF1() {
		return adf1;
	}
	
	public void setLatitude(float lat) {
		this.lat = lat;
	}
	
	public void setLongitude(float lon) {
		this.lon = lon;
	}
	
	public void setAltitude(float alt) {
		this.alt = alt;
	}
	
	public void setPitch(float pch) {
		this.pch = pch;
	}
	
	public void setBank(float bnk) {
		this.bnk = bnk;
	}
	
	public void setHeading(float hdg) {
		this.hdg = hdg;
	}
	
	public void setTAS(float tas) {
		this.tas = tas;
	}
	
	public void setIAS(float ias) {
		this.ias = ias;
	}
	
	public void setVS(float vs) {
		this.vs = vs;
	}
	
	public void setElevator(float ele) {
		this.ele = ele;
	}
	
	public void setAileron(float ail) {
		this.ail = ail;
	}
	
	public void setThrottle(float thr) {
		this.thr = thr;
	}
	
	public void setAttitudePitch(float ai_pch) {
		this.ai_pch = ai_pch;
	}
	
	public void setAttitudeBank(float ai_bnk) {
		this.ai_bnk = ai_bnk;
	}
	
	public void setCOM1(String com1) {
		this.com1 = com1;
	}
	
	public void setNAV1(String nav1) {
		this.nav1 = nav1;
	}
	
	public void setNAV2(String nav2) {
		this.nav2 = nav2;
	}
	
	public void setADF1(String adf1) {
		this.adf1 = adf1;
	}
}
