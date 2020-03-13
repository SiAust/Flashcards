package flashcards;

import java.io.*;
import java.util.*;

public class Main {

    private static final String ADD = "add";
    private static final String REMOVE = "remove";
    private static final String IMPORT = "import";
    private static final String EXPORT = "export";
    private static final String ASK = "ask";
    private static final String EXIT = "exit";
    private static final String LOG = "log";
    private static final String HARDEST = "hardest card";
    private static final String RESET = "reset stats";

    private static final String PRINT = "print";
    private static final String PRINT_MIS = "print mistakes";

    private static final ArrayList<String> LOGGER = new ArrayList<>();

    private static Map<String, String> flashCards =  new LinkedHashMap<>();
    private static Map<String, Integer> mistakes = new LinkedHashMap<>();

    public static void main(String[] args) {
        List<String> argList = new ArrayList<>(Arrays.asList(args));
        if (argList.contains("-import")) {
            String filePath = argList.get(argList.indexOf("-import") + 1);
            importCards(filePath);
        }

        String userInput;
        do {
            printMsg("Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):");
            userInput = inputMsg().toLowerCase();
            switch (userInput) {
                case ADD: {
                    addCard();
                    break;
                }
                case REMOVE: {
                    removeCard();
                    break;
                }
                case IMPORT: {
                    importCards(null);
                    break;
                }
                case EXPORT: {
                    exportCards(null);
                    break;
                }
                case ASK: {
                    ask();
                    break;
                }
                case LOG: {
                    log();
                    break;
                }
                case PRINT: {
                    printCards();
                    break;
                }
                case PRINT_MIS: {
                    printMistakes();
                    break;
                }
                case HARDEST: {
                    hardestCard();
                    break;
                }
                case RESET: {
                    resetStats();
                    break;
                }
            }

        } while (!userInput.equals(EXIT));
        System.out.println("Bye bye!");

        if (argList.contains("-export")) {
            if (argList.indexOf("-export") < argList.size() - 1) {
                String filePath = argList.get(argList.indexOf("-export") + 1);
                exportCards(filePath);
            }
        }
    }

    private static void addCard() {
        String term;
        String definition;

        printMsg("The card:");
        term = inputMsg();
        if (flashCards.containsKey(term)) { // if card term already exists, break from method
            printMsg("The card \"" + term + "\" already exists.");
            return;
        }
        printMsg("The definition of the card:");
        definition = inputMsg();
        if (flashCards.containsValue(definition)) { // if card definition already exists loop until until unused definition input
            printMsg("The definition \"" + definition + "\" already exists.");
            return;
        }
        flashCards.put(term, definition);
        printMsg(String.format("The pair (\"%s\":\"%s\") has been added.", term, definition));

    }

    private static void removeCard() {
        printMsg("The card:");
        String userInput = inputMsg();
        if (flashCards.containsKey(userInput)) {
            flashCards.remove(userInput);
            mistakes.remove(userInput);
            printMsg("The card has been removed.");
        } else {
            printMsg(String.format("Can't remove \"%s\": there is no such card.", userInput));
        }

    }

    private static void importCards(String filePath) {
        String fileName;
        File file = null;

        if (filePath != null) {
            file = new File("./" + filePath);
        } else {
            printMsg("File name:");
            fileName = inputMsg();
            file = new File("./" + fileName);
        }
        try (Scanner fileScanner = new Scanner(file);) {
            String term = null;
            String definition = null;
            int mistake = 0;
            int count = 0;
            while (fileScanner.hasNext()) {
                String entry = fileScanner.nextLine();
                String[] entrySplit = entry.split(":");
                term = entrySplit[0];
                definition = entrySplit[1];
                mistake = Integer.parseInt(entrySplit[2]);
                flashCards.put(term, definition);
                mistakes.put(term, mistake);
                count++;
            }
            printMsg(count + " " + (count > 1 ? "cards have been loaded." : "card has been loaded."));
        } catch (FileNotFoundException e) {
            printMsg("File not found. Error: " + e);
        }


    }

