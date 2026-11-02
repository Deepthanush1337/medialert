package medialert;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Terminal look and feel for MediAlert: ANSI colours, boxes, tables,
 * status tags, a shell-style prompt and small animations.
 *
 * Flags (pass to the program):
 *   --no-color   plain text output (also honours the NO_COLOR variable)
 *   --fast       skip all animations
 *   --demo       echo piped input as if it were typed (for sample_input.txt)
 */
public final class ConsoleUI {

    static boolean color = true;
    static boolean animate = true;
    static boolean demo = false;

    static final int WIDTH = 64;

    // ------------------------------------------------------------- palette (256-colour ANSI)
    static final String RESET = "\u001B[0m";
    static final String BOLD = "\u001B[1m";
    static final String DIM = "\u001B[2m";

    static final int TEAL = 43;
    static final int CYAN = 51;
    static final int CORAL = 203;
    static final int YELLOW = 221;
    static final int GREEN = 78;
    static final int RED = 196;
    static final int BLUE = 75;
    static final int PURPLE = 141;
    static final int GREY = 244;
    static final int DARK = 238;
    static final int WHITE = 255;

    private ConsoleUI() {
    }

    // ------------------------------------------------------------- setup

    static void init(String[] args) {
        if (System.getenv("NO_COLOR") != null) {
            color = false;
        }
        for (String a : args) {
            if (a.equals("--no-color")) color = false;
            if (a.equals("--fast")) animate = false;
            if (a.equals("--demo")) demo = true;
        }
        try {
            System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out), true, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            // keep the default stream
        }
    }

    // ------------------------------------------------------------- colour helpers

    static String fg(int code, String text) {
        return color ? "\u001B[38;5;" + code + "m" + text + RESET : text;
    }

    static String bold(int code, String text) {
        return color ? BOLD + "\u001B[38;5;" + code + "m" + text + RESET : text;
    }

    static String dim(String text) {
        return fg(GREY, text);
    }

    static String startColor(int code) {
        return color ? "\u001B[38;5;" + code + "m" : "";
    }

    static String reset() {
        return color ? RESET : "";
    }

    static String repeat(String s, int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) sb.append(s);
        return sb.toString();
    }

    /** Length of the text as it appears on screen (ANSI codes removed). */
    static int visible(String s) {
        return s.replaceAll("\u001B\\[[;\\d]*m", "").length();
    }

    static String pad(String s, int width) {
        int v = visible(s);
        return v >= width ? s : s + repeat(" ", width - v);
    }

    static String padLeft(String s, int width) {
        int v = visible(s);
        return v >= width ? s : repeat(" ", width - v) + s;
    }

    static String cut(String s, int max) {
        if (s == null) return "-";
        return s.length() <= max ? s : s.substring(0, max - 1) + "\u2026";
    }

    static String money(double amount) {
        return String.format("Rs. %,.2f", amount);
    }

    /** Coloured appointment status. */
    static String status(String s) {
        if (s == null) return "-";
        if (s.equals("Booked")) return bold(GREEN, "\u25CF " + s);
        if (s.equals("Rescheduled")) return bold(YELLOW, "\u25CF " + s);
        if (s.equals("Cancelled")) return bold(RED, "\u25CF " + s);
        return s;
    }

    static String yesNo(boolean b) {
        return b ? bold(GREEN, "\u25CF Available") : bold(RED, "\u25CF Away");
    }

    static void sleep(int ms) {
        if (!animate) return;
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    static void clearScreen() {
        if (color) {
            System.out.print("\u001B[2J\u001B[H");
            System.out.flush();
        }
    }

    static void println(String s) {
        System.out.println(s);
    }

    static void blank() {
        System.out.println();
    }

    // ------------------------------------------------------------- status tags

    static void ok(String msg) {
        println("  " + bold(GREEN, "[  OK  ]") + " " + msg);
    }

    static void fail(String msg) {
        println("  " + bold(RED, "[ FAIL ]") + " " + msg);
    }

    static void warn(String msg) {
        println("  " + bold(YELLOW, "[ WARN ]") + " " + msg);
    }

    static void info(String msg) {
        println("  " + bold(BLUE, "[ INFO ]") + " " + msg);
    }

    // ------------------------------------------------------------- banner & boot

    /** Logo in plain ASCII; decoded by logoLine(): # = block, = | r 7 L J = double-line box pieces. */
    private static final String[] LOGO = {
        "###7   ###7#######7######7 ##7 #####7 ##7     #######7######7 ########7",
        "####7 ####|##r====J##r==##7##|##r==##7##|     ##r====J##r==##7L==##r==J",
        "##r####r##|#####7  ##|  ##|##|#######|##|     #####7  ######rJ   ##|   ",
        "##|L##rJ##|##r==J  ##|  ##|##|##r==##|##|     ##r==J  ##r==##7   ##|   ",
        "##| L=J ##|#######7######rJ##|##|  ##|#######7#######7##|  ##|   ##|   ",
        "L=J     L=JL======JL=====J L=JL=J  L=JL======JL======JL=J  L=J   L=J   "
    };
    private static final int[] LOGO_SHADES = {51, 50, 44, 43, 37, 30};

    private static String logoLine(String raw) {
        return raw.replace('#', '\u2588').replace('=', '\u2550').replace('|', '\u2551')
                  .replace('r', '\u2554').replace('7', '\u2557').replace('L', '\u255A').replace('J', '\u255D');
    }

    static void banner() {
        clearScreen();
        blank();
        for (int i = 0; i < LOGO.length; i++) {
            println("  " + bold(LOGO_SHADES[i], logoLine(LOGO[i])));
            sleep(45);
        }
        heartbeat();
        println("  " + bold(WHITE, "Clinic Appointment & Prescription Manager")
                + dim("  \u00B7  v1.0  \u00B7  Java console edition"));
        println("  " + dim("26SC1101 PSP using Java  \u00B7  KL University  \u00B7  2026-27"));
        blank();
    }

    /** A pulse line drawn left to right. */
    static void heartbeat() {
        String pulse = "\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500/\\\u2500\u2500\u2500\u2500\u2500\u2500/\\/\\\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500/\\\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500 \u2665";
        if (!animate || !color) {
            println("  " + fg(CORAL, pulse));
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < pulse.length(); i++) {
            sb.append(pulse.charAt(i));
            System.out.print("\r  " + fg(CORAL, sb.toString()));
            System.out.flush();
            sleep(6);
        }
        System.out.println();
    }

    static void bootStep(String label, String detail) {
        String[] frames = {"|", "/", "-", "\\"};
        if (animate && color) {
            for (int i = 0; i < 8; i++) {
                System.out.print("\r  " + fg(TEAL, "[  " + frames[i % 4] + "   ]") + " " + label);
                System.out.flush();
                sleep(35);
            }
        }
        if (animate && color) System.out.print("\r");
        ok(pad(label, 26) + dim(detail));
    }

    static void progressBar(String label, int width) {
        for (int i = animate ? 0 : width; i <= width; i++) {
            int pct = i * 100 / width;
            String bar = fg(TEAL, repeat("\u2588", i)) + fg(DARK, repeat("\u2591", width - i));
            System.out.print("\r  " + pad(label, 10) + bar + " " + bold(WHITE, padLeft(pct + "%", 4)));
            System.out.flush();
            sleep(18);
        }
        System.out.println();
    }

    static String now() {
        return new SimpleDateFormat("dd MMM yyyy  HH:mm:ss").format(new Date());
    }

    // ------------------------------------------------------------- boxes & menus

    static void boxTop(String title, String right) {
        String inner = " " + bold(CYAN, title);
        String rightPart = right == null ? "" : right + " ";
        int gap = WIDTH - visible(inner) - visible(rightPart);
        println("  " + fg(TEAL, "\u250C" + repeat("\u2500", WIDTH) + "\u2510"));
        println("  " + fg(TEAL, "\u2502") + inner + repeat(" ", Math.max(1, gap)) + rightPart + fg(TEAL, "\u2502"));
        println("  " + fg(TEAL, "\u251C" + repeat("\u2500", WIDTH) + "\u2524"));
    }

    static void boxLine(String content) {
        println("  " + fg(TEAL, "\u2502") + pad(" " + content, WIDTH) + fg(TEAL, "\u2502"));
    }

    static void boxDivider() {
        println("  " + fg(TEAL, "\u251C" + repeat("\u2500", WIDTH) + "\u2524"));
    }

    static void boxBottom() {
        println("  " + fg(TEAL, "\u2514" + repeat("\u2500", WIDTH) + "\u2518"));
    }

    static String option(int n, String label, String hint) {
        String num = bold(YELLOW, n < 10 ? "0" + n : "" + n);
        return " " + num + "  " + pad(fg(WHITE, label), 27) + dim(hint);
    }

    static String backOption(int n, String label) {
        String num = bold(CORAL, n < 10 ? "0" + n : "" + n);
        return " " + num + "  " + fg(CORAL, label);
    }

    /** A sub-menu: title, options, and a coloured "back" option at the end. */
    static void subMenu(String title, String[][] options) {
        blank();
        boxTop(title, dim("MediAlert"));
        for (int i = 0; i < options.length; i++) {
            boxLine(option(i + 1, options[i][0], options[i][1]));
        }
        boxLine(backOption(options.length + 1, "Back to Main Menu"));
        boxBottom();
    }

    /** A titled card of key / value pairs, used for slips, invoices and reports. */
    static void card(String title, int accent, String[][] rows) {
        int w = Math.max(46, visible(title) + 4);
        for (String[] r : rows) {
            if (r.length == 2) w = Math.max(w, 19 + visible(r[1]) + 2);
        }
        blank();
        println("  " + fg(accent, "\u2554" + repeat("\u2550", w) + "\u2557"));
        println("  " + fg(accent, "\u2551") + pad(" " + bold(accent, title), w) + fg(accent, "\u2551"));
        println("  " + fg(accent, "\u2560" + repeat("\u2550", w) + "\u2563"));
        for (String[] r : rows) {
            if (r.length == 1) {
                println("  " + fg(accent, "\u255F" + repeat("\u2500", w) + "\u2562"));
                continue;
            }
            String line = " " + pad(dim(r[0]), 18) + r[1];
            println("  " + fg(accent, "\u2551") + pad(line, w) + fg(accent, "\u2551"));
        }
        println("  " + fg(accent, "\u255A" + repeat("\u2550", w) + "\u255D"));
    }

    // ------------------------------------------------------------- tables

    static void table(String[] headers, List<String[]> rows) {
        int[] w = new int[headers.length];
        for (int i = 0; i < headers.length; i++) w[i] = headers[i].length();
        for (String[] r : rows) {
            for (int i = 0; i < r.length; i++) w[i] = Math.max(w[i], visible(r[i]));
        }
        blank();
        println("  " + fg(TEAL, edge("\u250C", "\u252C", "\u2510", w)));
        StringBuilder head = new StringBuilder("  " + fg(TEAL, "\u2502"));
        for (int i = 0; i < headers.length; i++) {
            head.append(" ").append(pad(bold(CYAN, headers[i].toUpperCase()), w[i])).append(" ").append(fg(TEAL, "\u2502"));
        }
        println(head.toString());
        println("  " + fg(TEAL, edge("\u251C", "\u253C", "\u2524", w)));
        for (String[] r : rows) {
            StringBuilder line = new StringBuilder("  " + fg(TEAL, "\u2502"));
            for (int i = 0; i < r.length; i++) {
                line.append(" ").append(pad(r[i], w[i])).append(" ").append(fg(TEAL, "\u2502"));
            }
            println(line.toString());
        }
        println("  " + fg(TEAL, edge("\u2514", "\u2534", "\u2518", w)));
        println("  " + dim(rows.size() + (rows.size() == 1 ? " row" : " rows") + "  \u00B7  " + now()));
    }

    private static String edge(String l, String m, String r, int[] w) {
        StringBuilder sb = new StringBuilder(l);
        for (int i = 0; i < w.length; i++) {
            sb.append(repeat("\u2500", w[i] + 2)).append(i == w.length - 1 ? r : m);
        }
        return sb.toString();
    }

    static List<String[]> rows() {
        return new ArrayList<>();
    }

    // ------------------------------------------------------------- charts & tiles

    static void barChart(String[] labels, int[] values, String unit) {
        int max = 1;
        int labelW = 0;
        for (int i = 0; i < values.length; i++) {
            max = Math.max(max, values[i]);
            labelW = Math.max(labelW, labels[i].length());
        }
        int barW = 30;
        blank();
        for (int i = 0; i < values.length; i++) {
            int len = values[i] * barW / max;
            String bar = fg(i % 2 == 0 ? TEAL : CYAN, repeat("\u2588", len)) + fg(DARK, repeat("\u2591", barW - len));
            println("  " + pad(fg(WHITE, labels[i]), labelW + 2) + bar + "  " + bold(YELLOW, "" + values[i]) + dim(" " + unit + (values[i] == 1 ? "" : "s")));
            sleep(80);
        }
    }

    static void tiles(String[] labels, String[] values, int[] colors) {
        int w = 15;
        StringBuilder top = new StringBuilder("  ");
        StringBuilder lab = new StringBuilder("  ");
        StringBuilder val = new StringBuilder("  ");
        StringBuilder bot = new StringBuilder("  ");
        for (int i = 0; i < labels.length; i++) {
            top.append(fg(colors[i], "\u250C" + repeat("\u2500", w) + "\u2510"));
            lab.append(fg(colors[i], "\u2502")).append(pad(" " + dim(labels[i]), w)).append(fg(colors[i], "\u2502"));
            val.append(fg(colors[i], "\u2502")).append(pad(" " + bold(colors[i], values[i]), w)).append(fg(colors[i], "\u2502"));
            bot.append(fg(colors[i], "\u2514" + repeat("\u2500", w) + "\u2518"));
        }
        blank();
        println(top.toString());
        println(lab.toString());
        println(val.toString());
        println(bot.toString());
    }

    // ------------------------------------------------------------- prompt

    /** Shell-style prompt:  \u250C\u2500[medialert@clinic]\u2500[~/patients]  /  \u2514\u2500\u25BA */
    static void prompt(String path) {
        blank();
        println("  " + fg(DARK, "\u250C\u2500[") + bold(TEAL, "medialert") + fg(GREY, "@") + fg(CYAN, "clinic")
                + fg(DARK, "]\u2500[") + fg(YELLOW, path.isEmpty() ? "~" : "~/" + path) + fg(DARK, "]"));
        System.out.print("  " + fg(DARK, "\u2514\u2500") + bold(CORAL, "\u25BA "));
        System.out.flush();
    }

    static void help() {
        blank();
        println("  " + bold(CYAN, "Commands"));
        println("    " + bold(YELLOW, "1-8     ") + dim("pick a menu option"));
        println("    " + bold(YELLOW, "back, b ") + dim("go back one level"));
        println("    " + bold(YELLOW, "clear   ") + dim("clear the screen"));
        println("    " + bold(YELLOW, "help    ") + dim("show this help"));
        println("    " + bold(YELLOW, "exit    ") + dim("save & exit (from the main menu)"));
    }
}
