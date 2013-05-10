// FGFSConnection.java - client library for the Comete flight simulator.
// Started June 2002 by David Megginson, david@megginson.com
// This library is in the Public Domain and comes with NO WARRANTY.

package info.alni.comete.android;

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

	private String host;
	private Controls controls = new Controls();
	private Gson gson = new Gson();
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

	// //////////////////////////////////////////////////////////////////
	// Internal state.
	// //////////////////////////////////////////////////////////////////

	private Socket socket;
	private BufferedReader in;
	private InputStream content;
	private PrintWriter out;
	
	
	public static class Controls {
		private float lat, lon, alt, pch, bnk, hdg;
		
		public Controls() {
			
		}
		
		public float getLat() {
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
	}
}

// end of FGFSConnection.java
