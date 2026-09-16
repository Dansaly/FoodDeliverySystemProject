package persons;

import core.Service;

public abstract class Person {
	private String firstName;
	private String lastName;
	private String zipCode;

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		if (Service.isValidName(firstName) == false) {
			throw new IllegalArgumentException("Error: Invalid first name. Please enter letters only without numbers or spaces.");
		}
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		if (Service.isValidName(lastName) == false) {
			throw new IllegalArgumentException("Error: Invalid last name. Please enter letters only without numbers or spaces.");
		}
		this.lastName = lastName;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		if (zipCode == null || !zipCode.matches("\\d{5,7}")) {
			throw new IllegalArgumentException("Error: Invalid zip code. Please enter exactly 5 to 7 digits.");
		}
		this.zipCode = zipCode;
	}
}