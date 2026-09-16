package persons;

import java.util.ArrayList;

import core.Service;
import resturuants.Restaurant;

public class RestAdmin extends Person {
	private String managerId;
	private String username;
	private String password;
	private ArrayList<Restaurant> managedRestaurants;

	public RestAdmin(String firstName, String lastName, String zipCode, String managerId, String username, String password) {
		//with .set to make sure it is valid 

		this.setFirstName(firstName);
		this.setLastName(lastName);
		this.setZipCode(zipCode);
		this.setManagerId(managerId);
		this.setUsername(username);
		this.setPassword(password);

		this.managedRestaurants = new ArrayList<>();
	}

	public ArrayList<Restaurant> getManagedRestaurants() {
		return managedRestaurants;
	}
	
	public String getManagerId() {
		return managerId;
	}

	//making sure its valid. else throw excepetion
	public void setManagerId(String managerId) {
		if (Service.isValidID(managerId) == false) {
			throw new IllegalArgumentException("Error Invalid manager ID. Please enter a valid 9 digit ID.");
		}
		this.managerId = managerId;
	}
	
	//making sure its valid. else throw excepetion
	public void setUsername(String username) {
		if (username == null || username.trim().isEmpty()) {
			throw new IllegalArgumentException("Error Invalid username. Please enter a not empty text.");
		}
		this.username = username;
	}

	//making sure its valid. else throw excepetion
	public void setPassword(String password) {
		if (password == null || password.trim().isEmpty()) {
			throw new IllegalArgumentException("Error Invalid password . Please enter a not empty text");
		}
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}
	
	
	//making sure its valid. else throw excepetion
	public void setManagedRestaurants(ArrayList<Restaurant> managedRestaurants) {
		if (managedRestaurants == null) {
			throw new IllegalArgumentException("Error: Invalid restaurants list. Please provide an initialized list.");
		}
		this.managedRestaurants = managedRestaurants;
	}
	
	
	//making sure its valid. else throw excepetion
	public void addRestaurant(Restaurant r) {
		if (r == null) {
			throw new IllegalArgumentException("Error Invalid restaurant. resturant is null.");
		}
		this.managedRestaurants.add(r);
	}

	//returns true if (this) manages the resturaunt with the given id 
	public boolean managesRestaurant(String restId) {
		for (Restaurant r : managedRestaurants) {
			if (r.getRestaurantId().equals(restId) ) {
				return true;
			}
		}
		return false;
	}
}