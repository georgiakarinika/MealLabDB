package test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import karanika.meallab.exception.MealLabException;
import karanika.meallab.model.MealLabInfo;
import karanika.meallab.services.MealLabServices;


// Integration Tests for class MealLabServices. It checks the communication with the API
class MealLabServicesTest {
	
	private MealLabServices service;
	
	private static final String apiUrl = "https://www.themealdb.com/api/json/v1";
	private static final String apiKey = "1";
	
	
	// Setup: Runs before each test to ensure a clean state
	@BeforeEach
	void SetUp() {
		service = new MealLabServices(apiUrl, apiKey);
	}
	
//--------------------------------------Test 1: Search by Name--------------------------------------
	
@Test
@DisplayName("Test 1: Search by Name (Arrabiata)")
void testSearchMealsByName_Succes() throws IOException, MealLabException {
	System.out.println("Test 1: Searching for 'Arriabata'");
	
	//1. Action: Search for a known recipe
	List<MealLabInfo> results = service.searchMealsByName("Arrabiata");
	
	//2. Assertions: Verify the list is valid
	assertNotNull(results, "Result list should not be null");
	assertFalse(results.isEmpty(), "Result list should not be empty");
	
	//Verify specific data correctness
  	MealLabInfo meal = results.get(0);
  	assertEquals("Spicy Arrabiata Penne", meal.getName());
  	assertEquals("52771", meal.getId());
  	
  	//3. Visual Verification
  	System.out.println(" -> Success! Found" + meal.getName());
  	}

//---------------------------------Test 2: Search by Ingredient--------------------------------

@Test
@DisplayName("Test 2: Search by Ingredient (Chicken_Breast)")
void testSearchMealsByIngredient_Success() throws IOException, MealLabException {
	System.out.println("\nTest 2: Searching by ingredient 'Chicken_Breast'");
	
	
	//1. Action: Search by ingredient
	List<MealLabInfo> results = service.searchMealsByIngredient("Chicken_Breast");
	
	//2. Assertions
	assertNotNull(results, "Result list should not be null");
	assertFalse(results.isEmpty(), "Result list should not be empty");
	assertTrue(results.size() > 1, "Should find multiple recipes for chicken breast");
	
	//Verify that objects contain at least ID and Name
	// Note: Instructions may be null for this endpoint; as a result, they are not validated here.
    assertNotNull(results.get(0).getId());
    assertNotNull(results.get(0).getName());
    
    //3. Visual Verification
    System.out.println(" -> Success! Found" + results.size() + "recipes");
    }


//------------------------------------Test 3: Lookup By ID---------------------------------------

@Test
@DisplayName("Test 3: Get Details by ID (52772)")
void testGetMealDetailsById_Success() throws IOException, MealLabException {
	System.out.println("\nTest 3: Looking up ID 52772 (Teriyki Chicken)");
	
	//1. Action: Request details for a specific ID
	MealLabInfo meal = service.getMealDetailsById("52772");
	
	//2. Assertions
	assertNotNull(meal, "Meal object should not be null");
	assertEquals("Teriyaki Chicken Casserole", meal.getName());
	assertEquals("Chicken", meal.getCategory());
	
	//Check that instructions exist (proving that we got full details)
	assertNotNull(meal.getInstructions(), "Istructions should not be null");
	
	//Visual Verification 
	System.out.println(" -> Success! Retrieved details for: " + meal.getName());
	}

//----------------------------------Test 4: Random Meal---------------------------------------

@Test
@DisplayName("Test 4: Get Random Meal")
void testGetRandomMeal_Succsess() throws IOException, MealLabException {
	System.out.println("\nTest 4: Fetching a random meal");
	
	//1. Action
	MealLabInfo meal = service.getRandomMeal();
	
	//2. Assertions
	assertNotNull(meal, "Random meal should not be null");
	assertNotNull(meal.getId(), "Meal ID should not be null");
	assertNotNull(meal.getName(), "Meal Name should not be null");
	
	//3. Visual Verification
	System.out.println(" -> Success! Random meal is: " + meal.getName());
    }
	

//------------------------Test 5: Input Validation & Exception Handling--------------------------
	
@Test
@DisplayName("Test 5: Validations Checks")
void testInputValidation() {
	System.out.println("\nTest 5: Checking Exceptions");
	
	// CASE A: Empty Name Search
	assertThrows(IllegalArgumentException.class, () -> {
		service.searchMealsByName("");
	});
	
	// CASE B: Null ID lookup
	assertThrows(IllegalArgumentException.class, () -> {
		service.getMealDetailsById(null);
	});
	
	// CASE C: Null URL in Constructor
	assertThrows(IllegalArgumentException.class, () -> {
		new MealLabServices(null, apiKey);
	});
	
	System.out.println(" -> Success! Expected exceptions were thrown.");
	}
}
	




