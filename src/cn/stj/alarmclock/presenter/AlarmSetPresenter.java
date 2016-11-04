package cn.stj.alarmclock.presenter;

import cn.stj.alarmclock.entity.AlarmClockInfo;

public interface AlarmSetPresenter {

	/**
	 * 获取闹钟时间
	 */
	public void loadAlarmClock();

	/**
	 * 开启闹钟
	 */
	public void openAlarmClock(AlarmClockInfo alarmClockInfo);

	/**
	 * 关闭闹钟
	 */
	public void closeAlarmClock();

}
