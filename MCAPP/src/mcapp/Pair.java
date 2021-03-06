package mcapp;

//Custom class for pairs
public class Pair<L, M, R> 
{
    L l;
    M m;
    R r;
    
    //Constructor
    public Pair(L l, M m, R r)
    {
        this.l = l;
        this.m = m;
        this.r = r;
    }
    
    /* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() 
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((l == null) ? 0 : l.hashCode());
		result = prime * result + ((m == null) ? 0 : m.hashCode());
		result = prime * result + ((r == null) ? 0 : r.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) 
	{
		if (this == obj) 
		{
			return true;
		}
		if (obj == null) 
		{
			return false;
		}
		if (getClass() != obj.getClass()) 
		{
			return false;
		}
		Pair other = (Pair) obj;
		if (l == null) 
		{
			if (other.l != null) 
			{
				return false;
			}
		} 
		else if (!l.equals(other.l)) 
		{
			return false;
		}
		if (m == null) 
		{
			if (other.m != null) 
			{
				return false;
			}
		} 
		else if (!m.equals(other.m)) 
		{
			return false;
		}
		//This is done so Pairs are only compared to their L and M values.
		//This makes finding a note with a length different from 1 possible (since the length of the note is stored in the "pair"'s R value)
		/*if (r == null) 
		{
			if (other.r != null) 
			{
				return false;
			}
		} 
		else if (!r.equals(other.r)) 
		{
			return false;
		}
		*/
		return true;
	}
	
	//Getters
	public L getL()
    {
    	return l;    
    }
    
    public R getR()
    {
    	return r;
    }
    
    public M getM()
    {
    	return m;
    }
    
    //Setters
    public void setL(L l)
    {
    	this.l = l;
    }
    
    public void setM(M m)
    {
    	this.m = m;
    }
    
    public void setR(R r)
    {
    	this.r = r;
    }
}