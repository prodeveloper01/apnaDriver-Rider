package com.qboxus.gograbdriver.models;

import java.io.Serializable;

public class EditProfileUpdatePhoneNoModel implements Serializable {
    String countryId, countryCode, countryIos, countryName, phoneNo, otpCode,email;

    public EditProfileUpdatePhoneNoModel() {
    }

    public String getCountryId() {
        return countryId;
    }

    public void setCountryId(String countryId) {
        this.countryId = countryId;
    }

    public String getOtpCode() {
        return otpCode;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setOtpCode(String otpCode) {
        this.otpCode = otpCode;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCountryIos() {
        return countryIos;
    }

    public void setCountryIos(String countryIos) {
        this.countryIos = countryIos;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }
}
