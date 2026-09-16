package order;

import core.Service;
import resturuants.Restaurant;

public class Order {
	private String orderId;
	private int customerCode;
	private Restaurant restaurant;
	private String restaurantId;
	private String riderId;

	private int orderDay;
	private int orderMonth;
	private int orderYear;

	private int deliveryDay;
	private int deliveryMonth;
	private int deliveryYear;

	private double baseAmount;
	private double finalPrice;
	private String status;

	public static final String STATUS_CREATED = "Created";
	public static final String STATUS_ON_WAY = "On the way";
	public static final String STATUS_DELIVERED = "Delivered";

	public Order(String orderId, int customerCode, Restaurant restaurant, int orderDay, int orderMonth, int orderYear) {
		//with .set to make sure it is valid 

		this.setOrderId(orderId);
		this.setCustomerCode(customerCode);
		this.setRestaurant(restaurant);
		if(restaurant != null)
			this.setRestaurantId(restaurant.getRestaurantId());
		
		this.setRiderId("0");

		this.setOrderDay(orderDay);
		this.setOrderMonth(orderMonth);
		this.setOrderYear(orderYear);

		this.deliveryDay = 0;
		this.deliveryMonth = 0;
		this.deliveryYear = 0;

		this.setStatus(STATUS_CREATED);
	}

	public void setFinalPrice(double baseAmount) {
		if (baseAmount < 0) {
			throw new IllegalArgumentException("Error: Invalid base amount. Amount must be a positive number or zero.");
		}
		this.baseAmount = baseAmount;
		this.finalPrice = this.restaurant.calculateFinalPrice(baseAmount);
	}

	public String getOrderId() {
		return orderId;
	}

	public Restaurant getRestaurant() {
		return restaurant;
	}

	public void setRestaurant(Restaurant restaurant) {
		if(restaurant == null) {
			throw new IllegalArgumentException("Error: Missing restaurant data. Please provide an existing restaurant.");
		}
		this.restaurant = restaurant;
	}

	public String getRestaurantId() {
		return restaurantId;
	}

	public void setRestaurantId(String restaurantId) {
		if(Service.onlyNumbers(restaurantId) == false) {
			throw new IllegalArgumentException("Error: Invalid restaurant ID. ID must contain digits only.");
		}
		this.restaurantId = restaurantId;
	}

	public int getOrderDay() {
		return orderDay;
	}

	public void setOrderDay(int orderDay) {
		if(orderDay > 31 || orderDay <= 0) {
			throw new IllegalArgumentException("Error: Invalid order day. Please enter a valid day between 1 and 31.");
		}
		this.orderDay = orderDay;
	}

	public int getOrderMonth() {
		return orderMonth;
	}

	public void setOrderMonth(int orderMonth) {
		if(orderMonth <= 0 || orderMonth > 12) {
			throw new IllegalArgumentException("Error: Invalid order month. Please enter a valid month between 1 and 12.");
		}
		this.orderMonth = orderMonth;
	}

	public int getOrderYear() {
		return orderYear;
	}

	public void setOrderYear(int orderYear) {
		if(orderYear < 0) {
			throw new IllegalArgumentException("Error: Invalid order year. Please enter a valid positive year.");
		}
		this.orderYear = orderYear;
	}

	public int getDeliveryDay() {
		return deliveryDay;
	}

	public void setDeliveryDay(int deliveryDay) {
		if(deliveryDay <= 0 || deliveryDay > 31) {
			throw new IllegalArgumentException("Error: Invalid delivery day. Please enter a valid day between 1 and 31.");
		}
		this.deliveryDay = deliveryDay;
	}

	public int getDeliveryMonth() {
		return deliveryMonth;
	}

	public void setDeliveryMonth(int deliveryMonth) {
		if(deliveryMonth <= 0 || deliveryMonth > 12) {
			throw new IllegalArgumentException("Error: Invalid delivery month. Please enter a valid month between 1 and 12.");
		}
		this.deliveryMonth = deliveryMonth;
	}

	public int getDeliveryYear() {
		return deliveryYear;
	}

	public void setDeliveryYear(int deliveryYear) {
		if(deliveryYear < 0) {
			throw new IllegalArgumentException("Error: Invalid delivery year. Please enter a valid positive year.");
		}
		this.deliveryYear = deliveryYear;
	}

	public double getBaseAmount() {
		return baseAmount;
	}

	public void setBaseAmount(double baseAmount) {
		this.baseAmount = baseAmount;
	}

	public String getRiderId() {
		return riderId;
	}

	public double getFinalPrice() {
		return finalPrice;
	}

	public void setOrderId(String orderId) {
		if(Service.onlyNumbers(orderId) == false) {
			throw new IllegalArgumentException("Error: Invalid order ID. Order ID must contain digits only.");
		}
		this.orderId = orderId;
	}

	public void setCustomerCode(int code) {
		if(code < 0 ) {
			throw new IllegalArgumentException("Error: Invalid customer code. Code must be a positive integer or zero.");
		}
		this.customerCode = code;
	}

	public int getCustomerCode() {
		return customerCode;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		if (status.equals(STATUS_CREATED) == false && status.equals(STATUS_ON_WAY) == false && status.equals(STATUS_DELIVERED) == false) {
			throw new IllegalArgumentException("Error: Invalid order status. Allowed values are: Created, On the way, Delivered.");
		}
		this.status = status;
	}

	public void setDeliveryDate(int day, int month, int year) {		
		this.setDeliveryYear(year);
		this.setDeliveryMonth(month);
		this.setDeliveryDay(day);
	}

	public void setRiderId(String riderId) {
		if(riderId == null || riderId.trim().isEmpty()) {
			throw new IllegalArgumentException("Error: Invalid rider ID. Please provide a valid non-empty ID.");
		}
		this.riderId = riderId;
	}

	@Override
	public String toString() {
		String OrderDate = orderDay + "/" + orderMonth + "/" + orderYear;
		String DeliveryDate = deliveryDay + "/" + deliveryMonth + "/" + deliveryYear;

		return "Order [ID=" + orderId + ", CustomerID=" + customerCode + ", RestID=" + restaurantId + ", RiderID="
				+ riderId + ", Status=" + status + ", BaseAmount=" + baseAmount + ", FinalPrice=" + finalPrice
				+ ", OrderDate=" + OrderDate + ", DeliveryDate=" + DeliveryDate + "]";
	}
}