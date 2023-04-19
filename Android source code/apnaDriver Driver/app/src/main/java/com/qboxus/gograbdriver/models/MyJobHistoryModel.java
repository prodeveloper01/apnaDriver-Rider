package com.qboxus.gograbdriver.models;

import java.io.Serializable;
import java.util.ArrayList;

public class MyJobHistoryModel implements Serializable {

  private TripModel tripModel;

  private UserModel userModel,driverModel;

  private ArrayList<TripDriverRatingModel> driverRatingList;

  public ArrayList<RecipientModel> recipientList=new ArrayList<>();

  public MyJobHistoryModel() {
  }

  public TripModel getTripModel() {
    return tripModel;
  }

  public void setTripModel(TripModel tripModel) {
    this.tripModel = tripModel;
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

  public ArrayList<TripDriverRatingModel> getDriverRatingList() {
    return driverRatingList;
  }

  public void setDriverRatingList(ArrayList<TripDriverRatingModel> driverRatingList) {
    this.driverRatingList = driverRatingList;
  }
}
