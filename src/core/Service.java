package core;

public class Service {
	// a Email is valid if his in the format ().gmail(.com)\(.co.il)
	public static boolean isValidEmail(String s) {
		if (s == null) {
			return false;
		}

		int start = -1;
		for (int i = 0; i < s.length(); i++) {
			if (s.charAt(i) == '@')
				start = i;
		}

		return start != -1;
	}

	public static boolean onlyNumbers(String s) {

		if (s == null)
			return false;

		// checking if its only numbers
		for (int i = 0; i < s.length(); i++) {
			if (s.charAt(i) < '0' || s.charAt(i) > '9') {
				return false;
			}

		}
		return true;
	}

	private static boolean onlyLetters(String s) {
		if (s == null)
			return false;
		
		// checking if its only letters
		for (int i = 0; i < s.length(); i++) {
			char curr =s.charAt(i);
			if ((curr < 'a' || curr > 'z') && (curr < 'A' || curr > 'Z')) {
				return false;
			}

		}
		return true;

	}
	// a Phone is valid if his in the format 05-------- and have only numbers.
	public static boolean isValidPhone(String s) {
		if (s == null)
			return false;
		return s.startsWith("05") && s.length() == 10 && Service.onlyNumbers(s);
	}
	
	//a ID have 9 numbers and only numbers.
	public static boolean isValidID(String s) {
		if(s == null) {
			return false;
		}
		return Service.onlyNumbers(s) && s.length() == 9;
	}

	//its a valid name if it have only letters in it
	public static boolean isValidName(String s) {
		return Service.onlyLetters(s);
	}
}
