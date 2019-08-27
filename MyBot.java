import org.jibble.pircbot.PircBot;


public class MyBot extends PircBot{
	//extends allows you to use the basic functionality of pircbot,
	public MyBot() {
		this.setName("WeatherNewsBot");//establishes the name
	}
	
	public void onJoin(String channel, String sender, String login, String hostname) {//messages the bot sends upon joining the server
		sendMessage(channel, "Hello! Welcome to my bot. If you wish to get the weather, Enter a city or zipcode then a request separated by a comma enclosed in dashes");
		sendMessage(channel, "For example: [Dallas,weather] , or [71203,temp]");
		sendMessage(channel, "If you want to get any news regarding a specific topic, I can pull up to 10 news articles about the topic");
		sendMessage(channel, "To get that, enter the topic you want followed by how many articles you want, enclosed by dashes");
		sendMessage(channel, "For example: [politics,3] , or [Computer Science,10]");
		sendMessage(channel, "Thank you!");
	}
	
	public String[] parseMessage(String message) {//parses the user message
		String[] arr = new String[3];
		/*
		 * an array of three strings that holds"
		 * 1:User request of temp, weather, or the number of articles that they want
		 * 2:The user's topic, either a city, zipcode, or news topic
		 * 3:If the user wants news, this holds the string "news" so that it prints properly
		 */
		String city = "";
		String request = "";
		String intial = "";
		String topic = "";
		if(message.contains("[")){//only reads user input surrounded by brackets
			String hold = message;
			int start = message.indexOf("[");
			hold = hold.substring(start + 1);
			if(hold.contains("]")) {//breaks off the user input to that which is only surrounded by brackets
				int end = hold.indexOf("]");
				hold = hold.substring(0, end);
			}
			if(hold.contains(",")) {
				int first = hold.indexOf(",");
				intial = hold.substring(0, first);
				request = hold.substring(first + 1, hold.length());
				//breaks the string into two components, user topic and specific request
				
				if(request.charAt(0) == ' ') request = request.substring(1, request.length());//in case the user puts a space after the comma
				
				if(request.equalsIgnoreCase("temp") || request.equalsIgnoreCase("weather")) {//if its the weather api
					
					city = intial;
					
					if(Character.isDigit(city.charAt(0))) city = city + ",us";//if the user inputted a zipcode, appends us to the end so the api prints only us zipcodes
					
					arr[1] = city;//saves the city
					
					
				}else if(Character.isDigit(request.charAt(0)) && Integer.parseInt(request) <= 10 && Integer.parseInt(request) > 0){//if its the news api
					topic = intial;
					if(topic.contains(" ")) {//removes the spaces from the user request so that the api can receive the request properly
						for(int i = 0; i < topic.length(); i++){
							if(topic.charAt(i) == ' ') {
								topic = topic.substring(0,i) + topic.substring(i+1, topic.length());
							}
						}
					}
					arr[1] = topic;//saves the topic
					arr[2] = "news";//sets the third element in the array to news so that its printed properly
				}
			}
		}else{//ensures that the additional functionality can be reached if the user didnt make a specific request
			request = "";
			arr[1] = "";
			arr[2] = "";
		}
		arr[0] = request;//saves the request
		return arr;
	}
	
	public void onRequest(String message) {
		//initialization of variables
				String result = "";
				WeatherApi weatherAPI = null;//initializes the weatherapi
				GNewsAPI newsAPI = null;//initializes the newsapi
					
				String[] request = parseMessage(message);
				//parses the user input and puts what they want in the first element of the array, and the specific request in the second element
				
				if(request[0].equalsIgnoreCase("temp") || request[0].equalsIgnoreCase("weather")) {//if the user is requesting something weather related
					weatherAPI = new WeatherApi(request[1]);
					try {//try catch block in case the http request fails for whatever reason
						result = weatherAPI.connect();
					} catch (Exception e) {
						e.printStackTrace();//goes up the stack to see where the error occurred
					}
				}else {//if the used is requesting something news related
					newsAPI = new GNewsAPI(request[1]);
					try {//try catch block in case the http request fails for whatever reason
						result = newsAPI.connect();
					} catch (Exception e) {
						e.printStackTrace();//goes up the stack to see where the error occurred
					}
				}
				
				//put a try catch block around this in case the json is null
				
				//request 0 request: temp or weather or number of articles
				//request 1 city or topic
				//request 2 if there is news
				try {//in case the user's request returns a null object, breaks to the exception
					if(message.equalsIgnoreCase("time")) {//if the user requests the time
						String time = new java.util.Date().toString();
						System.out.println(": The time is now " + time);
					}else if(request[0].equalsIgnoreCase("temp")) {//if the user request the temperature
						String temp = weatherAPI.parseTemp(result);
						System.out.println(": The temperature in/at " + request[1] + " is now " + temp + " F");
					} else if(request[0].equalsIgnoreCase("coord")) {//if the user requests the coordinates of a city
						String coord = weatherAPI.parseCoord(result);
						System.out.println(": The coordinates of " + request[1] + " is:" + coord);
					}else if(request[0].equalsIgnoreCase("weather")){//if the user requests the weather
						String weather = weatherAPI.parseMain(result);
						System.out.println(": Weather at " + request[1] + ": " + weather);
					}else if(request[2].equalsIgnoreCase("news")) {//if the user requests news
						System.out.println(": News regarding your topic");
						String printMe = "st";
						for(int i = 0; i < Integer.parseInt(request[0]); i++) {//the array prints strings, but if this function is entered, request[0] is a string holding a number, so i use Integer.parseInt to pull it as a string for the for loop
							if(i+1 == 2) printMe = "nd";//so that printing is correct syntactically-English wise
							if(i+1 == 3) printMe = "rd";
							if(i+1 == 4) printMe = "th";
							System.out.println(": " + (i+1) + printMe + " : " + newsAPI.parseArticleTitle(result, i));
							System.out.println(": " + (i+1) + printMe + " : " + newsAPI.parseArticleDescription(result, i));
							System.out.println(": " + (i+1) + printMe + " : " + newsAPI.parseArticleLink(result, i));
							System.out.println(" ");//makes printing nicer by adding an extra white line
						}
					}else if(message.equalsIgnoreCase("Help")) {//prints commands
						System.out.println(": [topic,request] , and either type a zip code and city followed by the requests weather or temp, or a topic followed by the number of articles you want");
					}else {
						System.out.println(": I'm sorry, I didn't get that!");//in case the bot cannot parse the user's input
					}
				} catch (Exception e) {//in case the json object is null due to the user's request, print to make sure they clarify
					//e.printStackTrace();
					System.out.println(": I'm sorry, I didn't get that! Make sure you surround your question in brackets!");
					System.out.println(": For example: [Dallas, temp] or [politics,3]");
					System.out.println(": If you need help just type help!");
				}
	}
	
