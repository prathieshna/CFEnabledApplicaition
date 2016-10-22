package com.iit.prathieshna.cfenabledapplication;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.view.View;
import com.github.lzyzsd.circleprogress.ArcProgress;
import com.github.lzyzsd.circleprogress.CircleProgress;
import com.iit.prathieshna.cyberforagingframework.internal.StopWatch;

public class Test extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
    }

    public void localExecution (View v)
    {

        ArcProgress localTime = (ArcProgress) findViewById(R.id.arc_progress_local_time);
        localTime.setProgress(0);
        final StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        System.out.println(Util.localIntensiveTask());
        stopWatch.stop();
        localTime.setProgress((int) stopWatch.getTotalTimeMillis());
        double volt = getVoltage()/1000.0;
        double mAh = getBatteryCapacity();
        double Wh = mAh * (volt/1000);

        double Joules = (Wh/3600)*(stopWatch.getTotalTimeMillis()/1000.0);
        CircleProgress local = (CircleProgress) findViewById(R.id.circle_progress2);
        local.setProgress((int)(Joules*1000));
    }

    public void remoteExecution (View v)
    {
        ArcProgress remoteTime = (ArcProgress) findViewById(R.id.arc_progress_remote_Time);
        remoteTime.setProgress(0);
        final StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        System.out.println(Util.remoteIntensiveTask());
        stopWatch.stop();
        remoteTime.setProgress((int) stopWatch.getTotalTimeMillis());
        double volt = getVoltage()/1000.0;
        double mAh = getBatteryCapacity();
        double Wh = mAh * (volt/1000.0);
        double Joules = (Wh/3600)*(stopWatch.getTotalTimeMillis()/1000.0);
        CircleProgress remote = (CircleProgress) findViewById(R.id.circle_progress);
        remote.setProgress((int)(Joules*1000));
    }

    public int getVoltage()
    {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent b = this.registerReceiver(null, ifilter);
        return b != null ? b.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1) : 0;
    }


    public double getBatteryCapacity() {
        Object mPowerProfile_;
        final String POWER_PROFILE_CLASS = "com.android.internal.os.PowerProfile";
        try {
            mPowerProfile_ = Class.forName(POWER_PROFILE_CLASS)
                    .getConstructor(Context.class).newInstance(this);
            return (double) Class
                    .forName(POWER_PROFILE_CLASS)
                    .getMethod("getAveragePower", java.lang.String.class)
                    .invoke(mPowerProfile_, "battery.capacity");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0.0;
    }
}
