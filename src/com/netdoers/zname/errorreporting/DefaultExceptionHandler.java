/*
Copyright (c) 2009 nullwire aps

Permission is hereby granted, free of charge, to any person
obtaining a copy of this software and associated documentation
files (the "Software"), to deal in the Software without
restriction, including without limitation the rights to use,
copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the
Software is furnished to do so, subject to the following
conditions:

The above copyright notice and this permission notice shall be
included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
OTHER DEALINGS IN THE SOFTWARE.

Contributors: 
Mads Kristiansen, mads.kristiansen@nullwire.com
Glen Humphrey
Evan Charlton
Peter Hewitt
*/

package com.netdoers.zname.errorreporting;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

import android.os.AsyncTask;
import android.util.Log;

import com.netdoers.zname.AppConstants;
import com.netdoers.zname.utils.Mail;

public class DefaultExceptionHandler implements UncaughtExceptionHandler {

	private UncaughtExceptionHandler defaultExceptionHandler;
	String stackTracePath=null;
	String time=null;
	
	private static final String TAG = "UNHANDLED_EXCEPTION";

	// constructor
	public DefaultExceptionHandler(UncaughtExceptionHandler pDefaultExceptionHandler)
	{
		defaultExceptionHandler = pDefaultExceptionHandler;
	}
	 
	// Default exception handler
	@Override
	public void uncaughtException(Thread t, Throwable e) {
		// Here you should have a more robust, permanent record of problems
	    final Writer result = new StringWriter();
	    final PrintWriter printWriter = new PrintWriter(result);
	    e.printStackTrace(printWriter);
	    try {
	    	// Random number to avoid duplicate files
	    	Random generator = new Random();
	    	int random = generator.nextInt(99999);
	    	Calendar c = Calendar.getInstance();
	    	SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
	    	time = df.format(c.getTime());
	    	// Embed version in stacktrace filename
	    	String filename = G.APP_VERSION+"-"+Integer.toString(random);
	    	Log.d(TAG, "Writing unhandled exception to: " + G.FILES_PATH+"/"+filename+".txt");
		    // Write the stacktrace to disk
	    	BufferedWriter bos = new BufferedWriter(new FileWriter(G.FILES_PATH+"/"+filename+".txt"));
            bos.write("Android OS Version:"+G.ANDROID_VERSION + "\n");
            bos.write("Phone Model: "+G.PHONE_MODEL + "\n");
            bos.write("Phone Size: "+G.PHONE_SIZE + "\n");//ADDED DEVICE SIZE
            bos.write("Version    : "+G.APP_VERSION + "\n");//SA VERSION CODE
            bos.write("Version Code: "+G.APP_VERSION_CODE + "\n");
            bos.write("Package    : "+G.APP_PACKAGE + "\n");//EA VERSION CODE
            bos.write("Stack Trace as follows at: "+ time+"\n");
            bos.write("\n");
            bos.write(result.toString());
		    bos.flush();
			try {
				File imageDirectory =new File(AppConstants.IMAGE_DIRECTORY_PATH);
				imageDirectory.mkdirs();
				stackTracePath = AppConstants.IMAGE_DIRECTORY_PATH + "/"+ filename + ".txt";
				copy(new File(G.FILES_PATH + "/" + filename + ".txt"),new File(AppConstants.IMAGE_DIRECTORY_PATH + "/"+ filename + ".txt"));
			} catch (Exception ex) {
				Log.e("Copy Stack Trace", ex.toString());
			}

			try {
				new MailTask().execute();
			} catch (Exception exe) {
				Log.e("Sending Stack Trace", exe.toString());
			}
		    // Close up everything
		    bos.close();
	    } catch (Exception ebos) {
	    	// Nothing much we can do about this - the game is over
	    	ebos.printStackTrace();
	    }
		Log.d(TAG, result.toString());	    
		//call original handler  
    	defaultExceptionHandler.uncaughtException(t, e);        
	}
	
	public void copy(File src, File dst) throws IOException {
	    InputStream in = new FileInputStream(src);
	    OutputStream out = new FileOutputStream(dst);

	    // Transfer bytes from in to out
	    byte[] buf = new byte[1024];
	    int len;
	    while ((len = in.read(buf)) > 0) {
	        out.write(buf, 0, len);
	    }
	    in.close();
	    out.close();
	}
	
	public class MailTask extends AsyncTask<String,Void,String>{

		@Override
		protected String doInBackground(String... params) {
			/*
			 * MAIL SENDING
			 */
			 Mail m = new Mail("androidbugtrace@gmail.com", "android1"); 
		      String[] toArr = {"androidbugnetdoers@gmail.com", "androidbugtrace@gmail.com"}; 
		      m.setTo(toArr);
		      m.setFrom("androidbugnetdoers@gmail.com"); 
		      m.setSubject("Zname | Crash Report | "+time);
		      m.setBody("Bug Trace at: "+time); 
		      try { 
		    	  if(stackTracePath!=null && stackTracePath.length() > 0)
		        m.addAttachment(stackTracePath); 
		        if(m.send()) { 
		        	Log.i("MailApp", "Mail sent successfully!"); 
		        } else { 
		        	Log.e("MailApp", "Could not send email"); 
		        } 
		      } catch(Exception e) { 
		        //Toast.makeText(MailApp.this, "There was a problem sending the email.", Toast.LENGTH_LONG).show(); 
		        Log.e("MailApp", "Could not send email", e); 
		      } 
			return "MailSent";
		}
		@Override
		protected void onPostExecute(String result) {
        }
        @Override
		protected void onPreExecute() {}
	}
}