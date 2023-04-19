package com.qboxus.gograbdriver.models;

import java.io.Serializable;

public class TripPaymentModel implements Serializable {
    private String id, tripId, userId, driverId, estimatedFare, finalFare, paymentType, paymentFromWallet
            , paymentCollectFromWallet, paymentCollectFromCard, payCollectFromCash, paymentMethodId,
    Stripe_charge,created;

    public TripPaymentModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
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

    public String getEstimatedFare() {
        return estimatedFare;
    }

    public void setEstimatedFare(String estimatedFare) {
        this.estimatedFare = estimatedFare;
    }

    public String getFinalFare() {
        return finalFare;
    }

    public void setFinalFare(String finalFare) {
        this.finalFare = finalFare;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getPaymentFromWallet() {
        return paymentFromWallet;
    }

    public void setPaymentFromWallet(String paymentFromWallet) {
        this.paymentFromWallet = paymentFromWallet;
    }

    public String getPaymentCollectFromWallet() {
        return paymentCollectFromWallet;
    }

    public void setPaymentCollectFromWallet(String paymentCollectFromWallet) {
        this.paymentCollectFromWallet = paymentCollectFromWallet;
    }

    public String getPaymentCollectFromCard() {
        return paymentCollectFromCard;
    }

    public void setPaymentCollectFromCard(String paymentCollectFromCard) {
        this.paymentCollectFromCard = paymentCollectFromCard;
    }

    public String getPayCollectFromCash() {
        return payCollectFromCash;
    }

    public void setPayCollectFromCash(String payCollectFromCash) {
        this.payCollectFromCash = payCollectFromCash;
    }

    public String getPaymentMethodId() {
        return paymentMethodId;
    }

    public void setPaymentMethodId(String paymentMethodId) {
        this.paymentMethodId = paymentMethodId;
    }

    public String getStripe_charge() {
        return Stripe_charge;
    }

    public void setStripe_charge(String stripe_charge) {
        Stripe_charge = stripe_charge;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }
}