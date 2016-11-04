package cn.stj.alarmclock.activity;

import java.util.Calendar;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import cn.stj.alarmclock.R;
import cn.stj.alarmclock.entity.AlarmClockInfo;
import cn.stj.alarmclock.presenter.AlarmSetPresenter;
import cn.stj.alarmclock.presenter.AlarmSetPresenterImpl;
import cn.stj.alarmclock.util.PreferencesUtils;
import cn.stj.alarmclock.view.AlarmSetView;

/**
 * alarmclock setting acitivity
 * 
 * @author jackey
 * 
 */
public class AlarmSettingActivity extends BaseActivity implements
		BaseActivity.BottomKeyClickListener, AlarmSetView {

	private static final String TAG = AlarmSettingActivity.class
			.getSimpleName();

	private static final boolean DEBUG = false;

	private View mMainContentView;
	// hour edittext
	private EditText mHourEditText;
	// minute edittext
	private EditText mMinuteEditText;

	private boolean isOpenAlarmClock;

	private AlarmSetPresenter mAlarmSetPresenter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mAboveViewStub.setLayoutResource(R.layout.activity_main);
		setBottomKeyClickListener(this);
		setActivityBgResource(0);

		init();

	}

	@Override
	protected void onResume() {
		mAlarmSetPresenter = new AlarmSetPresenterImpl(this);
		mAlarmSetPresenter.loadAlarmClock();
		super.onResume();
	}

	private void init() {
		initView();
	}

	private void initView() {
		mMainContentView = mAboveViewStub.inflate();
		mHourEditText = (EditText) mMainContentView
				.findViewById(R.id.main_edit_hour);
		mMinuteEditText = (EditText) mMainContentView
				.findViewById(R.id.main_edit_minute);

		mHourEditText.addTextChangedListener(new TimeTextWatcher());
		mMinuteEditText.addTextChangedListener(new TimeTextWatcher());
		mHourEditText.setOnFocusChangeListener(new TimeFocusChangeListener());
		mMinuteEditText.setOnFocusChangeListener(new TimeFocusChangeListener());
		mHourEditText.setSelection(mHourEditText.getText().toString().length());

		setRightText(getResources().getString(R.string.delete));

	}

	@Override
	public void onLeftKeyPress() {
		if (DEBUG)
			Log.d(TAG, "onLeftKeyPress--->>isOpenAlarmClock:"
					+ isOpenAlarmClock);
		String hour = mHourEditText.getText().toString();
		String minute = mMinuteEditText.getText().toString();
		if (!TextUtils.isEmpty(hour) && !TextUtils.isEmpty(minute)) {
			if (isOpenAlarmClock) {
				// 关闭闹钟
				mAlarmSetPresenter.closeAlarmClock();
				setLeftBtnText(getResources().getString(R.string.alarm_open));
			} else {
				// 开启闹钟
				AlarmClockInfo alarmClockInfo = new AlarmClockInfo();
				alarmClockInfo.setHour(hour);
				alarmClockInfo.setMinute(minute);
				mAlarmSetPresenter.openAlarmClock(alarmClockInfo);
				setLeftBtnText(getResources().getString(R.string.alarm_close));
			}
		}
	}

	@Override
	public void onMiddleKeyPress() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRightKeyPress() {
		if (mHourEditText.isFocused()) {
			deleteEditTextInfo(mHourEditText);
		} else {
			deleteEditTextInfo(mMinuteEditText);
		}
	}

	public void deleteEditTextInfo(EditText editText) {
		String timeInfo = editText.getText().toString();
		if (TextUtils.isEmpty(timeInfo)) {
			finish();
		} else {
			int mmsInfoLen = timeInfo.length();
			mmsInfoLen--;
			timeInfo = timeInfo.substring(0, mmsInfoLen);
			editText.setText(timeInfo);
			editText.setSelection(timeInfo.length());
		}
	}

	private class TimeTextWatcher implements TextWatcher {

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			String timeInfo = s.toString().replaceAll("\\s*", "");
			if (!TextUtils.isEmpty(timeInfo)) {
				setRightText(getResources().getString(R.string.delete));
			} else {
				setRightText(getResources().getString(R.string.back));
			}
		}

		@Override
		public void afterTextChanged(Editable s) {

		}

	}

	private class TimeFocusChangeListener implements OnFocusChangeListener {
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			String editInfo = ((EditText) v).getText().toString();
			if (hasFocus) {
				if (!TextUtils.isEmpty(editInfo)) {
					setRightText(getResources().getString(R.string.delete));
				} else {
					setRightText(getResources().getString(R.string.back));
				}
			}
		}
	}

	@Override
	public void initAlarmClock(AlarmClockInfo alarmClockInfo) {
		isOpenAlarmClock = alarmClockInfo.isOpenAlarm();
		if (isOpenAlarmClock) {
			mHourEditText.setText(alarmClockInfo.getHour());
			mMinuteEditText.setText(alarmClockInfo.getMinute());
			setLeftBtnText(getResources().getString(R.string.alarm_close));
		} else {
			Calendar c = Calendar.getInstance();
			int hour = c.get(Calendar.HOUR_OF_DAY);
			int minute = c.get(Calendar.MINUTE);
			mHourEditText.setText(hour + "");
			mMinuteEditText.setText(minute + "");
			setLeftBtnText(getResources().getString(R.string.alarm_open));
		}
		mHourEditText.setSelection(mHourEditText.getText().toString().length());
	}

	@Override
	public void refreshAlarmClock() {
		isOpenAlarmClock = PreferencesUtils.getBoolean(this,
				cn.stj.alarmclock.Constants.KEY_ISOPEN_ALARMCLOCK);
	}

}
