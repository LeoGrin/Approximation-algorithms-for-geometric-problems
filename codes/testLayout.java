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

public class testLayout {
    public static void main(String args[]) {
        System.out.println("PI INF421 (2018)");
        if(args.length==0 || args.length>1) {
            System.out.println("Error: wrong arguments, one parameter required");
            System.out.println("Usage example:  java -jar NetworkLayout networks/network.mtx");

            System.exit(0);
        }
        if(args[0].endsWith(".mtx")==false) {
            System.out.println("Error: wrong input format (MTX format supported)");
            System.exit(0);
        }

        String filename=args[0];
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


        String prefix = "/Users/anne/IdeaProjects/PI/src/data/networks/";
        String suffix = ".mtx";
        List<String> messages = Arrays.asList("3elt", "ash85", "barth5", "bcsstk31", "dwt_592", "facebook", "fe_4elt2", "jagmesh7", "karate", "power_grid_4k", "qh882", "santa_fe", "siggraph_2005", "west0497");

        File file = new File("t_variable_graphs.csv" );
        // create FileWriter object with file as parameter
        try {
            FileWriter outputfile = new FileWriter(file);

            // create CSVWriter object filewriter object as parameter
            CSVWriter writer = new CSVWriter(outputfile);

            // adding header to csv
            String[] header = {"name", "n", "m","fast", "t", "nb_iter", "s", "dt"};
            double[] dt_values = {1, 5, 10, 20, 100};

            writer.writeNext(header);


            for (String message : messages) {
                System.out.println(prefix + message + suffix);
                g = reader.read(prefix + message + suffix); // read input network from file
                n  = String.valueOf(g.sizeVertices());
                m = String.valueOf(g.sizeEdges());
                name = message;
                for (double current_dt:dt_values) {
                    layout = new FastFR91Layout(g, 400, 400);
                    layout.setDt(current_dt);
                    //slow_layout = new FR91Layout(g, 400, 400);
                    Layout.setRandomPoints(g, 400, 400); // set initial locations at random
                    while (true) {
                        layout.computeLayout();
                        if (layout.iterationCount > 150) {
                            // add data to csv
                            t = String.valueOf(layout.accumulated_time);
                            nb_iter = String.valueOf(layout.iterationCount);
                            String[] data1 = {name, n, m, "1", t, nb_iter, "1", String.valueOf(current_dt)};
                            writer.writeNext(data1);

                            break;
                        }

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
