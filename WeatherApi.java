import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.stream.Collectors;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class WeatherApi {
	
	private String URL = "http://api.openweathermap.org/data/2.5/weather?q=Dallas&APPID=0b995111e3b680814f9d176391169ad1";
	//full url of the api i wish to use. 
	private String myAPIurl = "http://api.openweathermap.org/data/2.5/weather?q=";
	//first section of the string, the actual address of the api
	private String myApiToken = "&APPID=0b995111e3b680814f9d176391169ad1";//“&APPID=yourTokenFromOpenWeatherMap”;
	//the last section of the url; the key that i got from the actual service
	
	
	public WeatherApi(String u, String inp, String t) {
		URL = u + inp + t;
		myApiToken = t;
		myAPIurl = u;
	}
	
	public WeatherApi() {//default constructor
		URL = "";
	}
	
	public WeatherApi(String city) {//the main constructor used, which takes the user input and builds the api from the defaults
		URL = myAPIurl + city + myApiToken;
	}
	
	public void setURL(String u) {
		URL = u;
	}
	
	public String getURL() {
		return URL;
	}
	
	public String connect() throws Exception {//function that makes the actual http request and receives the json object, returns it as a string
		URL url = new URL(URL);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection(); //sets up the http connection
		conn.setRequestMethod("GET");
		/*
		 * The GET method is used to retrieve information from the given server using a given URI.
		 * Requests using GET should only retrieve data and should have no other effect on the data.
		 */
		BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		//buffered reader takes a the input sequence of data and stores it in the buffer
		String result = rd.lines().collect(Collectors.joining());
		return result;
	}
	
	public String parseMain(String jsonLine) {//gets a variety of information from the api in a readable format
		JsonElement jelement = new JsonParser().parse(jsonLine);
	    JsonObject  jobject = jelement.getAsJsonObject();
	    jobject = jobject.getAsJsonObject("main");
	    
	    String result = "Temp: " + parseTemp(jsonLine) +
	    				", " + parseHumidity(jsonLine) +
	    				", " + parseWind(jsonLine) + 
	    				", " + parseClouds(jsonLine);
	    return result;
	}
	
	public String parseWeather(String jsonLine) throws Exception {//gets the weather direct from the api
		JsonElement jelement = new JsonParser().parse(jsonLine);
	    JsonObject  jobject = jelement.getAsJsonObject();
	    JsonArray arr =  jobject.getAsJsonArray("weather");
	    JsonObject obj = arr.get(0).getAsJsonObject();
	    
	    String result = "Main: " + obj.get("main")
	    				+ ", Description: " + obj.get("description");
	    return result;
	}
	
	public String parseCoord(String jsonLine) {//gets the coordinates of the json
	    JsonElement jelement = new JsonParser().parse(jsonLine);
	    JsonObject  jobject = jelement.getAsJsonObject();
	    jobject = jobject.getAsJsonObject("coord");
	    String result = "Longitude: " + jobject.get("lon").getAsString() + 
	    		"\nLatitude: " + jobject.get("lat").getAsString();
	    return result;
	}
	/*
	 * this parse works the same as all of them
	 * gets the main weather as a jsonelement through jsonparser
	 * converts the json element to a json object
	 * I then grab what i want from the main object of the body as a jsonobject named coord-the coordinates
	 * I take the coordinate object, and from the grab the latitude and longitude from the coordinate object, and print
	 */
	
	public String parseTemp(String jsonLine) {//gets the temperature
	    JsonElement jelement = new JsonParser().parse(jsonLine);
	    JsonObject  jobject = jelement.getAsJsonObject();
	    jobject = jobject.getAsJsonObject("main");
	    String result = jobject.get("temp").getAsString();
	    Double resultAsDouble = Double.valueOf(result);
	    
	    //setup so that it can return only two decimals
	    //also returns it in Farenheit, rather than Kelvin
	    DecimalFormat df = new DecimalFormat("#.##");
	    df.setRoundingMode(RoundingMode.CEILING);
	    resultAsDouble = ((resultAsDouble - 273.15) * (9.0/5.0) + 32.0);
	    Double returnMe = resultAsDouble.doubleValue();
	    
	    return df.format(returnMe);
	}
	
	public String parseWind(String jsonLine) {//gets the wind
		JsonElement jelement = new JsonParser().parse(jsonLine);
	    JsonObject  jobject = jelement.getAsJsonObject();
	    jobject = jobject.getAsJsonObject("wind");
	    
	    String result = "Wind Speed: " + jobject.get("speed").getAsString();
	    return result;
	}
	
	public String parseClouds(String jsonLine) {//gets the cloud
		JsonElement jelement = new JsonParser().parse(jsonLine);
	    JsonObject  jobject = jelement.getAsJsonObject();
	    jobject = jobject.getAsJsonObject("clouds");
	    
	    String result = "Clouds: " + jobject.get("all").getAsString();
	    return result;
	}
	
	public String parseHumidity(String jsonLine) {//gets the humidity
		JsonElement jelement = new JsonParser().parse(jsonLine);
	    JsonObject  jobject = jelement.getAsJsonObject();
	    jobject = jobject.getAsJsonObject("main");
	    
	    String result = "Humidity: " + jobject.get("humidity").getAsString();
	    return result;
	}
	
	
}



