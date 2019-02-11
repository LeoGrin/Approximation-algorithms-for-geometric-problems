import com.opencsv.CSVWriter;
import jdg.graph.AdjacencyListGraph;
import jdg.io.GraphReader;
import jdg.io.GraphReader_MTX;
import jdg.layout.Layout;
import processing.core.PApplet;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class testLayoutDt {
    public static void main(String args[]) {


        GraphReader reader=new GraphReader_MTX(); // open networks stores in Matrix Market format (.mtx)
        AdjacencyListGraph g;
        FastFR91Layout layout;
        FR91Layout slow_layout;
        String n;
        String m;
        String t;
        String name;
        String nb_iter;
        String s;
        String dt;


        String filename = "/Users/anne/IdeaProjects/PI/src/data/networks/facebook.mtx";
        g = reader.read(filename);

        File file = new File("s1t_variable.csv" );
        // create FileWriter object with file as parameter

        double[] dt_values = {1, 5, 10, 20, 100};
        try {
            FileWriter outputfile = new FileWriter(file);

            // create CSVWriter object filewriter object as parameter
            CSVWriter writer = new CSVWriter(outputfile);

            // adding header to csv
            String[] header = {"name", "n", "m","fast", "t", "nb_iter", "s", "dt"};
            writer.writeNext(header);

            for (double current_dt:dt_values) {
                layout = new FastFR91Layout(g, 400, 400);
                layout.setDt(current_dt);
                Layout.setRandomPoints(g, 400, 400); // set initial locations at random
                n = String.valueOf(g.sizeVertices());
                m = String.valueOf(g.sizeEdges());

                while (true) {
                    layout.computeLayout();
                    if (layout.iterationCount > 300) {
                        // add data to csv
                        t = String.valueOf(layout.accumulated_time);
                        nb_iter = String.valueOf(layout.iterationCount);
                        String[] data1 = {"facebook", n, m, "1", t, nb_iter, "1", String.valueOf(current_dt)};
                        writer.writeNext(data1);

                        break;
                    }

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
