/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dijkstratools.pkg2;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author kikim
 */
public class Edges implements Serializable{
    Vertex p1,p2,owner,owner3=null;
    double distance;
    double physical_distance;
    double remain;
    Edges next;
    boolean used,taken=false;
    boolean first=true;//who get the first
    String orderk="",orderedk="";
    String road_type="residential",roadSurface="paved";
    String road_name="",osm_id="";
    double maxspeed=0;
    boolean querySegment=false;
    String edge_name="" ;
    ArrayList<used> traceF = new ArrayList<>();
    ArrayList<String> orderF = new ArrayList<>();
    
    ArrayList<Point2D> mbr = new ArrayList();
    Shapes mbrCh;

    
    
    
    public Edges(Vertex v1, Vertex v2, double dist)
    {
        this.p1=v1;
        this.p2=v2;
        this.distance=dist;
        this.remain=dist;
        next=null;
        this.used=true;
    }
    
    public int getUsed(Vertex v)
    {
        int result=-1;
        
        for (int i=0;i<traceF.size();i++)
        {
            if (traceF.get(i).usedby==v)
                result=i;
        }
        
        return result;
    }
    
    public String getToInfo(Vertex v)
    {
        //get vertex destination from vertex v
        if (p1==v)
            return p2.info;
        else
            return p1.info;
    }
    
    public boolean hasPairVertex(Vertex v1,Vertex v2)
    {
        if (((this.p1==v1)&&(this.p2==v2))||((this.p1==v2)&&(this.p2==v1)))
            return true;
        else
            return false;
    }

    public Vertex getToAddr(Vertex v)
    {
        //get vertex destination from vertex v
        if (p1==v)
            return p2;
        else
            return p1;
    }

    public String toGeom()
    {
        return "ST_GEOMFROMTEXT('LINESTRING("+this.p1.longitude+" "+this.p1.latitude+","+this.p2.longitude+" "+this.p2.latitude+")',4326)";
    }
    
    public String getString()
    {
        return this.road_name+" | "+this.p1.getString()+" - "+this.p2.getString();
    }

    public String getStringCSV()
    {
        return this.p1.info+";"+this.p2.info+";"+this.distance;
    }

    public String getStringCSV2()
    {
        return this.p1.getStringCSV(",")+";"+this.p2.getStringCSV(",")+";"+this.distance;
    }
    
    public String getGeom()
    {
        return "st_geomfromtext('linestring("+this.p1.longitude+" "+this.p1.latitude+","+this.p2.longitude+" "+this.p2.latitude+")',4326)";
    }
    
    public void copyOwner(Vertex v)
    {
        for (int i=0;i<v.orderF.size();i++)
            this.orderF.add(v.orderF.get(i));
    }
    
    
/**
 * 
 * @param p
 * @return in Degree. Multiply by 111 to obtain Km
 */ 
public double edgeDistance(Vertex p)
{
    Edges e = this;
    double result=0;
    double A = p.latitude - e.p1.latitude;
    double B = p.longitude - e.p1.longitude;
    double C = e.p2.latitude - e.p1.latitude;
    double D = e.p2.longitude - e.p1.longitude;
    
    double dot = A * C + B * D;
    double len_sq = C * C + D * D;
    double param=-1;
    if (len_sq!=0)
        param = dot/len_sq;
    
    double xx=0,yy=0;
    if (param < 0 )
    {
        xx = e.p1.latitude;
        yy = e.p1.longitude;
    }
    else if (param > 1)
    {
        xx = e.p2.latitude;
        yy = e.p2.longitude;
    }
    else
    {
        xx = e.p1.latitude + param * C;
        yy = e.p1.longitude + param * D;
    }
    
    double dx = p.latitude - xx;
    double dy = p.longitude - yy;
    result = Math.sqrt(dx * dx + dy * dy);
    return result;
}    

public boolean isIntersect(Edges e)
{
    boolean result=false;
    
    return result;
}
    
}
