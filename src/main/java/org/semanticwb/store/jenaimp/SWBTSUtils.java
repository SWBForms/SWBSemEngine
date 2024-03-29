/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.semanticwb.store.jenaimp;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.hp.hpl.jena.datatypes.BaseDatatype;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.graph.TripleMatch;
import com.hp.hpl.jena.graph.impl.LiteralLabel;
import com.hp.hpl.jena.graph.impl.LiteralLabelFactory;
import com.hp.hpl.jena.rdf.model.AnonId;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author javier.solis.g
 */
public class SWBTSUtils
{
    private static final int CACHE=0;
    private static LoadingCache<org.semanticwb.store.Node, Node> toJena = null;      
    private static LoadingCache<Node, org.semanticwb.store.Node> toSWB = null;      
    
    static
    {
        if(CACHE>0)
        {
            toJena = CacheBuilder.newBuilder()
                .maximumSize(CACHE)
                .expireAfterAccess(10, TimeUnit.MINUTES)
                .build(new CacheLoader<org.semanticwb.store.Node, Node>()
                {
                    @Override
                    public Node load(org.semanticwb.store.Node key) 
                    {
                        Node node=_toJenaNode(key);
                        toSWB.put(node, key);
                        //System.out.println("toJena:"+key.getEncValue()+"-->"+node);
                        return node;
                    }
                });

            toSWB = CacheBuilder.newBuilder()
                .maximumSize(CACHE)
                //.expireAfterAccess(5, TimeUnit.MINUTES)
                .build(new CacheLoader<Node,org.semanticwb.store.Node>()
                {
                    @Override
                    public org.semanticwb.store.Node load(Node key) 
                    {
                        org.semanticwb.store.Node node=_toSWBNode(key);
                        toJena.put(node, key);
                        //System.out.println("toSWB:"+key+"-->"+node);
                        return node;
                    }
                });  
        }
    }
    
       /**
     * Regresa la representacion en String del nodo de RDF
     * @param node
     * @return
     */
    public static org.semanticwb.store.Node toSWBNode(Node node)//, StringBuilder comp)
    {
        if(node==null)return null;
        try
        {
            if(CACHE>0)
            {
                return toSWB.get(node);
            }else
            {
                return _toSWBNode(node);
            }
        }catch(Exception e)
        {
            throw new RuntimeException(e);
        }
    }    
    
    
       /**
     * Regresa la representacion en String del nodo de RDF
     * @param node
     * @return
     */
    public static org.semanticwb.store.Node _toSWBNode(Node node)//, StringBuilder comp)
    {
        if(node==null)return null;
        if(node.equals(Node.ANY))return null;
        if(node.isBlank())return new org.semanticwb.store.Resource(node.getBlankNodeId().toString());
        if(node.isURI())
        {
            return new org.semanticwb.store.Resource("<"+node.getURI()+">");
        }
        if(node.isLiteral())
        {
            LiteralLabel l=node.getLiteral();
            //System.out.print(l.getLexicalForm());            
            //System.out.println(" - "+l.getValue());            
            String type=l.getDatatypeURI();
            if(type!=null)type="<"+type+">";
            String lang=l.language();
            String value=l.getLexicalForm();
            return new org.semanticwb.store.Literal(value,lang,type);            
        }
        return null;
    }
    
    public static Node toJenaNode(org.semanticwb.store.Node n)
    {
        if(n==null)return null;
        try
        {
            if(CACHE>0)
            {
                return toJena.get(n);
            }else
            {
                return _toJenaNode(n);
            }
        }catch(Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    
    public static Node _toJenaNode(org.semanticwb.store.Node n)
    {
        //log.debug("string2Node:"+value+":"+ext);
        if(n==null)return null;

        if(n.isResource())
        {
            return Node.createURI(n.asResource().getUri());
        }
        if(n.isBlank())return Node.createAnon(new AnonId(n.asResource().getId()));
        //System.out.println("valueClass:"+value.getClass()+" "+value);
        if(n.isLiteral())
        {
            org.semanticwb.store.Literal l=n.asLiteral();
            String tk2=l.getType();
            if(tk2==null)tk2="";
            else if(tk2.charAt(0)=='<')tk2=tk2.substring(1,tk2.length()-1);
            String tk3=l.getLang();
            String tk4=l.getValue();
            
            //System.out.println("("+l.getDatatype().stringValue()+")");
            LiteralLabel ll = null;
            if(tk2.endsWith("#boolean"))
            {
                ll= LiteralLabelFactory.create(Boolean.parseBoolean(tk4));
            }else if(tk2.endsWith("#character"))
            {
                //ll=LiteralLabelFactory.create(tk4,tk3);
            }else if(tk2.endsWith("#decimal"))
            {
                ll=LiteralLabelFactory.create( new BigDecimal(tk4));
            }else if(tk2.endsWith("#double"))
            {
                ll=LiteralLabelFactory.create( Double.parseDouble(tk4));
            }else if(tk2.endsWith("#float"))
            {
                ll=LiteralLabelFactory.create( Float.parseFloat(tk4));
            }else if(tk2.endsWith("#integer"))
            {
                ll=LiteralLabelFactory.create( new BigInteger(tk4));
            }else if(tk2.endsWith("#int"))
            {
                ll=LiteralLabelFactory.create( Integer.parseInt(tk4));
            }else if(tk2.endsWith("#long"))
            {
                ll=LiteralLabelFactory.create( Long.parseLong(tk4));
            }else if(tk2.endsWith("#dateTime"))
            {
                //ll=LiteralLabelFactory.create( Long.parseLong(l.stringValue()));
            }else if(tk2.endsWith("#date"))
            {
                //ll=LiteralLabelFactory.create( Long.parseLong(l.stringValue()));
            }
            if(ll!=null)
            {
                return Node.createLiteral( ll );
            }else
            {
                if(tk2.length()>0)
                {
                    return Node.createLiteral(tk4, tk3, new BaseDatatype(tk2));
                }else
                {
                    return Node.createLiteral(tk4, tk3, null);
                }
            }
        }
        return null;
    }        
    
    public static org.semanticwb.store.Triple toSWBTriple(Triple t)
    {
        //System.out.println(t);
        return new org.semanticwb.store.Triple(SWBTSUtils.toSWBNode(t.getSubject()),SWBTSUtils.toSWBNode(t.getPredicate()),SWBTSUtils.toSWBNode(t.getObject()));
    }
    
    public static Triple toJenaTriple(org.semanticwb.store.Triple t)
    {
        //System.out.println(t);
        return new Triple(SWBTSUtils.toJenaNode(t.getSubject()),SWBTSUtils.toJenaNode(t.getProperty()),SWBTSUtils.toJenaNode(t.getObject()));
    }
    
    public static org.semanticwb.store.Triple toSWBTriple(TripleMatch tm)
    {
        return new org.semanticwb.store.Triple(SWBTSUtils.toSWBNode(tm.getMatchSubject()), SWBTSUtils.toSWBNode(tm.getMatchPredicate()), SWBTSUtils.toSWBNode(tm.getMatchObject()));
    }

    
}
