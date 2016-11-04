package cn.stj.alarmclock.model;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import cn.stj.alarmclock.Constants;
import cn.stj.alarmclock.R;
import cn.stj.alarmclock.broadcastreceiver.AlarmReceiver;
import cn.stj.alarmclock.entity.AlarmClockInfo;
import cn.stj.alarmclock.util.DateTimeUtil;
import cn.stj.alarmclock.util.PreferencesUtils;

public class AlarmSetModelImpl implements AlarmSetModel {

	private static final String TAG = AlarmSetModelImpl.class.getSimpleName();

	private static final boolean DEBUG = false;

	private Context mContext;

	public AlarmSetModelImpl(Context mContext) {
		this.mContext = mContext;

	}

	@Override
	public AlarmClockInfo loadAlarmClock() {
		AlarmClockInfo alarmClockInfo = new AlarmClockInfo();
		boolean isOpenAlarmClock = PreferencesUtils.getBoolean(mContext,
				cn.stj.alarmclock.Constants.KEY_ISOPEN_ALARMCLOCK);
		String alarmClockHour = PreferencesUtils.getString(mContext,
				cn.stj.alarmclock.Constants.KEY_HOUR);
		String alarmClockMinute = PreferencesUtils.getString(mContext,
				cn.stj.alarmclock.Constants.KEY_MINUTE);
		long alarmClockTime = PreferencesUtils.getLong(mContext,
				cn.stj.alarmclock.Constants.KEY_DATETIME);
		alarmClockInfo.setHour(alarmClockHour);
		alarmClockInfo.setMinute(alarmClockMinute);
		alarmClockInfo.setDateTime(alarmClockTime);
		alarmClockInfo.setOpenAlarm(isOpenAlarmClock);
		if (DEBUG)
			Log.d(TAG, "loadAlarmClock---->>hour:" + alarmClockHour
					+ " minute:" + alarmClockMinute + " alarmClockTime:"
					+ alarmClockTime);
		return alarmClockInfo;
	}

	@Override
	public void openAlarmClock(AlarmClockInfo alarmClockInfo,
			OnSetAlarmClockFinish onSetAlarmClockFinish) {
		if (DEBUG)
			Log.d(TAG, "openAlarmClock--->>hour:" + alarmClockInfo.getHour()
					+ " minute:" + alarmClockInfo.getMinute());
		if (alarmClockInfo != null
				&& !TextUtils.isEmpty(alarmClockInfo.getHour())
				&& !TextUtils.isEmpty(alarmClockInfo.getMinute())) {
			long systemTime = System.currentTimeMillis();
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(System.currentTimeMillis());
			calendar.set(Calendar.HOUR_OF_DAY,
					Integer.valueOf(alarmClockInfo.getHour()));
			calendar.set(Calendar.MINUTE,
					Integer.valueOf(alarmClockInfo.getMinute()));
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			// 选择定时的时间
			long selectTime = calendar.getTimeInMillis();
			// 如果当前时间大于设置时间，那么就从第二天的设定时间开始
			if (systemTime > selectTime) {
				calendar.add(Calendar.DAY_OF_MONTH, 1);
				selectTime = calendar.getTimeInMillis();
			}
			long interval = selectTime - systemTime;
			AlarmManager alarmManager = (AlarmManager) mContext
					.getSystemService(Context.ALARM_SERVICE);
			Intent intent = new Intent(mContext, AlarmReceiver.class);
			PendingIntent pi = PendingIntent.getBroadcast(mContext, 0, intent,
					0);
			alarmManager.cancel(pi);
			alarmManager.set(AlarmManager.RTC_WAKEUP, selectTime, pi);

			PreferencesUtils.putBoolean(mContext,
					Constants.KEY_ISOPEN_ALARMCLOCK, true);
			PreferencesUtils.putLong(mContext, Constants.KEY_DATETIME,
					selectTime);
			PreferencesUtils.putString(mContext, Constants.KEY_HOUR,
					alarmClockInfo.getHour());
			PreferencesUtils.putString(mContext, Constants.KEY_MINUTE,
					alarmClockInfo.getMinute());
			Toast.makeText(mContext,
					mContext.getString(R.string.open_alarm_clock),
					Toast.LENGTH_LONG).show();
			Toast.makeText(
					mContext,
					mContext.getString(R.string.set_alarm_time)
							+ DateTimeUtil.getIntervalCN(mContext, interval)
							+ mContext.getString(R.string.later),
					Toast.LENGTH_LONG).show();
			if (onSetAlarmClockFinish != null) {
				onSetAlarmClockFinish.onFinish();
			}
		}
	}

	@Override
	public void closeAlarmClock(OnSetAlarmClockFinish onSetAlarmClockFinish) {
		if (DEBUG)
			Log.d(TAG, "closeAlarmClock--->>");
		AlarmManager alarmManager = (AlarmManager) mContext
				.getSystemService(Context.ALARM_SERVICE);
		PendingIntent pi = getPendingIntent();
		alarmManager.cancel(pi);
		PreferencesUtils.putBoolean(mContext, Constants.KEY_ISOPEN_ALARMCLOCK,
				false);
		PreferencesUtils.putLong(mContext, Constants.KEY_DATETIME, 0l);
		PreferencesUtils.putString(mContext, Constants.KEY_HOUR, null);
		PreferencesUtils.putString(mContext, Constants.KEY_MINUTE, null);
		Toast.makeText(mContext,
				mContext.getString(R.string.close_alarm_clock),
				Toast.LENGTH_LONG).show();
		if (onSetAlarmClockFinish != null) {
			onSetAlarmClockFinish.onFinish();
		}
	}

	public PendingIntent getPendingIntent() {
		Intent intent = new Intent(mContext, AlarmReceiver.class);
		PendingIntent pi = PendingIntent.getBroadcast(mContext, 0, intent, 0);
		return pi;
	}

}
