package test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import karanika.meallab.model.MealLabInfo;
import karanika.meallab.model.MealResponse;

/**Unit tests for MealResponse wrapper class, this class verifies that the wrapper class handles 
 * the date from TheMealDB API correctly.
 */


class MealResponseTest {
	
	//Sample data objects to be used in tests
	private MealLabInfo sampleMeal1;
	private MealLabInfo sampleMeal2;
	private MealLabInfo sampleMeal3;
	
	// Set up method. Runs automatically before each test.
	
 @BeforeEach
 void setUp() {
	 sampleMeal1 = new MealLabInfo("52772", "Teriyaki Chicken", "Chicken", "Instr..", "Japanese", "url1");
	 sampleMeal2 = new MealLabInfo("52795", "Chicken Handi", "Chicken", "Instr..", "Indian", "url2");
	 sampleMeal3 = new MealLabInfo("52940", "Brown Stew", "Chicken", "Instr..", "Jamaican", "url3");
	 }

 //----------------------------Test 1: Null response (Defensive Programming)---------------------------
 
 @Test
 void testNullMealsList() {
	 System.out.println("Test 1: Handling NULL List from API");
	 
	 //a) SetUp & Action: Simulate API returning 'null'
	 MealResponse response = new MealResponse(null);
	 
	 
	 //b) Assertions: Verify we get an empty list instead of null
	 assertNotNull(response.getMeals(), "getMeals() should never return null");
	 assertTrue(response.getMeals().isEmpty(), "getMeals() should return an empty list");
	 assertFalse(response.hasMeals(), "hasMeals() should return false");
	 assertEquals(0, response.getMealCount(), "Count should be 0");
	 assertNull(response.getFirstMeal(), "getFirstMeal() should return null");
	 
	 //d) Visual Verification
	 System.out.println(" -> Input was NULL");
	 System.out.println(" -> Output is: " + response.getMeals() + "(Safe Empty List");
	 System.out.println(" -> Test Passed.\n");	 
}

 
//------------------------------------Test 2: Empty List Scenario-----------------------------------------
 
 @Test
 void testEmptyMealsList() {
	 System.out.println("Test 2: Handling Empty List");
	 
	 //a) SetUp & Action: Create response with an empty ArrayList
	 MealResponse response = new MealResponse(new ArrayList<>());
	 
	 //b) Assertions
	 assertNotNull(response.getMeals());
	 assertTrue(response.getMeals().isEmpty());
	 assertFalse(response.hasMeals());
	 assertEquals(0, response.getMealCount());
	 
	 //c) Visual Verification
	 System.out.println(" -> Input was Empty List");
	 System.out.println(" -> getMealsCount() returned: " + response.getMealCount());
	 System.out.println(" -> getMeals() content:" + response.getMeals());
 }
 
 
 //----------------------------------Test 3: Single Meal Response---------------------------------------
 
 @Test
 void testSingleMealResponse() {
	 System.out.println("Test 3: Single Meal Response");
	 
	 //a) SetU & Action
	 List<MealLabInfo> meals = new ArrayList<>();
	 meals.add(sampleMeal1);
	 MealResponse response = new MealResponse(meals);
	 
	 //b) Assertions
	 assertTrue(response.hasMeals());
	 assertEquals(1, response.getMealCount(), "Should contain exactly 1 meal");
	 
	 //Check getFirstMeal specific logic
	 assertNotNull(response.getFirstMeal());
	 assertEquals("52772", response.getFirstMeal().getId());
	 
	 //c) Visual Verification
	 System.out.println(" ->Found 1 meal: " + response.getFirstMeal().getName());
	  }
 
 
 //------------------------------Test 4: Multiple Meals & Integrity---------------------------------
 
 @Test
 void testMultipleMealsResponse() {
	 System.out.println("Test 4: Multiple Meals & Integrity");
	 
	 //a) SetUp & Action 
	 List<MealLabInfo> meals = new ArrayList<>();
	 meals.add(sampleMeal1);
	 meals.add(sampleMeal2);
	 meals.add(sampleMeal3);
	 MealResponse response = new MealResponse(meals);
 
 
    //b) Assertions
    assertEquals(3, response.getMealCount(), "Should count 3 meals");
    assertEquals("52772", response.getFirstMeal().getId(), "First meal should be Teriyaki Chicken");
    assertEquals("52940", response.getMeals().get(2).getId(), "Last meal should be Brown Stew");
    
    //c) Visual Verification
    System.out.println(" -> Total meals found:" + response.getMealCount());
    System.out.println(" -> First meal: " + response.getFirstMeal().getName());
    System.out.println(" -> Last meal: " + response.getMeals().get(2).getName());
 }
 
//------------------------------Test 5: Default Constructor (Jackson)-----------------------------
 
 @Test
 void testDefaultConstructor() {
	 System.out.println("Test 5: Default Constructor (Jackson Compatibility");
	 
	 //a) SetUp & Action
	 MealResponse response = new MealResponse();
	 
	 //b) Assertions
	 assertNotNull(response.getMeals()); // Should be empty list, not null
	 assertEquals(0, response.getMealCount());
	 
	 //c) Visual Verification
	 System.out.println(" ->  Default constructor created safe state.");
  }
}
