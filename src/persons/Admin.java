package persons;

public class Admin extends Person {
	private String managerId;
	private String username;
	private String password;

	public Admin(String firstName, String lastName, String zipCode, String managerId, String username, String password) {
		
		//with .set to make sure it is valid 

		this.setFirstName(firstName);
		this.setLastName(lastName);
		this.setZipCode(zipCode);
		this.setManagerId(managerId);
		this.setUsername(username);
		this.setPassword(password);
	}

	public Admin() {
	}

	public String getManagerId() {
		return managerId;
	}
	
	//making sure its valid. else throw excepetion
	public void setManagerId(String managerId) {
		if (managerId == null || managerId.trim().isEmpty()) {
			throw new IllegalArgumentException("Error: Invalid manager ID. Please enter a non-empty text.");
		}
		this.managerId = managerId;
	}

	
	//making sure its valid. else throw excepetion
	public void setUsername(String username) {
		if (username == null || username.trim().isEmpty()) {
			throw new IllegalArgumentException("Error: Invalid username. Please enter a non-empty text.");
		}
		this.username = username;
	}

	
	//making sure its valid. else throw excepetion
	public void setPassword(String password) {
		if (password == null || password.trim().isEmpty()) {
			throw new IllegalArgumentException("Error: Invalid password. Please enter a non-empty text.");
		}
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}
}