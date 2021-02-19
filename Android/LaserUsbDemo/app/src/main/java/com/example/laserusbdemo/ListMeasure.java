package com.example.laserusbdemo;

public class ListMeasure
{
    private String id;
    private String dis;
    public ListMeasure(String id, String dis)
    {
        this.id = id;
        this.dis = dis;
    }

    public String getId(){return id;}
    public String getDis(){return dis;}

    public void setId(String id) {   this.id = id; }
    public void setDis(String dis) { this.dis = dis; }
}
