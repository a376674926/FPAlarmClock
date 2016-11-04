package cn.stj.alarmclock.broadcastreceiver;

import java.util.Calendar;

import cn.stj.alarmclock.Constants;
import cn.stj.alarmclock.R;
import cn.stj.alarmclock.activity.AlarmComingActivity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;

/**
 * alarm broadcastreceiver
 * @author jackey
 *
 */
public class AlarmReceiver extends BroadcastReceiver {

	private static final String TAG = AlarmReceiver.class.getSimpleName() ;
	private static final boolean DEBUG = false;
    private Context mContext ;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		mContext = context ;
		// 接受其他闹钟事件，电话事件，短信事件等，进行交互处理
		String action = intent.getAction();
		if (action != null
				&& action.equals("android.intent.action.PHONE_STATE")) {
			if (DEBUG)
			Log.v(TAG, "onReceive:action.PHONE_STATE");
			stopAlarm();
		} else if (action != null
				&& action.equals("android.provider.Telephony.SMS_RECEIVED")) {
			if (DEBUG)
			Log.v(TAG, "onReceive:Telephony.SMS_RECEIVED");
			stopAlarm();
		} else {
			if (DEBUG) {
				Log.d(TAG, "the time is up,start the alarm..."); 
			}
		    Intent myIntent = new Intent(context,AlarmComingActivity.class) ;
		    myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		    context.startActivity(myIntent) ;
		}
	}
	
	private void stopAlarm() {
		Intent cancelIntent = new Intent();
		cancelIntent.setAction(Constants.STOP_ALARM_ACTION);
		mContext.sendBroadcast(cancelIntent);
	}

}
