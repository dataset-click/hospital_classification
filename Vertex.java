/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dijkstratools.pkg2;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;


/**
 *
 * @author kikim
 */
public class Vertex implements Serializable{
   //general attributes
    String info;    
    String color=""; //this will be applied to generator points only. For web visualisation only
    String fname="";

    int out; //to show number of edges
    int proc;    

    double latitude,
           longitude,
           distance,findDistance=0,
           physical_distance=0,//physical distance in metres
           speedlimit=0,
           traffic_wait=0, //waiting time 30 seconds
           traffic_prob=General.getChance(0), // probablity between 0.00 - 1.00,
            traffic_time=0, //how many times can get caught in the traffic light?
           z=0, //support for 3D
           weight=1; //by default, no weight
           
    

    adjList adjacent;

    //attributes belong to vertices
    String group="",orderk="",orderedk="",trk_ele="",trk_time="";
    Vertex next; 
    Vertex owner;
    Vertex via; //record who can reach this node
    Vertex own1,own2,via1,via2,via3, //these parameters only applied for border
            p_nearest,
            own3=null, //for nnh identifier or order-2
            activeGateway = null //active gateway, for simmulation
            ;

    ArrayList<orderK> order = new ArrayList<>();
    ArrayList<String> orderF = new ArrayList<>();
    ArrayList<Vertex> delaunay = new ArrayList<>(); //for delaunay triangulation
    //proposed failure concept is actually for delaunay triangulation, not for the graph
    
    
    //attributes for borders
    double  distance1,distance2,distance3, //only for borders
            overlap_distance,
            activeDistance;
    
    boolean taken=false, //flag in Dijkstra
            border, //flag as border point
            nvd_last=false //flag as the border node in NVD
            , traffic_light=false //by default, a node is not a traffic light
            ;

    
    //attributes belong to facilities
    int total_member=0, //to show members of NVD
        fail_member=0,//number of member during failure simmulation
        hops_total=0,
        hops_max=0,
        hops_add_nvd=0,
        fidx=-1;
    
    adjList facility_adjacent;
    
    double  f_nearest, //nearest distance to point from a facility point
            max_distance=0, //maximum distance to the border. F only
            farthest=0;

    boolean isFacility, //flag as facility point
            isFacilityFailure = false, //for failure simmulation. Belongs to facility only
            isNewFacility=false //for border point
            ;

    Vertex nextFacility; 

    pathList fProcess, fTaken;
    
    int[] max_hop_array; 
    int[] member_k;
    int[] stat_hops = new int[60];
    int k;
    
    String report="";
    
    
    //for Trajectory
    //use group to indicate group path
    //use 
    String road_name="";
    int mbrIdx=-1;
//-----------------------------------------------------------------------------            

    
    public Vertex(String s, double lat, double lon, int k)
    {
        this.info=s;
        this.latitude=lat;
        this.longitude=lon;
        this.isFacility=false;
        this.taken=false;
        this.next=null; //list of points
        this.nextFacility=null;//list of facility
        this.adjacent = new adjList(); //prepare for list of adjacents
        this.facility_adjacent = new adjList();
        this.fProcess = new pathList();
        this.fTaken=new pathList();
        this.owner=null;
        this.out=0;
        this.proc=0;
        this.distance=Calculate.max_dist;//set distance to max
        this.via=null;
        this.group="";
        this.f_nearest=Calculate.max_dist;//set nearest points to max
        this.overlap_distance=0;
        this.max_distance=0;
        this.k = k;
        
        //disable this step for orderdijkstra#1
        //for (int i=0;i<k+1;i++)
       //     order.add(new orderK());
    }
    
    
    public Vertex(Vertex v)
    {
       //clone vertex
        this.info=v.info;
        this.latitude=v.latitude;
        this.longitude=v.longitude;
        this.isFacility=v.isFacility;
        this.border=v.border;
        this.taken=false;
        this.next=null; //list of points
        this.nextFacility=null;//list of facility
        this.adjacent = new adjList(); //prepare for list of adjacents
        this.facility_adjacent = new adjList();
        this.fProcess = new pathList();
        this.fTaken=new pathList();
        this.owner=null;
        this.out=0;
        this.proc=0;
        this.distance=Calculate.max_dist;;//set distance to max
        this.via=null;
        this.group=v.group;
        this.f_nearest=Calculate.max_dist;;//set nearest points to max
        this.overlap_distance=0;        
        this.max_distance=0;
        this.k = v.k;
        
        this.road_name = v.road_name;
        this.mbrIdx = v.mbrIdx;
        
        //for GPX
        this.trk_ele=v.trk_ele;
        this.trk_time=v.trk_time;
        
        //disable this step for orderdijkstra#1
        //for (int i=0;i<k;i++)
        //    order.add(new orderK(v.order.get(i)));
    }

    
   public Vertex(String s, double lat, double lon)
    {
        this.info=s;
        this.latitude=lat;
        this.longitude=lon;
        this.isFacility=false;
        this.taken=false;
        this.next=null; //list of points
        this.nextFacility=null;//list of facility
        this.adjacent = new adjList(); //prepare for list of adjacents
        this.facility_adjacent = new adjList();
        this.fProcess = new pathList();
        this.fTaken=new pathList();
        this.owner=null;
        this.out=0;
        this.proc=0;
        this.distance=Calculate.max_dist;;//set distance to max
        this.via=null;
        this.group="";
        this.f_nearest=Calculate.max_dist;;//set nearest points to max
        this.overlap_distance=0;
        this.max_distance=0;
        this.k = k;        
        //disable this step for orderdijkstra#1
        //for (int i=0;i<k+1;i++)
       //     order.add(new orderK());
    }

