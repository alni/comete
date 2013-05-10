package info.alni.comete.android;

import static info.alni.comete.android.Common.fgfs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import org.flightgear.fgfsclient.FGFSConnection;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;


public class PropertyGetterThread extends Thread {

	private class PP {
		public String prop;
		public String format;
		public boolean oneTime=false;		
		public boolean done=false;
		
	}
	
	private ArrayList<PP> props=new ArrayList<PP>();
	
	FGFSConnection myFGFSConn;
	

	private long sleepTime=1000;
	
	private float value=0;
	
	private Handler handler;

	private boolean enabled=true;
	
	public ArrayList<PP> getProp() {
		return props;
	}

	public void setProp(ArrayList<PP> props) {
		this.props = props;
	}
	public void addProp(String prop,String format,boolean oneTime) {
		PP pp=new PP();
		pp.prop=prop;
		pp.format=format;
		pp.oneTime=oneTime;
		
		props.add(pp);
	}
	public void addProp(String prop,String format) {
		addProp(prop, format,false);
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}


	
	
	public PropertyGetterThread(Handler handler) {
	    this.handler=handler;
	}

	 private void notifyMessage(String propName,String str) {
		 if (handler!=null) {
		    Message msg = handler.obtainMessage();
		    Bundle b = new Bundle();
		    b.putString(propName, ""+str);
		    msg.setData(b);
		    handler.sendMessage(msg);
		  }
	 }
	 
	 int propIdx=0;
	
	@Override
	public void run() {
		PP pInUse=null;
		while (true) {
			if (Common.msfs != null && isEnabled()) {
				notifyMessage("", "");
			} else {
				if (fgfs != null && isEnabled()) {    		
			    	try {
	
			    		if (myFGFSConn==null) {
			    			myFGFSConn=new FGFSConnection(fgfs.getHost(), fgfs.getPort());
			    		}
			    		
			    		if (propIdx<props.size()) {
				    		pInUse=props.get(propIdx);
			    			
			    			value=myFGFSConn.getFloat(pInUse.prop);
		
			    			notifyMessage(
			    					pInUse.prop,
		    					String.format(
									Locale.ENGLISH,
									pInUse.format,
									value
								)
							);	
			    			
			    			pInUse.done=true;
			    			
			    			propIdx++;
				    		if (propIdx>=props.size()) {
				    			propIdx=0;		    			
				    		}		    				
			    			while (props.get(propIdx).oneTime && props.get(propIdx).done) {
			    				propIdx++;
					    		if (propIdx>=props.size()) {
					    			propIdx=0;		    			
					    		}		    				
			    			}
			    			
			    		}
				    } catch (IOException e) {
				    	e.printStackTrace();
				    }
				} else {
					if (fgfs==null) {
						if (myFGFSConn!=null) {
							try {
								myFGFSConn.close();
						    } catch (IOException e) {
						    	e.printStackTrace();
						    }
							myFGFSConn=null;
						}
					}
					
				}
			}
			
			
			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	    

	}
}
