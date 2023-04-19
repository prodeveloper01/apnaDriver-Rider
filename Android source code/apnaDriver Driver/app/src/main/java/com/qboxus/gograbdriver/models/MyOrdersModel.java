package com.qboxus.gograbdriver.models;

import java.io.Serializable;
import java.util.ArrayList;

public class MyOrdersModel implements Serializable {

    public String
            id,
            status,
            orderCreateDate,
            orderPickupTime,
            deliveryDatetime,
            senderLocationString,
            recevierLocationString,
            onTheWayToPickup,
            pickupDatetime,
            onTheWayToDropoff,
            delivered,
            currentView,
            orderPersonName,
            orderPersonId,
            orderPersonPhone,
            orderPersonEmail,
            orderType, senderName, orderPersonImage, senderId, senderPhone, senderEmail;

    public String senderLat, senderLong, receiverLat, receiverLong, signature, addressInstructionDropOff = "", receiverAddressDetail = "",
            addressInstructionPickUp = "", deliveryInstruction = "", senderAddressDetail = "";


    public ArrayList<RecipientModel> recipientList=new ArrayList<>();
    public ArrayList<RiderOrderMultiStop> orderMultiStops=new ArrayList<>();

}
