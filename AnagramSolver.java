import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Scanner;
import java.util.Set;

/*
 * Class that handles the solving of an anagram.
 * Can process input in three different ways to find the longest word that 
 * can be made, all the words that can be made, and all two word combination 
 * that can be made from the input.
 * 
 * Put the dictionary.txt in a new folder called dictionary to connect the 
 * .txt file without fiddling with the code. Else, change the filename
 * portion of the main function to make the code work for any other .txt file
 * with the same format as dictionary.txt.
 * 
 * Assignment 1 of STEP at Google Tokyo, plus some extra functionality.
 * 
 * @author Naho Kitade
 */
public class AnagramSolver{

	// constants used
	private static final Integer CONTAINED = 1;				// Getting rid of magic numbers. Acts as a label
	private static final Integer CHAR_LENGTH = 16;		// Length of input 


	/*
	 * Function that reads in dictionary words from a txt file
	 * and creates a map of those words of String -> Set of String with
	 * the key as a sorted version of the original word, and the 
	 * original words corresponding to the sorted word as its value. 
	 * 
	 * @param filename the txt file containing dictionary words.
	 * @return the created hashtable described above. 
	 */
	public static Hashtable<String, Set<String>> readWordsToHash(String fileName) {
		try {
			// all constructs needed for the processing of txt file
			FileReader reader = new FileReader(fileName);
			Scanner in = new Scanner(reader);
			Hashtable<String, Set<String>> dictWords = new Hashtable<String, Set<String>>();

			// read every line 
			while(in.hasNextLine()) {
				Set<String> words;
				String word = (in.nextLine()).toLowerCase();

				// Make hashtable of sorted words to unsorted words
				String sortedWord = QuickSort.sort(word);
				if(dictWords.containsKey(sortedWord)){		// if contained, shouldnt make new Set.
					words = dictWords.get(sortedWord);
					words.add(word);												// just add current word to existing Set.
					dictWords.put(sortedWord, words);
				}
				else{																			// sorted word does not exist yet.
					words = new HashSet<String>();					// create new set with just current word
					words.add(word);
					dictWords.put(sortedWord, words);				// add to hashtable
				}
			}		
			return dictWords;														// return the existing final hashtable
		}
		// error reading the file
		catch (IOException exception) {
			System.out.println("File processing error: " + exception);
		}
		return null;  // In case of an exception still need to return.
	}

	/*
	 * Function that handles taking input of CHAR_LENGTH length
	 * from the user.
	 * 
	 * @return the correct input from the user.
	 */
	public static String takeInput() throws IOException{
		String inputString = "";
		String inputBuffer = "";
		int length = 0;								// keeps track if input is of correct length
		Scanner input = new Scanner(System.in);
		System.out.print("Enter " + CHAR_LENGTH + " characters: ");
		do{						// input must be CHAR_LENGTH length
			inputBuffer = input.next().toLowerCase();		
			inputString = inputString + inputBuffer;						// get input and its length
			length = inputString.length();
			if(length > CHAR_LENGTH){
				System.err.println("input length too long...");
				inputString = "";
				break;
			}
		}while (length != CHAR_LENGTH);
		return inputString;						// return the final input
	}


	/*
	 * Function that handles the longest word that can be made
	 * from a given input as an anagram.
	 * 
	 * @param input input from the user
	 * @param dictHash the hashtable returned in the readWordsToHash
	 * function.
	 * @return set of strings of the longest words that can be made
	 * from the given input (if there are multiple of the same length
	 * all will be in the set)
	 */
	public static Set<String> findLongestWord(String input, Hashtable<String, Set<String>> dictHash){
		Set<String> result = new HashSet<String>();
		String toSearchWord;
		ArrayList<String> queue = new ArrayList<String>();
		Hashtable<String, Integer> searchedHash = new Hashtable<String, Integer>();
		
		// sort first user input for easy search in hash
		String sortedInput = QuickSort.sort(input);
		queue.add(sortedInput);		// basically like a BFS algorithm. Add first sorted input into queue
		searchedHash.put(sortedInput, CONTAINED);			// add to Hash to mark as visited.
		
		// go on until you go through all combinations (even with less characters) of the original
		// sorted input
		while(!queue.isEmpty()){	
			// deque
			String dequedWord = queue.remove(0);
			if(dictHash.containsKey(dequedWord)){		// if that word exists in dictionary, we found longest word
				result = dictHash.get(dequedWord);	
				break;																// break and return that word
			}
			// if that word was not in the library, add to the queue, all the different strings
			// that can be made by taking away one of the characters from the string.
			for(int i = 0; i < dequedWord.length(); i++){
				toSearchWord = dequedWord.substring(0, i) + dequedWord.substring(i + 1, dequedWord.length());
				if(searchedHash.containsKey(toSearchWord)) continue;	// make sure that we haven't already visited that word.
				queue.add(toSearchWord);
				searchedHash.put(toSearchWord, CONTAINED);						// mark that word as visited
			}
		}
		return result;
	}


