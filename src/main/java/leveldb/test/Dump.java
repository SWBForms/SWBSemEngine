package leveldb.test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.semanticwb.store.Graph;
import org.semanticwb.store.Triple;
import org.semanticwb.store.TripleIterator;
import org.semanticwb.store.leveldb.GraphImp;

/**
 *
 * @author serch
 */
public class Dump {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, InterruptedException {

        long time = System.currentTimeMillis();
        Map params = new HashMap();
        params.put("path", "/data/leveldb/");

        //Graph tGraph = new GraphImp("infoboxes", params);      
        Graph graph = new GraphImp("SWBF2", params);

        System.out.println("Count:" + graph.count());
        System.out.println("time: " + (System.currentTimeMillis() - time));
        time = System.currentTimeMillis();

        TripleIterator it = graph.findTriples(new Triple((String)null, null, null));
        while (it.hasNext()) {
            Triple triple = it.next();
            System.out.println(triple);
        }

        //System.out.println("it:"+it);
        System.out.println("time: " + (System.currentTimeMillis() - time));
        time = System.currentTimeMillis();

        graph.close();

        System.out.println("time: " + (System.currentTimeMillis() - time));
    }

}
