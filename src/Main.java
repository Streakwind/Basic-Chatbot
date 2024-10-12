import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        Chatbot chatbot = new Chatbot();

        System.out.println(chatbot.getGreeting());

        Scanner scnr = new Scanner(System.in);
        String statement = scnr.nextLine();

        while (!statement.equals("Bye")) {
            System.out.println(chatbot.getResponse(statement));
            statement = scnr.nextLine();
        }
    }
}