	//server run
	public void onMessage(String channel, String sender, String login, String hostname, String message){
		//initialization of variables
		String result = "";
		WeatherApi weatherAPI = null;//initializes the weatherapi
		GNewsAPI newsAPI = null;//initializes the newsapi
			
		String[] request = parseMessage(message);
		//parses the user input and puts what they want in the first element of the array, and the specific request in the second element
		
		if(request[0].equalsIgnoreCase("temp") || request[0].equalsIgnoreCase("weather")) {//if the user is requesting something weather related
			weatherAPI = new WeatherApi(request[1]);
			try {//try catch block in case the http request fails for whatever reason
				result = weatherAPI.connect();
			} catch (Exception e) {
				e.printStackTrace();//goes up the stack to see where the error occurred
			}
		}else {//if the used is requesting something news related
			newsAPI = new GNewsAPI(request[1]);
			try {//try catch block in case the http request fails for whatever reason
				result = newsAPI.connect();
			} catch (Exception e) {
				e.printStackTrace();//goes up the stack to see where the error occurred
			}
		}
		
		//put a try catch block around this in case the json is null
		
		//request 0 request: temp or weather or number of articles
		//request 1 city or topic
		//request 2 if there is news
		try {//in case the user's request returns a null object, breaks to the exception
			if(message.equalsIgnoreCase("time")) {//if the user requests the time
				String time = new java.util.Date().toString();
				sendMessage(channel, sender + ": The time is now " + time);
			}else if(request[0].equalsIgnoreCase("temp")) {//if the user request the temperature
				String temp = weatherAPI.parseTemp(result);
				sendMessage(channel, sender + ": The temperature in/at " + request[1] + " is now " + temp + " F");
			} else if(request[0].equalsIgnoreCase("coord")) {//if the user requests the coordinates of a city
				String coord = weatherAPI.parseCoord(result);
				sendMessage(channel, sender + ": The coordinates of " + request[1] + " is:" + coord);
			}else if(request[0].equalsIgnoreCase("weather")){//if the user requests the weather
				String weather = weatherAPI.parseMain(result);
				sendMessage(channel, sender + ": Weather at " + request[1] + ": " + weather);
			}else if(request[2].equalsIgnoreCase("news")) {//if the user requests news
				sendMessage(channel, sender + ": News regarding your topic");
				String printMe = "st";
				for(int i = 0; i < Integer.parseInt(request[0]); i++) {//the array prints strings, but if this function is entered, request[0] is a string holding a number, so i use Integer.parseInt to pull it as a string for the for loop
					if(i+1 == 2) printMe = "nd";//so that printing is correct syntactically-English wise
					if(i+1 == 3) printMe = "rd";
					if(i+1 == 4) printMe = "th";
					sendMessage(channel, sender + ": " + (i+1) + printMe + " : " + newsAPI.parseArticleTitle(result, i));
					sendMessage(channel, sender + ": " + (i+1) + printMe + " : " + newsAPI.parseArticleDescription(result, i));
					sendMessage(channel, sender + ": " + (i+1) + printMe + " : " + newsAPI.parseArticleLink(result, i));
					sendMessage(channel, " ");//makes printing nicer by adding an extra white line
				}
			}else if(message.equalsIgnoreCase("Help")) {//prints commands
				sendMessage(channel, sender + ": [topic,request] , and either type a zip code and city followed by the requests weather or temp, or a topic followed by the number of articles you want");
			}else {
				sendMessage(channel, sender + ": I'm sorry, I didn't get that!");//in case the bot cannot parse the user's input
			}
		} catch (Exception e) {//in case the json object is null due to the user's request, print to make sure they clarify
			//e.printStackTrace();
			sendMessage(channel, sender + ": I'm sorry, I didn't get that! Make sure you surround your question in brackets!");
			sendMessage(channel, sender + ": For example: [Dallas, temp]");
			sendMessage(channel, sender + ": If you need help just type help!");
		}
		
	}
}


/*
 * Notes:
 * Rest = representational state transfer
 * 
 * 
 */
