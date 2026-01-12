package karanika.meallab.services;



import okhttp3.OkHttpClient;                             // Library that allows connection with the Internet
import okhttp3.Request;
import okhttp3.Response;
import com.fasterxml.jackson.databind.ObjectMapper;      // Library that allows deserialization for Json
import karanika.meallab.model.MealLabInfo;
import karanika.meallab.model.MealResponse;
import karanika.meallab.exception.MealLabException;

import java.io.IOException;                              // Exception for handling input/output issues
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class MealLabServices {
	
//---------------------------------------------Constants-----------------------------------------
//Define the API endpoints as constants to avoid typos.
   private static final String SEARCH_ENDPOINT = "search.php";
   private static final String FILTER_ENDPOINT = "filter.php";
   private static final String LOOKUP_ENDPOINT = "lookup.php";
   private static final String RANDOM_ENDPOINT = "random.php";
   
 
//---------------------------------------------Fields---------------------------------------------  

   private final String apiUrl;
   private final String apiKey;
  
   private final OkHttpClient client;
   private final ObjectMapper mapper;
   
    
//---------------Constructor: Initializes the service with the necessary configuration-------------
   
   public MealLabServices(String apiUrl, String apiKey) {
	   
	   // Validation: Prevent service startup without URL or API key
	   if (apiUrl == null || apiUrl.trim().isEmpty()) {
		   throw new IllegalArgumentException("API URL cannot be empty");
	   }
	   if (apiKey == null || apiKey.trim().isEmpty()) {
		   throw new IllegalArgumentException("API KEY cannot be empty");
	   }
	
	// Ensure the URL ends with "/" for proper concatenation
	   this.apiUrl = apiUrl.endsWith("/") ? apiUrl : apiUrl + "/";
	   this.apiKey = apiKey;
	   
	// Configure client timeouts (abort if connection exceeds 10 seconds)
	   this.client = new OkHttpClient.Builder()
			   .connectTimeout(10, TimeUnit.SECONDS)
			   .readTimeout(30, TimeUnit.SECONDS)
			   .build();
	   
	   this.mapper = new ObjectMapper();
       }
 
   
//-------------------------------------------Public Methods-------------------------------------------
	   
// FUNCTION 1: Search recipes by Name. Endpoint: search.php?s={name}

    public List<MealLabInfo> searchMealsByName(String mealName)
           throws IOException, MealLabException {
    	
    	//We check whether the parameter is valid
    	validateParameter(mealName, "Meal name");
    	
    	// Build the URL: .../search.php?s=Arrabiata
    	String url = buildUrl(SEARCH_ENDPOINT, "s", mealName);
    	
    	//Execute the request, expecting a list
    	return executeRequestForList(url);  	
    	}
    
// FUNCTION 2: Search recipes by Ingredient. Endpoint: filter.php?i={ingredient}
    
    public List<MealLabInfo> searchMealsByIngredient(String ingredient)
           throws IOException, MealLabException {
    	
    	validateParameter(ingredient, "ingredient");
    	
    	// Build the URL:.../filter.php?i=Chicken_Breast
    	String url = buildUrl(FILTER_ENDPOINT, "i", ingredient);
    	
    	return executeRequestForList(url);
        }
    
// FUNCTION 3: Retrieving full details based on the ID. Endpoint lookup.php?i={id}
    
    public MealLabInfo getMealDetailsById(String id)
           throws IOException, MealLabException {
    	
    	validateParameter(id, "Meal ID");
    	
    	// Build the URL:.../lookup.php?i=52772
    	String url = buildUrl(LOOKUP_ENDPOINT, "i", id);
    	
    	// Execute the request, expecting a single object
    	return executeRequestForSingleMeal(url);
        }
    
// FUNCTION 4: Retrieving a random recipe. Endpoint random.php
    
    public MealLabInfo getRandomMeal() 
           throws IOException, MealLabException {
    	
        // Here we have no parameters, we build the URL directly
    	String url = apiUrl + apiKey + "/" + RANDOM_ENDPOINT;
    	
    	MealLabInfo meal = executeRequestForSingleMeal(url);
    	
    	// Extra check: If for some reason the random returns null
        if (meal == null) {
        	throw new MealLabException("Random meal API returned no results", 0);
        }
        return meal;
    }
    

//--------------------------------Private Methods (Helper Methods)-------------------------------------
        
// Helper 1) Builds a safe URL with Encoding
        
     private String buildUrl(String endpoint, String paramName, String paramValue) {
          String encodedValue = URLEncoder.encode(paramValue.trim(), StandardCharsets.UTF_8);
          return apiUrl + apiKey + "/" + endpoint + "?" + paramName + "=" + encodedValue;
          }
     
// Helper 2) Executes Request and gives a List (List<MealLabInfo>). Is used for Search & Filter
     
     private List<MealLabInfo> executeRequestForList(String url)
             throws IOException, MealLabException {
    	 
    	 Request request = new Request.Builder().url(url).build();
    	 
    	 // try-with-resources: Shuts down the connection
    	 try (Response response = client.newCall(request).execute()) {
    		 validateResponse(response);
    		 
    		 String jsonString = response.body().string();
    		 MealResponse mealResponse = mapper.readValue(jsonString, MealResponse.class);
    		 
    		 // The getMeals() returns a safe list (never null)
    		 return mealResponse.getMeals();
    	     }
        }
     
// Helper 3) Executes Request and gives an Object (MealLabInfo). Is used for Lookup ID & Random
     
     private MealLabInfo executeRequestForSingleMeal(String url)
             throws IOException, MealLabException {
    	 
    	 Request request = new Request.Builder().url(url).build();
    	 
    	 try (Response response = client.newCall(request).execute()) {
    		 validateResponse(response);
    		 
    		 String jsonString = response.body().string();
    		 MealResponse mealResponse = mapper.readValue(jsonString, MealResponse.class);
    		 
    		 // The getFirstMeal() returns an Object or null
    		 return mealResponse.getFirstMeal();
    	     }
       }
     
// Helper 4) Checks if the API answered correctly
     
     private void validateResponse(Response response)
             throws MealLabException {
    	 // HTTP Status Code Check (404, 500 etc)
    	 if (!response.isSuccessful()) {
    		 throw new MealLabException("API request failed with code: " + response.code(), response.code());
    	     }
    	 
    	 // Check for an empty response body
    	 if (response.body() == null) {
    		 throw new MealLabException("API returned empty body", 0);
    	     }
         }
     
 // Helper 5) Checks that the entrance parameters are not empty
     
     private void validateParameter(String param, String paramName) {
    	 if (param == null || param.trim().isEmpty()) {
    		 throw new IllegalArgumentException(paramName + " cannot be null or empty");
    	 }
     }
        
  }
    
    	
    	
    
 
   
   




