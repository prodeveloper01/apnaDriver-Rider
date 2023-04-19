package com.qboxus.gograbdriver.models;

import java.io.Serializable;

public class RideModel implements Serializable {
    RideRequestModel rideRequestModel;
    UserModel userModel, driverModel;
    VehicleModel vehicleMode;

    public RideModel() {
    }

    public RideRequestModel getRideRequestModel() {
        return rideRequestModel;
    }

    public void setRideRequestModel(RideRequestModel rideRequestModel) {
        this.rideRequestModel = rideRequestModel;
    }

    public UserModel getUserModel() {
        return userModel;
    }

    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;
    }

    public UserModel getDriverModel() {
        return driverModel;
    }

    public void setDriverModel(UserModel driverModel) {
        this.driverModel = driverModel;
    }

    public VehicleModel getVehicleMode() {
        return vehicleMode;
    }

    public void setVehicleMode(VehicleModel vehicleMode) {
        this.vehicleMode = vehicleMode;
    }
}
