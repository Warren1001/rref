public class Term {
	
	private final int    pos;
	private final double coef;
	
	public Term(int pos, double coef) {
		this.pos = pos;
		this.coef = coef;
	}
	
	public int getPos() {
		return pos;
	}
	
	public double getCoef() {
		return coef;
	}
	
}
