package karanika.meallab.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;



// This Class is used by Jackson for mapping JSON responses from TheMealDB API to Java Objects.
// Core fields are immutable (final) and set via constructor.
// Ingredient and measure lists are mutable and populated via @JsonAnySetter.

 @JsonIgnoreProperties (ignoreUnknown = true)
public class MealLabInfo {
//-------------------------------- Immutable core fields.---------------------------------
	 
	private final String id;                     // Meal unique identifier from API
	private final String name;                  // Meal name
	private final String category;              // Meal category (e.g. Vegetarian)
	private final String instructions;          // Step-by-step cooking instructions
	private final String area;                  // Geographic origin (e.g. Italian, Chinese)
	private final String thumbnail;            // URL of the meal thumbnail image

//---------------------------Mutable Ingredients and measure fields.----------------------
	
	private final List<String> ingredients;      // populated from strIngredients1-20 via @JsonAnySetter
	private final List<String> measures;         // populated from strMeasures1-20 via @JsonAnySetter
	
//------------------------------------Constructor------------------------------------------	
	
	@JsonCreator
	public MealLabInfo(
			           @JsonProperty ("idMeal") String id,
			           @JsonProperty ("strMeal") String name, 
			           @JsonProperty ("strCategory") String category, 
			           @JsonProperty ("strInstructions") String instructions, 
			           @JsonProperty ("strArea") String area, 
			           @JsonProperty ("strMealThumb") String thumbnail) {
		
	     this.id = id;
	     this.name = name;
	     this.category = category;
	     this.instructions = instructions;
	     this.area = area;
	     this.thumbnail = thumbnail;
	     
	     // Initialize empty lists for dynamic population
	     this.ingredients = new ArrayList<>();
	     this.measures = new ArrayList<>();
	    }
//---------------------------------Dynamic Property Handler------------------------------------
	
	
	@JsonAnySetter
	public void setDynamicProperty(String key, String value) {
		// Handles ingredients fields (strIngredient1 - strIngredient20)
		if(key != null && key.startsWith("strIngredient")) {
			if(value != null && !value.trim().isEmpty()) {
				ingredients.add(value.trim());
			}
		}
		// Handles measure fields (strMeasure1 - strMeasure20)
		else if(key != null && key.startsWith("strMeasure")) {
			if(value != null && !value.trim().isEmpty()) {
				measures.add(value.trim());
			}
		}
		
	}
	
//---------------------------------------Getters------------------------------------------------	
	
	public String getId() {                     // Gets the meal's unique identifier
		return id;
	}
	
	public String getName() {                  // Gets the meal's name
		return name;
	}
	
	public String getCategory() {              // Gets the meal's category
		return category;
	}
	
	public String getInstructions() {         // Gets the meal's instructions
		return instructions;
	}
	
	public String getArea() {                 // Gets the meal's geographic origin
		return area;
	}
	
	public String getThumbnail() {            // Gets the meal's thumbnail image URL
		return thumbnail;
	}
	
	public List<String> getIngredients() {            // Gets the list of ingredients
		return new ArrayList<>(ingredients);
	}
	
	public List<String> getMeasures() {              // Gets the list of measures
		return new ArrayList<>(measures);
	}
	
//-------------------------------------Utility Methods--------------------------------------
	
	public int getIngredientsCount() {              //Gets the number of ingredients in this meal
		return ingredients.size();
	}
	
	// Gets a formatted string combining ingredients with their measurements.
	public String getFormattedIngredients() {            
		StringBuilder sb = new StringBuilder();
		int size = ingredients.size();
	    
		for (int i = 0; i < size; i++) {
			if (i > 0) {
				sb.append("\n");
			}
			String ingredient = ingredients.get(i);
			String measure = (i < measures.size()) ? measures.get(i) : "";
			
			if(!measure.isEmpty()) {
				sb.append(measure).append(" ");
			}
			sb.append(ingredient);
		}
		
		return sb.toString();	
	}
	
//----------------This method checks if the recipe is valid to be appeared on the UI-----------
	
	public boolean isValid() {
		// Checks the basics (id, name etc)
		boolean hasBasicInfo = id != null && !id.trim().isEmpty() &&
				               name != null && !name.trim().isEmpty() &&
				               thumbnail != null && !thumbnail.trim().isEmpty() &&
				               instructions != null && !instructions.trim().isEmpty();
		
		//Checks the ingredients
		boolean hasIngredients = ingredients != null && !ingredients.isEmpty();
		
		return hasBasicInfo && hasIngredients;
	}
	
//------------------------Returns a formatted string representation of the meal----------------	
	
	public String toString() {
		return "MealLabInfo{" +
	            "id=" + id + "'\n" +
				", name='" + name + "'\n" +
	            ", category='" + category + "'\n" +
				", instructions='" + instructions + "'\n" +
	            ", area='" + area + "'\n" +
				", ingredients=" + ingredients.size() + "items" +
	            ",measures=" + measures.size() + "items" +
				", thumbnail='" + thumbnail + "'\n" +	          
	            '}';
	}
		
}



