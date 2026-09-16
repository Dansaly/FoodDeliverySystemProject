package core;

import java.io.*;
import java.util.ArrayList;

import order.Order;
import persons.Customer;
import persons.RestAdmin;
import persons.Rider;
import resturuants.Restaurant;

public class FileManager {
	
	
	//saving customers from given arrayList of customers
	public static void saveCustomers(ArrayList<Customer> customers) throws IOException {
		File file = new File("customers.txt");
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(file));
			for (Customer c : customers) {
				String line = c.getCode() + "," + c.getFirstName() + "," + c.getLastName() + "," + c.getZipCode() + "," +
							  c.getAddress() + "," + c.getPhone() + "," + c.getEmail() + "," + c.getBalance();
				bw.write(line);
				bw.newLine();
			}
		} finally {
			if (bw != null) {
				bw.close();
			}
		}
	}

	//loading customers
	public static ArrayList<Customer> loadCustomers() throws IOException {
		ArrayList<Customer> loaded = new ArrayList<>();
		File file = new File("customers.txt");
		if (!file.exists()) return loaded;

		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
			String line;
			while ((line = br.readLine()) != null) {
				String[] parts = line.split(",");
				if (parts.length >= 8) {
					Customer c = new Customer(Integer.parseInt(parts[0]), parts[1], parts[2], parts[3],
											  parts[4], parts[5], parts[6], Double.parseDouble(parts[7]));
					loaded.add(c);
				}
			}
		} finally {
			if (br != null) {
				br.close();
			}
		}
		return loaded;
	}

	
	//saving restaurants from given array list of restaraunts
	public static void saveRestaurants(ArrayList<Restaurant> restaurants) throws IOException {
		File file = new File("restaurants.txt");
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(file));
			for (Restaurant r : restaurants) {
				String line = r.getRestaurantId() + "," + r.getName() + "," + r.getRestType() + "," + 
				              r.getRating() + "," + r.getIsOpen() + "," + r.getBaseDeliveryFee();
				bw.write(line);
				bw.newLine();
			}
		} finally {
			if (bw != null) {
				bw.close();
			}
		}
	}

	//loading Restaurants

	public static ArrayList<Restaurant> loadRestaurants() throws IOException {
		ArrayList<Restaurant> loaded = new ArrayList<>();
		File file = new File("restaurants.txt");
		if (!file.exists()) return loaded;

		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
			String line;
			while ((line = br.readLine()) != null) {
				String[] parts = line.split(",");
				if (parts.length >= 6) {
					Restaurant r = new Restaurant(
					    parts[0], 
					    parts[1], 
					    parts[2], 
					    Double.parseDouble(parts[3]), 
					    Boolean.parseBoolean(parts[4]), 
					    Double.parseDouble(parts[5])
					);
					loaded.add(r);
				}
			}
		} finally {
			if (br != null) {
				br.close();
			}
		}
		return loaded;
	}
	
	
	//saving Orders from given array list of Orders

	public static void saveOrders(ArrayList<Order> orders) throws IOException {
	    File file = new File("orders.txt");
	    BufferedWriter bw = null;
	    try {
	        bw = new BufferedWriter(new FileWriter(file));
	        for (Order o : orders) {
	            String restId = (o.getRestaurant() != null) ? o.getRestaurant().getRestaurantId() : "none";
	            String line = o.getOrderId() + "," + o.getCustomerCode() + "," + restId + "," + 
	                          o.getOrderDay() + "," + o.getOrderMonth() + "," + o.getOrderYear() + "," + 
	                          o.getStatus() + "," + o.getFinalPrice();
	            bw.write(line);
	            bw.newLine();
	        }
	    } finally {
	        if (bw != null) bw.close();
	    }
	}

	
	
	//loading orders
	public static ArrayList<Order> loadOrders(ArrayList<Restaurant> restaurants) throws IOException {
	    ArrayList<Order> loaded = new ArrayList<>();
	    File file = new File("orders.txt");
	    if (!file.exists()) return loaded;

	    BufferedReader br = null;
	    try {
	        br = new BufferedReader(new FileReader(file));
	        String line;
	        while ((line = br.readLine()) != null) {
	            String[] parts = line.split(",");
	            if (parts.length >= 8) { 
	                Restaurant orderRest = null;
	                for (Restaurant r : restaurants) {
	                    if (r.getRestaurantId().equals(parts[2])) {
	                        orderRest = r;
	                        break;
	                    }
	                }
	                Order o = new Order(
	                    parts[0], 
	                    Integer.parseInt(parts[1]), 
	                    orderRest,
	                    Integer.parseInt(parts[3]), 
	                    Integer.parseInt(parts[4]), 
	                    Integer.parseInt(parts[5])
	                );
	                o.setStatus(parts[6]);
	                o.setFinalPrice(Double.parseDouble(parts[7]));
	                loaded.add(o);
	            }
	        }
	    } finally {
	        if (br != null) br.close();
	    }
	    return loaded;
	}
	
	
	
	//saving riders from given array list of riders
	public static void saveRiders(ArrayList<Rider> riders) throws IOException {
		File file = new File("riders.txt");
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(file));
			for (Rider r : riders) {
				String line = r.getIdNumber() + "," + r.getFirstName() + "," + r.getLastName() + "," + 
				              r.getPhone() + "," + r.getVehicle() + "," + r.getIsAvailable();
				bw.write(line);
				bw.newLine();
			}
		} finally {
			if (bw != null) {
				bw.close();
			}
		}
	}
		
	
	
	//loading riders
	public static ArrayList<Rider> loadRiders() throws IOException {
		ArrayList<Rider> loaded = new ArrayList<>();
		File file = new File("riders.txt");
		if (!file.exists()) return loaded;

		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
			String line;
			while ((line = br.readLine()) != null) {
				String[] parts = line.split(",");
				if (parts.length >= 6) {
					Rider r = new Rider(
					    parts[0], 
					    parts[1], 
					    parts[2], 
					    parts[3], 
					    parts[4], 
					    Boolean.parseBoolean(parts[5])
					);
					loaded.add(r);
				}
			}
		} finally {
			if (br != null) {
				br.close();
			}
		}
		return loaded;
	}
	
	
	//saving restAdmins from given array list of RestAdmin
	public static void saveRestAdmins(ArrayList<RestAdmin> restAdmins) throws IOException {
		File file = new File("restAdmins.txt");
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(file));
			for (RestAdmin ra : restAdmins) {
				String line = ra.getFirstName() + "," + ra.getLastName() + "," + ra.getZipCode() + "," + 
				              ra.getManagerId() + "," + ra.getUsername() + "," + ra.getPassword();
				bw.write(line);
				bw.newLine();
			}
		} finally {
			if (bw != null) {
				bw.close();
			}
		}
	}

	
	//loading RestAdmins
	public static ArrayList<RestAdmin> loadRestAdmins() throws IOException {
		ArrayList<RestAdmin> loaded = new ArrayList<>();
		File file = new File("restAdmins.txt");
		if (!file.exists()) return loaded;

		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
			String line;
			while ((line = br.readLine()) != null) {
				String[] parts = line.split(",");
				if (parts.length >= 6) {
					RestAdmin ra = new RestAdmin(
					    parts[0], 
					    parts[1], 
					    parts[2], 
					    parts[3], 
					    parts[4], 
					    parts[5]
					);
					loaded.add(ra);
				}
			}
		} finally {
			if (br != null) {
				br.close();
			}
		}
		return loaded;
	}
	
}