package freecharge.restcontroller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import freecharge.wordcount.job.WordCount;
import freecharge.wso.WordCountWSO;

@RestController
@RequestMapping("/word")
public class WordController {
	
	@Autowired
	private Environment env;

	/*
	 * Reads from the output file generated by MR job
	 * @Return word:count
	 */
	@RequestMapping("/count")
	public WordCountWSO getCount(@RequestParam(required=true, value="query") String queryWord) throws Exception{
		String path = env.getProperty("output.file.path");
		//path = path + "/part-r-00000";
		File directory = new File (path);
		File[] files = directory.listFiles();
		for (File file : files) {
			if (file.getName().startsWith("part")) {
				BufferedReader br = new BufferedReader(new FileReader (file));
				String line = null;
				
				try {
					while ((line = br.readLine()) != null) {
						//String[] content = line.split(" ");
						StringTokenizer content = new StringTokenizer(line);
						String word = content.nextToken();
						String count = content.nextToken();
						
						if (word.equals(queryWord)) {
							return new WordCountWSO (queryWord, Integer.valueOf(count));
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
					throw new Exception("Application failed." + e.getMessage());
				} finally {
					br.close();
				}
			}
		}
		
		return new WordCountWSO(queryWord, 0);
	}
	
	/*
	 * Execute MapReduce job for word count.
	 * Input and Output directory locations
	 * would be picked up from properties file
	 */
	@PostConstruct
    public void initialize() throws Exception{
            try{
            	String input = "file://" + env.getProperty("input.files.path");
            	String output = "file://" + env.getProperty("output.file.path");
            	String[] args = {input, output};
            	WordCount.jobRunner(args);
            }catch(Exception e){
            		e.printStackTrace();
                    throw new Exception("Failed to initialize application.");
            }
            
    }
}
