package resturuants;

import java.util.Comparator;

public class RestaurantRatingComparator implements Comparator<Restaurant> {
	@Override
	public int compare(Restaurant r1, Restaurant r2) {
		return Double.compare(r2.getRating(), r1.getRating());
	}
}