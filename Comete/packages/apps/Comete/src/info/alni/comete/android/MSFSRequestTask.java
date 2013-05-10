package info.alni.comete.android;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;

public class MSFSRequestTask extends AsyncTask<String, String, MSFSConnection.Controls>{

    @Override
    protected MSFSConnection.Controls doInBackground(String... data/*uri*/) {
    	try {
    		if (Common.msfs != null) {
    			//System.out.println("DATA="+data[0]);
    			return Common.msfs.send(data[0]);
    		}
    	} catch (IOException ex) {
    		ex.printStackTrace();
    	}
    	return null;
//        HttpClient httpclient = new DefaultHttpClient();
//        HttpResponse response;
//        String responseString = null;
//        try {
//            response = httpclient.execute(new HttpGet(uri[0]));
//            StatusLine statusLine = response.getStatusLine();
//            if(statusLine.getStatusCode() == HttpStatus.SC_OK){
//                ByteArrayOutputStream out = new ByteArrayOutputStream();
//                response.getEntity().writeTo(out);
//                out.close();
//                responseString = out.toString();
//            } else{
//                //Closes the connection.
//                response.getEntity().getContent().close();
//                throw new IOException(statusLine.getReasonPhrase());
//            }
//        } catch (ClientProtocolException e) {
//            //TODO Handle problems..
//        } catch (IOException e) {
//            //TODO Handle problems..
//        }
//        return responseString;
    }

    @Override
    protected void onPostExecute(MSFSConnection.Controls result) {
        super.onPostExecute(result);
        //Do anything with response..
    }
}
