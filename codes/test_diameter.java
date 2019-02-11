
import Jcg.geometry.Point_3;
import com.opencsv.CSVWriter;
import jdg.layout.Layout;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Marc
 */
public class test_diameter {
    public static void main(String[] args){




        Point_3[] resSlow;
        long tpsFin;
        long tpsDep;
        Point_3[] resFast;



        File file = new File("diameter.csv" );
        // create FileWriter object with file as parameter

        double[] epsilon_values = {0.9, 0.5, 0.1};
        int[] n_values = {10, 50, 100, 500, 1000, 2000, 5000};
        try {
            FileWriter outputfile = new FileWriter(file);

            // create CSVWriter object filewriter object as parameter
            CSVWriter writer = new CSVWriter(outputfile);

            // adding header to csv
            String[] header = {"n", "fast", "t", "espilon"};
            writer.writeNext(header);

            for (int n:n_values){
                for(int k=0; k<20; k+=1) {
                    Point_3[] ptAleatoires = new Point_3[n];
                    for (int i = 0; i < ptAleatoires.length; i++) {
                        ptAleatoires[i] = new Point_3(100 * Math.random(), 100. * Math.random(), 100 * Math.random());

                    }

                    for (double current_epsilon : epsilon_values) {
                        tpsDep = System.currentTimeMillis();
                        resFast = new FastDiameter_3(current_epsilon).findFarthestPair(ptAleatoires);
                        tpsFin = System.currentTimeMillis();
                        String[] data1 = {String.valueOf(n), "1", String.valueOf(tpsFin - tpsDep), String.valueOf(current_epsilon)};
                        writer.writeNext(data1);

                    }
                    tpsDep = System.currentTimeMillis();
                    resSlow = new SlowDiameter_3().findFarthestPair(ptAleatoires);
                    tpsFin = System.currentTimeMillis();
                    String[] data1 = {String.valueOf(n), "0", String.valueOf(tpsFin - tpsDep), "0"};
                    writer.writeNext(data1);
                }

            }


            writer.close();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    }
}
