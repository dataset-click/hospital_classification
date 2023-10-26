/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dijkstratools.pkg2;

import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 *
 * @author kikim
 */
public class NetVoronoiLimit {
    Graph graph; 
    ListEdges allEdges; 
    Graph facility;
    double rangeLimit;
    ArrayList<Point2D> border = new ArrayList();

    public NetVoronoiLimit(Graph graph, ListEdges allEdges, Graph facility)
    {
        this.graph=graph;
        this.allEdges=allEdges;
        this.facility=facility;    
    }
    
    public void generateNVDLimit(double r)
    {       
        this.rangeLimit = r;
        this.djikstraLimit();
        this.displayDijkstraLimit();
        //THESE LINES MUST BE PROCESSED TO GET NVD
        
        this.NVDEdgesLimit(); //splitting points
        this.checkEdgesLimit();  
        this.getBorder();
    }
    
    public void nvdStatistic()
    {
        Vertex v = graph.first;
        while (v!=null)
        {
            if (v.owner!=null)
                System.out.println(v.info+" : "+v.adjacent.total_member);
        }
    }
    
    
    
    public void djikstraLimit()
    {
        System.out.println("Start Dijkstra");
        //test djikstra's algorithm
        Vertex vx,to, firstf;
        DijkstraMember dm;
        adjMember m;
        double max_distance=0;
        boolean something=false;
        
        //put all facilities in process
        DijkstraList process =new DijkstraList();
        firstf = facility.first;
        while (firstf!=null)
        {
            firstf.distance=0;
            firstf.physical_distance=0;
            process.insertMember(new DijkstraMember(firstf));
            //all facilities must be final
            //firstf.proc=firstf.out;          
            
            //for each F, get the adjacent
            System.out.println("Process facility "+firstf.info);
            m=firstf.adjacent.first;
            while (m!=null)
            {
                //put all adjacent into the process
                to=m.edge.getToAddr(firstf);//get facility's adjacents
                //System.out.println(to.info+" : "+m.edge.distance);
                //cannot have facility as neighbours
                if ((!to.isFacility)&&(m.edge.distance<=rangeLimit))                
                {
                    //if within range
                    if (to.owner==null)
                    {
                        //if no owner
                        to.owner=firstf;//claim
                        //to.nvd_prev=firstf;

                        to.distance=m.edge.distance+to.getWait();
                        to.physical_distance = m.edge.physical_distance;
                        if (m.edge.distance+to.getWait()<firstf.f_nearest)
                        {
                            firstf.f_nearest=m.edge.distance+to.getWait();
                        }
                        to.proc++;
//                        System.out.println("TAKE "+to.info+" for "+to.owner.info +" dist "+to.distance);  
                        to.via=firstf;
                        to.hops_total=firstf.hops_total+1;
                        process.insertMember(new DijkstraMember(to));
                    }
                    else if (to.distance>m.edge.distance)
                    {
                        //claim back
                        to.owner=firstf;
                        //to.nvd_prev=firstf;
                        to.distance=m.edge.distance+to.getWait();
                        to.physical_distance = m.edge.physical_distance;
                        if (m.edge.distance+to.getWait()>max_distance)
                            max_distance=m.edge.distance+to.getWait();
                        if (m.edge.distance+to.getWait()<firstf.f_nearest)
                            firstf.f_nearest=m.edge.distance+to.getWait();
                        to.proc++;
                        to.via=firstf;
                        to.hops_total=firstf.hops_total+1;
//                        System.out.println("RETAKE "+to.info+" for "+to.owner.info);
                    }
                }
                else
                {
                    to.proc++;
                }
                
                m=m.next;
            }
            firstf.proc=firstf.out;
            firstf=firstf.nextFacility;          
        }
        
        System.out.println("\nDONE F PREPARATION\n");
        
        dm = process.first;
        System.out.println("first dm "+dm.member.info);
        while(dm!=null)// first run
        {
//        System.out.println("dm "+dm.member.info);
           
            if ((dm.member.proc<dm.member.out)) //if this node hasn't been processed completely
            {
//                    System.out.println("\n"+dm.member.info+" ("+dm.member.proc +" / "+ dm.member.out+")");

                    //get the adjacent lists
                    m=dm.member.adjacent.first;
                    something=false;
                    while (m!=null)
                    {
                        to=m.edge.getToAddr(dm.member);
//                        System.out.println("Adjacent "+to.info+" ("+to.proc +" / "+ to.out+")");
                        if (dm.member.distance+m.edge.distance+to.getWait()<rangeLimit)
                        {   
                                if ((to.proc<to.out)&&(!to.isFacility)&&(to.owner!=dm.member.owner))//if it can be processed and it is not F
                                {
                                    //check the distance
                                    if (to.distance>dm.member.distance+m.edge.distance+to.getWait())
                                    {
//                                        System.out.println(to.distance+" "+dm.member.distance+" "+m.edge.distance);
                                        //take over
        //                                if (to.distance<999)
        //                                  System.out.println("RETAKE "+to.info+" from "+to.owner.info+" ("+to.distance+") Proc "+to.proc+" to "+dm.member.owner.info+" dist "+(dm.member.distance+m.edge.distance));
        //                                else
        //                                  System.out.println("NEW "+to.info+" ("+to.distance+") Proc "+to.proc+" to "+dm.member.owner.info+" dist "+(dm.member.distance+m.edge.distance));

                                        to.distance=dm.member.distance+m.edge.distance+to.getWait();
                                        to.physical_distance = dm.member.physical_distance+m.edge.physical_distance;
                                        to.owner=dm.member.owner;
                                        //to.nvd_prev=dm.member;
                                        to.via=dm.member;
                                        to.hops_total=dm.member.hops_total+1;
                                        if (to.proc==0) //new 
                                        {
//                                            System.out.println("ADD to Process "+to.info+" for "+to.owner.info+" Proc "+to.proc);
                                            process.insertMember(new DijkstraMember(to));
                                        }
                                        to.proc++;
                                        something=true;

                                    }
                                    else
                                    {
                                        //nothing we can do. The distance is optimum. mark process
                                        to.proc++;
//                                        System.out.println("Skip "+to.info+" ("+to.proc +" / "+ to.out+") (Optimal Distance)"+to.distance);
                                    }
                                }
                                else
                                {
          //                          System.out.println("Skip "+to.info+" (FINAL)");
          //                          System.out.println(to.owner.info+"|"+dm.member.owner.info);
                                    if (to.owner==dm.member.owner)
                                    {
                                        //if they belong to the same F
                                        //check which one is the shortest one
                                        if (to.distance>dm.member.distance+m.edge.distance+to.getWait())
                                        {
                                            //take over
        //                                    System.out.println("RETAKE "+to.info+" for "+to.owner.info+" ("+to.distance+") Proc"+to.proc + "("+(dm.member.distance+m.edge.distance)+")");
                                            to.distance=dm.member.distance+m.edge.distance+to.getWait();
                                            to.physical_distance=dm.member.physical_distance+m.edge.physical_distance;
                                            to.owner=dm.member.owner;
                                            to.via=dm.member;
                                            to.hops_total=dm.member.hops_total+1;
                                            to.proc++;
                                        }

                                    }
                                    //do nothing, skip. These points are final
                                }
                        }
                        else
                        {
                            to.proc++;
                        }
                        m=m.next;
                   }
                //compare the distance and can be take over
               //reset pointer to the first
               //if (something)
               //     dm=process.first;
               //else
                   dm=dm.next;
            }
            else
            {
                //no need to do anything           
                dm=dm.next;

            }
            
            
           // dm=dm.next;
        }
        
      
    }

