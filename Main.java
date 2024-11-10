import com.opencsv.exceptions.CsvValidationException;

import java.io.IOException;

public class Main {


    public static void main(String[] args) throws IOException, CsvValidationException {
        //Utilerias ut=new Utilerias("testInstance.csv");
        Conf conf=new Conf(20,1,3,100, 500,"Test.csv");
        SimModel sim=new SimModel(conf);
        //sim.generate(conf);
        if (conf.getInstance().isEmpty()){
            sim.generate(conf);
        }
        else{
            sim.start();
        }
    }
}