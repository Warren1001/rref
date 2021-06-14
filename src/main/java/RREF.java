import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class RREF {
	
	public static final String EQUATION_REGEX = "(?<=op)|(?=op)".replace("op", "[-+=]");
	public static final String VARIABLE_REGEX = "(?<=op)|(?=op)".replace("op", "[xyz]");
	
	public static void main(String[] ignored) {
		EquationArrayComparator equationArrayComparator = new EquationArrayComparator();
		String                  repeat                  = "y";
		boolean                 hardBreak;
		
		while (repeat.equalsIgnoreCase("y")) {
			Scanner        scanner      = new Scanner(System.in);
			List<Equation> equationList = new ArrayList<>();
			
			hardBreak = false;
			
			System.out.println("Input equations in the form of Ax + By + Cz = D or As1 + Bs2 + Cs3 + ... = D, where A, B, C are" +
					" the coefficients and D is the constant in integer or decimal form and x, y, z and s1, s2, s3, ... are variables," +
					" one equation at a time, separating them with returns.");
			System.out.println("Once you have input all the equations you would like to RREF, return a blank statement.");
			
			String line;
			while (!(line = scanner.nextLine()).replace(" ", "").equals("")) {
				
				Equation equation = new Equation();
				String[] args     = line.replace(" ", "").split(EQUATION_REGEX);
				
				for (int i = 0; i < args.length; i++) {
					String arg = args[i];
					
					if (arg.equals("-") || arg.equals("+") || arg.equals("=")) {
						continue;
					}
					
					String[] coefAndPos = arg.split(arg.contains("s") ? "s" : VARIABLE_REGEX);
					//for (String s : coefAndPos) {
					//	System.out.print("'" + s + "', ");
					//}
					//System.out.println();
					double  coef;
					boolean negative;
					int     pos;
					
					if ((arg.contains("s") && coefAndPos.length == 1) || coefAndPos.length > 2) {
						System.err.printf("'%s' is an invalid formula equation.%n", line);
						hardBreak = true;
						break;
					}
					
					coef = (coefAndPos.length == 1 && i != args.length - 1) || coefAndPos[0].equals("") ? 1 : Double.parseDouble(coefAndPos[0]);
					
					if (i == 0) {
						negative = false;
						pos = getArrayPosition(coefAndPos[coefAndPos.length - 1]);
					} else if (args[i - 1].equalsIgnoreCase("-")) {
						negative = true;
						if (i > 2 && args[i - 2].equalsIgnoreCase("=")) {
							pos = Integer.MAX_VALUE;
						} else {
							pos = getArrayPosition(coefAndPos[coefAndPos.length - 1]);
						}
					} else if (args[i - 1].equalsIgnoreCase("=")) {
						negative = false;
						pos = Integer.MAX_VALUE;
					} else {
						negative = false;
						pos = getArrayPosition(coefAndPos[coefAndPos.length - 1]);
					}
					
					if (pos == -1) {
						System.err.printf("'%s' is an invalid formula equation.%n", line);
						hardBreak = true;
						break;
					}
					
					if (negative) {
						coef *= -1;
					}
					
					equation.addTerm(new Term(pos, coef));
					
				}
				if (hardBreak) {
					continue;
				}
				
				equationList.add(equation);
				
			}
			
			int size = equationList.stream().mapToInt(equation -> equation.getHighestPos() + 2).max().orElse(-1);
			List<double[]> arrayEquationList = equationList.stream().map(equation -> equation.convert(size)).sorted(equationArrayComparator)
					.collect(Collectors.toList());
			
			arrayEquationList.forEach(RREF::makePivotOne);
			for (int pos = 0; pos < Math.min(arrayEquationList.size(), size - 1); pos++) {
				//System.out.println("doing RREF for pos: " + pos);
				boolean  doingAbove       = true;
				double[] subEquationArray = null;
				for (double[] equationArray : arrayEquationList) {
					boolean zeroesBefore = true;
					if (pos > 0) {
						for (int i = 0; i < pos; i++) {
							zeroesBefore = zeroesBefore && equationArray[pos - (i + 1)] == 0;
						}
					}
					if (subEquationArray == null && equationArray[pos] == 1 && zeroesBefore) {
						subEquationArray = equationArray;
						//System.out.println("using equation " + toString(subEquationArray) + " as subtraction array");
						break;
					}
				}
				if (subEquationArray == null) {
					continue;
				}
				for (double[] equationArray : arrayEquationList) {
					if (equationArray == subEquationArray) {
						//System.out.println("arrays are the same: " + toString(equationArray) + " = " + toString(subEquationArray));
						doingAbove = false;
						continue;
					}
					if (equationArray[pos] == 0) {
						//System.out.println("skipping array as its already zeroed");
						continue;
					}
					double multiplier = doingAbove ? equationArray[pos] : 1;
					//System.out.println("multiplier when reducing: " + multiplier);
					//System.out.println("subtracting from: " + toString(equationArray));
					for (int i = 0; i < size; i++) {
						//System.out.println("equationArray: " + (equationArray == null));
						//System.out.println("subEquationArray: " + (subEquationArray == null));
						equationArray[i] = equationArray[i] - (subEquationArray[i] * multiplier);
					}
					//System.out.println("after subtraction: " + toString(equationArray));
					makePivotOne(equationArray);
				}
				arrayEquationList.sort(equationArrayComparator);
			}
			
			for (double[] equationArray : arrayEquationList) {
				StringBuilder builder = new StringBuilder();
				builder.append("| ");
				for (int i = 0; i < size; i++) {
					if (i == size - 1) {
						builder.append(": ");
					}
					double d  = format(equationArray[i]);
					int    in = (int)d;
					double r  = d - in;
					if (r == 0.0) {
						builder.append(in);
					} else {
						builder.append(d);
					}
					builder.append(" ");
				}
				builder.append("|");
				System.out.println(builder);
			}
			
			System.out.println();
			System.out.println("Would you like to input another set of equations? (y/n)");
			
			repeat = scanner.nextLine();
			
			for (int i = 0; i < 100; i++) {
				System.out.println();
			}
			
		}
		
	}
	
	private static int getArrayPosition(String input) {
		int pos;
		try {
			pos = Integer.parseInt(input) - 1;
		} catch (NumberFormatException ignore) {
			//System.out.println(input.toLowerCase());
			switch (input.toLowerCase()) {
				case "x":
					pos = 0;
					break;
				case "y":
					pos = 1;
					break;
				case "z":
					pos = 2;
					break;
				default:
					//System.out.println("? 1.1");
					pos = -1;
					break;
			}
		}
		return pos;
	}
	
	private static void makePivotOne(double[] array) {
		//System.out.println("pivoting - before pivot: " + toString(array));
		double divideBy = 0;
		for (int i = 0; i < array.length; i++) {
			double d = array[i];
			if (divideBy == 0) {
				if (d != 0) {
					divideBy = d;
				} else {
					continue;
				}
			}
			//System.out.println("pivoting - d: " + d + ", divideBy: " + divideBy);
			array[i] = d / divideBy;
		}
		//System.out.println("pivoting - after pivot: " + toString(array));
	}
	
	private static double format(double d) {
		return new BigDecimal(d).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
		//return (double)((int)(d * 100)) / 100;
	}
	
	private static String toString(double[] array) {
		StringBuilder builder = new StringBuilder("{ ");
		for (double d : array) {
			builder.append(d).append(", ");
		}
		builder.append("}");
		return builder.toString();
	}
	
}
