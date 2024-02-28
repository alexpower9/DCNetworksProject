import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

public class WordCounter
{
	/*
	 * @param path path to the file
	 * @return total number of words in the file references by path
	 * @throws FileNotFoundException if file doesn't exist
	 * @precondition file must be a valid text file
	 * @postcondition wordCount represents total number of words separated by space
	 */
	public static int wordCount(String path) throws FileNotFoundException
	{
		// File object
		File file = new File(path);
		
		// file existence check
		if(!file.exists())  
			throw new FileNotFoundException();
		
		Scanner reader = new Scanner(file);
		
	    int wordCount = 0;
		
	    // 1. read file line by line, count # of words, accumulate result
	    // 2. this approach is faster for large file, limits stack overflow error
		while(reader.hasNext())
			wordCount += reader.nextLine().trim().split("\\s+").length;
		
	    reader.close();
	    return wordCount;
	}

	public static int lineCount(String path) throws FileNotFoundException
	{
		// File object
		File file = new File(path);
		
		// file existence check
		if(!file.exists())  
			throw new FileNotFoundException();
		
		Scanner reader = new Scanner(file);
		
	    int lineCount = 0;
		
		while(reader.hasNextLine())
		{
			reader.nextLine();
			lineCount++;
		} 
		
	    reader.close();
	    return lineCount;
	}

	public static void main(String[] args)
	{
		try
		{
			//This almost seems to count new lines as word, which is odd
			System.out.println(lineCount("src/WordFile/ProjectTextFile.txt"));
		} 
		catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}