	/*
	 * Function that finds all words that can be made from a certain
	 * input string from the user.
	 * 
	 * @param input input from the user
	 * @param dictHash the hashtable returned in the readWordsToHash
	 * function.
	 * @return set of strings of the all words that can be made
	 * from the given input 
	 */
	public static Set<String> findAllWords(String input, Hashtable<String, Set<String>> dictHash){
		Set<String> resultSet = new HashSet<String>();
		String toSearchWord;
		ArrayList<String> queue = new ArrayList<String>();
		Hashtable<String, Integer> searchedHash = new Hashtable<String, Integer>();
		String sortedInput = QuickSort.sort(input);
		queue.add(sortedInput);
		searchedHash.put(sortedInput, CONTAINED);
		// very similar function as findLongestWord, but instead of breaking this while loop
		// after finding the first word, we continue adding to a Set of strings, all the different
		// words that we found in the dictionary.
		while(!queue.isEmpty()){
			String dequedWord = queue.remove(0);
			if(dictHash.containsKey(dequedWord)){
				Set<String> resultAdd = dictHash.get(dequedWord);
				for(String resultWord : resultAdd){
					resultSet.add(resultWord);			// the only different part. Just add the found word to a Set.
				}
			}
			// same as findLongestWord function.
			for(int i = 0; i < dequedWord.length(); i++){
				toSearchWord = dequedWord.substring(0, i) + dequedWord.substring(i + 1, dequedWord.length());
				if(searchedHash.containsKey(toSearchWord)) continue;
				queue.add(toSearchWord);
				searchedHash.put(toSearchWord, CONTAINED);
			}
		}
		return resultSet;
	}



	/*
	 * Function that handles finding two word combinations from
	 * the taken anagram input.
	 * 
	 * @param input input from the user
	 * @param dictHash the hashtable returned in the readWordsToHash
	 * function.
	 * @return set of strings of all two word combinations that 
	 * can be made from the input.
	 */
	public static Set<String> findCombinedWords(String input, Hashtable<String, Set<String>> dictHash){
		// returns set of all words that can be made from given input
		Set<String> initialWords = findAllWords(input, dictHash);		
		//stores all two word combinations that can be made
		Set<String> combinedWords = new HashSet<String>();				

		// sort the original input, sort the words that can be made from that input,
		// find the left over characters between the original input and the words,
		// see if you can make another word out of the leftover characters.
		// Add to the combinedWords set.
		String sortedInput = QuickSort.sort(input);
		for(String initialWord : initialWords){
			String sortedInitialWord = QuickSort.sort(initialWord);
			String leftChars = leftChars(sortedInput, sortedInitialWord);		// returns a string of leftover characters
			Set<String> secondaryWords = findAllWords(leftChars, dictHash);
			for(String secondaryWord : secondaryWords){
				combinedWords.add(initialWord + " " + secondaryWord);
			}
		}
		return combinedWords;
	}


	/*
	 * Function that returns the left over characters of the longerStr
	 * after taking away all the characters of the shorterStr.
	 * 
	 * @param longerStr The longer string to compare
	 * @param shorterStr The shorter string to compare
	 * @return the string where the characters of the shorterStr is
	 * taken away from the longerStr. Both strings must be in alphabetical
	 * order.
	 */
	public static String leftChars(String longerStr, String shorterStr){
		char curChar;
		int j = 0;
		ArrayList<Character> leftStr = new ArrayList<Character>();

		// Create an array list of characters of the longerStr.
		for(int n = 0; n < longerStr.length(); n++){
			leftStr.add(longerStr.charAt(n));
		}

		// Because both strings are in alphabetical order, iterate through
		// the shorterStr and take out the corresponding character in the 
		// longerStr.
		for(int i = 0; i < leftStr.size(); i++){
			if(!(j < shorterStr.length())) break;
			curChar = shorterStr.charAt(j);
			if(curChar != leftStr.get(i)){
				continue;
			}
			else{
				leftStr.remove(i);					// removing from the char array list.
				i--;												// need to increment and decrement both counters
				j++;
			}
		}
		
		String listString = "";
		// Make the resulting char array list into a string format.
		for (Character s : leftStr){
			listString += s;
		}
		return listString;
	}


	/*
	 * The main program that demonstrates all the functions created in
	 * this class. Includes user interaction for taking in inputs and commands.
	 */
	public static void main(String[] args) throws IOException{
		char command = ' ';      // a command
		Scanner input2 = new Scanner(System.in);
		
		// quit qith q command
		while (command != 'q'){
			Hashtable<String, Set<String>> dictHash = readWordsToHash("dictionary/dictionary.txt");
			
			String input1 = takeInput();				// get character input from user
			System.out.println(input1);
			if(input1 == "") break;
			
			// get command input from user
			System.out.println("Commands are\nq: quit\na: all words\nl: longest word\nc: combined two words ");
			System.out.print("input a command: ");
			command = input2.nextLine().charAt(0);
			
			switch (command){
			case 'q':                   // Quit
				System.out.println("Bye");
				break;
			case 'a':										// Print all the words
				System.out.println(findAllWords(input1, dictHash));
				break;
			case 'l':										// Print the longest word 
				System.out.println(findLongestWord(input1, dictHash));
				break;
			case 'c':										// Print all two word combinations 
				System.out.println(findCombinedWords(input1, dictHash));
				break;
			case '?':                   // Print all the commands
				System.out.println("Commands are\nq: quit\na: all words\nl: longest word\nc: combined two words");
				break;
			default:										// Print confused defult message
				System.out.println("Huh?");
				break;
			}
		}
	}



}

