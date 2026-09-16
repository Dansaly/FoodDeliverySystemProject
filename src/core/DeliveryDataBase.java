package core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import exceptions.CustomerNotFoundException;
import exceptions.DeliveryPersonUnavailableException;
import exceptions.InsufficientBalanceException;
import exceptions.RestaurantNotFoundException;
import order.Order;
import persons.Admin;
import persons.Customer;
import persons.RestAdmin;
import persons.Rider;
import resturuants.PremiumRestaurant;
import resturuants.Restaurant;

public class DeliveryDataBase {
	private Admin systemAdministrator;
	private ArrayList<RestAdmin> restAdmins;
	private ArrayList<Restaurant> restaurants;
	private ArrayList<Customer> customers;
	private ArrayList<Rider> riders;
	private ArrayList<Order> orders;
	
	private HashMap<Integer, ArrayList<Order>> customerOrdersMap;
	private Hashtable<Integer, ArrayList<Restaurant>> customerRestaurantsMap;
	private HashMap<Integer, Double> customerPaymentsMap;

	public DeliveryDataBase() {
		this.systemAdministrator = new Admin("System", "Admin", "00000", "1", "admin", "12345");
		
		this.restAdmins = new ArrayList<>();
		this.restaurants = new ArrayList<>();
		this.customers = new ArrayList<>();
		this.riders = new ArrayList<>();
		this.orders = new ArrayList<>();
		
		this.customerOrdersMap = new HashMap<>();
		this.customerRestaurantsMap = new Hashtable<>();
		this.customerPaymentsMap = new HashMap<>();
	}
	
	public Admin getSystemAdministrator() {
		return systemAdministrator;
	}	

	//adding order to customer map while making sure it isnt null
	public void addOrderToCustomerMap(int code, Order order) {
		if (order == null) return;
		
		customerOrdersMap.putIfAbsent(code, new ArrayList<>());
		ArrayList<Order> custOrders = customerOrdersMap.get(code);
		
		if (!custOrders.contains(order)) {
			custOrders.add(order);
		}
	}

	//returning the orders of the rider with the given ID
	public ArrayList<Order> getRiderActiveOrDeliveredOrders(String riderId) {
		ArrayList<Order> res = new ArrayList<>();
		for (Rider rider : riders) {
			if (rider.getIdNumber().equals(riderId)) {
				for (Order o : rider.getHandledOrders()) {
					if (o.getStatus().equals(Order.STATUS_ON_WAY) || o.getStatus().equals(Order.STATUS_DELIVERED)) {
						res.add(o);
					}
				}
				break;
			}
		}
		return res;
	}

	public ArrayList<Restaurant> getPremiumRestaurantsForCustomer(Customer customer) {
		ArrayList<Restaurant> premiumRests = new ArrayList<>();
		if (customer == null) return premiumRests;
		
		int code = customer.getCode();
		if (customerRestaurantsMap.containsKey(code)) {
			for (Restaurant r : customerRestaurantsMap.get(code)) {
				if (r instanceof PremiumRestaurant && !premiumRests.contains(r)) {
					premiumRests.add(r);
				}
			}
		}
		return premiumRests;
	}

	//returning the customer with the most orders
	public Customer getCustomerWithMaxOrders() {
		int maxOrders = -1;
		Customer topCustomer = null;
		
		for (Customer c : customers) {
			int code = c.getCode();
			int orderCount;
			if(customerOrdersMap.containsKey(code)) {
				orderCount = customerOrdersMap.get(code).size();
			}
			else {
				orderCount = 0;
			}
			if (orderCount > maxOrders) {
				maxOrders = orderCount;
				topCustomer = c;
			}
		}
		return topCustomer;
	}

	//returning the riders with the most deliverys
	public Rider getRiderWithMaxDeliveries() {
		int maxDeliveries = -1;
		Rider topRider = null;
		
		for (Rider r : riders) {
			int count = 0;
			for (Order o : r.getHandledOrders()) {
				if (o.getStatus().equals(Order.STATUS_DELIVERED)) count++;
			}
			if (count > maxDeliveries) {
				maxDeliveries = count;
				topRider = r;
			}
		}
		return topRider;
	}

	//returning an array list of restuarants that are open and match the given cuisine
	public ArrayList<Restaurant> getOpenRestaurantsByCuisine(String cuisine) {
		ArrayList<Restaurant> res = new ArrayList<>();
		for (Restaurant r : restaurants) {
			if (r.getIsOpen() && r.getRestType().equals(cuisine)) {
				res.add(r);
			}
		}
		return res;
	}

	//adding the given rest to the  cust with the given code
	public void updateCustomerRestaurantMap(int code, Restaurant r) {
		customerRestaurantsMap.putIfAbsent(code, new ArrayList<>());
		ArrayList<Restaurant> custRests = customerRestaurantsMap.get(code);
		if (!custRests.contains(r)) {
			custRests.add(r);
		}
	}
	
	
	public void updateCustomerPaymentMap(int code, double amount) {
		double currentAmount = customerPaymentsMap.getOrDefault(code, 0.0);
		customerPaymentsMap.put(code, currentAmount + amount);
	}

	//adding an customer while makking sure he is not null and doesnt already exist
	public boolean addCustomer(Customer c) {
		if (c == null) {
			throw new IllegalArgumentException("Error: Customer object is null.");
		}
		for (Customer existing : customers) {
			if (existing.getCode() == c.getCode()) {
				throw new IllegalArgumentException("Error: A customer with this code already exists in the system.");
			}
		}
		customers.add(c);
		return true;
	}
	//adding an rest admin while making sure he is not null and doesnt already exist

