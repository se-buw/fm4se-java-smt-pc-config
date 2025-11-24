package de.buw.fm4se.java_smt.pcconfig;

import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.LinkedHashMap;

import java.util.ArrayList;
import java.util.Arrays;

import org.sosy_lab.common.ShutdownManager;
import org.sosy_lab.common.configuration.Configuration;
import org.sosy_lab.common.log.BasicLogManager;
import org.sosy_lab.common.log.LogManager;
import org.sosy_lab.java_smt.SolverContextFactory;
import org.sosy_lab.java_smt.SolverContextFactory.Solvers;
import org.sosy_lab.java_smt.api.BooleanFormula;
import org.sosy_lab.java_smt.api.BooleanFormulaManager;
import org.sosy_lab.java_smt.api.FormulaManager;
import org.sosy_lab.java_smt.api.IntegerFormulaManager;
import org.sosy_lab.java_smt.api.Model;
import org.sosy_lab.java_smt.api.NumeralFormula.IntegerFormula;
import org.sosy_lab.java_smt.api.ProverEnvironment;
import org.sosy_lab.java_smt.api.SolverContext;
import org.sosy_lab.java_smt.api.SolverContext.ProverOptions;

public class PcConfigGeneratorAndSolver {
	public static void main(String[] args) throws Exception {

		// INFO this is just to see how to access the information from the files
		System.out.println("\nAvailable components:");
		printComponents("CPU");
		printComponents("motherboard");
		printComponents("RAM");
		printComponents("GPU");
		printComponents("storage");
		printComponents("opticalDrive");
		printComponents("cooler");
		
		System.out.println("\nConstraints:");
		printConstraints("requires");
		printConstraints("excludes");
		Scanner scan = new Scanner(System.in);
		System.out.print("Please enter a budget: ");
		int budget = scan.nextInt();
		scan.close();

		String result = configSolver(args, budget);
		Map<String, String> components = getComponents(result);
		for (String key : components.keySet()) {
			System.out.println(key + " " + components.get(key));
		}
	}

	private static BooleanFormula createConfigFormula(BooleanFormulaManager bmgr, IntegerFormulaManager imgr,
			int budgetIN) {

		// TODO implement the translation to SMT

		// Construct the full formula for SMT-LIB output and return it
		return bmgr.and();
	}

	public static String configSolver(String[] args, int budgetIN) throws Exception {
		return configSolver(args, budgetIN, false);
	}

	public static String configSolver(String[] args, int budgetIN, boolean dump) throws Exception {
		System.out.println("\nSearching for a configuration costing at most " + budgetIN);

		// Configuration
		Configuration config = Configuration.fromCmdLineArguments(args);
		LogManager logger = BasicLogManager.create(config);
		ShutdownManager shutdown = ShutdownManager.create();
		SolverContext context = SolverContextFactory.createSolverContext(config, logger, shutdown.getNotifier(),
				Solvers.PRINCESS);
		FormulaManager fmgr = context.getFormulaManager();
		IntegerFormulaManager imgr = fmgr.getIntegerFormulaManager();
		BooleanFormulaManager bmgr = fmgr.getBooleanFormulaManager();

		// Create the configuration formula
		BooleanFormula fullFormula = createConfigFormula(bmgr, imgr, budgetIN);

		if (dump) {
			SmtLibFormulaWriter.dumpFormula(fullFormula, fmgr, "task-2/budget" + budgetIN + ".smt2");
		}

		Model model = null;

		// feed to the solver
		try (ProverEnvironment prover = context.newProverEnvironment(ProverOptions.GENERATE_MODELS)) {
			prover.addConstraint(fullFormula);

			boolean unsat = prover.isUnsat();
			if (!unsat) {
				model = prover.getModel();
				prover.close();
				return model.toString();
			} else {
				System.out.println("unsat :-(");
				prover.close();
			}
		} catch (Exception e) {
			System.out.println("Exception: " + e);
		}

		return model == null ? "" : model.toString();
	}

	private static void printConstraints(String kind) {
		for (String[] pair : PcConfigReader.getConstraints(kind)) {
			System.out.println(pair[0] + " " + kind + " " + pair[1]);
		}
	}

	private static void printComponents(String type) {
		Map<String, Integer> components = PcConfigReader.getComponents(type);
		for (String cmp : components.keySet()) {
			System.out.println(cmp + " costs " + components.get(cmp));
		}
	}

	/**
	 * Extracts components from the JavaSMT model string.
	 * 
	 * @param model
	 * @return A map of component types to their selected components.
	 */
	private static Map<String, String> getComponents(String model) {
		model = model.replaceAll("[{}]", "");
		String[] ls = model.split(",");
		ls = Arrays.stream(ls).map(String::strip).toArray(String[]::new);

		Map<String, String> componentDict = new LinkedHashMap<>();

		try {
			for (String f : ls) {
				String[] parts = f.split(" -> ");
				if (parts.length == 2) {
					componentDict.put(parts[0], parts[1]);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return componentDict;
	}

}