    public void displayDijkstraLimit()
    {
        System.out.println("Displaying Djikstra result");
        Vertex v=graph.first;
        adjMember a;
        
        while(v!=null)
        {
            if (v==v.owner) //facility point
                
            {
//                System.out.println(v.getString() +" is Facility Point. Nearest distance to point is : "+v.f_nearest);
            }
            else if (v.owner!=null)
            {
//                System.out.println(v.info+" to "+v.via.info+" Distance ("+v.distance+" | Hops ("+v.hops_total+")to F :"+v.owner.info);
                v.owner.total_member++;
            }
            else
            {
                if ((v.own1==null)||(v.own2==null))
                {
//                   System.out.println("Unreachable Edges");
                }
                else
                {
//                System.out.println(v.getString() +" is Border ");
//                System.out.println(v.info+" to "+v.via1.info+" Distance ("+v.distance1+") F :"+v.own1.info);
//                System.out.println(v.info+" to "+v.via2.info+" Distance ("+v.distance2+") F :"+v.own2.info);
                }
                if (v.owner==null)
                {
//                    System.out.println(v.info+" is unused");
                }
            }    
            v=v.next;            
        }
    }

    public void NVDEdgesLimit()
    {
        System.out.println("Verify all Edges for NVD");
        boolean reset=false;
        Edges e = allEdges.first;
        double bpoint;
        Vertex border;
        int idx=1;
        while ((e!=null))
        {
            if (e.used)
            {
//                System.out.println("e is used : "+ e.getString());

                if (e.p1.owner==null)
                {
//                    System.out.println("p"+e.p1.getString()+" owner is NULL");
                }
                if (e.p2.owner==null)
                {
//                    System.out.println("p"+e.p2.getString()+" owner is NULL");
                }

                if (((!e.p1.border)&&(!e.p2.border))&&((e.p1.owner!=null)&&(e.p2.owner!=null)))//ignore unreachable edges
                {
//                        System.out.println("\nEdge "+e.getString());
//                        System.out.println(" -->"+e.p1.info+ " owner: "+e.p1.owner.info);
//                        System.out.println(" -->"+e.p2.info+ " owner: "+e.p2.owner.info);
                     if (e.p1.owner==e.p2.owner) //
                    {
                        e.owner=e.p1.owner; //claim this edge
//                        System.out.println("e owner : "+e.owner.info);
//                        System.out.println(" ==> e : "+e.owner.info);
//                        System.out.println(e.p1.info+","+e.p2.info+","+e.distance+" owner :"+e.owner.info);
                    }
                    else
                    {

                        //Problem in Dijkstra
                        //if the distance between points are not uniform (differences are huge), unequal splitting points will occur!
                        //you have to back track to fix the splitting point!
               //         System.out.println("Split");
                        bpoint=((e.p1.distance+e.distance+e.p2.distance)/2)-e.p1.distance;
//                        System.out.println(" p"+e.p1.getString()+" = "+e.p1.distance+ " | p"+e.p2.getString()+" = "+e.p2.distance);
//                        System.out.println("bpoint = "+bpoint+" | e = "+e.distance);
//                        System.out.println("Distance from p1 : "+bpoint+" = (("+e.p1.distance+"+"+e.distance+"+"+e.p2.distance+")/2)-"+e.p1.distance);

                        if ((bpoint<0)||(bpoint>e.distance))
                        {
//                            System.out.println("PROBLEM!!!");
                            //which one is longer? p1 or p2
                            bpoint-=e.distance;
                            if (e.p1.distance>e.p2.distance)
                            {
                                //take over p1 for p2
                                e.owner=e.p2.owner;
                                //take over p1 for p2;
                                e.p1.owner=e.p2.owner;
                                e.p1.via=e.p2; //p1 go to p2.owner through p2
                                e.p1.distance=e.p2.distance+e.distance;
//                                System.out.println(">>E="+e.owner.info+" E status = "+e.used+" p"+e.p1.info+"="+e.p1.owner.info + " p"+e.p2.info+"="+e.p2.owner.info);
//                                System.out.println("RESET.......");

                                reset=true;
                            }
                            else
                            {                            
                                e.owner=e.p1.owner;
                                //take over p2 for p1
                                e.p2.owner=e.p1.owner;
                                e.p2.via=e.p1; //p2 go to p1.owner through p1
                                e.p2.distance=e.p1.distance+e.distance;
//                                System.out.println(e.owner.info+" "+e.p1.owner.info + " "+e.p2.owner.info);
//                                System.out.println(">>E="+e.owner.info+" E status = "+e.used+" p"+e.p1.info+"="+e.p1.owner.info + " p"+e.p2.info+"="+e.p2.owner.info);
//                                System.out.println("RESET....");
                                reset=true;
                            }
//                            System.out.println(" ==> e : "+e.owner.info);
                        }
                        else
                        {
    //something wrong with splitting algorithm
    //will be disabled                    
                        //split this edge. create new splitting point
                      //  System.out.println(idx);
                        idx=graph.splitEdges(e.p1, e.p2, bpoint, idx, allEdges);
                        }
                    }
                }
                else
                {
//                    System.out.println("e is used but...");
                    if (e.owner==null)
                    {
//                        System.out.println("Owner is null");                        
                    }
                    else
                    {
//                        System.out.println("Owner "+e.owner.info);                 
                    }
/*                    
                    System.out.println("p1 "+e.p1.info+" owner "+e.p1.owner);
                    System.out.println("Border "+e.p1.border);
                    System.out.println("p2 "+e.p1.info+" owner "+e.p2.owner);
                    System.out.println("Border "+e.p2.border);
*/
                }

            }//end of e.used
            else
            {
                //System.out.println("From "+e.p1.info+" to "+e.p2.info+" is unused");
            }
//            System.out.println("RESET STATUS :: "+reset);
            if(!reset)
            {
                e=e.next;
            }
            else
            {
//                allEdges.setUnused();
                reset=false;
                e=allEdges.first;
            }

        }
        
        System.out.println("DONE NVDEdges check. e==NULL :"+(e==null));
    }
    
public void checkEdgesLimit()
{
    System.out.println("Check Edges");
    Edges e = allEdges.first;
    while (e!=null)
    {
        if (e.used)
        {
            if ((e.p1.owner!=null)&&(e.p2.owner!=null))
            {
                if((e.owner!=e.p1.owner)&&(e.owner!=e.p2.owner))
                {   
                    //System.out.println("PROBLEM : Edge Owner : "+e.owner.info+" , p1 owner : "+e.p1.owner.info + ", p2.owner : "+e.p2.owner.info);
                }
            }
            else if ((e.p1.owner!=null)&&(e.p2.owner==null))
            {
                if((e.owner!=e.p1.owner)) 
                {
                    //System.out.println("PROBLEM : Edge Owner : "+e.owner.info+" , p1 owner : "+e.p1.owner.info + ", p2.owner : unknown");
                }
            }
            else if ((e.p2.owner!=null)&&(e.p1.owner==null))
            {
                if((e.owner!=e.p2.owner)) 
                    {
                        //System.out.println("PROBLEM Edge Owner : "+e.owner.info+" , p1 owner : unknown , p2.owner : "+e.p2.owner.info);                           
                        if (e.p1.own1!=null)
                        {
                            //System.out.println("own1 : "+e.p1.own1.info);
                            if (e.p2.owner==e.p1.own1)
                                e.owner=e.p2.owner;
                            else
                                e.owner=e.p2.owner;
                            //System.out.println("Changing Edge owner to "+e.owner.info);
                        }
                        else
                        {
                            //System.out.println("own2 : "+e.p1.own2.info);
                        }                    
                    }
                    
            }
        }
                
        e=e.next;
    }
    }

