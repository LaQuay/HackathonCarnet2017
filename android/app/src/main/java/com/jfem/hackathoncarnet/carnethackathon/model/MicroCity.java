package com.jfem.hackathoncarnet.carnethackathon.model;

public class MicroCity {

    private String name;
    private Coordinates coordinates;
    private Services services;

    public Services getServices ()
    {
        return services;
    }

    public void setServices (Services services)
    {
        this.services = services;
    }

    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        this.name = name;
    }

    public Coordinates getCoordinates ()
    {
        return coordinates;
    }

    public void setCoordinates (Coordinates coordinates)
    {
        this.coordinates = coordinates;
    }

    @Override
    public String toString()
    {
        return "MicroCity [name = " + name + ",\n services = "+services+",\n coordinates = "+coordinates+"]";
    }
}