    private static void exportCards(String filePath) {
        String fileName;
        File cardsFile = null;

        if (filePath != null) {
            cardsFile = new File("./" + filePath);
        } else {
            printMsg("File name:");
            fileName = inputMsg();
            cardsFile = new File("./" + fileName);
        }
        try (FileWriter writer = new FileWriter(cardsFile);) {
            for (var entry : flashCards.entrySet()) {
                writer.write(entry.getKey() + ":" + entry.getValue() + ":"
                        + (mistakes.get(entry.getKey()) == null ? 0 : mistakes.get(entry.getKey()))
                        + "\n");
            }
            printMsg(flashCards.size() + " cards have been saved.");
        } catch (IOException e) {
            printMsg(String.format("File cannot be written. Error: %s", e));
        }

    }

    private static void ask() {
        String term;
        int n;
        int count = 1;

        printMsg("How many times to ask?");
        n = Integer.parseInt(inputMsg());

        while (count <= n) {
            for (String key : flashCards.keySet()) {
//               System.out.println("Print the definition of \"" + key + "\":");
                printMsg("Print the definition of \"" + key + "\":");
                term = inputMsg();
                if (term.equals(flashCards.get(key))) {
                    printMsg("Correct answer.");
                } else {
                    if (flashCards.containsValue(term)) {
                        printMsg("Wrong answer. The correct one is \"" + flashCards.get(key)
                                + "\", you've just written the definition of \""
                                + getKeyFromValue(flashCards, term) + "\".");

                        mistakes.put(key , mistakes.containsKey(key) ?
                                mistakes.get(key) + 1 : 1 );
                    } else {
                        printMsg("Wrong answer. The correct one is \"" + flashCards.get(key) + "\".");
                        mistakes.put(key , mistakes.containsKey(key) ?
                                mistakes.get(key) + 1 : 1 );
                    }
                }
                count++;
                if (count > n) {
                    break;
                }
            }
        }
    }

    private static void log() {
        printMsg("File name:");
        String file = inputMsg();
        File log = new File("./" + file);
        try (FileWriter writer = new FileWriter(log)) {
            for (String str : LOGGER) {
                writer.append(str + "\n");
            }
            printMsg("The log has been saved.");
        } catch (IOException e) {
            printMsg("File could not be written. Error: " + e);
        }
    }

    private static void printMsg(String string) {
        System.out.println(string);
        LOGGER.add(string);
    }

    private static String inputMsg() {
        Scanner sc = new Scanner(System.in);
        String input = sc.nextLine();
        LOGGER.add("> " + input);
        return input;
    }

    private static void printCards() {
        for (var entry : flashCards.entrySet()) {
            System.out.println(entry);
        }
    }

    private static void printMistakes() {
        for (var entry : mistakes.entrySet()) {
            System.out.println(entry);
        }
    }

    private static void hardestCard() {
        int max = 0;
        ArrayList<String> mistake = new ArrayList<>();
        for (var entry : mistakes.entrySet()) {
            if (entry.getValue() > max) {
                max = entry.getValue();
            }
        }
        if (max == 0) {
            printMsg("There are no cards with errors.");
            return;
        }
        for (var entry : mistakes.entrySet()) {
            if (entry.getValue() == max) {
                mistake.add(entry.getKey());
            }
        }
        if (mistake.size() > 1) { // if more than one card with same number of mistakes
            StringBuilder sb = new StringBuilder();
            for (String str : mistake) {
                if (mistake.get(mistake.size() - 1).equals(str)) { // if this is the last element
                    sb.append("\"" + str + "\".");
                } else {
                    sb.append(String.format("\"%s\", ", str));
                }
            }
            printMsg("The hardest cards are "+ sb.toString() + " You have " + max + " errors answering them.");
        } else {
            printMsg("The hardest card is " + mistake.get(0) + ". You have " + max + " errors answering it."); //todo: max > 1 errors : error
        }
    }

    private static void resetStats() {
        for (var entry : mistakes.entrySet()) {
            entry.setValue(0);
        }
        printMsg("Card statistics has been reset.");
    }

    private static String getKeyFromValue(Map<String, String> map, String value) {
        for (String key : map.keySet()) {
            if (map.get(key).equals(value)) {
//                System.out.println("map.get(entry)= " + map.get(key) + " value= " + value + " key=" + key);
                return key;
            }
        }
        return "unknown";
    }
}
