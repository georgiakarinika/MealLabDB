package test;


import static org.junit.jupiter.api.Assertions.*; 
import org.junit.jupiter.api.Test;              
import karanika.meallab.model.MealLabInfo;

class MealLabInfoTest { 

    //-------------------------------Test 1: tests the Constructor and Getters-------------------------
    @Test
    void testModelInitialization() { 
        System.out.println("Test 1: Initialization");
        
        // a) SetUp & Action. 
        MealLabInfo meal = new MealLabInfo(
                "52772",
                "Teriyaki Chicken Casserole",
                "Chicken",
                "Mix everything....",
                "Japanese",
                "http://thumbnail.url"
                );
        
        // b) Assertion 
        assertNotNull(meal);
        assertEquals("52772", meal.getId());
        assertEquals("Teriyaki Chicken Casserole", meal.getName());
        assertEquals("Japanese", meal.getArea());
        
        // c) Visual Verification
        System.out.println("Meal Created: " + meal.getName());
        System.out.println("Category: " + meal.getCategory());
    }

    //-----------------------------Test 2: tests the Dynamic Property Handling---------------------------
    @Test
    void testDynamicIngredientsLogic() {
        System.out.println("Test 2: Dynamic Ingredients Logic");
        
        MealLabInfo meal = new MealLabInfo("1", "Test Meal", "Test", "Test", "Test", "Test");
        
        // Simulation of the data from JSON
        meal.setDynamicProperty("strIngredient1","Soy Sauce");
        meal.setDynamicProperty("strMeasure1", "1 cup");
        
        meal.setDynamicProperty("strIngredient2","Water");
        meal.setDynamicProperty("strMeasure2", "1/2 cup");
        
        // Test with missing data
        meal.setDynamicProperty("strIngredient3", "");
        meal.setDynamicProperty("strIngredient4", null);
        meal.setDynamicProperty("strRandomKey", "IgnoredValue");
        
        // Assertions
        assertEquals(2, meal.getIngredients().size(), "Should have 2 ingredients"); // Προσοχή: Το μήνυμα μπαίνει ΣΤΟ ΤΕΛΟΣ στο JUnit 5
        assertEquals(2, meal.getMeasures().size(), "Should have 2 measures");
        
        // Visual
        System.out.println("Ingredients found: " + meal.getIngredients());
        System.out.println("Measures found: " + meal.getMeasures());
    }
    
    //---------------------------Test 3: tests the method getFormattedIngredients-------------------------
    @Test
    void testFormattedIngredientsString() {
        System.out.println("Test 3: Formatted String");
        
        MealLabInfo meal = new MealLabInfo("1", "Pancakes", "Breakfast", "Cook it", "American", "url");
        
        // Add ingredients
        meal.setDynamicProperty("strIngredient1", "Flour");
        meal.setDynamicProperty("strMeasure1", "200g");
        meal.setDynamicProperty("strIngredient2", "Eggs");     
        
        String result = meal.getFormattedIngredients();
        
        // Assertions
        assertNotNull(result);
        assertTrue(result.contains("200g Flour"));
        assertTrue(result.contains("Eggs"));
        
        // Visual
        System.out.println("Formatted Output:\n" + result);
    }
    
    
    //-------------------------------Test 4: test the validity (isValid)---------------------------------
    @Test
    void testValidationLogic() {
        System.out.println("Test 4: Validation Logic (isValid)");
        
        // Case 1: Valid Meal
        MealLabInfo validMeal = new MealLabInfo("1", "Valid", "Cat", "Instr", "Area", "Thumb");
        validMeal.setDynamicProperty("strIngredient1", "Salt");
        
        assertTrue(validMeal.isValid(), "Meal should be valid");
        System.out.println("Valid Meal Check: Passed");
        
        // Case 2: Invalid Meal (Missing ID)
        MealLabInfo noIdMeal = new MealLabInfo(null, "No ID", "Cat", "Instr", "Area", "Thumb");
        assertFalse(noIdMeal.isValid(), "Meal without ID should be invalid");
        System.out.println("Missing ID Check: Passed");
        
        // Case 3: Invalid Meal (No Ingredients)
        MealLabInfo noIngrMeal = new MealLabInfo("2", "No Food", "Cat", "Instr", "Area", "Thumb");
        assertFalse(noIngrMeal.isValid(), "Meal without ingredients should be invalid");
        System.out.println("Missing Ingredients Check: Passed");                
    }
}