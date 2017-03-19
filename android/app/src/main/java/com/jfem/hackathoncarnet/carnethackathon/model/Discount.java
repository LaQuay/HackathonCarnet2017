package com.jfem.hackathoncarnet.carnethackathon.model;

import org.json.JSONArray;

/**
 * Created by francescdepuigguixe on 19/3/17.
 */

public class Discount {
    private Integer microcityId;
    private String discount;
    private String serviceName;
    private Coordinates servicePosition;
    private JSONArray serviceCategoryName;

    public Integer getMicrocityId() {
        return microcityId;
    }

    public void setMicrocityId(Integer microcityId) {
        this.microcityId = microcityId;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public Coordinates getServicePosition() {
        return servicePosition;
    }

    public void setServicePosition(Coordinates servicePosition) {
        this.servicePosition = servicePosition;
    }

    public JSONArray getServiceCategoryName() {
        return serviceCategoryName;
    }

    public void setServiceCategoryName(JSONArray serviceCategoryName) {
        this.serviceCategoryName = serviceCategoryName;
    }
}
