package com.qboxus.gograbdriver.models;

import java.io.Serializable;

public class RiderOrderMultiStop implements Serializable {
    public String id;
    public String rider_order_id;
    public String parcel_order_id;
    public String on_the_way_to_pickup;
    public String pickup_datetime;
    public String on_the_way_to_dropoff;
    public String delivered;
    public String signature;
    public String created;
}
