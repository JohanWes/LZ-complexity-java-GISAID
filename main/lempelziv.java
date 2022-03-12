/**
 * Performs the LZ-complexity algorithm on a given string 
 * 
 * 1. Convert .txt into string
 * 2. Parse string into stringarray based on regex conditions (split on >hCoV.....year)
 * 3. Convert String to binary sequence
 * 4. Run LZ-complexity on each string in the array
 * 5. Print results to some file
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Duration;
import java.time.Instant;

public class lempelziv
{  
    public static void main(String[] args) throws IOException{


        //timing related objects and variables
        Instant start = Instant.now();
        Instant end;
        Duration timeElapsed; 
        Double timeElapsedSeconds;
        Double secondsRemainining;
        Duration timeRemaining;   
        int percentageCounter = 0;

        PrintWriter out = new PrintWriter("main/lzoutput.txt");

        //Converts .txt to a String
        String filePath = "main/180_samples.txt";        
        String input = usingBufferedReader(filePath);  
        //Count number of N's    
        //double nCount = input.length() - input.replace("N", "").length();        
        //Split String into an array based on regex to track dna header
        String splitString[] = input.trim().split(">.*(20..)");

        out.println("NUMBER OF DATAPOINTS: " + (splitString.length-1) + "\n" + "NUMBER OF N's: " + nCount(input) + " , AVERAGE: " + nCount(input)/(splitString.length-1) + " PER ENTRY" + "\n");

        //Iterates through the whole splitString array and performs the LZ-complexity algorithm
        //Start from 1, it will put some metadata into the first index because of the regex conditions so ignore it.
        for (int i = 1; i < splitString.length; i++)
        {
            StringBuilder sb = new StringBuilder();

            //Converts a String into an 8-bit binary sequence
            char[] chars = splitString[i].toCharArray();
            for (char c : chars){
                String binary = Integer.toBinaryString(c);
                String formatted = String.format("%8s", binary);
                String output = formatted.replaceAll(" ", "0");
                sb.append(output);

                //with or without spaces between each 8-bit number. Barely affects performance nor LZ-complexity number.
                //sb.append(" ");                
            }
            out.println(lempelZivComplexity(sb.toString()) + ", " + (int)nCount(splitString[i]));   
            percentageCounter++;
            //Assuming that sequencing is for 1000 entries
            //TODO: Not very scalable - support any size of entries? 
            if(percentageCounter % 10 == 0){                   
                end = Instant.now();
                timeElapsed = Duration.between(start, end);   
                timeElapsedSeconds = (double) timeElapsed.toSeconds();  
                secondsRemainining = timeElapsedSeconds*(100-(percentageCounter/10))/(percentageCounter/10);
                timeRemaining = Duration.ofSeconds(secondsRemainining.intValue());       
                System.out.println(percentageCounter/10 + "% - TIME ELAPSED: " + createTime(timeElapsed) + ", TIME REMAINING: " + createTime(timeRemaining));                
            }
        }        
        out.close();
    }  
    
    /**
     * Counts the number of N in a string
     * @param countedString any String
     * @return the number of N's in countedString
     */
    public static double nCount(String countedString){
        return countedString.length() - countedString.replace("N", "").length();
    }

    /**
     * Creates a time String using a Duration object. Formats as (hh:mm:ss)
     * @param dur the incoming Duration object, used while timing 
     * @return a String on 
     */
    public static String createTime(Duration dur)
    {
        int hours = (int) (dur.toSeconds() / 3600);
        int minutes = (int) ((dur.toSeconds() % 3600) / 60);
        int seconds = (int) (dur.toSeconds() % 60);
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);        
    }

    /**
     * Calculates the LZ complexity of a given binary sequence in a linear number of operations O(n) = length(sequence)
     * 
     * u is the length of the current prefix
     * v is the length of the current component for the current pointer p
     * vmax is the final length used for the current compontent (largest on all the possible pointers p)
     * @param sequence an 8 bit binary sequence
     * @return the Lempel-Ziv Complexity C, incremented iteratively
     */
    public static int lempelZivComplexity (String sequence){

        int i = 0;
        int c = 1;
        int u = 1;
        int v = 1;
        int vmax = v;

        while (u+v <= sequence.length() - 1){
            if (sequence.charAt(i+v) == sequence.charAt(u + v)){
                v = v + 1;
            }
            else{                
                vmax = Math.max(v, vmax);
                i = i + 1;
                if (i == u){
                    c = c + 1;
                    u = u + vmax;
                    v = 1;
                    i = 0;
                    vmax = v;
                }
                else{
                    v = 1;
                }
            }            
        }
        if (v != 1){
            c = c + 1;
        }
        return c;
    }

    /**
     * Converts a .txt file into a java String. BufferedReader reads one line at a time
     * @param filePath the file path of the String
     * @return the txt file expressed as a String
     */
    private static String usingBufferedReader(String filePath) 
    {
        StringBuilder contentBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))){ 

            String sCurrentLine;

            while ((sCurrentLine = br.readLine()) != null){ 
                contentBuilder.append(sCurrentLine).append("\n"); 
            }
        } 
        catch (IOException e){
            e.printStackTrace();
        }
        return contentBuilder.toString();
    }

}