package karanika.meallab.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


// This Class is used by Jackson for mapping JSON responses from TheMealDB API to Java Objects.
 @JsonIgnoreProperties (ignoreUnknown = true)
public class MealLabInfo {
	private final String id;                     // Meal unique identifier from API
	private final String name;                  // Meal name
	private final String category;              // Meal category (e.g. Vegetarian)
	private final String instructions;          // Step-by-step cooking instructions
	private final String area;                  // Geographic origin (e.g. Italian, Chinese)
	private final String thumbnail;            // URL of the meal thumbnail image
	
	
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
	    
   }
	
	public String getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public String getCategory() {
		return category;
	}
	
	public String getInstructions() {
		return instructions;
	}
	
	public String getArea() {
		return area;
	}
	
	public String getThumbnail() {
		return thumbnail;
	}
	
	
	
	
	public String toString() {
		return "MealLabInfo{" +
	            "id=" + id + "'\n" +
				", name='" + name + "'\n" +
	            ", category='" + category + "'\n" +
				", instructions='" + instructions + "'\n" +
	            ", area='" + area + "'\n" +
				", thumbnail='" + thumbnail + "'\n" +	          
	            '}';
	}
		
}



