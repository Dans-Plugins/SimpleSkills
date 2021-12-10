package dansplugins.simpleskills.api.preponderous.ponder.toolbox.tools;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Daniel Stephenson
 */
public class ArgumentParser {

    /**
     * Method to drop the first argument from an Array of Strings. This may return an empty array.
     *
     * @param args to modify.
     * @return Modified Array of Strings.
     * @throws IllegalArgumentException if the arguments given are invalid.
     */
    public String[] dropFirstArgument(String[] args) {
        if (args == null || args.length == 0) {
            throw new IllegalArgumentException("Arguments not valid.");
        }
        String[] toReturn = new String[args.length - 1];
        System.arraycopy(args, 1, toReturn, 0, args.length - 1);
        return toReturn;
    }

    /**
     * Method to get all arguments within Double Quotes.
     *
     * @param args to compile and scan.
     * @return {@link ArrayList} of {@link String} which were surrounded by " or Double-Quotes.
     * @throws IllegalArgumentException if the arguments given are invalid.
     */
    public ArrayList<String> getArgumentsInsideDoubleQuotes(String[] args) {
        if (args == null || args.length == 0) throw new IllegalArgumentException("Arguments not valid.");
        final ArrayList<String> toReturn = new ArrayList<>();
        final String argumentString = String.join(" ", args);
        final Matcher matcher = Pattern.compile("\"[^\"]*\"").matcher(argumentString);
        while (matcher.find()) toReturn.add(matcher.group().replace("\"", ""));
        return toReturn;
    }

    /**
     * Test harness to confirm the functionality of the Argument Parser class.
     *
     * @param args of the program.
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            String input = "/test fly \"one\" \"two\" banana";
            args = input.split(" ");
        }
        System.out.println("Argument Parser - Test Harness");
        System.out.println("To utilise the Argument Parser Test Harness, provide some Program Arguments!");
        System.out.println(new ArgumentParser().getArgumentsInsideDoubleQuotes(args));
    }

}