    public void blankEdgesLimit()
    {
        //display edges after Dijkstra method to check edges between two owner
        Edges e = allEdges.first;
        System.out.println("Display all Border Edges ");
        
        while (e!=null)
        {
//            if ((e.p1.info.equalsIgnoreCase("14"))||(e.p2.info.equalsIgnoreCase("14")))
            if((e.p1.owner!=e.p2.owner)&&(e.p1.owner!=null)&&(e.p2.owner!=null))
            {
                System.out.println("Edge "+e.p1.info+"("+e.p1.owner.info+") - "+e.p2.info+"("+e.p2.owner.info+")");
                
                //register maximum distance to a generator points
            }
            e=e.next;
        }
    }
    
    public void getBorder()
    {       
        System.out.println("Border");
        Edges e = allEdges.first;
        while (e!=null)
        {
            if (((e.p1.owner==null)&&(e.p2.owner!=null))||((e.p2.owner==null)&&(e.p1.owner!=null)))
            {
                if (e.p1.owner!=null)
                {
//                    System.out.println("P1:"+e.p1.getStringCSV(","));
                    border.add(new Point2D.Double(e.p1.latitude,e.p1.longitude));
                }
                else
                {
//                    System.out.println("P2:"+e.p2.getStringCSV(","));
                    border.add(new Point2D.Double(e.p2.latitude,e.p2.longitude));
                    
                }
            }
            e=e.next;
        }
    }
}
