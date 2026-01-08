package services;

import okhttp3.OkHttpClient;                             // Library that allows connection with the Internet
import com.fasterxml.jackson.databind.ObjectMapper;      // Library that allows deserialization for Json

public class MealDBService {
   private final String apiUrl;
   private final String apiKey;
   
   private final OkHttpClient client;
   private final ObjectMapper mapper;
   
    
   
   public MealDBService(String apiUrl, String apiKey) {
	   this.apiUrl = apiUrl;
	   this.apiKey = apiKey;
	   
	   this.client = new OkHttpClient();
	   this.mapper = new ObjectMapper();
   }
}
