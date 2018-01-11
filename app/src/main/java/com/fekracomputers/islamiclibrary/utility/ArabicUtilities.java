package com.fekracomputers.islamiclibrary.utility;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * بسم الله الرحمن الرحيم
 * Created by moda_ on 23/2/2017.
 */

public class ArabicUtilities {


    public static final char ALEF = '\u0627';
    public static final char ALEF_MADDA = '\u0622';
    public static final char ALEF_HAMZA_ABOVE = '\u0623';
    public static final char ALEF_HAMZA_BELOW = '\u0625';
    public static final char YEH = '\u064A';
    public static final char DOTLESS_YEH = '\u0649';
    public static final char TEH_MARBUTA = '\u0629';
    public static final char HEH = '\u0647';
    public static final char TATWEEL = '\u0640';
    public static final char FATHATAN = '\u064B';
    public static final char DAMMATAN = '\u064C';
    public static final char KASRATAN = '\u064D';
    public static final char FATHA = '\u064E';
    public static final char DAMMA = '\u064F';
    public static final char KASRA = '\u0650';
    public static final char SHADDA = '\u0651';
    public static final char SUKUN = '\u0652';
    public static final char HAMZAH = '\u0621';
    private static final Pattern CLEANING_TASHKEEL = Pattern.compile("[ًٌٍَُِّْ]");
    /**
     * matches any character that is not (letter other category or space or newline)
     * this will leave only the basic arabic letters (and other language which have characters in the
     * this category) which means this will not match (and hence remove when used with replaceAll("")
     * english letters ,punctation and other things
     */
    private static final Pattern REMOVE_PATTERN = Pattern.compile("[^\\p{L}\\p{Z}]");
    private static final Pattern SPACE_REPLACED_PATTERN = Pattern.compile("[\\p{Z}\\p{S}\\p{C}\\p{Pc}\\p{Pd}\\p{Po}&&[^\"]]");
    private static final String ALEF_str = "\u0627";
    private static final String ALEF_MADDA_str = "\u0622";
    private static final String ALEF_HAMZA_ABOVE_str = "\u0623";
    private static final String ALEF_HAMZA_BELOW_STR = "\u0625";
    private static final String YEH_STR = "\u064A";
    private static final String DOTLESS_YEH_STR = "\u0649";
    private static final String TEH_MARBUTA_STR = "\u0629";
    private static final String HEH_STR = "\u0647";
    private static final Pattern equvilancePattern = Pattern.compile(
            ALEF_MADDA_str + "|" + ALEF_HAMZA_ABOVE_str + "|" + ALEF_HAMZA_BELOW_STR + "|" + DOTLESS_YEH_STR + "|" + TEH_MARBUTA_STR);
    private static final Pattern REMOVE_REPEATED_SPACES = Pattern.compile("\\s\\s+");

    public static String cleanTashkeel(String s) {
        Matcher matcher = CLEANING_TASHKEEL.matcher(s);
        return matcher.replaceAll("");
    }

    public static String cleanTextForSearchingWithRegex(String s) {
        Matcher matcher = SPACE_REPLACED_PATTERN.matcher(s);
        String space_replaced = matcher.replaceAll(" ");

        Matcher matcher1 = REMOVE_PATTERN.matcher(space_replaced);
        String removed = matcher1.replaceAll("");

        Matcher matcher2 = REMOVE_REPEATED_SPACES.matcher(removed);
        String removed_duplicat_spaces = matcher2.replaceAll(" ");


        Matcher equivlanceMatcher = equvilancePattern.matcher(removed_duplicat_spaces);

        StringBuffer sb = new StringBuffer();
        while (equivlanceMatcher.find()) {
            switch (equivlanceMatcher.group(0)) {
                case DOTLESS_YEH_STR:
                    equivlanceMatcher.appendReplacement(sb, YEH_STR);
                    break;
                case ALEF_MADDA_str:
                case ALEF_HAMZA_ABOVE_str:
                case ALEF_HAMZA_BELOW_STR:
                    equivlanceMatcher.appendReplacement(sb, ALEF_str);
                    break;
                case TEH_MARBUTA_STR:
                    equivlanceMatcher.appendReplacement(sb, HEH_STR);
            }
        }
        equivlanceMatcher.appendTail(sb);
        return sb.toString();
    }

    public static String cleanTextForSearchingWthStingBuilder(String s) {
        StringBuilder sb = new StringBuilder(s);
        for (int i = 0; i < sb.length(); i++) {
            char c = sb.charAt(i);
            //   if ( (!Character.isWhitespace(c) && c < 128 )|| Character.getType(c)==Character.NON_SPACING_MARK) {
            if ((c < HAMZAH || c > YEH) & !Character.isSpace(c)) {
                sb.deleteCharAt(i);
                i--;
            } else if (Character.isSpace(c)) {
                sb.setCharAt(i, ' ');
            } else {
                switch (c) {
                    case ALEF_MADDA:
                    case ALEF_HAMZA_ABOVE:
                    case ALEF_HAMZA_BELOW:
                        sb.setCharAt(i, ALEF);
                        break;
                    case DOTLESS_YEH:
                        sb.setCharAt(i, YEH);
                        break;
                    case TEH_MARBUTA:
                        sb.setCharAt(i, HEH);
                        break;
                    default:
                        break;
                }
            }
        }

        return sb.toString();
    }

    /**
     * prepare the string to pre prefixed with lam
     * قواعد الإملاء لعبد السلام هارون الباب الرابع
     */
    public static String prepareForPrefixingLam(String string) {

        if (startsWithDefiniteArticle(string)) {
            if (string.startsWith("ل", 2)) {
                return string.substring(2);
            } else {
                return string.substring(1);
            }
        } else if (string.startsWith("أبو") || string.startsWith("ابو")) {
            return string.replaceFirst("و", "ي");
        } else {
            return string;
        }

    }

    public static boolean startsWithDefiniteArticle(String string) {
        return string.startsWith("ال");
    }
}
