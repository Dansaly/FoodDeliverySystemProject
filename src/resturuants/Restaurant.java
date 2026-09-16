package resturuants;

import core.Service;

public class Restaurant {
    protected String restaurantId;
    protected String name;
    protected String restType;
    protected double rating;
    protected boolean isOpen;
    protected double baseDeliveryFee;

    public Restaurant(String restaurantId, String name, String restType, 
                      double rating, boolean isOpen, double baseDeliveryFee) {
    	//with .set to make sure it is valid 

    	this.setRestaurantId(restaurantId);
        this.setName(name);
        this.setRestType(restType);
        this.setRating(rating);
        this.setIsOpen(isOpen);
        this.setBaseDeliveryFee(baseDeliveryFee);
    }

    public Restaurant(String restaurantId, String name, String restType, double rating) {
    	//with .set to make sure it is valid 

        this.setRestaurantId(restaurantId);
        this.setName(name);
        this.setRestType(restType);
        this.setRating(rating);
        this.setIsOpen(false); 
        this.setBaseDeliveryFee(0.0); 
    }

    public String getRestType() {
        return restType;
    }
	//making sure its valid. else throw excepetion
    public void setRestType(String restType) {
        if (restType == null) {
            throw new IllegalArgumentException("invalid cuisine type. its null");
        }
        this.restType = restType;
    }

    public double getRating() {
        return rating;
    }

    
	//making sure its valid. else throw excepetion

    public void setRating(double rating) {
        if (rating < 0 || rating > 5) {
            throw new IllegalArgumentException("invalid rating. rating is between 0-5");
        }
        this.rating = rating;
    }

    public double getBaseDeliveryFee() {
        return baseDeliveryFee;
    }
	//making sure its valid. else throw excepetion

    public void setBaseDeliveryFee(double baseDeliveryFee) {
        if (baseDeliveryFee < 0) {
            throw new IllegalArgumentException("invalid base delivery fee.");
        }
        this.baseDeliveryFee = baseDeliveryFee;
    }
    
    public String getRestaurantId() { 
        return restaurantId; 
    }
	//making sure its valid. else throw excepetion

    public void setRestaurantId(String restaurantId) {
        if (Service.onlyNumbers(restaurantId) == false) {
            throw new IllegalArgumentException("invalid restaurant ID. needs to be only numbers");
        }
        this.restaurantId = restaurantId;
    }

    public String getName() { 
        return name; 
    }

    
	//making sure its valid. else throw excepetion

    public void setName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("invalid name. its null");
        }
        this.name = name;
    }

    public boolean getIsOpen() { 
        return isOpen; 
    }

    public void setIsOpen(boolean isOpen) {
        this.isOpen = isOpen;
    }

    public double calculateFinalPrice(double baseAmount) {
        return baseAmount + baseDeliveryFee;
    }

    @Override
    public String toString() {
        return "Restaurant [ID=" + restaurantId + ", Name=" + name + ", Cuisine=" + restType + 
               ", Rating=" + rating + ", Open=" + isOpen + ", DeliveryFee=" + baseDeliveryFee + "]";
    }
}