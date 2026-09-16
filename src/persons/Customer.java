package persons;

public class Customer extends Person implements Comparable<Customer> {
	private int code;
	private String address;
	private String phone;
	private String email;
	private double balance;

	public Customer(int code, String firstName, String lastName, String zipCode, String address, String phone, String email, double balance) {
		//with .set to make sure it is valid 

		this.setCode(code);
		this.setFirstName(firstName);
		this.setLastName(lastName);
		this.setZipCode(zipCode);
		this.setAddress(address);
		this.setPhone(phone);
		this.setEmail(email);
		this.setBalance(balance);
	}

	public Customer() {
	}

	public int getCode() {
		return this.code;
	}

	public void setCode(int codeI) {
		if(codeI < 0) {
			throw new IllegalArgumentException("Error: Invalid customer code. Please enter a positive integer.");
		}
		this.code = codeI;	
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		if(address == null || address.trim().isEmpty()) {
			throw new IllegalArgumentException("Error: Invalid address. Please enter a non-empty text.");
		}
		this.address = address;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		if(phone == null || phone.trim().isEmpty()) {
			throw new IllegalArgumentException("Error: Invalid phone number. Please enter a valid number without letters.");
		}
		this.phone = phone;
	}

	public void setEmail(String email) {
		if(email == null || email.trim().isEmpty() || !email.contains("@")) {
			throw new IllegalArgumentException("Error: Invalid email address. Please enter a valid email containing the @ symbol.");
		}
		this.email = email;
	}

	public void setBalance(double balance) {
		if(balance < 0) {
			throw new IllegalArgumentException("Error: Invalid balance. Please enter a positive amount or zero.");
		}
		this.balance = balance;
	}

	public String getEmail() {
		return email;
	}

	public double getBalance() {
		return balance;
	}

	@Override
	public int compareTo(Customer other) {
		return Double.compare(other.getBalance(), this.balance);
	}

	@Override
	public String toString() {
		return "Customer [ID=" + code + ", Name=" + this.getFirstName() + " " + this.getLastName() + ", ZipCode=" + this.getZipCode() + ", Address=" + address
				+ ", Phone=" + phone + "]";
	}
}