package com.conary.ipin7.screen_sub;

public class ListSensor
{
    private boolean isAlarmDetect;
    private String time;
    private String logs;
    public ListSensor(boolean isActivity,String time, String logs)
    {
        this.isAlarmDetect = isActivity;
        this.time = time;
        this.logs = logs;
    }

    public boolean getAlarmDetectStatuss(){return isAlarmDetect;}
    public String getTime(){return time;}
    public String getLogs(){return logs;}

    public void setAlarmDetectStatuss(boolean isActivity){ this.isAlarmDetect = isActivity;}
    public void setTime(String time) { this.time = time; }
    public void setMsg(String logs) { this.logs = logs; }
}
