package com.conary.ipin7.screen_sub;

public class ListRaceCnt
{
    private String time;
    private String speed;
    public ListRaceCnt(String time, String speed)
    {
        this.time = time;
        this.speed = speed;
    }

    public String getTime(){return time;}
    public String getSpeed(){return speed;}
    public void setTime(String time) { this.time = time; }
    public void setSpeed(String speed) { this.speed = speed; }
}