   public Vertex(String s, double lat, double lon, String group)
    {
        this.info=s;
        this.latitude=lat;
        this.longitude=lon;
        this.isFacility=false;
        this.taken=false;
        this.next=null; //list of points
        this.nextFacility=null;//list of facility
        this.adjacent = new adjList(); //prepare for list of adjacents
        this.facility_adjacent = new adjList();
        this.fProcess = new pathList();
        this.fTaken=new pathList();
        this.owner=null;
        this.out=0;
        this.proc=0;
        this.distance=Calculate.max_dist;;//set distance to max
        this.via=null;
        this.f_nearest=Calculate.max_dist;;//set nearest points to max
        this.overlap_distance=0;
        this.max_distance=0;
        this.k = k;        
        this.group=group;
        //disable this step for orderdijkstra#1
        //for (int i=0;i<k+1;i++)
       //     order.add(new orderK());
    }
   
   //add order attributes
   
   public double getSpeed()
   {
       
       return 0;
   }
   
   public double getWait()
   {
       if ((traffic_light) && (traffic_prob==1))
       {
           //must stop
           return traffic_wait;           
       }
       else
       return 0;
   }

   public void setColor()
    {
        int R = (int)(Math.random()*256);
        int G = (int)(Math.random()*256);
        int B= (int)(Math.random()*256);
        this.color=R+","+G+","+B;
    }
   
   public  void addOrder()
   {
      System.out.println(" K : "+k);              
      for (int i=0;i<k;i++)
         order.add(new orderK());
   }
   
   public String getLocation()
   {
       return this.latitude+","+this.longitude;
   }
   
    public String toGeom()
    {
//        System.out.println("ST_GEOMFROMTEXT('POINT("+this.longitude+" "+this.latitude+")',4326)");
        return "ST_GEOMFROMTEXT('POINT("+this.longitude+" "+this.latitude+")',4326)";
    }
  
    public Edges addAdjacent(Vertex to, double dist, ListEdges l)
    {
        Edges e = new Edges(this,to,dist); //this edges automatically link to the vertices
        //add physical distance
//        e.physical_distance = this.calculateDistance(to,false);

        //put this edge into list of Edges
        l.addEdges(e);
        //now we have to link back from the vertices to this edge
        this.adjacent.addAdjacent(new adjMember(e));//maintain adjacent list for each vertex
        to.adjacent.addAdjacent(new adjMember(e));     
        this.out++;
        to.out++;
//        System.out.println("in method : "+e);
        return e;
    }

