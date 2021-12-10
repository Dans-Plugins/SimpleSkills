/*
 * Decompiled with CFR 0.150.
 */
package preponderous.ponder.toolbox.tools;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ArgumentParser {
    public String[] dropFirstArgument(String[] args) {
        if (args == null || args.length == 0) {
            throw new IllegalArgumentException("Arguments not valid.");
        }
        String[] toReturn = new String[args.length - 1];
        System.arraycopy(args, 1, toReturn, 0, args.length - 1);
        return toReturn;
    }

    public ArrayList<String> getArgumentsInsideDoubleQuotes(String[] args) {
        if (args == null || args.length == 0) {
            throw new IllegalArgumentException("Arguments not valid.");
        }
        ArrayList<String> toReturn = new ArrayList<String>();
        String argumentString = String.join((CharSequence)" ", args);
        Matcher matcher = Pattern.compile("\"[^\"]*\"").matcher(argumentString);
        while (matcher.find()) {
            toReturn.add(matcher.group().replace("\"", ""));
        }
        return toReturn;
    }

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

