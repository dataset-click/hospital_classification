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
public class DijkstraList {

    DijkstraMember first,last;

    public DijkstraList()
    {
        this.first=null;
        this.last=null;
    }
    
    public boolean isEmpty()
    {
        return (first==null);
    }
    
      
    public void insertMember(DijkstraMember v)
    {
        if(this.isEmpty())
        {
            this.first=v;
            this.last=v;
        }
        else
        {
            //insert sort ascending
            DijkstraMember m = this.first;
            DijkstraMember bef=null;
            while ((m!=null)&&(m.member.distance<v.member.distance))
                {
                    bef=m;
                    m = m.next;
                }
            if (bef==null) //insert first
            {
                this.first=v;
                v.next=m;
            }
            else
            if (m==null)//insert last
            {
                this.last.next=v;
                this.last=v;
            }
            else //insert after bef
            {
                bef.next=v;
                v.next=m;
            }              
        }
//        this.displayAll();
    }
    
    /**
     * this is insert for nearest path
     * @param v
     */
    public void insertMemberFind(DijkstraMember v)
    {
        if(this.isEmpty())
        {
            this.first=v;
            this.last=v;
        }
        else
        {
            //insert sort ascending
            DijkstraMember m = this.first;
            DijkstraMember bef=null;
            while ((m!=null)&&(m.member.findDistance<v.member.findDistance))
                {
                    bef=m;
                    m = m.next;
                }
            if (bef==null) //insert first
            {
                this.first=v;
                v.next=m;
            }
            else
            if (m==null)//insert last
            {
                this.last.next=v;
                this.last=v;
            }
            else //insert after bef
            {
                bef.next=v;
                v.next=m;
            }              
        }
//        this.displayAll();
    }    
    
    public void insertFirstMember(DijkstraMember v)
    {
        if(this.isEmpty())
        {
            this.first=v;
            this.last=v;
        }
        else
        {
            v.next=this.first;
            this.first=v;
        }
            
    }
    
    public void removeMember(DijkstraMember v)
    {
        DijkstraMember prev,curr;
        prev=this.first;
        curr=prev;
        while (curr!=null)
        {
            if (curr==v)
                break;
            prev=curr;
            curr=curr.next;
        }
        if (curr==prev)
        {
            //delete first
            this.first=curr.next;
        }
        else
        {
            prev.next=curr.next;
        }       
    }
    
}