    public void addFacilityAdjacent(Vertex to, double dist, ListEdges l)
    {
        Edges e = new Edges(this,to,dist); //this edges automatically link to the vertices
        //add physical distance
        e.physical_distance = this.calculateDistance(to,false);

        //put this edge into list of Edges
        l.addEdges(e);
        //now we have to link back from the vertices to this edge
        
        adjMember m = new adjMember(e);        
        this.facility_adjacent.addAdjacent(m);//maintain adjacent list for each vertex
        m = new adjMember(e);
        to.facility_adjacent.addAdjacent(m);     
    }
    
    
    public double getBearing(Vertex to)
    {
        double result=0;
        /*
        Bearing from point A to B, can be calculated as,
        ? = atan2(X,Y),
        where, X and Y are two quantities and can be calculated as:
        X = cos ?b * sin ?L
        Y = cos ?a * sin ?b � sin ?a * cos ?b * cos ?L
         * 
         */
        
        double X,Y,
                fromLat = Math.toRadians(this.latitude),
                fromLon = Math.toRadians(this.longitude),
                toLat = Math.toRadians(to.latitude),
                toLon = Math.toRadians(to.longitude);
        X = Math.cos(toLat)*Math.sin((toLon - fromLon));
//        System.out.println("cos("+to.getX()+")* sin("+(to.getY() - from.getY())+")");
//        System.out.println("cos ("+to.getX()+") = "+Math.cos(Math.toRadians(to.getX())));
//        System.out.println("X : "+X);
        Y = Math.cos(fromLat)*Math.sin(toLat)- Math.sin(fromLat)*Math.cos(toLat)*Math.cos((toLon - fromLon));
//        System.out.println("Y : "+Y);
        result = Math.toDegrees(Math.atan2(X, Y));
        result = Math.round(result*10);
//        System.out.println(result/100);        
        
        return Double.valueOf(result/10);
    }
    
    public boolean updateOrder(double dist, int loc)
    {
        //check if this vallue acceptable in order
        boolean ok=false, possible=true;
        int pos=-1,count=0;
//#        System.out.println("\n vertex "+this.info +" from loc "+loc);
        for (int i=0;i<this.order.size();i++)
        {
//#           System.out.println(this.order.get(i).distance+"("+i+") vs "+dist+"("+loc+")");
            if (this.order.get(i).distance<Calculate.max_dist)
                count++;
            if ((this.order.get(i).distance>dist) && (this.order.get(i).distance!=Calculate.max_dist))
                pos=i;
            
            if(i==loc) //if from the same F
            {
                if (dist>=this.order.get(i).distance)
                    possible=false;
            }
        }
        
//#        System.out.println(" Pos "+pos+ " count "+count);      
        


        if (possible)
        {
            if (count<this.k) //in the range of k
            {
                //possible to insert
//#                System.out.println("Possible to insert");
                //if not in the same location
                if (this.order.get(loc).distance<dist)
                {
                    //ignore
//#                    System.out.println("Ignore");
                    ok = false;
                }
                else if (this.order.get(loc).distance>dist)
                {
//#                    System.out.println("Replace idx "+loc+" dist "+this.order.get(loc).distance+" to "+dist);
                    this.order.get(loc).distance=dist;
                    if (dist>this.farthest)
                        this.farthest=dist;
                    ok=true;
                }
            }
            else
            {
                if (pos!=-1)
                {
                //replace the farthest
//#                System.out.println("farthest idx "+pos);                
//#                System.out.println("Replace farthest");
                this.order.get(pos).distance=Calculate.max_dist;;//reset it
                this.order.get(loc).distance=dist;
                this.farthest=dist;
                ok=true;
                }
            }
        }
        return ok;
    }
    
    public void addPusher(Vertex f)
    {
        //check if f exist in order
            
            
    }
    
    public int isFexist(DijkstraMember dm)
    {
        int stat=-1;
        
        for (int i=0;i<this.order.size();i++)
        {
            if (this.order.get(i).owner==dm.whos)
                stat=i;
        }
        
        return -1;
    }
    
    public boolean isEqual(Vertex v)
    {
        boolean status=false;
        
        if ((this.latitude==v.latitude)&&(this.longitude==v.longitude))
            status=true;
        
        return status;
    }
    public Edges getEdges(Vertex v)
    {
        Edges found=null;
        //get edges location for this and v2
        adjMember a = this.adjacent.first;
        
        while (a!=null)
        {
            if (a.edge.hasPairVertex(this, v))               
            {
                found=a.edge;
                break;
            }
            a = a.next;
        }
        return found;
    }
    
    public void displayEdges()
    {
        //display all edges from this vertex
    }

    public adjMember getAdjacent(Vertex v)
    {
        adjMember found=null;
        //get edges location for this and v2
        adjMember a = this.adjacent.first;
        
        while (a!=null)
        {
            if (a.edge.hasPairVertex(this, v))               
            {
                found=a;
                break;
            }
            a = a.next;
        }
        return found;
    }
    
    public double haversine(double lat1, double lon1, double lat2, double lon2) 
    {    double R = 6372.8; // In kilometers
        double dist=0;

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);
 
