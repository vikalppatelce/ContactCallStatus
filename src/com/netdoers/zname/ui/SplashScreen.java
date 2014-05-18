package com.netdoers.zname.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

import com.netdoers.zname.R;

public class SplashScreen extends Activity
{
  private final int SPLASH_DISPLAY_LENGTH = 2500;
  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    setContentView(R.layout.activity_splashscreen);
   
    new Thread()
    {
      @Override
	public void run()
      {
        try
        {
          sleep(SPLASH_DISPLAY_LENGTH);
          Intent localIntent = new Intent(SplashScreen.this, MotherActivity.class);
          SplashScreen.this.startActivity(localIntent);
          SplashScreen.this.finish();
          return;
        }
        catch (Exception localException)
        {
        }
      }
    }
    .start();
  }
}
