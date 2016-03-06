package commentRemover;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

public class Remover {
	
	private Remover()
	{
		// don't allow use of constructor
	}
	
	public static void main(String[] args) throws IOException
	{
		String filename = "/Users/me/Desktop/sampleText.txt";
		String outputFilename = "/Users/me/Desktop/sampleText_out.txt";
		int bytesToRead = 1000;
		String encoding = "UTF-8";
		
		try(OutputStream out = new FileOutputStream(outputFilename)) {
			removeComments(filename, out, bytesToRead, encoding);
		}
		
	}
	
	public static void removeComments(String filename, OutputStream out, int bytesToRead, String encoding) throws FileNotFoundException, IOException
	{
		InputStream input = new FileInputStream(filename);
		
		try
		{
			byte[] buf = new byte[bytesToRead];
			int bytesRead = 0;
			
			boolean noPrint = false;
			Character prevChar = null;
			
			// input.read returns -1 if end of document
			// read until end of document
			while((bytesRead = input.read(buf)) != -1)
			{
				String str = new String(buf, 0, bytesRead, Charset.forName(encoding));
				String matchedEnd = "";
	
				for (char c : str.toCharArray())
				{
					if (!noPrint)
					{
						//beginning of doc or beginning of non-comment
						if (prevChar == null)
						{
							prevChar = c;
							continue;
						}
						// prevChar not a start of a comment
						else if (prevChar != '/')
						{
							out.write(prevChar.toString().getBytes());
						}

						// at this point we know prevChar = '/'
						// now check for current char... if '/' or '*'... it's the start of comment
						// if true, set the matchedEnd to look for.
						else if (c == '/')
						{
							noPrint = true;
							matchedEnd = "\n";
						}
						else if (c == '*')
						{
							noPrint = true;
							matchedEnd = "*/";
						}

						prevChar = c;
						continue;
					}

					// noPrint = True
					else
					{
						// check for matchedEnd... if true, turn printing back on
						if ((prevChar == '*' && c == '/' && matchedEnd.equals("*/")) ||
								(c == '\n' && matchedEnd.equals("\n")))
						{
							noPrint = false;
							matchedEnd = "";
							prevChar = null;
							continue;
						}
						
						prevChar = c; 
						continue;
					
					}
				}	
			}//end of document
			
			// now take care of last char that was read
			if (!noPrint && prevChar != null)
			{
				out.write(prevChar.toString().getBytes());
			}
		}
		finally
		{
			try {
				input.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