        double a = Math.pow(Math.sin(dLat / 2),2) + Math.pow(Math.sin(dLon / 2),2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.asin(Math.sqrt(a));
        dist= R * c; //in km
        
        //dist = (dist/50)*3600; //in sec
        
        return dist;
    }
/**
 * Calculate using haversine formula with hops parameter
 * @param x
 * @param hops
 * @return 
 */
    public double calculateDistance(Vertex x,boolean hops)
    {
        double result = 0;
        
        if (hops)
            result=1;
        else
//            result = Math.sqrt(Math.pow(this.latitude-x.latitude, 2)+Math.pow(this.longitude-x.longitude, 2));
            result=haversine(this.latitude,this.longitude,x.latitude,x.longitude);
        return result;
    }

/**
 * Calculate using haversine formula without hops parameter
 * @param x
 * @param hops
 * @return 
 */
    public double calculateDistance(Vertex x)
    {
        double result = 0;
        
//        result = Math.sqrt(Math.pow(this.latitude-x.latitude, 2)+Math.pow(this.longitude-x.longitude, 2));
            result=haversine(this.latitude,this.longitude,x.latitude,x.longitude);
            
        return result;
    }
    
    
    public double calculateEuclideanDistance(Vertex x)
    {
        double result = 0;
        
        result = Math.sqrt(Math.pow(this.latitude-x.latitude, 2)+Math.pow(this.longitude-x.longitude, 2));
//         result=haversine(this.latitude,this.longitude,x.latitude,x.longitude);
        return result;
    }
    
    public double getDistance(Vertex v)
    {
        Edges e = this.getEdges(v);
//        System.out.println("Edges between "+this.info+" and "+v.info+" : "+e);
        return e.distance;
    }
    
    public String getString()
    {
        return this.info+" ("+this.latitude+","+this.longitude+")";
    }
/**
 * 
 * @param sep separator character
 * @return 
 */
    public String getStringCSV(String sep)
    {
        return this.info+sep+this.latitude+sep+this.longitude;
    }

    
    public double getDistancetoGateway(Vertex g)
    {
        double res=0;
        
        for (int i=0;i<this.order.size();i++)
        {
            if (this.order.get(i).owner==g)
                res = this.order.get(i).hop;
        }
        
        return res;
    }
    
    public double getX()
    {
        return this.latitude;
    }
 
    public double getY()
    {
        return this.longitude;
    }
/**
 *  using Haversine formula
 * @param v
 * @param range
 * @return 
 */    
    public Vertex getIntermediatePoint(Vertex v, double range)
    {
        Vertex result = null;
        
        double dist = this.calculateDistance(v);
        double ratio = range / dist;
        double newX = this.latitude + ratio * (v.latitude - this.latitude);
        double newY = this.longitude + ratio * (v.longitude - this.longitude);
        result = new Vertex(this.info+"'",newX,newY,this.group);
        
        return result;        
    }

/**
 *  Using Euclidean distance
 * @param v
 * @param range
 * @return 
 */
    public Vertex getIntermediateEucPoint(Vertex v, double range)
    {
        Vertex result = null;
        
        double dist = this.calculateEuclideanDistance(v);
        double ratio = range / dist;
        double newX = this.latitude + ratio * (v.latitude - this.latitude);
        double newY = this.longitude + ratio * (v.longitude - this.longitude);
        result = new Vertex(this.info,newX,newY,this.group);
        return result;        
    }
    
    public double calculateBearing(Vertex v)
    {
        double result=0;
        /*
        Bearing from point A to B, can be calculated as,
        ? = atan2(X,Y),
        where, X and Y are two quantities and can be calculated as:
        X = cos ?b * sin ?L
        Y = cos ?a * sin ?b � sin ?a * cos ?b * cos ?L
         * 
         */
        
        double X,Y,
                fromLat = Math.toRadians(this.getX()),
                fromLon = Math.toRadians(this.getY()),
                toLat = Math.toRadians(v.getX()),
                toLon = Math.toRadians(v.getY());
        X = Math.cos(toLat)*Math.sin((toLon - fromLon));
//        System.out.println("cos("+to.getX()+")* sin("+(to.getY() - from.getY())+")");
//        System.out.println("cos ("+to.getX()+") = "+Math.cos(Math.toRadians(to.getX())));
//        System.out.println("X : "+X);
        Y = Math.cos(fromLat)*Math.sin(toLat)- Math.sin(fromLat)*Math.cos(toLat)*Math.cos((toLon - fromLon));
//        System.out.println("Y : "+Y);
        result = Math.toDegrees(Math.atan2(X, Y));
        result = Math.round(result*10);
//        System.out.println(result/100);        
        
        return Double.valueOf(result/10);
    }
}
