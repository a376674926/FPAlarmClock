package cn.stj.alarmclock.model;

import cn.stj.alarmclock.entity.AlarmClockInfo;

public interface AlarmSetModel {

	/**
	 * 获取闹钟时间
	 */
	public AlarmClockInfo loadAlarmClock();

	/**
	 * 开启闹钟
	 */
	public void openAlarmClock(AlarmClockInfo alarmClockInfo,
			OnSetAlarmClockFinish onSetAlarmClockFinish);

	/**
	 * 关闭闹钟
	 */
	public void closeAlarmClock(OnSetAlarmClockFinish onSetAlarmClockFinish);

	/**
	 * 关闭或者开启闹钟回调接口
	 */
	public static interface OnSetAlarmClockFinish {

		/**
		 * 开启或者关闭的时候执行此函数
		 */
		public void onFinish();
	}
}
