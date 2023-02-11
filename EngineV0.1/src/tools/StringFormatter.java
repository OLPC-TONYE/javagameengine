package tools;

public class StringFormatter {

	public static String firstLetterUpperCase(String string) {
		
		String oldWord = string.toUpperCase();
		char firstLetter = oldWord.charAt(0);
		StringBuilder newWord = new StringBuilder(string);
		newWord.setCharAt(0, firstLetter);
		return newWord.toString();
	}
	
	public static void main(String[] args) {
		System.out.println(firstLetterUpperCase("crggit"));
	}
}
