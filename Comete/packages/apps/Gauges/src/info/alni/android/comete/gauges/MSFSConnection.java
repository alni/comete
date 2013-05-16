// FGFSConnection.java - client library for the Comete flight simulator.
// Started June 2002 by David Megginson, david@megginson.com
// This library is in the Public Domain and comes with NO WARRANTY.

package info.alni.android.comete.gauges;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.Gson;

/**
 * A connection to a running instance of Comete.
 * 
 * <p>
 * This class currently uses the Comete telnet interface, though it may be
 * modified to use a different TCP/IP interface in the future. Client
 * applications can use this library to examine and modify internal Comete
 * properties.
 * </p>
 * 
 * <p>
 * To start Comete with the telnet server activated, use a command like this
 * (to listen on port 9000):
 * </p>
 * 
 * <blockquote>
 * 
 * <pre>
 * fgfs --telnet=9000
 * </pre>
 * 
 * </blockquote>
 * 
 * <p>
 * Then create a connection to Comete from your Java client application:
 * </p>
 * 
 * <blockquote>
 * 
 * <pre>
 * FGFSConnection fgfs = new FGFSConnection(&quot;localhost&quot;, 9000);
 * </pre>
 * 
 * </blockquote>
 * 
 * <p>
 * Now you can use the connection to get and set Comete properties:
 * </p>
 * 
 * <blockquote>
 * 
 * <pre>
 * double altitude = fgfs.getDouble(&quot;/position/altitude-ft&quot;);
 * fgfs.setDouble(&quot;/orientation/heading&quot;, 270.0);
 * </pre>
 * 
 * </blockquote>
 * 
 * <p>
 * All methods that communicate directly with Comete are synchronized, since
 * they must work over a single telnet connection.
 * </p>
 */
public class MSFSConnection {
	
	public static Gson gson = new Gson();
	

	private String host;
	private Controls controls = new Controls();
	private Controls lastSent = new Controls();
	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
	
	public Controls getControls() {
		return this.controls;
	}
	
	public void setControls(Controls controls) {
		this.controls = controls;
	}
	
	public Controls getLastSent() {
		return this.lastSent;
	}
	
	public void setLastSent(Controls lastSent) {
		this.lastSent = lastSent;
	}

	private int port;
	
	// //////////////////////////////////////////////////////////////////
	// Constructor.
	// //////////////////////////////////////////////////////////////////

	/**
	 * Constructor.
	 * 
	 * <p>
	 * Create a new connection to a running Comete program. The program must
	 * have been started with the --telnet=&lt;port&gt; command-line option.
	 * </p>
	 * 
	 * @param host
	 *            The host name or IP address to connect to.
	 * @param port
	 *            The port number where Comete is listening.
	 * @exception IOException
	 *                If it is not possible to connect to a Comete process.
	 */
	public MSFSConnection(String host, int port) throws IOException {
		setHost(host);
		setPort(port);
		socket = new Socket(host, port);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		// content = socket.getInputStream();
		out = new PrintWriter(socket.getOutputStream(), true);
		//out.println("data\r");
	}

	// //////////////////////////////////////////////////////////////////
	// Primitive getter and setter.
	// //////////////////////////////////////////////////////////////////

	/**
	 * Close the connection to Comete.
	 * 
	 * <p>
	 * The client application should always invoke this method when it has
	 * finished with a connection, to allow cleanup.
	 * </p>
	 * 
	 * @exception IOException
	 *                If there is an error closing the connection.
	 */
	public synchronized void close() throws IOException {
		out.println("quit\r");
		in.close();
		out.close();
		socket.close();
	}
	
	private long timeLastSent = -1;
	
	public synchronized Controls send(String data) throws IOException {
		if (timeLastSent == -1 || System.currentTimeMillis() > timeLastSent + 125) {
			out.println(data + '\r');
			out.flush();
			timeLastSent = System.currentTimeMillis();
			controls = gson.fromJson(in.readLine(), Controls.class);
		}
		return controls;
	}
	
	public synchronized Controls get() throws IOException {
		if (timeLastSent == -1 || System.currentTimeMillis() > timeLastSent + 125) {
			out.println("get\r");
			out.flush();
			timeLastSent = System.currentTimeMillis();
			controls = gson.fromJson(in.readLine(), Controls.class);
		}
		return controls;
	}
	
	public synchronized Controls get(String what) throws IOException {
		if (timeLastSent == -1 || System.currentTimeMillis() > timeLastSent + 125) {
			String command = "get";
			if (what != null && what.trim().length() > 0) {
				command += " " + what.trim();
			}
			out.println(command + '\r');
			out.flush();
			timeLastSent = System.currentTimeMillis();
			controls = gson.fromJson(in.readLine(), Controls.class);
		}
		return controls;
	}
	
	public synchronized Controls send(Controls data) throws IOException {
		if (timeLastSent == -1 || System.currentTimeMillis() > timeLastSent + 125) {
			out.println(gson.toJson(data, Controls.class) + '\r');
			out.flush();
			timeLastSent = System.currentTimeMillis();
			this.lastSent = data;
			//controls = gson.fromJson(in.readLine(), Controls.class);
		}
		return controls;
	}

	// //////////////////////////////////////////////////////////////////
	// Internal state.
	// //////////////////////////////////////////////////////////////////

	private Socket socket;
	private BufferedReader in;
	private InputStream content;
	private PrintWriter out;
	
	
	public static class Controls {
		private float lat, lon, alt, pch, bnk, hdg, tas, ias, vs, ele, ail, thr, ai_bnk, ai_pch;
		
		public Controls() {
			
		}
		
		public float getLatitude() {
			return this.lat;
		}
		
		public float getLon() {
			return this.lon;
		}
		
		public float getAlt() {
			return this.alt;
		}
		
		public float getPch() {
			return this.pch;
		}
		
		public float getBnk() {
			return this.bnk;
		}
		
		public float getHdg() {
			return this.hdg;
		}
		
		public float getTas() {
			return this.tas;
		}
		
		public float getIas() {
			return this.ias;
		}
		
		public float getVs() {
			return this.vs;
		}
		
		public float getEle() {
			return this.ele;
		}
		
		public float getAil() {
			return this.ail;
		}
		
		public float getThr() {
			return this.thr;
		}
		
		public float getAiPch() {
			return this.ai_pch;
		}
		
		public float getAiBnk() {
			return this.ai_bnk;
		}
		
		public void setLat(float lat) {
			this.lat = lat;
		}
		
		public void setLon(float lon) {
			this.lon = lon;
		}
		
		public void setAlt(float alt) {
			this.alt = alt;
		}
		
		public void setPch(float pch) {
			this.pch = pch;
		}
		
		public void setBnk(float bnk) {
			this.bnk = bnk;
		}
		
		public void setHdg(float hdg) {
			this.hdg = hdg;
		}
		
		public void setTas(float tas) {
			this.tas = tas;
		}
		
		public void setIas(float ias) {
			this.ias = ias;
		}
		
		public void setVs(float vs) {
			this.vs = vs;
		}
		
		public void setEle(float ele) {
			this.ele = ele;
		}
		
		public void setAil(float ail) {
			this.ail = ail;
		}
		
		public void setThr(float thr) {
			this.thr = thr;
		}
		
		public void setAiPch(float ai_pch) {
			this.ai_pch = ai_pch;
		}
		
		public void setAiBnk(float ai_bnk) {
			this.ai_bnk = ai_bnk;
		}
	}
}

// end of FGFSConnection.java
