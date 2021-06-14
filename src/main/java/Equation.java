import java.util.HashSet;
import java.util.Set;

public class Equation {
	
	private final Set<Term> terms = new HashSet<>();
	
	public void addTerm(Term term) {
		terms.add(term);
	}
	
	public int getHighestPos() {
		return terms.stream().mapToInt(Term::getPos).filter(i -> i != Integer.MAX_VALUE).max().orElse(-1);
	}
	
	public double[] convert(int size) {
		//System.out.println("size: " + size);
		double[] equation = new double[size];
		terms.forEach(t -> {
			//System.out.println("pos: " + t.getPos());
			equation[t.getPos() == Integer.MAX_VALUE ? size - 1 : t.getPos()] = t.getCoef();
		});
		return equation;
	}
	
}
