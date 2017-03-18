package com.jfem.hackathoncarnet.carnethackathon.model;

public class Mobility {

    private String name;
    private Positioning[] positioning;

    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        this.name = name;
    }

    public Positioning[] getPositioning ()
    {
        return positioning;
    }

    public void setPositioning (Positioning[] positioning)
    {
        this.positioning = positioning;
    }

    @Override
    public String toString()
    {
        return "[name = "+name+", positioning = "+positioning+"]";
    }
}
