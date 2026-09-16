package core;

import resturuants.*;
import order.Order;
import order.OrderPriceComparator;
import persons.Admin;
import persons.Customer;
import persons.Person;
import persons.RestAdmin;
import persons.Rider;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import exceptions.CustomerNotFoundException;
import exceptions.DeliveryPersonUnavailableException;
import exceptions.InsufficientBalanceException;
import exceptions.RestaurantNotFoundException;

public class Main {

	//the data base
    private DeliveryDataBase db = new DeliveryDataBase();
    private Scanner scanner = new Scanner(System.in);

    public void start() {
        loadInitialData();
        OpeningScreen();
    }

    private void loadInitialData() {
        File checkFile = new File("customers.txt");
        if (checkFile.exists()) {
            try {
                db.getCustomers().addAll(FileManager.loadCustomers());
                db.getRestaurants().addAll(FileManager.loadRestaurants());
                db.getOrders().addAll(FileManager.loadOrders(db.getRestaurants()));
                db.getRiders().addAll(FileManager.loadRiders());
                db.getRestAdmins().addAll(FileManager.loadRestAdmins());
                db.resetAndRebuildMaps();
            } catch (Exception e) {
            	alert("ERROR", "Loading Error", "Save files might be corrupted. Loading default data instead.");
                initData();
            }
        } else {
            initData();
        }
    }

