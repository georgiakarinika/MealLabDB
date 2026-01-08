package karanika.meallab.model;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

// A wrapper class responsible for mapping the root-level JSON object returned by TheMealDB API.

@JsonIgnoreProperties(ignoreUnknown = true)
public class MealResponse {
	
	// The list of meals populated by Jackson from the JSON meals key.
    @JsonProperty("meals")
    private List<MealLabInfo> meals;
    
//----------------------------Default constructor for Jackson deserialization------------------------
    
    public MealResponse() {
    }
    
//------------------------Constructor for manual creation, useful for unit testing-------------------
    
    public MealResponse(List<MealLabInfo> meals) {
    	this.meals = meals;
    }
    
/** Defensive Programming Practice:
 *  TheMealDB API returns 'null' when no meals are found. The following method intercepts that null and
 *  returns an empty list instead preventing NullPointerException in the UI.    
 */
  
    public List<MealLabInfo> getMeals() {
    	if (meals == null) {
    		return Collections.emptyList();
    	}
    	return meals;
    }
    
 //-------------------------------Method to check if results exist----------------------------------
    
    public boolean hasMeals() {
    	return meals != null && !meals.isEmpty();
    }
    
//-----------------------Method which returns the count of meals found-----------------------------
    
    public int getMealCount() {
		if (meals == null) {
			return 0;
		}
		return meals.size();
	}
       
    
 //-------------------------------Method for single-result queries----------------------------------
    
    public MealLabInfo getFirstMeal() {
    	if (meals == null || meals.isEmpty()) {
    		return null;
    	}
    	return meals.get(0);
    }
    
}
