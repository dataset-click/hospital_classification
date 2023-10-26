/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dijkstratools.pkg2;

/**
 *
 * @author kikim
 */
public class DijkstraMember {
    Vertex member; //what is pushed
    DijkstraMember next;
    Vertex whos;
    double distance;
    int hop=0;
    Vertex via;

    
    public DijkstraMember(Vertex v)
    {
        this.member=v;
        this.next=null;
//        this.distance=99999; //set to max
        
    }

//    public DijkstraMember(Vertex v, Vertex whos, double dist, Vertex via, int hop)
    public DijkstraMember(Vertex v, Vertex whos, double dist, Vertex via)
    {
        //create this stack on behalf of whos
        this.member=v;
        this.next=null;
        this.whos = whos; //register who push it (facility)
        this.distance=dist;
        this.via=via;
        //this.hop=hop;
    }    
}
