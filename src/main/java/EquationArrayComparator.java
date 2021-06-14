import java.util.Comparator;

public class EquationArrayComparator implements Comparator<double[]> {
	
	@Override
	public int compare(double[] a1, double[] a2) {
		return compareIndex(a1, a2, 0);
	}
	
	public int compareIndex(double[] a1, double[] a2, int index) {
		int compare = Double.compare(Math.abs(a2[index]), Math.abs(a1[index]));
		if (compare == 0 && index < a1.length - 1) {
			compare = compareIndex(a1, a2, index + 1);
		}
		return compare;
	}
	
}
