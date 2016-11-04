package cn.stj.alarmclock.service;

import java.io.IOException;

import cn.stj.alarmclock.Constants;
import cn.stj.alarmclock.R;
import cn.stj.alarmclock.util.MediaUtil;
import cn.stj.alarmclock.util.PreferencesUtils;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;

public class AlarmService extends Service {

	private static final String TAG = AlarmService.class.getSimpleName();

	private Vibrator mVibrator;
	private boolean mIsVibrate;
	private MediaPlayer mMediaPlayer;
	private String mRingToneUriStr;
	private String mRingName;
	//系统默认铃声
	private String defaultUri = "content://settings/system/ringtone";
	String external = "content://media/external/";

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	public void onCreate() {
		super.onCreate();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.STOP_ALARM_ACTION);
		// 防止在灭屏时不能接受停止闹钟的广播，所以用静态注册。
		this.registerReceiver(stopAlarmReceiver, filter);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		initDatas();
		setVibrator();
		// playTest() ;
		play();
		// TODO Auto-generated method stub
		return super.onStartCommand(intent, flags, startId);
	}

	/**
	 * 初始化数据
	 * 
	 * @param intent
	 */
	private void initDatas() {
		// 设置mRingToneUriStr ringname IsVibrate
		mRingToneUriStr = "default";
		mIsVibrate = true;
		mRingName = "default";
	}

	// 设置震动
	private void setVibrator() {
		mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		if (mIsVibrate) {
			mVibrator.vibrate(new long[] { 1000, 2000 }, 0);
		} else {
			closeVirbrator();
		}
	}

	private void playTest() {
		mMediaPlayer = MediaPlayer.create(this, R.raw.alarmtone);
		mMediaPlayer.start();
		mMediaPlayer.setLooping(true);

	}

	private void play() {
		if (mRingToneUriStr == null) {
			return;
		}
		mMediaPlayer = new MediaPlayer();
		AudioManager adm = (AudioManager) getSystemService(AUDIO_SERVICE);
		try {
			if (mRingToneUriStr.equals("default")) {
				mMediaPlayer.setDataSource(AlarmService.this, Uri.parse(defaultUri));
			} else if (mRingToneUriStr.length() > 25
					&& mRingToneUriStr.substring(0, 25).equals(external)
					&& !(MediaUtil.isSongExist(this, mRingName))) {
				mMediaPlayer.setDataSource(AlarmService.this,
						Uri.parse(defaultUri));
			} else {
				mMediaPlayer.setDataSource(AlarmService.this,
						Uri.parse(mRingToneUriStr));
			}

			if (adm.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
				// 设置声音播放通道
				mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
				mMediaPlayer.setLooping(true);
				mMediaPlayer.prepare();
				mMediaPlayer.start();
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
	}

	private BroadcastReceiver stopAlarmReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			if (action != null && action.equals(Constants.STOP_ALARM_ACTION)) {
				closePlay();
				closeVirbrator();
				PreferencesUtils.putBoolean(AlarmService.this,
						Constants.KEY_ISOPEN_ALARMCLOCK, false);
			}
		}
	};

	private void closePlay() {
		try {
			if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
				Log.v(TAG, "onDestroy closeMediaPlayer");
				mMediaPlayer.stop();
				mMediaPlayer.release();
				mMediaPlayer = null;
			}
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
	}

	private void closeVirbrator() {
		if (mVibrator != null) {
			mVibrator.cancel();
			mVibrator = null;
		}
	}

	public void onDestroy() {
		super.onDestroy();
		closeVirbrator();
		closePlay();
		PreferencesUtils.putBoolean(AlarmService.this,
				Constants.KEY_ISOPEN_ALARMCLOCK, false);
		unregisterReceiver(stopAlarmReceiver);
	}

}
