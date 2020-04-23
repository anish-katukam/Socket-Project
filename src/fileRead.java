import java.util.ArrayList;
import java.io.*;

public class fileRead {
    public static void main(String[] args) {

        
        //~~~~~~~~~~~~~~~~~~CSV PARSER~~~~~~~~~~~~~~~~~//
        ArrayList<String[]> tuples = new ArrayList<String[]>(); 
        File file = new File("src/StatsCountry.csv");

        try{
            BufferedReader csvReader = new BufferedReader(new FileReader(file));
            String line = "";
            try{
                while((line = csvReader.readLine()) != null){
                    String[] soloTuple = line.split(",");
                    tuples.add(soloTuple);
                }
            }
            catch(IOException e){
                e.printStackTrace();
            }
        }
        catch(FileNotFoundException e){
            e.printStackTrace();
        }
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//


        for(int i = 0; i < tuples.size(); i++){
            for(int j = 0; j < tuples.get(i).length; j++){
                System.out.print(tuples.get(i)[j] + " ");
            }
            System.out.println();
        }
    }
}