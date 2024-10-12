import java.io.*;
import java.util.*;

public class Chatbot {
    //Declare a map storing inputs and answers to those inputs
    //The array list allows us to have duplicate values
    //The more common an answer, the more the bot will use it
    private HashMap<String, ArrayList<String>> inputAndAnswers = new HashMap<>();
    //Declare a file where program will read and write
    private final File file;
    //Declare a string storing the latest string the system printed
    private String lastStr;

    public Chatbot () {
        file = new File("resources/inputAnswer.txt");
        lastStr = "Hello, let's talk."; //This is the default response
    }

    private ArrayList<String> ParseAnswers (String allAnswers) {
        int previousStart = 0;
        ArrayList<String> answers = new ArrayList<>();

        for (int i = 0; i < allAnswers.length(); i++) {
            if (allAnswers.charAt(i) == '|') {
                answers.add(allAnswers.substring(previousStart, i)); //end at i to ignore |

                previousStart = i + 1; //start at i+1 to ignore |
            }
        }

        answers.add(allAnswers.substring(previousStart)); //one word missing

        return answers;
    }

    private void ParseInputs () throws FileNotFoundException {
        //Setup scanner to scan file
        Scanner scnr = new Scanner(file);

        /*
         * inputAnswer.txt is formatted in a way where the inputs and answers come in pairs
         * the one that appears first is a String, the input
         * the next one will be an ArrayList containing all the possible answers to that input
         * the input is formatted like: input
         * the answers are formatted like: answer1|answer2|answer3
         * the vertical line (|) is not commonly used in responses
         * it will not interfere with the answers when being removed
         */

        int lineNum = 1; //start with 1, switch to 2, back to 1, etc.
        String input = "";
        String allAnswers; //parse answers as string and separate them

        while (scnr.hasNextLine()) {
            if (lineNum == 1) {
                input = scnr.nextLine();

                lineNum = 2;
            } else if (lineNum == 2) {
                allAnswers = scnr.nextLine();

                inputAndAnswers.put(input, ParseAnswers(allAnswers));

                lineNum = 1;
            }
        }

        scnr.close();
    }

    public String getGreeting() throws IOException {
        ParseInputs ();

        //setup file for more writing
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true)));
        writer.newLine();
        writer.close();


        return "NOTE: It's case sensitive. \nType \"BYE\" to exit.\n\nHello, let's talk.";
    }

    public String getResponse(String statement) throws IOException {
        ParseInputs();

        String response;
        Scanner scnr = new Scanner(System.in);
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true)));

        if (inputAndAnswers.containsKey(statement)) { //CASE 1: statement is known
            ArrayList<String> possibleAnswers = inputAndAnswers.get(statement);

            int index = (int) (Math.random() * possibleAnswers.size());

            response = possibleAnswers.get(index);
        } else {
            System.out.println("How would you respond to this?");

            ArrayList<String> toAdd = new ArrayList<>();

            toAdd.add(scnr.nextLine());

            inputAndAnswers.put(statement, toAdd);

            writer.write(statement);
            writer.newLine();
            writer.write(toAdd.getFirst());
            writer.newLine();

            response = getRandomResponse();
            //lastStr = response;

            writer.close();

            return response;
        }

        //clear file: append being false (default) clears it
        BufferedWriter writer2 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));

        //then, close writer 2 as it is useless
        writer2.close();

        //update old responses
        ArrayList<String> ans = (ArrayList<String>) inputAndAnswers.get(statement).clone();
        String newAnswer = ""; //use as a string so writing is easier

        inputAndAnswers.remove(lastStr);
        ans.add(statement);
        inputAndAnswers.put(lastStr, ans);

        //System.out.println(ans);

        for (String key : inputAndAnswers.keySet()) {
            writer.write(key);
            writer.newLine();

            ans = (ArrayList<String>) inputAndAnswers.get(key).clone();

            //concatenate strings
            for (int i = 0; i < ans.size(); i++) {
                if (i == 0) {
                    newAnswer = ans.get(i);
                } else {
                    newAnswer += "|" + ans.get(i);
                }
            }

            writer.write(newAnswer);
            writer.newLine();
        }

        lastStr = response;

        writer.close();

        return response;
    }

    private String getRandomResponse() {
        final int NUMBER_OF_RESPONSES = 5;
        double r = Math.random();
        int whichResponse = (int)(r * NUMBER_OF_RESPONSES);
        String response = "";

        if (whichResponse == 0) {
            response = "That's great!";
        } else if (whichResponse == 1) {
            response = "Hmmm.";
        } else if (whichResponse == 2) {
            response = "Good response.";
        } else if (whichResponse == 3) {
            response = "Interesting!";
        } else if (whichResponse == 4) {
            response = "Thanks for telling me!";
        }

        return response;
    }
}