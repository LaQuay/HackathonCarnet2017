package com.jfem.hackathoncarnet.carnethackathon.model;

public class Services {
    private String[] car;

    private String[] leisure;

    private Mobility[] mobility;

    public String[] getCar ()
    {
        return car;
    }

    public void setCar (String[] car)
    {
        this.car = car;
    }

    public String[] getLeisure ()
    {
        return leisure;
    }

    public void setLeisure (String[] leisure)
    {
        this.leisure = leisure;
    }

    public Mobility[] getMobility ()
    {
        return mobility;
    }

    public void setMobility (Mobility[] mobility)
    {
        this.mobility = mobility;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [car = "+car+", leisure = "+leisure+", mobility = "+mobility+"]";
    }
}
