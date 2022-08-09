package dev.the_fireplace.shoprun;

import java.io.Serial;
import java.io.Serializable;

public class LocationData implements Serializable
{
    @Serial
    private static final long serialVersionUID = 8032982022515525435L;
    public String locationName;
    int itemPrice, itemCount, roundTripTime, restockTime;

    public LocationData(String locationName, int itemPrice, int itemCount, int roundTripTime, int restockTime) {
        this.locationName = locationName;
        this.itemPrice = itemPrice;
        this.itemCount = itemCount;
        this.roundTripTime = roundTripTime;
        this.restockTime = restockTime;
    }
}