	public boolean addRestAdmin(RestAdmin ra) {
		if (ra == null) {
			throw new IllegalArgumentException("Error: Restaurant Admin object is null.");
		}
		for (RestAdmin existing : restAdmins) {
			if (existing.getManagerId().equals(ra.getManagerId())) {
				throw new IllegalArgumentException("Error: An admin with this ID already exists in the system.");
			}
		}
		restAdmins.add(ra);
		return true;
	}
	//adding an rest while making sure it is not null and doesnt already exist

	public boolean addRestaurant(Restaurant r) {
		if (r == null) {
			throw new IllegalArgumentException("Error: Restaurant object is null.");
		}
		for (Restaurant existing : restaurants) {
			if (existing.getRestaurantId().equals(r.getRestaurantId())) {
				throw new IllegalArgumentException("Error: A restaurant with this ID already exists in the system.");
			}
		}
		restaurants.add(r);
		return true;
	}
	//adding an rider while makking sure he is not null and doesnt already exist

	public boolean addRider(Rider r) {
		if (r == null) {
			throw new IllegalArgumentException("Error: Rider object is null.");
		}
		for (Rider existing : riders) {
			if (existing.getIdNumber().equals(r.getIdNumber())) {
				throw new IllegalArgumentException("Error: A rider with this ID already exists in the system.");
			}
		}
		riders.add(r);
		return true;
	}
	//adding an order while making sure its not null
	public void addOrder(Order o) {
		if (o != null) orders.add(o);
	}

	public Customer getCustomerByCode(int code) throws CustomerNotFoundException {
		for (Customer c : customers) {
			if (c.getCode() == code) return c;
		}
		throw new CustomerNotFoundException("Error: The requested customer does not exist in the system. Please verify the customer code.");
	}

	public RestAdmin getRestAdminById(String id) {	
		for (RestAdmin ra : restAdmins) {
			if (ra.getManagerId().equals(id)) return ra;
		}
		return null;
	}

	public RestAdmin getRestAdminByAuth(String user, String pass) {
		for (RestAdmin ra : restAdmins) {
			if (ra.getUsername().equals(user) && ra.getPassword().equals(pass)) return ra;
		}
		return null;
	}

	public Restaurant getRestaurantById(String id) throws RestaurantNotFoundException {
		for (Restaurant r : restaurants) {
			if (r.getRestaurantId().equals(id)) return r;
		}
		throw new RestaurantNotFoundException("Error: The requested restaurant was not found. Please verify the restaurant ID.");
	}

	public Rider getRiderById(String id) {
		for (Rider r : riders) {
			if (r.getIdNumber().equals(id)) return r;
		}
		return null;
	}

	public Order getOrderById(String id) {
		for (Order o : orders) {
			if (o.getOrderId().equals(id)) return o;
		}
		return null;
	}
	
	public void processOrderPayment(Customer customer, double finalPrice) throws InsufficientBalanceException {
		if (customer.getBalance() < finalPrice) {
			throw new InsufficientBalanceException("Error: The customer does not have enough balance to complete the order. Please top up the balance first.");
		}
		customer.setBalance(customer.getBalance() - finalPrice);
	}

	public void cancelOrder(Customer customer, Order order) throws Exception {
		if (order == null || customer == null) {
			throw new IllegalArgumentException("Error: Order or Customer is null.");
		}
		
		String status = order.getStatus();
		double refund = 0.0;
		
		if (status.equals("Created") || status.equals("נוצרה")) { 
			refund = order.getFinalPrice();
		} else if (status.equals("On The Way") || status.equals("בדרך") || status.equals(Order.STATUS_ON_WAY)) {
			refund = order.getFinalPrice() / 2.0;
		} else {
			throw new Exception("Error: Cannot cancel an order that is already delivered or in an invalid state.");
		}
		
		customer.setBalance(customer.getBalance() + refund);
		order.setStatus("Cancelled");
	}

	public ArrayList<Order> getCustomerOrders(int code) {
		if (customerOrdersMap.containsKey(code)) {
			return customerOrdersMap.get(code);
		}
		return new ArrayList<>();
	}

	public void assignOrderToRider(Order o, Rider r) throws DeliveryPersonUnavailableException {
		if (r == null || !r.getIsAvailable()) { 
			throw new DeliveryPersonUnavailableException("Error: The requested delivery person is not available.");
		}
		r.getHandledOrders().add(o);
	}

	
	public void resetAndRebuildMaps() {
	    customerOrdersMap.clear();
	    customerRestaurantsMap.clear();
	    
	    for (Order o : orders) {
	        addOrderToCustomerMap(o.getCustomerCode(), o);
	        if (o.getRestaurant() != null) {
	            updateCustomerRestaurantMap(o.getCustomerCode(), o.getRestaurant());
	        }
	    }
	}
	public ArrayList<Restaurant> getRestaurants() {
		return restaurants;
	}

	public ArrayList<RestAdmin> getRestAdmins() {
		return restAdmins;
	}

	public Hashtable<Integer, ArrayList<Restaurant>> getCustomerRestaurantsMap() {
		return customerRestaurantsMap;
	}
	
	public ArrayList<Order> getOrders() {
		return orders;
	}
	
	public ArrayList<Customer> getCustomers() {
		return customers;
	}
	
	public ArrayList<Rider> getRiders() {
		return riders;
	}
}