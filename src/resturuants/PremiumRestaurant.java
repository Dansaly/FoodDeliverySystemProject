package resturuants;

public class PremiumRestaurant extends Restaurant {
	private double minOrderCost;
	private double extraCommissionPercent;

	public PremiumRestaurant(String restaurantId, String name, String cuisineType, double rating, boolean isOpen,
			double baseDeliveryFee, double minOrderCost, double extraCommissionPercent) {
		super(restaurantId, name, cuisineType, rating, isOpen, baseDeliveryFee);
		//with .set to make sure it is valid 

		this.setMinOrderCost(minOrderCost);
		this.setExtraCommissionPercent(extraCommissionPercent);
	}

	public double getExtraCommissionPercent() {
		return extraCommissionPercent;
	}
	//making sure its valid. else throw excepetion

	public void setExtraCommissionPercent(double extraCommissionPercent) {
		if (extraCommissionPercent < 0) {
			throw new IllegalArgumentException("Error: Invalid extra commission percent. Please enter a positive number or zero.");
		}
		this.extraCommissionPercent = extraCommissionPercent;
	}

	
	//making sure its valid. else throw excepetion
	public void setMinOrderCost(double minOrderCost) {
		if (minOrderCost < 0) {
			throw new IllegalArgumentException("Error: Invalid minimum order cost. The amount must be a positive number or zero.");
		}
		this.minOrderCost = minOrderCost;
	}

	public double getMinOrderCost() {
		return minOrderCost;
	}

	@Override
	public double calculateFinalPrice(double baseAmount) {
		double commission = baseAmount * (extraCommissionPercent / 100.0);
		double total = baseAmount + baseDeliveryFee + commission;
		return Math.max(total, minOrderCost);
	}

	@Override
	public String toString() {
		return "Premium " + super.toString() + " [MinOrder=" + minOrderCost + ", Commission=" + extraCommissionPercent
				+ "%]";
	}
}