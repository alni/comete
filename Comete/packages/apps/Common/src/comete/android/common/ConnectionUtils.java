package comete.android.common;

import static alni.android.common.CommonUtils.showMsg;

import java.io.IOException;

import org.flightgear.fgfsclient.FGFSConnection;

import alni.android.common.StringUtils;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.Message;

public class ConnectionUtils {
    FGFSConnection fgfs;
    Context context;
    
    public ConnectionUtils(Context context, FGFSConnection fgfs) {
	this.context = context;
	this.fgfs = fgfs;
    }
    
    public void connect(String ip, String port, SharedPreferences connectionPrefs) {
	if (StringUtils.validateIPAddress(ip)
		&& StringUtils.validatePort(port)) {
	    Editor editor = connectionPrefs.edit();
	    editor.putString("ipAddress", ip);
	    editor.putString("port", port);
	    editor.commit();
	    try {
		mConnectingDialog = ProgressDialog
			.show(context,
				"",
				"Connecting. Please wait...",
				true);

		Thread t = new Thread(new ConnectTask(
			ip, Integer
				.parseInt(port)));
		t.start();
	    } catch (NumberFormatException e) {
		showMsg(context, e.toString());
		e.printStackTrace();
	    }
	}
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
		showMsg(context, (String) msg.obj);
		return;
	    }
	    if (fgfs == null)
		return;
	    try {
		fgfs.setBoolean("/sim/auto-coordination", true);
	    } catch (IOException e) {
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
		//Bundle extras = new Bundle();
		Message message = new Message();
		message.obj = e.toString();
		// CommonUtils.showMsg(FlightGear.this, e.toString());
		mConnectHandler.sendMessage(message);
		e.printStackTrace();
	    } finally {
		mConnectHandler.sendEmptyMessage(0);
	    }

	}

    }
}
