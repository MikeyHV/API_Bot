import java.util.Scanner;

public class MyBotMain {
	private static Scanner in;
	public static void main(String[] args) throws Exception {
		MyBot bot = null;
		in = new Scanner(System.in);
		bot = new MyBot();
		
		System.out.println("Command line run or freenode.net run? 1 or 2");
		String hold = in.nextLine();
		if(hold.equals("1")) {
			System.out.println("Hello! Welcome to my bot. If you wish to get the weather, Enter a city or zipcode then a request separated by a comma enclosed in dashes");
			System.out.println("For example: [Dallas,weather] , or [71203,temp]");
			System.out.println("If you want to get any news regarding a specific topic, I can pull up to 10 news articles about the topic");
			System.out.println("To get that, enter the topic you want followed by how many articles you want, enclosed by dashes");
			System.out.println("For example: [politics,3] , or [Computer Science,10]");
			System.out.println("Thank you!");
			
			while(!hold.equals("disconnect")) {
				System.out.println("Please enter a question");
				hold = in.nextLine();
				bot.onRequest(hold);
			}
		}else {
			bot.setAutoNickChange(true);//if the nickname on the channel is not available, automatically appends some stuff to the end of the nickname so that it can join smoothly
			bot.setVerbose(true);//anything from the server is printed normally
			bot.connect("irc.freenode.net");//connects to an Internet relay chat--this is why the main throws an exception, in case the connection fails or something goes horribly wrong
			bot.joinChannel("#privatebot");//joins a channel through the specified server
		}
		
		
		
		
	}

}

/*
*The code that code that initializes the bot, establishes its settings, and runs it
*/