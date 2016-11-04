package cn.stj.alarmclock.presenter;

import android.content.Context;
import android.provider.AlarmClock;
import cn.stj.alarmclock.entity.AlarmClockInfo;
import cn.stj.alarmclock.model.AlarmSetModel;
import cn.stj.alarmclock.model.AlarmSetModel.OnSetAlarmClockFinish;
import cn.stj.alarmclock.model.AlarmSetModelImpl;
import cn.stj.alarmclock.view.AlarmSetView;

public class AlarmSetPresenterImpl implements AlarmSetPresenter {

	private AlarmSetModel mAlarmSetModel;
	private AlarmSetView mAlarmSetView;
	private Context mContext;

	public AlarmSetPresenterImpl(AlarmSetView mAlarmSetView) {
		super();
		mContext = (Context) mAlarmSetView;
		this.mAlarmSetModel = new AlarmSetModelImpl(mContext);
		this.mAlarmSetView = mAlarmSetView;
	}

	@Override
	public void loadAlarmClock() {
		AlarmClockInfo alarmClockInfo = mAlarmSetModel.loadAlarmClock();
		if (mAlarmSetView != null) {
			mAlarmSetView.initAlarmClock(alarmClockInfo);
		}
	}

	@Override
	public void openAlarmClock(AlarmClockInfo alarmClockInfo) {
		mAlarmSetModel.openAlarmClock(alarmClockInfo,
				new OnSetAlarmClockFinish() {

					@Override
					public void onFinish() {
						if (mAlarmSetView != null) {
							mAlarmSetView.refreshAlarmClock();
						}
					}
				});
	}

	@Override
	public void closeAlarmClock() {
		mAlarmSetModel.closeAlarmClock(new OnSetAlarmClockFinish() {

			@Override
			public void onFinish() {
				if (mAlarmSetView != null) {
					mAlarmSetView.refreshAlarmClock();
				}
			}
		});
	}

}
