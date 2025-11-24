package de.buw.fm4se.java_smt.pcconfig;

import java.util.ArrayList;
import java.util.List;

import org.sosy_lab.java_smt.api.BooleanFormula;
import org.sosy_lab.java_smt.api.FormulaManager;

public class SmtLibFormulaWriter {
	
	public static void dumpFormula(BooleanFormula fullFormula, FormulaManager fmgr, String filePath) {
		
		// Extract conjuncts from the formula
		List<BooleanFormula> conjuncts = extractConjuncts(fullFormula, fmgr);
		
		// Collect all declarations and formulas
		java.util.Set<String> declarations = new java.util.LinkedHashSet<>();
		List<String> assertions = new ArrayList<>();
		
		for (BooleanFormula conjunct : conjuncts) {
			String dump = fmgr.dumpFormula(conjunct).toString();
			String[] lines = dump.split("\n");
			
			// Separate declarations from the assertion
			for (String line : lines) {
				line = line.trim();
				if (line.startsWith("(declare-fun") || line.startsWith("(define-fun")) {
					declarations.add(line);
				} else if (line.startsWith("(assert")) {
					// Extract the formula inside the assert
					String formula = line.substring(8, line.length() - 1).trim();
					assertions.add(formula);
				} else if (!line.isEmpty()) {
					// Standalone formula without assert wrapper
					assertions.add(line);
				}
			}
		}
		
		// Dump to SMT-LIB file
		try (java.io.FileWriter writer = new java.io.FileWriter(filePath)) {
			// Write all declarations first
			for (String decl : declarations) {
				writer.write(decl + "\n");
			}
			// Write each assertion
			for (String assertion : assertions) {
				writer.write("(assert " + assertion + ")\n");
			}
			writer.write("(check-sat)\n(get-model)");
		}
		catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	private static List<BooleanFormula> extractConjuncts(BooleanFormula formula, FormulaManager fmgr) {
		List<BooleanFormula> conjuncts = new ArrayList<>();
		
		// Use a visitor to extract conjuncts recursively
		fmgr.visit(formula, new org.sosy_lab.java_smt.api.visitors.DefaultFormulaVisitor<Void>() {
			@Override
			protected Void visitDefault(org.sosy_lab.java_smt.api.Formula f) {
				// Not an AND, add as-is
				conjuncts.add((BooleanFormula) f);
				return null;
			}
			
			@Override
			public Void visitFunction(org.sosy_lab.java_smt.api.Formula f, List<org.sosy_lab.java_smt.api.Formula> args,
					org.sosy_lab.java_smt.api.FunctionDeclaration<?> functionDeclaration) {
				// Check if this is an AND operation
				if (functionDeclaration.getKind() == org.sosy_lab.java_smt.api.FunctionDeclarationKind.AND) {
					// Recursively extract conjuncts from each argument
					for (org.sosy_lab.java_smt.api.Formula arg : args) {
						conjuncts.addAll(extractConjuncts((BooleanFormula) arg, fmgr));
					}
				} else {
					// Not an AND, add the whole formula
					conjuncts.add((BooleanFormula) f);
				}
				return null;
			}
		});
		
		return conjuncts;
	}
}
