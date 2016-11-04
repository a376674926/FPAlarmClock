package cn.stj.alarmclock.broadcastreceiver;

import java.util.Calendar;
import java.util.TimeZone;

import cn.stj.alarmclock.Constants;
import cn.stj.alarmclock.activity.AlarmComingActivity;
import cn.stj.alarmclock.util.DateStyle;
import cn.stj.alarmclock.util.DateTimeUtil;
import cn.stj.alarmclock.util.PreferencesUtils;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

public class BootReceiver extends BroadcastReceiver {
	
	private static final long DAY = 24*60*60*1000 ;

	@Override
	public void onReceive(Context context, Intent intent) {
		long selectedTime = PreferencesUtils.getLong(context, Constants.KEY_DATETIME) ;
		boolean isOpenAlarmClock = PreferencesUtils.getBoolean(context, Constants.KEY_ISOPEN_ALARMCLOCK);
		if(selectedTime != -1 && isOpenAlarmClock){
			AlarmManager alarmManager =  (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
	        Intent myIntent = new Intent(context,AlarmReceiver.class) ;
	        PendingIntent pi = PendingIntent.getBroadcast(context, 0, myIntent, 0) ;
	        alarmManager.cancel(pi) ;
	        alarmManager.set(AlarmManager.RTC_WAKEUP, selectedTime, pi) ;
		}
        
	}

}
