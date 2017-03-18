package com.jfem.hackathoncarnet.carnethackathon.model;

import java.util.ArrayList;

public class Services {

    private ArrayList<String> car;
    private ArrayList<String> leisure;
    private ArrayList<Mobility> mobility;

    public ArrayList<String> getCar ()
    {
        return car;
    }

    public void setCar (ArrayList<String> car)
    {
        this.car = car;
    }

    public ArrayList<String> getLeisure ()
    {
        return leisure;
    }

    public void setLeisure (ArrayList<String> leisure)
    {
        this.leisure = leisure;
    }

    public ArrayList<Mobility> getMobility ()
    {
        return mobility;
    }

    public void setMobility (ArrayList<Mobility> mobility)
    {
        this.mobility = mobility;
    }

    @Override
    public String toString()
    {
        return "[car = "+car+", leisure = "+leisure+", mobility = "+mobility+"]";
    }
}
