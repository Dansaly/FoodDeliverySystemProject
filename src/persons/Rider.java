package persons;

import java.util.ArrayList;

import core.Service;
import order.Order;

public class Rider extends Person {
	private String idNumber;
	private String phone;
	private String vehicle;
	private boolean isAvailable;
	private ArrayList<Order> handledOrders;

	
	public Rider(String idNumber, String firstName, String lastName, String phone, String vehicle,
			boolean isAvailable) {
		//with .set to make sure it is valid 

		this.setIdNumber(idNumber);
		this.setFirstName(firstName);
		this.setLastName(lastName);
		this.setPhone(phone);
		this.setVehicle(vehicle);
		this.setAvailable(isAvailable);

		this.handledOrders = new ArrayList<>();
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {		
		if(Service.isValidPhone(phone) == false) {
			throw new IllegalArgumentException("invalid phone.");
		}
		this.phone=phone;
	}

	public String getVehicle() {
		return vehicle;
	}
	//making sure its valid. else throw excepetion

	public void setVehicle(String vehicle) {
		if (vehicle == null) {
			throw new IllegalArgumentException("invalid vehicle. its null");
		}
		this.vehicle = vehicle;
	}

	public ArrayList<Order> getHandledOrders() {
		return handledOrders;
	}

	//making sure its valid. else throw excepetion
	public void setHandledOrders(ArrayList<Order> handledOrders) {
		if (handledOrders == null) {
			throw new IllegalArgumentException("invalid handled Orders. its null");
		}
		this.handledOrders = handledOrders;
	}

	
	//making sure its valid. else throw excepetion
	public void setIdNumber(String idNumber) {
		if (Service.isValidID(idNumber) == false) {
			throw new IllegalArgumentException("invalid id number. needs to be ordinary id number.");
		}
		this.idNumber = idNumber;
	}

	public String getIdNumber() {
		return idNumber;
	}

	public boolean getIsAvailable() {
		return isAvailable;
	}

	public void setAvailable(boolean available) {
		isAvailable = available;
	}
	
	
	//making sure its valid. else throw excepetion
	public void addOrder(Order order) {
		if (order == null) {
			throw new IllegalArgumentException("invalid order. its null");
		}
		this.handledOrders.add(order);
	}

	//tostring for all orders
	public String getOrdersDetails() {
		StringBuilder sb = new StringBuilder();
		sb.append("Orders for Rider: ").append(getFirstName()).append(" ").append(getLastName()).append("\n");
		if (handledOrders.isEmpty()) {
			sb.append("No orders found.\n");
		} else {
			for (Order order : handledOrders) {
				sb.append(order.toString()).append("\n");
			}
		}
		return sb.toString();
	}
}