package cn.stj.alarmclock.view;

import cn.stj.alarmclock.entity.AlarmClockInfo;

public interface AlarmSetView {
  
	/**
	 * 初始化闹钟时间
	 */
	public void initAlarmClock(AlarmClockInfo alarmClockInfo) ;
	
	public void refreshAlarmClock() ;
}
