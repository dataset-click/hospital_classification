/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dijkstratools.pkg2;

/**
 *
 * @author KAdhinugraha
 */
public class Point {

    
    double latitude,longitude;
    boolean isIntersect=false;
    
    public Point(double x,double y)
    {
        this.latitude=x;
        this.longitude=y;
    }
    
    public double getX()
    {
        return this.latitude;
    }
    
    public double getY()
    {
        return this.longitude;
    }
        
}
