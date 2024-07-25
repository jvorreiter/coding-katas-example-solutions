package algodat.i_recursion_and_basic_data_structures.recursion.palindrome_check;

public class RecursivePalindromeCheckMain {
    public static void main(String[] args) {
        var palindrome1 = "ABCDEDCBA";
        var palindrome2 = "ABCDEEDCBA";
        var noPalindrome = "AAAABAAA";

        System.out.printf("Is \"%s\" a palindrome: %s%n", palindrome1, RecursivePalindromeCheck.IsPalindome(palindrome1));
        System.out.printf("Is \"%s\" a palindrome: %s%n", palindrome2, RecursivePalindromeCheck.IsPalindome(palindrome2));
        System.out.printf("Is \"%s\" a palindrome: %s%n", noPalindrome, RecursivePalindromeCheck.IsPalindome(noPalindrome));
    }
}
