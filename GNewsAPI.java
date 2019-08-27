import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.stream.Collectors;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class GNewsAPI {
	
	private String URL = "https://gnews.io/api/v2/?q=politics&max=2&token=f420c4132ff215e6577cc94924f00b96";
	//full url of the api i wish to use
	
	private String myAPIurl = "https://gnews.io/api/v2/?";
	//first section of the url, the address of api i wish to access
	
	private String myApiToken = "&token=f420c4132ff215e6577cc94924f00b96";//“&APPID=yourTokenFromOpenWeatherMap”;
	//the last section of the url; the key that i got from the actual service
	
	
	public GNewsAPI(String u, String inp, String t) {//puts all parts of the url together and saves it for use
		myApiToken = t;
		myAPIurl = u;
		URL = myAPIurl + "q=" + inp + "&max=10" + myApiToken;
	}
	
	public GNewsAPI() {//default constructor
		URL = "";
	}
	
	public GNewsAPI(String topic) {//the constructor used most often, only takes user input and automatically connects
		URL = myAPIurl + "q=" + topic + "&max=10" + myApiToken;
	}
	
	public void setURL(String u) {
		URL = u;
	}
	
	public String getURL() {
		return URL;
	}
	
	public String connect() throws Exception {//function that makes the actual http request and recieves the json object, returns it as a string
		URL url = new URL(URL);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		//sets up the http request
		conn.setRequestMethod("GET");
		/*
		 * You can either make a get or post request here. 
		 * In this situation i made a get request because I want information
		 */
		BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		//opens up an input stream to read from the server, and the buffered reader reads the input stream and saves it to return
		String result = rd.lines().collect(Collectors.joining());//pulls the information and gets it into a string to return
		return result;
	}
	
	public String parseArticles(String jsonLine) {//parses the first article
		JsonElement jelement = new JsonParser().parse(jsonLine);//gets the entire json
	    JsonObject  jobject = jelement.getAsJsonObject();//converst the entire json element to a json object
	    JsonArray arr =  jobject.getAsJsonArray("articles");//the json is one giant array, so pull that
	    JsonObject obj = arr.get(0).getAsJsonObject();//pulls the first article of the array
	    
	    String result = "Title: " + obj.get("title")//gets basic information from the article
						+ ", Description: " + obj.get("desc")
						+ ", Link: " + obj.get("link");
	    return result;
	}
	/*
	 * this parse works the same as all of them
	 * gets the main article as a jsonelement through jsonparser
	 * converts the json element to a json objects
	 * --in this particular case, i am pulling a json array from the json object, and using throught the get____ function, pulling the array named after articles
	 * i then get the first element of the array, which is another json object, i then parse that to get what I want
	 */
	
	public JsonObject parseArticles(String jsonLine, int num) {//parses any given article by accessing the specified index in the json array
		JsonElement jelement = new JsonParser().parse(jsonLine);
	    JsonObject  jobject = jelement.getAsJsonObject();
	    JsonArray arr =  jobject.getAsJsonArray("articles");
	    JsonObject obj = arr.get(num).getAsJsonObject();
	    return obj;
	}
	/*
	 * this parse works the same as all of them
	 * gets the main article as a jsonelement through jsonparser
	 * converts the json element to a json objects
	 * --in this particular case, i am pulling a json array from the json object, and using throughout the get____ function, pulling the array named after articles
	 * i then get the first element of the array, which is another json object, i then parse that to get what I want
	 * I am also setting this function up so it pulls the article i want, through an integer parameter
	 */
	
	public String parseArticleTitle(String jsonLine, int num) {//gets the title from the article
		JsonObject obj = parseArticles(jsonLine, num);
		String result = "Title: " + obj.get("title");
		return result;
	}
	
	public String parseArticleDescription(String jsonLine, int num) {//gets the description from the article
		JsonObject obj = parseArticles(jsonLine, num);
		String result = "Description: " + obj.get("desc");
		return result;
	}
	
	public String parseArticleLink(String jsonLine, int num) {//gets the link
		JsonObject obj = parseArticles(jsonLine, num);
		String result = "Link: " + obj.get("link");
		return result;
	}
	
}


