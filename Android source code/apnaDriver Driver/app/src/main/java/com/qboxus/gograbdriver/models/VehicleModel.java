package com.qboxus.gograbdriver.models;

import java.io.Serializable;

public class VehicleModel implements Serializable {
    String id, userId, driverId,make,model,year, licensePlate,color, rideTypeId,image,
    lat,lng,online, accUpdated, accCreated;

    RideTypeModel rideTypeModel;

    public VehicleModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getRideTypeId() {
        return rideTypeId;
    }

    public void setRideTypeId(String rideTypeId) {
        this.rideTypeId = rideTypeId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getOnline() {
        return online;
    }

    public void setOnline(String online) {
        this.online = online;
    }

    public String getAccUpdated() {
        return accUpdated;
    }

    public void setAccUpdated(String accUpdated) {
        this.accUpdated = accUpdated;
    }

    public String getAccCreated() {
        return accCreated;
    }

    public void setAccCreated(String accCreated) {
        this.accCreated = accCreated;
    }

    public RideTypeModel getRideTypeModel() {
        return rideTypeModel;
    }

    public void setRideTypeModel(RideTypeModel rideTypeModel) {
        this.rideTypeModel = rideTypeModel;
    }
}