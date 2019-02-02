import Jcg.geometry.PointCloud_3;
import Jcg.geometry.Point_3;

import java.util.ArrayList;


/**
 *
 * Class to test the WSPD on small examples
 */
public class test1 {

        public static void main(String args[]) {

            Point_3 a = new Point_3(-1,-1,-1);
            Point_3 b = new Point_3(1,1,1);
            Point_3 c = new Point_3(1.01,1,1);
            Point_3 d = new Point_3(-1.01,-1,-1);
            Point_3 e = new Point_3(1.01,1.01,1);
            Point_3 f = new Point_3(1.01,1.02,1);



            //Point_3 c = new Point_3(-1,3,1);
            //Point_3 d = new Point_3(-2,-3,1);
            //Point_3 e = new Point_3(3,-4,1);
            //Point_3 f = new Point_3(1,2,1);

            //Point_3[] points = new Point_3[]{a,b,c,d,e,f};
            Point_3[] points = new Point_3[]{a,b, c, d, e , f};



            Octree o = new Octree(points);

            System.out.println("octree = " + o);
            System.out.println();


//            System.out.println(o.root.toString());

            WSPD w = new WSPD(o, 0.1);

//            System.out.println(o.root.getPoints().toString());
//
            System.out.println(w.listOfWSPD.size());
            int i =1;
            OctreeNode[] test_pair = w.listOfWSPD.get(0);
            for (OctreeNode[] pair : w.listOfWSPD) {
                System.out.println("Paire " + i);
                System.out.print("Points u :");
                System.out.println(pair[0].getPoints());
//                if ((double)test_pair[0].p.distanceFrom(pair[0].p) < 0.1 && (double)test_pair[1].p.distanceFrom(pair[1].p) < 0.1) {
//                    System.out.println(pair[0].p);
//                    System.out.println(pair[1].p);
//                    System.out.println();
//
//                }
                System.out.print("Points v :");
                System.out.println(pair[1].getPoints());
                i++;
            }






        }
}