    //printing the opening screen
    private void OpeningScreen() {
        boolean exit = false;
        while (!exit) {
            System.out.println("\n=== Food Delivery System ===");
            System.out.println("1. System Admin Login");
            System.out.println("2. User Login");
            System.out.println("3. Exit System");
            System.out.print("Choose an option: ");
            
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    adminLoginScreen();
                    break;
                case "2":
                    userLoginScreen();
                    break;
                case "3":
                    exit = true;
                    autoSave();
                    System.out.println("Exiting system. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice, please try again.");
            }
        }
    }

    // printing the admin login screen
    private void adminLoginScreen() {
        System.out.println("\n--- System Admin Login ---");
        System.out.print("Username: ");
        String txtUser = scanner.nextLine().trim();
        System.out.print("Password: ");
        String txtPass = scanner.nextLine().trim();

        Admin admin = db.getSystemAdministrator();
        if (admin.getUsername().equals(txtUser) && admin.getPassword().equals(txtPass)) {
            systemAdminScreen();
        } else {
        	alert("ERROR", "Login Failed", "Invalid username or password.");
        }
    }

    // printing the user login screen
    private void userLoginScreen() {
        System.out.println("\n--- User Login ---");
        System.out.println("Select User Type:");
        System.out.println("1. Customer");
        System.out.println("2. Rider");
        System.out.println("3. Restaurant Admin");
        System.out.println("4. Back");
        System.out.print("Choice: ");
        
        String choice = scanner.nextLine().trim();
        if (choice.equals("4")) return;

        String type = "";
        if (choice.equals("1")) type = "Customer";
        else if (choice.equals("2")) type = "Rider";
        else if (choice.equals("3")) type = "Restaurant Admin";
        else {
        	alert("WARNING", "Warning", "Please select a valid user type.");
            return;
        }

        System.out.print("Enter ID/Code: ");
        String idStr = scanner.nextLine().trim();

        if (idStr.isEmpty()) {
        	alert("WARNING", "Input Error", "ID/Code field cannot be empty.");
            return;
        }

        try {
            if (type.equals("Customer")) {
                Customer c = db.getCustomerByCode(Integer.parseInt(idStr));
                customerPersonalScreen(c);
            } else if (type.equals("Rider")) {
                Rider r = db.getRiderById(idStr);
                if (r != null) {
                    riderPersonalScreen(r);
                } else {
                	alert("ERROR", "Not Found", "Rider with ID " + idStr + " was not found.");
                }
            } else if (type.equals("Restaurant Admin")) {
                RestAdmin ra = db.getRestAdminById(idStr);
                if (ra != null) {
                    restAdminPersonalScreen(ra);
                } else {
                	alert("ERROR", "Not Found", "Restaurant Admin with ID " + idStr + " was not found.");
                }
            }
        } catch (NumberFormatException ex) {
        	alert("ERROR", "Invalid Format", "The ID/Code must be a number.");
        } catch (CustomerNotFoundException ex) {
        	alert("ERROR", "Not Found", ex.getMessage());
        }
    }

    // printing the customer personal screen
    private void customerPersonalScreen(Customer c) {
        boolean logout = false;
        while (!logout) {
            System.out.println("\n--- Welcome Customer: " + c.getFirstName() + " ---");
            System.out.printf("Current Balance: %.2f\n", c.getBalance());
            System.out.println("\nMy Orders:");
            
            Collection<Order> orders = db.getCustomerOrders(c.getCode());
            if (orders == null || orders.isEmpty()) {
                System.out.println("No orders found.");
            } else {
                for (Order o : orders) {
                    System.out.println(o.toString());
                }
            }

            System.out.println("\nActions:");
            System.out.println("1. View Personal Details");
            System.out.println("2. Update Details");
            System.out.println("3. New Order");
            System.out.println("4. Cancel Order");
            System.out.println("5. Load Money");
            System.out.println("6. Withdraw Money");
            System.out.println("7. My Restaurants");
            System.out.println("8. My Premium Rests");
            System.out.println("9. Search Restaurant");
            System.out.println("0. Logout");
            System.out.print("Choice: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                	alert("INFORMATION", "Details", c.toString());
                    break;
                case "2":
                    doubleInputForm("Update Details", "New Phone:", "New Address:", (BiConsumer<String, String>)(String newPhone, String newAddress) -> {
                        if (newPhone != null && newAddress != null && !newPhone.trim().isEmpty() && !newAddress.trim().isEmpty()) {
                            c.setPhone(newPhone);
                            c.setAddress(newAddress);
                            alert("INFORMATION", "Success", "Personal details updated successfully.");
                        } else {
                        	alert("WARNING", "Invalid Input", "Phone number and address cannot be empty.");
                        }
                    });
                    break;
                case "3":
                    doubleInputForm("New Order", "Restaurant ID:", "Order Price:", (BiConsumer<String, String>)(String restId, String priceStr) -> {
                        if (restId != null && priceStr != null && !restId.trim().isEmpty() && !priceStr.trim().isEmpty()) {
                            try {
                                Restaurant rest = db.getRestaurantById(restId.trim());
                                double price = Double.parseDouble(priceStr.trim());
                                
                                db.processOrderPayment(c, price); 
                                
                                Order newOrder = new Order("ORD" + (db.getOrders().size() + 1), c.getCode(), rest, 1, 9, 2026);
                                newOrder.setFinalPrice(price);
                                newOrder.setStatus("Created");
                                
                                db.addOrder(newOrder);
                                db.addOrderToCustomerMap(c.getCode(), newOrder);
                                db.updateCustomerRestaurantMap(c.getCode(), rest);
                                
                                alert("INFORMATION", "Success", "Order placed successfully!");
                            } catch (RestaurantNotFoundException | InsufficientBalanceException ex) {
                            	alert("ERROR", "Order Failed", ex.getMessage());
                            } catch (NumberFormatException ex) {
                            	alert("ERROR", "Invalid Format", "Price must be a valid number.");
                            }
                        } else {
                        	alert("WARNING", "Invalid Input", "Restaurant ID and Price cannot be empty.");
                        }
                    });
                    break;
                case "4":
                    singleInputForm("Cancel Order", "Order ID to cancel:", (Consumer<String>)(String orderId) -> {
                        if (orderId != null && !orderId.trim().isEmpty()) {
                            try {
                                Order o = db.getOrderById(orderId.trim());
                                if (o == null) throw new Exception("Order ID not found in the system.");
                                db.cancelOrder(c, o);
                                alert("INFORMATION", "Success", "Order cancelled successfully. Refund applied.");
                            } catch (Exception ex) {
                            	alert("ERROR", "Error", ex.getMessage());
                            }
                        }
                    });
                    break;
                case "5":
                   singleInputForm("Load Money", "Amount to load:",(Consumer<String>) (String amountStr) -> {
                        if (amountStr != null && !amountStr.trim().isEmpty()) {
                            try {
                                double amount = Double.parseDouble(amountStr.trim());
                                if(amount <= 0) {
                                	alert("WARNING", "Invalid Amount", "Please enter a positive amount.");
                                    return;
                                }
                                c.setBalance(c.getBalance() + amount); 
                                alert("INFORMATION", "Success", "Loaded " + amount + " to balance.");
                            } catch (NumberFormatException ex) {
                            	alert("ERROR", "Invalid Format", "Amount must be a valid number.");
                            }
                        }
                    });
                    break;
                case "6":
                    singleInputForm("Withdraw Money", "Amount to withdraw:", (Consumer<String>)(String amountStr) -> {
                        if (amountStr != null && !amountStr.trim().isEmpty()) {
                            try {
                                double amount = Double.parseDouble(amountStr.trim());
                                if(amount <= 0) {
                                	alert("WARNING", "Invalid Amount", "Please enter a positive amount.");
                                    return;
                                }
                                if (c.getBalance() >= amount) {
                                    c.setBalance(c.getBalance() - amount);
                                    alert("INFORMATION", "Success", "Withdrew " + amount + " from balance.");
                                } else {
                                	alert("ERROR", "Error", "Insufficient balance.");
                                }
                            } catch (NumberFormatException ex) {
                            	alert("ERROR", "Invalid Format", "Amount must be a valid number.");
                            }
                        }
                    });
                    break;
                case "7":
                    Collection rests = db.getCustomerRestaurantsMap().get(c.getCode());
                    if (rests == null || rests.isEmpty()) {
                    	alert("INFORMATION", "My Restaurants", "No restaurants found.");
                    } else {
                    	alert("INFORMATION", "My Restaurants", rests.toString());
                    }
                    break;
                case "8":
                    Collection premRests = db.getPremiumRestaurantsForCustomer(c);
                    if (premRests == null || premRests.isEmpty()) {
                    	alert("INFORMATION", "Premium Restaurants", "No premium restaurants found.");
                    } else {
                    	alert("INFORMATION", "Premium Restaurants", premRests.toString());
                    }
                    break;
                case "9":
                    singleInputForm("Search Restaurant", "Restaurant ID:", (Consumer<String>) (String restId) -> {
                        if(restId != null && !restId.trim().isEmpty()) {
                            try {
                                Restaurant r = db.getRestaurantById(restId.trim());
                                alert("INFORMATION", "Restaurant Details", r.toString());
                            } catch (RestaurantNotFoundException ex) {
                            	alert("ERROR", "Not Found", ex.getMessage());
                            }
                        }
                    });
                    break;
                case "0":
                    logout = true;
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    // printing the rider personal screen
    private void riderPersonalScreen(Rider r) {
        boolean logout = false;
        while (!logout) {
            System.out.println("\n--- Welcome Rider: " + r.getIdNumber() + " ---");
            long totalDelivCount = r.getHandledOrders().stream().filter(o -> o.getStatus().equals(Order.STATUS_DELIVERED)).count();
            System.out.println("Total Deliveries: " + totalDelivCount);
            
            System.out.println("\nMy Assigned Orders:");
            System.out.println(r.getOrdersDetails());

            System.out.println("\nActions:");
            System.out.println("1. View Active Order");
            System.out.println("2. Set Status: On The Way");
            System.out.println("3. Set Status: Delivered");
            System.out.println("4. Delivery History");
            System.out.println("0. Logout");
            System.out.print("Choice: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    List active = r.getHandledOrders().stream().filter(o -> o.getStatus().equals(Order.STATUS_ON_WAY)).collect(Collectors.toList());
                    alert("INFORMATION", "Active Orders", active.isEmpty() ? "No active orders found." : active.toString());
                    break;
                case "2":
                    singleInputForm("Set On Way", "Order ID:", (Consumer<String>)(String oId) -> {
                        if(oId != null && !oId.trim().isEmpty()) {
                            for (Order o : r.getHandledOrders()) {
                                if(o.getOrderId().equals(oId.trim())) {
                                    o.setStatus(Order.STATUS_ON_WAY);
                                    alert("INFORMATION", "Success", "Status updated to On The Way.");
                                    return;
                                }
                            }
                            alert("ERROR", "Not Found", "Order not found in your list.");
                        }
                    });
                    break;
                case "3":
                    singleInputForm("Set Delivered", "Order ID:",(Consumer<String>)  (String oId) -> {
                        if(oId != null && !oId.trim().isEmpty()) {
                            for (Order o : r.getHandledOrders()) {
                                if(o.getOrderId().equals(oId.trim())) {
                                    o.setStatus(Order.STATUS_DELIVERED);
                                    alert("INFORMATION", "Success", "Status updated to Delivered.");
                                    return;
                                }
                            }
                            alert("ERROR", "Not Found", "Order not found in your list.");
                        }
                    });
                    break;
                case "4":
                	alert("INFORMATION", "History", r.getOrdersDetails());
                    break;
                case "0":
                    logout = true;
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    // printing the rest admin personal screen
    private void restAdminPersonalScreen(RestAdmin ra) {
        boolean logout = false;
        while (!logout) {
            System.out.println("\n--- Welcome Restaurant Admin: " + ra.getManagerId() + " ---");
            System.out.println("1. My Restaurants");
            System.out.println("2. Add New Customer");
            System.out.println("3. Add New Order");
            System.out.println("4. Assign Rider");
            System.out.println("5. View All Orders");
            System.out.println("6. Show Open By Cuisine");
            System.out.println("7. Update Rating");
            System.out.println("8. Open/Close Restaurant");
            System.out.println("9. Basic Reports");
            System.out.println("0. Logout");
            System.out.print("Choice: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                	alert("INFORMATION", "Restaurants", db.getRestaurants().toString());
                    break;
                case "2":
                    addCustomerForm();
                    break;
                case "3":
                    addOrderForm();
                    break;
                case "4":
                    doubleInputForm("Assign Rider", "Order ID:", "Rider ID:",(BiConsumer<String, String>) (String orderId, String riderId) -> {
                        if (orderId != null && riderId != null && !orderId.trim().isEmpty() && !riderId.trim().isEmpty()) {
                            try {
                                Order o = db.getOrderById(orderId.trim());
                                Rider r = db.getRiderById(riderId.trim());
                                if (o == null || r == null) throw new Exception("Order or Rider not found!");
                                
                                db.assignOrderToRider(o, r);
                                alert("INFORMATION", "Success", "Rider assigned successfully!");
                            } catch (DeliveryPersonUnavailableException ex) {
                            	alert("ERROR", "Assignment Failed", ex.getMessage());
                            } catch (Exception ex) {
                            	alert("ERROR", "Error", ex.getMessage());
                            }
                        }
                    });
                    break;
                case "5":
                	alert("INFORMATION", "Orders", db.getOrders().toString());
                    break;
                case "6":
                   singleInputForm("Open Restaurants", "Cuisine Type:",(Consumer<String>)  (String cuisine) -> {
                        if(cuisine != null && !cuisine.trim().isEmpty()) {
                        	alert("INFORMATION", "Results", db.getOpenRestaurantsByCuisine(cuisine.trim()).toString());
                        }
                    });
                    break;
                case "7":
                	doubleInputForm("Update Rating", "Restaurant ID:", "New Rating (0-5):", (BiConsumer<String, String>) (restId, newRating) -> {
                	    if(restId != null && newRating != null && !restId.trim().isEmpty() && !newRating.trim().isEmpty()) {
                	        try {
                	            Restaurant rest = db.getRestaurantById(restId.trim());
                	            double rating = Double.parseDouble(newRating.trim());
                	            if(rating < 0 || rating > 5) {
                	            	alert("ERROR", "Invalid Value", "Rating must be between 0 and 5.");
                	                return;
                	            }
                	            rest.setRating(rating);
                	            alert("INFORMATION", "Success", "Rating updated successfully.");
                	        } catch (RestaurantNotFoundException ex) {
                	        	alert("ERROR", "Error", ex.getMessage());
                	        } catch (NumberFormatException ex) {
                	        	alert("ERROR", "Invalid Format", "Rating must be a valid number.");
                	        }
                	    }
                	});
                	break;                case "8":
                    singleInputForm("Open/Close", "Restaurant ID:", (Consumer<String>) (String restId) -> {
                        if(restId != null && !restId.trim().isEmpty()) {
                            try {
                                Restaurant rest = db.getRestaurantById(restId.trim());
                                rest.setIsOpen(!rest.getIsOpen());
                                String status = rest.getIsOpen() ? "Open" : "Closed";
                                alert("INFORMATION", "Success", "Restaurant is now " + status);
                            } catch (RestaurantNotFoundException ex) {
                            	alert("ERROR", "Error", ex.getMessage());
                            }
                        }
                    });
                    break;
                case "9":
                    String report = "System Report:\n" +
                                    "Total Customers: " + db.getCustomers().size() + "\n" +
                                    "Total Orders: " + db.getOrders().size() + "\n" +
                                    "Total Restaurants: " + db.getRestaurants().size();
                    alert("INFORMATION", "Reports", report);
                    break;
                case "0":
                    logout = true;
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }


	// printing the system admin screen
    private void systemAdminScreen() {
        boolean logout = false;
        while (!logout) {
            System.out.println("\n--- System Admin Dashboard ---");
            System.out.println("1. Customer Management");
            System.out.println("2. Restaurant Management");
            System.out.println("3. Order Management");
            System.out.println("4. Rider Management");
            System.out.println("5. Rest Admin Management");
            System.out.println("6. Reports & Sorting");
            System.out.println("7. Load Data");//to try to load again if it failed
            System.out.println("0. Logout");
            System.out.print("Choice: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    customerManagementScreen();
                    break;
                case "2":
                    restaurantManagementScreen();
                    break;
                case "3":
                    orderManagementScreen();
                    break;
                case "4":
                    riderManagementScreen();
                    break;
                case "5":
                    restAdminManagementScreen();
                    break;
                case "6":
                    reportsAndSortingScreen();
                    break;
                case "7":
                    try {
                        File checkFile = new File("customers.txt");
                        if (!checkFile.exists()) {
                        	alert("WARNING", "Warning", "No save files exist. Please Save first.");
                            break;
                        }

                        db.getCustomers().clear();
                        db.getCustomers().addAll(FileManager.loadCustomers());
                        
                        db.getRestaurants().clear();
                        db.getRestaurants().addAll(FileManager.loadRestaurants());
                        
                        db.getOrders().clear();
                        db.getOrders().addAll(FileManager.loadOrders(db.getRestaurants()));
                        
                        db.getRiders().clear();
                        db.getRiders().addAll(FileManager.loadRiders());
                        
                        db.getRestAdmins().clear();
                        db.getRestAdmins().addAll(FileManager.loadRestAdmins());
                        
                        db.resetAndRebuildMaps();
                        
                        alert("INFORMATION", "Success", "All data loaded successfully.");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        alert("ERROR", "Loading Error", "Failed to load data: " + ex.getMessage());
                    }
                    break;
                case "0":
                    logout = true;
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    // printing the customer management screen
    private void customerManagementScreen() {
        System.out.println("\n--- Customer Management ---");
        System.out.printf("%-10s %-15s %-15s %-10s\n", "Code", "First Name", "Last Name", "Balance");
        System.out.println("-------------------------------------------------------");
        for (Customer c : db.getCustomers()) {
            System.out.printf("%-10d %-15s %-15s %-10.2f\n", c.getCode(), c.getFirstName(), c.getLastName(), c.getBalance());
        }
        System.out.println("\nPress Enter to return to Dashboard...");
        scanner.nextLine();
    }

    // printing the restaurant management screen
    private void restaurantManagementScreen() {
        System.out.println("\n--- Restaurant Management ---");
        System.out.printf("%-5s %-20s %-15s %-10s\n", "ID", "Name", "Cuisine", "Rating");
        System.out.println("-------------------------------------------------------");
        for (Restaurant r : db.getRestaurants()) {
            System.out.printf("%-5s %-20s %-15s %-10.1f\n", r.getRestaurantId(), r.getName(), r.getRestType(), r.getRating());
        }
        
        System.out.println("\n1. Add New Restaurant");
        System.out.println("0. Back to Dashboard");
        System.out.print("Choice: ");
        String choice = scanner.nextLine().trim();
        
        if (choice.equals("1")) {
            addRestaurantForm();
        }
    }

    private void orderManagementScreen() {
        System.out.println("\n--- Order Management ---");
        System.out.printf("%-10s %-15s %-15s %-10s\n", "Order ID", "Customer Code", "Status", "Price");
        System.out.println("-------------------------------------------------------");
        for (Order o : db.getOrders()) {
            System.out.printf("%-10s %-15d %-15s %-10.2f\n", o.getOrderId(), o.getCustomerCode(), o.getStatus(), o.getFinalPrice());
        }
        System.out.println("\nPress Enter to return to Dashboard...");
        scanner.nextLine();
    }
    
    // printing the rider management screen
    private void riderManagementScreen() {
        System.out.println("\n--- Rider Management ---");
        System.out.printf("%-15s %-15s %-15s %-10s\n", "ID", "First Name", "Last Name", "Available");
        System.out.println("-------------------------------------------------------");
        for (Rider r : db.getRiders()) {
            System.out.printf("%-15s %-15s %-15s %-10b\n", r.getIdNumber(), r.getFirstName(), r.getLastName(), r.getIsAvailable());
        }
        System.out.println("\nPress Enter to return to Dashboard...");
        scanner.nextLine();
    }

    // printing the rest admin management screen
    private void restAdminManagementScreen() {
        System.out.println("\n--- Rest Admin Management ---");
        System.out.printf("%-15s %-15s %-15s %-15s\n", "Manager ID", "First Name", "Last Name", "Username");
        System.out.println("-------------------------------------------------------");
        for (RestAdmin ra : db.getRestAdmins()) {
            System.out.printf("%-15s %-15s %-15s %-15s\n", ra.getManagerId(), ra.getFirstName(), ra.getLastName(), ra.getUsername());
        }
        System.out.println("\nPress Enter to return to Dashboard...");
        scanner.nextLine();
    }

    // printing the reports and sorting screen
    private void reportsAndSortingScreen() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Reports & Sorting ---");
            System.out.println("1. Sort Customers by Balance");
            System.out.println("2. Sort Customers by Name");
            System.out.println("3. Sort Restaurants by Rating");
            System.out.println("4. Sort Orders by Price");
            System.out.println("5. Sort Orders by Date");
            System.out.println("6. Sort Riders by Deliveries");
            System.out.println("7. Show Open Restaurants");
            System.out.println("8. Show Premium Restaurants");
            System.out.println("9. Show Available Riders");
            System.out.println("10. Show Total Payments");
            System.out.println("0. Back to Dashboard");
            System.out.print("Choice: ");

            String choice = scanner.nextLine().trim();
            System.out.println("\n--- Results ---");
            switch (choice) {
                case "1":
                    List list1 = db.getCustomers();
                    Collections.sort(list1);
                    list1.forEach(System.out::println);
                    break;
                case "2":
                    List list2 = db.getCustomers();
                    list2.sort((c1, c2) -> ((Person) c1).getFirstName().compareToIgnoreCase(((Person) c2).getFirstName()));
                    list2.forEach(System.out::println);
                    break;
                case "3":
                    List list3 = db.getRestaurants(); 
                    list3.sort(new RestaurantRatingComparator());
                    list3.forEach(System.out::println);
                    break;
                case "4":
                    List list4 = db.getOrders();
                    list4.sort(new OrderPriceComparator());
                    list4.forEach(System.out::println);
                    break;
                case "5":
                    List list5 = db.getOrders();
                    list5.sort((o1, o2) -> {
                        if (((Order) o1).getOrderYear() != ((Order) o2).getOrderYear()) return Integer.compare(((Order) o1).getOrderYear(), ((Order) o2).getOrderYear());
                        if (((Order) o1).getOrderMonth() != ((Order) o2).getOrderMonth()) return Integer.compare(((Order) o1).getOrderMonth(), ((Order) o2).getOrderMonth());
                        return Integer.compare(((Order) o1).getOrderDay(), ((Order) o2).getOrderDay());
                    });
                    list5.forEach(System.out::println);
                    break;
                case "6":
                    List list6 = db.getRiders();
                    list6.sort((r1, r2) -> {
                        long d1 = ((Rider) r1).getHandledOrders().stream().filter(o -> o.getStatus().equals(Order.STATUS_DELIVERED)).count();
                        long d2 = ((Rider) r2).getHandledOrders().stream().filter(o -> o.getStatus().equals(Order.STATUS_DELIVERED)).count();
                        return Long.compare(d2, d1);
                    });
                    list6.forEach(System.out::println);
                    break;
                case "7":
                    List openRests = db.getRestaurants().stream().filter(Restaurant::getIsOpen).collect(Collectors.toList());
                    openRests.forEach(System.out::println);
                    break;
                case "8":
                    List premiums = db.getRestaurants().stream().filter(r -> r instanceof PremiumRestaurant).collect(Collectors.toList());
                    premiums.forEach(System.out::println);
                    break;
                case "9":
                    List available = db.getRiders().stream().filter(Rider::getIsAvailable).collect(Collectors.toList());
                    available.forEach(System.out::println);
                    break;
                case "10":
                    double total = db.getOrders().stream().mapToDouble(Order::getFinalPrice).sum();
                    System.out.printf("Total Payments in System: %.2f\n", total);
                    break;
                case "0":
                    back = true;
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    // help method (to not rep code) - 
    private void alert(String type, String title, String content) {
        System.out.println("\n[" + type + "] " + title);
        System.out.println(content);
        System.out.println("--------------------------------------------------");
    }
    
    private void singleInputForm(String title, String labelText, Consumer onSubmit) {
        System.out.println("\n--- " + title + " ---");
        System.out.print(labelText + " ");
        String input = scanner.nextLine();
        onSubmit.accept(input);
    }
    
    // printing the double InputForm
    private void doubleInputForm(String title, String label1, String label2, BiConsumer onSubmit) {
        System.out.println("\n--- " + title + " ---");
        System.out.print(label1 + " ");
        String txt1 = scanner.nextLine();
        System.out.print(label2 + " ");
        String txt2 = scanner.nextLine();
        onSubmit.accept(txt1, txt2);
    }
    
    // printing the adding customer form 
    private void addCustomerForm() {
        System.out.println("\n--- Add New Customer ---");
        try {
            System.out.print("Code: "); String code = scanner.nextLine();
            System.out.print("First Name: "); String fName = scanner.nextLine();
            System.out.print("Last Name: "); String lName = scanner.nextLine();
            System.out.print("Zip Code: "); String zip = scanner.nextLine();
            System.out.print("Address: "); String address = scanner.nextLine();
            System.out.print("Phone: "); String phone = scanner.nextLine();
            System.out.print("Email: "); String email = scanner.nextLine();
            System.out.print("Balance: "); String balance = scanner.nextLine();

            Customer newCust = new Customer(
                Integer.parseInt(code.trim()),
                fName.trim(),
                lName.trim(),
                zip.trim(),
                address.trim(),
                phone.trim(),
                email.trim(),
                Double.parseDouble(balance.trim())
            );
            db.addCustomer(newCust);
            alert("INFORMATION", "Success", "Customer added successfully!");
        } catch (NumberFormatException ex) {
            alert("ERROR", "Input Error", "Code and Balance must be valid numbers.");
        } catch (Exception ex) {
            alert("ERROR", "Error", ex.getMessage());
        }
    }
    
    // printing the adding order form
    private void addOrderForm() {
        System.out.println("\n--- Add New Order ---");
        try {
            System.out.print("Order ID: "); String orderId = scanner.nextLine();
            System.out.print("Customer Code: "); String custCode = scanner.nextLine();
            System.out.print("Price: "); String price = scanner.nextLine();
            
            Order o = new Order(orderId.trim(), Integer.parseInt(custCode.trim()), null, 1, 9, 2026);
            o.setFinalPrice(Double.parseDouble(price.trim()));
            o.setStatus("Created");
            db.addOrder(o);
            db.addOrderToCustomerMap(Integer.parseInt(custCode.trim()), o);
            alert("INFORMATION", "Success", "Order added successfully!");
        } catch (NumberFormatException ex) {
            alert("ERROR", "Input Error", "Customer Code and Price must be valid numbers.");
        } catch (Exception ex) {
            alert("ERROR", "Error", ex.getMessage());
        }
    }

    // printing the adding resatuant form
    private void addRestaurantForm() {
        System.out.println("\n--- Add New Restaurant ---");
        try {
            System.out.print("ID: "); String id = scanner.nextLine();
            System.out.print("Name: "); String name = scanner.nextLine();
            System.out.print("Cuisine: "); String type = scanner.nextLine();
            System.out.print("Rating (0-5): "); String rating = scanner.nextLine();
            System.out.print("Is Open (true/false): "); String isOpen = scanner.nextLine();
            System.out.print("Delivery Fee: "); String fee = scanner.nextLine();

            Restaurant r = new Restaurant(
                id.trim(),
                name.trim(),
                type.trim(),
                Double.parseDouble(rating.trim()),
                Boolean.parseBoolean(isOpen.trim()),
                Double.parseDouble(fee.trim())
            );
            db.addRestaurant(r);
            alert("INFORMATION", "Success", "Restaurant added!");
        } catch (Exception ex) {
           alert("ERROR", "Error", "Invalid inputs. Ensure Rating and Fee are numbers.");
        }
    }
    
    private void autoSave() {
        try {
            FileManager.saveCustomers(db.getCustomers());
            FileManager.saveRestaurants(db.getRestaurants());
            FileManager.saveOrders(db.getOrders());
            FileManager.saveRiders(db.getRiders());
            FileManager.saveRestAdmins(db.getRestAdmins());
            alert("INFORMATION", "Success", "All data saved successfully.");
        } catch (IOException ex) {
        	alert("ERROR", "Save Error", "Failed to save data. Please check file permissions.");
        }
    	
    }

    private void initData() {
        Customer c1 = new Customer(123, "Alice", "Smith", "12345", "Tel Aviv", "0501111111", "alice@gmail.com", 500.0);
        Customer c2 = new Customer(456, "Bob", "Cohen", "67890", "Haifa", "0502222222", "bob@walla.com", 200.0);
        Customer c3 = new Customer(101, "Charlie", "Levi", "11111", "Jerusalem", "0503333333", "charlie@gmail.com", 150.0);
        Customer c4 = new Customer(102, "Diana", "Prince", "22222", "Eilat", "0504444444", "diana@yahoo.com", 300.0);
        Customer c5 = new Customer(103, "Eve", "Malka", "33333", "Rishon LeZion", "0505555555", "eve@gmail.com", 450.0);
        Customer c6 = new Customer(104, "Frank", "Ocean", "44444", "Ashdod", "0506666666", "frank@gmail.com", 50.0);
        Customer c7 = new Customer(105, "Grace", "Hopper", "55555", "Netanya", "0507777777", "grace@tech.com", 1000.0);
        Customer c8 = new Customer(106, "Heidi", "Klum", "66666", "Tel Aviv", "0508888888", "heidi@model.com", 0.0);
        Customer c9 = new Customer(107, "Ivan", "Drago", "77777", "Haifa", "0509999999", "ivan@gym.com", 75.5);
        Customer c10 = new Customer(108, "Judy", "Garland", "88888", "Ramat Gan", "0501234567", "judy@star.com", 320.0);
        
        db.addCustomer(c1); db.addCustomer(c2); db.addCustomer(c3); db.addCustomer(c4); db.addCustomer(c5);
        db.addCustomer(c6); db.addCustomer(c7); db.addCustomer(c8); db.addCustomer(c9); db.addCustomer(c10);

        Restaurant r1 = new Restaurant("1", "Burger King", "Fast Food", 4.2, true, 15.0);
        Restaurant r2 = new Restaurant("2", "Sushi", "Japanese", 4.8, true, 25.0);
        Restaurant r3 = new Restaurant("3", "Pizza Hut", "Italian", 4.0, true, 12.0);
        Restaurant r4 = new Restaurant("4", "Shawarma HaNasi", "Middle Eastern", 4.5, true, 10.0);
        Restaurant r5 = new Restaurant("5", "Cafe Cafe", "Cafe", 3.9, false, 18.0);
        Restaurant r6 = new Restaurant("6", "El", "Meat", 4.7, true, 30.0);
        Restaurant r7 = new Restaurant("7", "Wok", "Asian", 4.3, true, 15.0);
        Restaurant r8 = new Restaurant("8", "Pasta Basta", "Italian", 4.6, true, 14.0);
        Restaurant r9 = new Restaurant("9", "Vegan reee", "Vegan", 4.9, true, 20.0);
        Restaurant r10 = new Restaurant("10", "Sweet", "Dessert", 4.1, false, 12.0);
        
        db.addRestaurant(r1); db.addRestaurant(r2); db.addRestaurant(r3); db.addRestaurant(r4); db.addRestaurant(r5);
        db.addRestaurant(r6); db.addRestaurant(r7); db.addRestaurant(r8); db.addRestaurant(r9); db.addRestaurant(r10);

        RestAdmin a1 = new RestAdmin("John", "Doe", "12345", "111111111", "admin1", "pass1");
        RestAdmin a2 = new RestAdmin("Jane", "Smith", "67890", "222222222", "admin2", "pass2");
        RestAdmin a3 = new RestAdmin("Mike", "Tyson", "11111", "333333333", "admin3", "pass3");
        RestAdmin a4 = new RestAdmin("Sarah", "Connor", "22222", "444444444", "admin4", "pass4");
        RestAdmin a5 = new RestAdmin("Tom", "Hanks", "33333", "555555555", "admin5", "pass5");    
        
        db.addRestAdmin(a1); db.addRestAdmin(a2); db.addRestAdmin(a3); db.addRestAdmin(a4); db.addRestAdmin(a5);

        Rider rd1 = new Rider("100000001", "Speedy", "Gonzales", "0521111111", "Scooter", true);
        Rider rd2 = new Rider("100000002", "Flash", "Gordon", "0522222222", "Bicycle", true);
        Rider rd3 = new Rider("100000003", "Mad", "Max", "0523333333", "Car", false);
        Rider rd4 = new Rider("100000004", "Ghost", "Rider", "0524444444", "Motorcycle", true);
        Rider rd5 = new Rider("100000005", "Tony", "Hawk", "0525555555", "Skateboard", true);
        Rider rd6 = new Rider("100000006", "Barry", "Allen", "0526666666", "Bicycle", false);
        Rider rd7 = new Rider("100000007", "Usain", "Bolt", "0527777777", "Scooter", true);
        Rider rd8 = new Rider("100000008", "Clark", "Kent", "0528888888", "Car", true);
        Rider rd9 = new Rider("100000009", "Bruce", "Wayne", "0529999999", "Motorcycle", false);
        Rider rd10 = new Rider("100000010", "Peter", "Parker", "0521231234", "Scooter", true);
        
        db.addRider(rd1); db.addRider(rd2); db.addRider(rd3); db.addRider(rd4); db.addRider(rd5);
        db.addRider(rd6); db.addRider(rd7); db.addRider(rd8); db.addRider(rd9); db.addRider(rd10);

        Order o1 = new Order("1", 123, r1, 1, 9, 2026); o1.setStatus("Delivered"); o1.setFinalPrice(120.0);
        Order o2 = new Order("2", 456, r2, 2, 9, 2026); o2.setStatus("On the way"); o2.setFinalPrice(85.5);
        Order o3 = new Order("3", 101, r3, 3, 9, 2026); o3.setStatus("Created"); o3.setFinalPrice(45.0);
        Order o4 = new Order("4", 102, r4, 4, 9, 2026); o4.setStatus("Delivered"); o4.setFinalPrice(210.0);
        Order o5 = new Order("5", 103, r5, 5, 9, 2026); o5.setStatus("Created"); o5.setFinalPrice(55.0);
        Order o6 = new Order("6", 104, r6, 6, 9, 2026); o6.setStatus("Delivered"); o6.setFinalPrice(150.0);
        Order o7 = new Order("7", 105, r7, 7, 9, 2026); o7.setStatus("On the way"); o7.setFinalPrice(300.0);
        Order o8 = new Order("8", 106, r8, 8, 9, 2026); o8.setStatus("Created"); o8.setFinalPrice(20.0);
        Order o9 = new Order("9", 107, r9, 9, 9, 2026); o9.setStatus("Delivered"); o9.setFinalPrice(95.0);
        Order o10 = new Order("10", 108, r10, 10, 9, 2026); o10.setStatus("Created"); o10.setFinalPrice(115.0);
        Order o11 = new Order("11", 123, r6, 11, 9, 2026); o11.setStatus("Delivered"); o11.setFinalPrice(250.0);
        Order o12 = new Order("12", 456, r7, 12, 9, 2026); o12.setStatus("On the way"); o12.setFinalPrice(70.0);
        Order o13 = new Order("13", 101, r8, 13, 9, 2026); o13.setStatus("Delivered"); o13.setFinalPrice(180.0);
        Order o14 = new Order("14", 102, r9, 14, 9, 2026); o14.setStatus("Created"); o14.setFinalPrice(90.0);
        Order o15 = new Order("15", 103, r10, 15, 9, 2026); o15.setStatus("Created"); o15.setFinalPrice(130.0);        
        
        db.addOrder(o1); db.addOrder(o2); db.addOrder(o3); db.addOrder(o4); db.addOrder(o5);
        db.addOrder(o6); db.addOrder(o7); db.addOrder(o8); db.addOrder(o9); db.addOrder(o10);
        db.addOrder(o11); db.addOrder(o12); db.addOrder(o13); db.addOrder(o14); db.addOrder(o15);

        db.addOrderToCustomerMap(123, o1); db.updateCustomerRestaurantMap(123, r1);
        db.addOrderToCustomerMap(456, o2); db.updateCustomerRestaurantMap(456, r2);
        db.addOrderToCustomerMap(101, o3); db.updateCustomerRestaurantMap(101, r3);
        db.addOrderToCustomerMap(102, o4); db.updateCustomerRestaurantMap(102, r4);
        db.addOrderToCustomerMap(103, o5); db.updateCustomerRestaurantMap(103, r5);
        db.addOrderToCustomerMap(104, o6); db.updateCustomerRestaurantMap(104, r6);
        db.addOrderToCustomerMap(105, o7); db.updateCustomerRestaurantMap(105, r7);
        db.addOrderToCustomerMap(106, o8); db.updateCustomerRestaurantMap(106, r8);
        db.addOrderToCustomerMap(107, o9); db.updateCustomerRestaurantMap(107, r9);
        db.addOrderToCustomerMap(108, o10); db.updateCustomerRestaurantMap(108, r10);
        db.addOrderToCustomerMap(123, o11); db.updateCustomerRestaurantMap(123, r6);
        db.addOrderToCustomerMap(456, o12); db.updateCustomerRestaurantMap(456, r7);
        db.addOrderToCustomerMap(101, o13); db.updateCustomerRestaurantMap(101, r8);
        db.addOrderToCustomerMap(102, o14); db.updateCustomerRestaurantMap(102, r9);
        db.addOrderToCustomerMap(103, o15); db.updateCustomerRestaurantMap(103, r10);

        rd1.getHandledOrders().add(o1);
        rd2.getHandledOrders().add(o2);
        rd3.getHandledOrders().add(o4);
        rd4.getHandledOrders().add(o6);
        rd5.getHandledOrders().add(o7);
        rd6.getHandledOrders().add(o9);
        rd7.getHandledOrders().add(o11);
        rd8.getHandledOrders().add(o12);
        rd9.getHandledOrders().add(o13);
    }

    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }
}