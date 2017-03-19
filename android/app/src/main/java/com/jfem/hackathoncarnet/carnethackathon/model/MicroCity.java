package com.jfem.hackathoncarnet.carnethackathon.model;

public class MicroCity {

    private Integer id;
    private String name;
    private String address;
    private Coordinates coordinates;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    @Override
    public String toString() {
        return "MicroCity [id = " + id + ", name = " + name + ",\n address = " + address + ",\n coordinates = " + coordinates + "]";
    }
}
