package com.qboxus.gograbdriver.models;

import java.io.Serializable;

public class RideRequestModel implements Serializable {
    String id;
    String userId;
    String driverId;
    String vehicleId;
    String pickupLat;
    String pickupLng;
    String destinationLat;
    String destinationLng;
    String request;
    String driverResponceDatetime;
    String driverRideResponse;
    String userRideResponse;
    String reason;
    String onTheWay;
    String arriveOnLocation;
    String startRide;
    String endRide;
    String estimatedFare;
    String walletPay;
    String paymentType;
    String paymentMethodId;
    String collectPayment;
    String created;
    String finalFare ="0";
    String pickupString;
    String destinationString;
    String trip_fare;

    public RideRequestModel() {
    }

    public String getTrip_fare() {
        return trip_fare;
    }

    public void setTrip_fare(String trip_fare) {
        this.trip_fare = trip_fare;
    }

    public String getPickupString() {
        return pickupString;
    }

    public void setPickupString(String pickupString) {
        this.pickupString = pickupString;
    }

    public String getDestinationString() {
        return destinationString;
    }

    public void setDestinationString(String destinationString) {
        this.destinationString = destinationString;
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

    public String getPickupLat() {
        return pickupLat;
    }

    public void setPickupLat(String pickupLat) {
        this.pickupLat = pickupLat;
    }

    public String getPickupLng() {
        return pickupLng;
    }

    public void setPickupLng(String pickupLng) {
        this.pickupLng = pickupLng;
    }

    public String getDestinationLat() {
        return destinationLat;
    }

    public void setDestinationLat(String destinationLat) {
        this.destinationLat = destinationLat;
    }

    public String getDestinationLng() {
        return destinationLng;
    }

    public void setDestinationLng(String destinationLng) {
        this.destinationLng = destinationLng;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getDriverResponceDatetime() {
        return driverResponceDatetime;
    }

    public void setDriverResponceDatetime(String driverResponceDatetime) {
        this.driverResponceDatetime = driverResponceDatetime;
    }

    public String getDriverRideResponse() {
        return driverRideResponse;
    }

    public void setDriverRideResponse(String driverRideResponse) {
        this.driverRideResponse = driverRideResponse;
    }

    public String getUserRideResponse() {
        return userRideResponse;
    }

    public void setUserRideResponse(String userRideResponse) {
        this.userRideResponse = userRideResponse;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getOnTheWay() {
        return onTheWay;
    }

    public void setOnTheWay(String onTheWay) {
        this.onTheWay = onTheWay;
    }

    public String getArriveOnLocation() {
        return arriveOnLocation;
    }

    public void setArriveOnLocation(String arriveOnLocation) {
        this.arriveOnLocation = arriveOnLocation;
    }

    public String getStartRide() {
        return startRide;
    }

    public void setStartRide(String startRide) {
        this.startRide = startRide;
    }

    public String getEndRide() {
        return endRide;
    }

    public void setEndRide(String endRide) {
        this.endRide = endRide;
    }

    public String getEstimatedFare() {
        return estimatedFare;
    }

    public void setEstimatedFare(String estimatedFare) {
        this.estimatedFare = estimatedFare;
    }

    public String getWalletPay() {
        return walletPay;
    }

    public void setWalletPay(String walletPay) {
        this.walletPay = walletPay;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getPaymentMethodId() {
        return paymentMethodId;
    }

    public void setPaymentMethodId(String paymentMethodId) {
        this.paymentMethodId = paymentMethodId;
    }

    public String getCollectPayment() {
        return collectPayment;
    }

    public void setCollectPayment(String collectPayment) {
        this.collectPayment = collectPayment;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getFinalFare() {
        return finalFare;
    }

    public void setFinalFare(String finalFare) {
        this.finalFare = finalFare;
    }
}
