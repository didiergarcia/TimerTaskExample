package com.softwaresushi.timertask;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity
{
  public static final String TIME_LEFT = "time-left";
  TextView         mTextView;
  PowerUpTimerTask mPowerUpTimerTask;
  private static final String TAG = "powerup/";

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    // Default to 10 seconds.
    long powerUpTime = 10;

    // Did the screen rotate? get the time left.
    if (savedInstanceState != null)
    {

      powerUpTime = savedInstanceState.getLong(TIME_LEFT, powerUpTime);
      Log.d(TAG, "Restoring time left: " + powerUpTime);
    }

    mTextView = findViewById(R.id.timerTextView);

    // Set starting value.
    if (powerUpTime > 0)
    {
      mTextView.setText(String.valueOf(powerUpTime));
    }

    // Will count down from 10 by 1s.
    mPowerUpTimerTask = new PowerUpTimerTask(this, powerUpTime, 1);

    // Schedule for every second.
    Timer t = new Timer();
    t.scheduleAtFixedRate(mPowerUpTimerTask, 1000, 1000);
  }

  @Override
  protected void onSaveInstanceState(Bundle outState)
  {
    super.onSaveInstanceState(outState);

    long timeLeft = mPowerUpTimerTask.mPeriod;
    Log.d(TAG, "Saving time left: " + timeLeft);
    outState.putLong(TIME_LEFT, timeLeft);

    // Cancel this timer task.
    mPowerUpTimerTask.cancel();

  }

  // This TimerTask Updates the UI so it needs a reference to the Activity.
  static class PowerUpTimerTask extends TimerTask
  {

    WeakReference<AppCompatActivity> mActivity;
    long                             mInterval;
    long                             mPeriod;

    public PowerUpTimerTask(AppCompatActivity activity, long period, long interval)
    {
      mActivity = new WeakReference<>(activity);
      mInterval = interval;
      mPeriod = period;
    }

    @Override
    public void run()
    {
      AppCompatActivity activity = mActivity.get();
      if (activity != null)
      {
        final TextView timerTextView = activity.findViewById(R.id.timerTextView);

        mPeriod -= mInterval;

        if (mPeriod > 0)
        {
          // Set time remaining
          activity.runOnUiThread(new Runnable()
          {
            @Override
            public void run()
            {
              timerTextView.setText(String.valueOf(mPeriod));
            }
          });

        }
        else
        {
          // Out of time...clear the Text and stop the timer task.
          activity.runOnUiThread(new Runnable()
          {
            @Override
            public void run()
            {
              timerTextView.setText("");
            }
          });

          Log.d(TAG, "Timer done. Canceling.");
          cancel();
        }
      }
      else
      {
        // Cancel this timer task since we don't have a reference to
        // the Activity any more.
        Log.d(TAG, "Lost reference to Activity. Canceling.");
        cancel();
      }
    }
  }
}
