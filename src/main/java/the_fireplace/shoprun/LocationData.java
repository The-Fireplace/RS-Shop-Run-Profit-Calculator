package the_fireplace.shoprun;

import java.io.Serializable;

public class LocationData implements Serializable {
	String locationName;
	int itemPrice, itemCount, roundTripTime, restockTime;

	public LocationData(String locationName, int itemPrice, int itemCount, int roundTripTime, int restockTime) {
		this.locationName = locationName;
		this.itemPrice = itemPrice;
		this.itemCount = itemCount;
		this.roundTripTime = roundTripTime;
		this.restockTime = restockTime;
	}
}
