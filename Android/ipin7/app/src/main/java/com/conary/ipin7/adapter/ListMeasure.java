package com.conary.ipin7.adapter;

public class ListMeasure
{
    private String time;
    private String logs;
    public ListMeasure(String time, String logs)
    {
        this.time = time;
        this.logs = logs;
    }

    public String getTime(){return time;}
    public String getLogs(){return logs;}

    public void setTime(String time) { this.time = time; }
    public void setMsg(String logs) { this.logs = logs; }
}
