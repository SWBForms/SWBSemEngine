/*
 * SemanticWebBuilder es una plataforma para el desarrollo de portales y aplicaciones de integración,
 * colaboración y conocimiento, que gracias al uso de tecnología semántica puede generar contextos de
 * información alrededor de algún tema de interés o bien integrar información y aplicaciones de diferentes
 * fuentes, donde a la información se le asigna un significado, de forma que pueda ser interpretada y
 * procesada por personas y/o sistemas, es una creación original del Fondo de Información y Documentación
 * para la Industria INFOTEC, cuyo registro se encuentra actualmente en trámite.
 *
 * INFOTEC pone a su disposición la herramienta SemanticWebBuilder a través de su licenciamiento abierto al público (‘open source’),
 * en virtud del cual, usted podrá usarlo en las mismas condiciones con que INFOTEC lo ha diseñado y puesto a su disposición;
 * aprender de él; distribuirlo a terceros; acceder a su código fuente y modificarlo, y combinarlo o enlazarlo con otro software,
 * todo ello de conformidad con los términos y condiciones de la LICENCIA ABIERTA AL PÚBLICO que otorga INFOTEC para la utilización
 * del SemanticWebBuilder 4.0.
 *
 * INFOTEC no otorga garantía sobre SemanticWebBuilder, de ninguna especie y naturaleza, ni implícita ni explícita,
 * siendo usted completamente responsable de la utilización que le dé y asumiendo la totalidad de los riesgos que puedan derivar
 * de la misma.
 *
 * Si usted tiene cualquier duda o comentario sobre SemanticWebBuilder, INFOTEC pone a su disposición la siguiente
 * dirección electrónica:
 *  http://www.semanticwebbuilder.org
 */
package org.semanticwb.store.jenaimp;

import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.graph.TripleMatch;
import com.hp.hpl.jena.graph.impl.GraphBase;
//import com.hp.hpl.jena.graph.query.QueryHandler;
import com.hp.hpl.jena.shared.PrefixMapping;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import org.semanticwb.rdf.GraphExt;

/**
 *
 * @author jei
 */
public class SWBTSGraph extends GraphBase implements GraphExt
{
    //private static Logger log = SWBUtils.getLogger(SWBTSGraph.class);

    private String name;
    private org.semanticwb.store.Graph g;

    private PrefixMapping pmap;
    //private BigdataTransactionHandler trans;

    //SWBTSQueryHandler query=null;

    public SWBTSGraph(org.semanticwb.store.Graph g)
    {
        this.g=g;
        pmap=new SWBTSPrefixMapping(g);
        //query=new SWBTSQueryHandler(this);
    }

    @Override
    public ExtendedIterator<Triple> graphBaseFind(TripleMatch tm)
    {
        return new SWBTSIterator(g.findTriples(SWBTSUtils.toSWBTriple(tm)));
    }

    @Override
    public void performAdd(Triple t)
    {
        g.addTriple(SWBTSUtils.toSWBTriple(t));
    }    

    @Override
    public void performDelete(Triple t)
    {
        g.removeTriples(SWBTSUtils.toSWBTriple(t));
    }    

    public String getName()
    {
        return name;
    }

    @Override
    public void close()
    {
        super.close();
        g.close();
    }

    @Override
    public PrefixMapping getPrefixMapping()
    {
        return pmap;
    }
/*
    @Override
    public QueryHandler queryHandler()
    {
        return query;
    }
*/    
    public org.semanticwb.store.Graph getGraphBase()
    {
        return g;
    }

    @Override
    public long count(TripleMatch tm, String stype)
    {
        return g.count(SWBTSUtils.toSWBTriple(tm), stype);
    }

    @Override
    public ExtendedIterator<Triple> find(TripleMatch tm, String stype, Long limit, Long offset, String order)
    {
        boolean reverse=false;
        if(order!=null && order.indexOf("desc")>-1)reverse=true;        
        return new SWBTSIterator(g.findTriples(SWBTSUtils.toSWBTriple(tm),stype,limit,offset,reverse));     
    }
}

