package com.qboxus.gograbdriver.models;

import java.io.Serializable;

public class TripModel implements Serializable {
    private String id, userId, driverId, vehicleId, requestId,
            pickupLocation, destinationLocation, pickupLat, pickupLong,
            destinationLat, destinationLong, pickupDatetime, destinationDatetime,
             map,completed,created;

    private TripPaymentModel tripPaymentModel;


    public TripModel() {
    }

    public String getMap() {
        return map;
    }

    public void setMap(String map) {
        this.map = map;
    }

    public TripPaymentModel getTripPaymentModel() {
        return tripPaymentModel;
    }

    public void setTripPaymentModel(TripPaymentModel tripPaymentModel) {
        this.tripPaymentModel = tripPaymentModel;
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

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(String pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public String getDestinationLocation() {
        return destinationLocation;
    }

    public void setDestinationLocation(String destinationLocation) {
        this.destinationLocation = destinationLocation;
    }

    public String getPickupLat() {
        return pickupLat;
    }

    public void setPickupLat(String pickupLat) {
        this.pickupLat = pickupLat;
    }

    public String getPickupLong() {
        return pickupLong;
    }

    public void setPickupLong(String pickupLong) {
        this.pickupLong = pickupLong;
    }

    public String getDestinationLat() {
        return destinationLat;
    }

    public void setDestinationLat(String destinationLat) {
        this.destinationLat = destinationLat;
    }

    public String getDestinationLong() {
        return destinationLong;
    }

    public void setDestinationLong(String destinationLong) {
        this.destinationLong = destinationLong;
    }

    public String getPickupDatetime() {
        return pickupDatetime;
    }

    public void setPickupDatetime(String pickupDatetime) {
        this.pickupDatetime = pickupDatetime;
    }

    public String getDestinationDatetime() {
        return destinationDatetime;
    }

    public void setDestinationDatetime(String destinationDatetime) {
        this.destinationDatetime = destinationDatetime;
    }

    public String getCompleted() {
        return completed;
    }

    public void setCompleted(String completed) {
        this.completed = completed;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }
}
