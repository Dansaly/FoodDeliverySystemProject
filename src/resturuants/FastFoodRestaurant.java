package resturuants;

public class FastFoodRestaurant extends Restaurant {
	private int avgPrepTimeMins;
	private double fastDeliveryExtraCost;

	public FastFoodRestaurant(String restaurantId, String name, String cuisineType, double rating, 
							  boolean isOpen, double baseDeliveryFee, int avgPrepTimeMins, double fastDeliveryExtraCost) {
		super(restaurantId, name, cuisineType, rating, isOpen, baseDeliveryFee);
		//with .set to make sure it is valid 

		this.setAvgPrepTimeMins(avgPrepTimeMins);
		this.setFastDeliveryExtraCost(fastDeliveryExtraCost);
	}

	@Override
	public double calculateFinalPrice(double baseAmount) {
		return baseAmount + baseDeliveryFee + fastDeliveryExtraCost;
	}

	public int getAvgPrepTimeMins() {
		return avgPrepTimeMins;
	}

	public void setAvgPrepTimeMins(int avgPrepTimeMins) {
		if(avgPrepTimeMins <= 0) {
			throw new IllegalArgumentException("Error: Invalid average preparation time. Please enter a number greater than 0 minutes.");
		}
		this.avgPrepTimeMins = avgPrepTimeMins;
	}

	public double getFastDeliveryExtraCost() {
		return fastDeliveryExtraCost;
	}

	public void setFastDeliveryExtraCost(double fastDeliveryExtraCost) {
		if(fastDeliveryExtraCost < 0) {
			throw new IllegalArgumentException("Error: Invalid fast delivery extra cost. It must be a positive number or zero.");
		}
		this.fastDeliveryExtraCost = fastDeliveryExtraCost;
	}

	@Override
	public String toString() {
		return "FastFood " + super.toString() + " [PrepTime=" + avgPrepTimeMins + "m, FastFee=" + fastDeliveryExtraCost + "]";
	}
}