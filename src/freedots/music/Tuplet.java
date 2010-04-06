package freedots.music;

import freedots.math.Fraction;
import freedots.musicxml.Note;



public class Tuplet extends java.util.LinkedList<TupletElement> implements TupletElement {
	
	protected Tuplet parent=null; //if parent is null, the tuplet is the main tuplet
    protected Fraction actualType=null;
    protected Fraction normalType=null;
	
	public Tuplet(){
		super();	
	}
	
	public boolean isFirstOfTuplet(TupletElement tE) {
	    try {
	      return getFirst() == tE;
	    } catch (java.util.NoSuchElementException e) {
	      return false;
	    }
	  }
	
	public int getType(){
		return getActualType().numerator();
	}
	
	public Tuplet getParent(){
    	return parent;
    }
    
	 public void setActualType(Fraction actualType){
		 this.actualType=actualType;
	 }
		    
	 public void setNormalType(Fraction normalType){
		 this.normalType=normalType;
	 }
	
    public Fraction getActualType(){
    	if (actualType==null){
    		setActualType(getActual());
    	}	
    	return actualType;
    }

    public Fraction getNormalType(){
    	if (normalType==null){
    		setNormalType(getNormal());
    	}
    	return normalType;
    }
    
    private Fraction getNormal(){
	if (getParent()==null && getFirst() instanceof Note){
	    Note note=(Note)getFirst();
	    int num=note.getTimeModification().getNormalNotes()*note.getTimeModification().getNormalType().numerator();
	    int den=note.getTimeModification().getNormalType().denominator();
	    return new Fraction(num,den).simplify();
	} 
	return null;
    }
    
    private Fraction getActual(){
	if (getParent()==null && getFirst() instanceof Note){
	    Note note=(Note)getFirst();
	    int num=note.getTimeModification().getActualNotes()*note.getTimeModification().getNormalType().numerator();
	    int den=note.getTimeModification().getNormalType().denominator();
	    return new Fraction(num,den).simplify();
	} 
	return null;
    }
	
}
