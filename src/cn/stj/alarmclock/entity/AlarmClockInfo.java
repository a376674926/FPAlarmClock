package cn.stj.alarmclock.entity;

public class AlarmClockInfo {

	// 设置闹钟时间
	private long dateTime;
	// 设置闹钟小时
	private String hour;
	// 闹钟分钟
	private String minute;
	// 是否开启闹钟
	private boolean isOpenAlarm;

	public String getHour() {
		return hour;
	}

	public void setHour(String hour) {
		this.hour = hour;
	}

	public String getMinute() {
		return minute;
	}

	public void setMinute(String minute) {
		this.minute = minute;
	}

	public boolean isOpenAlarm() {
		return isOpenAlarm;
	}

	public void setOpenAlarm(boolean isOpenAlarm) {
		this.isOpenAlarm = isOpenAlarm;
	}

	public long getDateTime() {
		return dateTime;
	}

	public void setDateTime(long dateTime) {
		this.dateTime = dateTime;
	}

}
