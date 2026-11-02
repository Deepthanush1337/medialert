package medialert;

import java.util.Scanner;

/**
 * Safe console input. Every read re-prompts on bad input
 * instead of crashing the program. Typed text shows in yellow.
 */
public class InputHelper {

    private static final Scanner scanner = new Scanner(System.in);

    /** Returned by readChoice when the menu should simply be shown again. */
    static final int REDRAW = -1;

    private InputHelper() {
    }

    private static String nextLine() {
        System.out.print(ConsoleUI.startColor(ConsoleUI.YELLOW));
        System.out.flush();
        if (!scanner.hasNextLine()) {
            System.out.println(ConsoleUI.reset());
            ConsoleUI.warn("Input closed - leaving MediAlert.");
            System.exit(0);
        }
        String line = scanner.nextLine().trim();
        if (ConsoleUI.demo) {
            for (char c : line.toCharArray()) {
                System.out.print(c);
                System.out.flush();
                ConsoleUI.sleep(25);
            }
            System.out.println();
        }
        System.out.print(ConsoleUI.reset());
        return line;
    }

    /** Field prompt, e.g.  "    \u203A Patient ID        : " */
    public static String readLine(String label) {
        System.out.print("    " + ConsoleUI.fg(ConsoleUI.TEAL, "\u203A") + " "
                + ConsoleUI.pad(ConsoleUI.fg(ConsoleUI.WHITE, label), 22) + ConsoleUI.dim(": "));
        return nextLine();
    }

    public static int readInt(String label) {
        while (true) {
            String text = readLine(label);
            try {
                return Integer.parseInt(text);
            } catch (NumberFormatException e) {
                ConsoleUI.fail("'" + text + "' is not a whole number. Try again.");
            }
        }
    }

    public static long readLong(String label) {
        while (true) {
            String text = readLine(label);
            try {
                return Long.parseLong(text);
            } catch (NumberFormatException e) {
                ConsoleUI.fail("Digits only, please. Try again.");
            }
        }
    }

    public static double readDouble(String label) {
        while (true) {
            String text = readLine(label);
            try {
                return Double.parseDouble(text);
            } catch (NumberFormatException e) {
                ConsoleUI.fail("'" + text + "' is not a valid amount. Try again.");
            }
        }
    }

    public static boolean readBoolean(String label) {
        while (true) {
            String text = readLine(label).toLowerCase();
            if (text.equals("true") || text.equals("yes") || text.equals("y")) {
                return true;
            }
            if (text.equals("false") || text.equals("no") || text.equals("n")) {
                return false;
            }
            ConsoleUI.fail("Type true / false (or y / n).");
        }
    }

    /**
     * Menu input at the shell prompt. Accepts a number or a command:
     * back / b, clear, help, exit. Returns REDRAW when the menu
     * should just be shown again.
     */
    public static int readChoice(String path, int backOption) {
        ConsoleUI.prompt(path);
        String text = nextLine().toLowerCase();
        if (text.isEmpty()) {
            return REDRAW;
        }
        if (text.equals("back") || text.equals("b") || text.equals("exit") || text.equals("q")) {
            return backOption;
        }
        if (text.equals("clear") || text.equals("cls")) {
            ConsoleUI.clearScreen();
            return REDRAW;
        }
        if (text.equals("help") || text.equals("?")) {
            ConsoleUI.help();
            return REDRAW;
        }
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            ConsoleUI.fail("command not found: " + text + "   " + ConsoleUI.dim("(type help)"));
            return REDRAW;
        }
    }
}
