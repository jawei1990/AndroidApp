package com.example.laserusbdemo;

public class ListMeasure
{
    private String id;
    private String dis;
    private String times;
    private String temp;
    public ListMeasure(String id, String dis,String temp,String times)
    {
        this.id = id;
        this.dis = dis;
        this.temp = temp;
        this.times = times;
    }

    public String getId(){return id;}
    public String getDis(){return dis;}
    public String getTimes(){return times;}
    public String getTemp() {return  temp;}

    public void setId(String id) {   this.id = id; }
    public void setDis(String dis) { this.dis = dis; }
    public void setTime(String times) { this.times = times; }
    public void setTemp(String temp){this.temp = temp;}
}
