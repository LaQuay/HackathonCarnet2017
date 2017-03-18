package com.jfem.hackathoncarnet.carnethackathon.model;

public class Positioning {

    private Schedule schedule;
    private String name;
    private String lng;
    private String lat;

    public Schedule getSchedule ()
    {
        return schedule;
    }

    public void setSchedule (Schedule schedule)
    {
        this.schedule = schedule;
    }

    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        this.name = name;
    }

    public String getLng ()
    {
        return lng;
    }

    public void setLng (String lng)
    {
        this.lng = lng;
    }

    public String getLat ()
    {
        return lat;
    }

    public void setLat (String lat)
    {
        this.lat = lat;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [schedule = "+schedule+", name = "+name+", lng = "+lng+", lat = "+lat+"]";
    }
}
