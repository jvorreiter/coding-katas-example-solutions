package algodat.i_recursion_and_basic_data_strucutures;

public class RecursivePalindomeCheck {
    public static void main(String[] args) {
        var palindrome1 = "ABCDEDCBA";
        var palindrome2 = "ABCDEEDCBA";
        var noPalindrome = "AAAABAAA";

        System.out.println(String.format("Is \"%s\" a palindrome: %s", palindrome1, IsPalindome(palindrome1)));
        System.out.println(String.format("Is \"%s\" a palindrome: %s", palindrome2, IsPalindome(palindrome2)));
        System.out.println(String.format("Is \"%s\" a palindrome: %s", noPalindrome, IsPalindome(noPalindrome)));
    }

    public static boolean IsPalindome(String word) {
        // besteht das Wort aus 0 oder 1 Zeichen, so ist es automatisch ein Palindrom
        if (word.length() < 2) {
            return true;
        }

        // ansonsten müssen wir prüfen, ob die äußeren beiden Buchstaben gleich sind,
        // und der Text zwischen dem ersten und letzten Buchstaben auch ein Palindrom ist.
        // Beispiel: Wort ist "ALENA"
        // der erste Buchstabe ("A") und der letzte Buchstabe ("A") sind gleich.
        // nun muss also noch geprüft werden, ob der Rest "LEN" auch ein Palindrom ist.
        var lastIndex = word.length() - 1;

        var outerCharsEqual = word.charAt(0) == word.charAt(lastIndex);
        if(!outerCharsEqual) {
            return false;
        }

        var innerWordIsPalindrome = IsPalindome(word.substring(1, lastIndex));
        return innerWordIsPalindrome;
    }
}
