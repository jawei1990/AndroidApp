package com.conary.ipin7.screen_sub;

public class ListSensor
{
    private int idx;
    private long time;
    private String logs;
    private ListSensor(int idx, long time, String logs)
    {
        this.idx = idx;
        this.time = time;
        this.logs = logs;
    }

    public int getIdx() { return idx;}
    public long getTime(){return time;}
    public String getLogs(){return logs;}


}
