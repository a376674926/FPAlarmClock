package cn.stj.alarmclock.activity;

import java.util.Calendar;

import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import cn.stj.alarmclock.Constants;
import cn.stj.alarmclock.R;
import cn.stj.alarmclock.entity.AlarmClockInfo;
import cn.stj.alarmclock.presenter.AlarmSetPresenter;
import cn.stj.alarmclock.presenter.AlarmSetPresenterImpl;
import cn.stj.alarmclock.service.AlarmService;
import cn.stj.alarmclock.util.DateStyle;
import cn.stj.alarmclock.util.DateTimeUtil;
import cn.stj.alarmclock.util.PreferencesUtils;
import cn.stj.alarmclock.view.AlarmSetView;

/**
 * alarmClock coming activity
 * 
 * @author jackey
 * 
 */
public class AlarmComingActivity extends BaseActivity implements
		BaseActivity.BottomKeyClickListener, AlarmSetView {

	private static final String TAG = AlarmComingActivity.class.getSimpleName();
	public static final String ALARM_DONE = "com.android.deskclock.ALARM_DONE";

	private static final int TIMEOUT = 55;// 闹铃响时长（s）
	private Handler mHandler = new Handler();
	private MediaPlayer mMediaPlayer;
	private View mAlarmComingView;
	private TextView mAlarmDateTextView;
	private TextView mAlarmTimeTextView;
	private AlarmSetPresenter mAlarmSetPresenter;
	private PowerManager.WakeLock mWakeLock;
	private NotificationManager mNotificationManager;
	private static final int NOTIFICATION_ID = 100000010;
	private static final boolean DEBUG = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Window win = this.getWindow();
		WindowManager.LayoutParams params = win.getAttributes();
		params.flags |= WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;

		mMiddleViewStub.setLayoutResource(R.layout.activity_alarmcoming);
		setActivityBgResource(0);
		setBottomKeyClickListener(this);

		init();

		mAlarmSetPresenter = new AlarmSetPresenterImpl(this);
		mAlarmSetPresenter.loadAlarmClock();

		// 设置唤醒锁屏
		setWakeLock();

		// 设置闹铃通知
		setNotication();

		// 设置在电话来时处理
		setMuteOnTel();

		// 设置间隔时间自动停止
		setAutoStop();

		// 注册停止响铃接收者
		registerStopReceiver();

		// 设置FM播放时停止闹钟
		stopWhenGoFM();
	}

	private void registerStopReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.STOP_ALARM_ACTION);
		// 防止在灭屏时不能接收停止闹钟的广播
		this.registerReceiver(stopAlarmReceiver, filter);
	}

	// 用于接受广播，停止震动
	private BroadcastReceiver stopAlarmReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action != null && action.equals(Constants.STOP_ALARM_ACTION)) {
				closeMediaPlayer();
			}
		}
	};

	private void setNotication() {
		mNotificationManager = (NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);
		Notification n = new Notification();
		// 设置显示图标，该图标会在状态栏显示
		int icon = R.drawable.clock;
		// 设置显示提示信息，该信息也会在状态栏显示
		String tickerText = getResources().getString(
				R.string.alarm_notification_ticker);
		// 显示时间
		long when = System.currentTimeMillis();
		String notifyTitle = getResources().getString(
				R.string.alarm_notification_ticker);

		n.icon = icon;
		n.tickerText = tickerText;
		n.when = when;
		n.defaults |= Notification.DEFAULT_VIBRATE;

		final Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(System.currentTimeMillis());
		Intent intent1 = new Intent();
		intent1.setAction(Constants.STOP_ALARM_ACTION); // 如果在非AlarmAlert界面，则不能接受处理广播。

		PendingIntent broadcast = PendingIntent.getBroadcast(this, 0, intent1,
				0);
		n.setLatestEventInfo(
				this,
				notifyTitle,
				getResources().getString(R.string.click_cancel)
						+ " "
						+ getResources().getString(R.string.alarm_time)
						+ DateTimeUtil.longToString(cal.getTimeInMillis(),
								DateStyle.HH_MM), broadcast);
		n.flags |= Notification.FLAG_AUTO_CANCEL
				| Notification.FLAG_ONGOING_EVENT;

		mNotificationManager.notify(NOTIFICATION_ID, n);
	}

	private void setMuteOnTel() {
		// 通知背景播放音乐mp3暂停，这个action被mp3注册
		Intent alarm_alert = new Intent("com.android.deskclock.ALARM_ALERT");
		this.sendBroadcast(alarm_alert);

		// 获得来电状态，如果正在通话或者来电，闹铃时间到不响闹铃，只是通知。
		TelephonyManager mTelephonyMgr = (TelephonyManager) this
				.getSystemService(this.TELEPHONY_SERVICE);
		int callstate = mTelephonyMgr.getCallState();
		if (callstate == 0) {
			// 调用服务，播放闹铃
			Intent intent2 = new Intent(this, AlarmService.class);
			startService(intent2);
			// 音量控制
			setVolumeControlStream(AudioManager.STREAM_ALARM);
			if (DEBUG) {
				Log.d(TAG,
						"AlarmAlert:mStartTime=" + System.currentTimeMillis());
			}
		}
	}

	private void setAutoStop() {
		mHandler.postDelayed(stopTask, TIMEOUT * 1000);
	}

	private Runnable stopTask = new Runnable() {
		@Override
		public void run() {
			if (DEBUG)
				Log.d(TAG, "auto stop task run");
			closeMediaPlayer();
		}
	};

	public void closeMediaPlayer() {
		mHandler.removeCallbacks(stopTask);
		Intent intent3 = new Intent(this, AlarmService.class);
		stopService(intent3);

		// 恢复音量控制状态
		setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);

		// 通知背景播放音乐mp3再次启动
		Intent alarm_done = new Intent(ALARM_DONE);
		this.sendBroadcast(alarm_done);

		// 更新闹钟数据，设置闹钟状态为关闭
		PreferencesUtils.putBoolean(this, Constants.KEY_ISOPEN_ALARMCLOCK,
				false);

		this.finish();
	}

	private void stopWhenGoFM() {
		IntentFilter stopFromFM = new IntentFilter(
				"com.android.fm.stopmusicservice");
		this.registerReceiver(stopFromFmReceiver, stopFromFM);
	}

	private BroadcastReceiver stopFromFmReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Boolean isPlaying = intent.getExtras().getBoolean("playingfm");
			if (intent.getAction().equals("com.android.fm.stopmusicservice")) {
				if (isPlaying != null && isPlaying) {
					closeMediaPlayer();
				}
			}
		}
	};

	private void init() {
		initView();
	}

	private void initView() {
		mAlarmComingView = mMiddleViewStub.inflate();
		mAlarmDateTextView = (TextView) mAlarmComingView
				.findViewById(R.id.alarmcoming_tv_date);
		mAlarmTimeTextView = (TextView) mAlarmComingView
				.findViewById(R.id.alarmcoming_tv_time);

		setRightText(getResources().getString(R.string.alarm_close));
		setLeftBtnText(null);
	}

	@Override
	protected void onDestroy() {
		if (stopAlarmReceiver != null) {
			this.unregisterReceiver(this.stopAlarmReceiver);
		}
		if (stopFromFmReceiver != null) {
			this.unregisterReceiver(stopFromFmReceiver);
		}
		super.onDestroy();
	}

	@Override
	public void onLeftKeyPress() {
	}

	@Override
	public void onMiddleKeyPress() {
	}

	@Override
	public void onRightKeyPress() {
		mAlarmSetPresenter.closeAlarmClock();
	}

	@Override
	public void initAlarmClock(AlarmClockInfo alarmClockInfo) {
		mAlarmDateTextView.setText(DateTimeUtil.longToString(
				alarmClockInfo.getDateTime(), DateStyle.YYYY_MM_DD));
		mAlarmTimeTextView.setText(DateTimeUtil.longToString(
				alarmClockInfo.getDateTime(), DateStyle.HH_MM));
	}

	@Override
	public void refreshAlarmClock() {
		closeMediaPlayer();
		cancelNotification();
		this.finish();
	}

	private void setWakeLock() {
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		if (!pm.isScreenOn()) {
			// 点亮屏
			mWakeLock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP
					| PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
			mWakeLock.acquire();
		}
	}

	private void cancelNotification() {
		if (mNotificationManager != null) {
			mNotificationManager.cancel(NOTIFICATION_ID);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	public void onStop() {
		if (mWakeLock != null && mWakeLock.isHeld()) {
			try {
				mWakeLock.release();
			} catch (Exception e) {
				if (DEBUG) {
					Log.d(TAG, e.getMessage());
				}
			}

		}
		super.onStop();
	}
}
