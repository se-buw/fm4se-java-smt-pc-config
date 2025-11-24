#### 💯Points: ![Points bar](../../blob/badges/.github/badges/points-bar.svg)

#### 📝 [Report](../../blob/badges/report.md)

---

## Task 1: Automated PC configuration using an SMT solver

For this task you are asked to build a PC configuration SMT problem and extract a solution from the model using JavaSMT.

> [!TIP]
> As a first step see the introduction video for using JavaSMT for our purposes [https://youtu.be/9ptEo4apVcU](https://youtu.be/9ptEo4apVcU) (36min).

> [!TIP]
> Instead of encoding a fixed set of components, their prices, and constraints, (as shown in the JavaSMT video [after minute 19:50](https://youtu.be/9ptEo4apVcU?t=1190)) we are going to read them using an API provided in the project.

**Implementation:** All the code you need to write is in method [createConfigFormula(BooleanFormulaManager bmgr, IntegerFormulaManager imgr, int budgetIN)](/src/main/java/de/buw/fm4se/java_smt/pcconfig/PcConfigGeneratorAndSolver.java#L41). This method should return a full formula representing the PC configuration problem as described below. You can add as many helper methods as you need.

- Components, their categories, and their prices can be read from files, i.e., they may change.
  - The CSV formal for components is as follows: `category, name, price`, e.g. `CPU, i3, 113` or `motherboard, ASUS, 100`.
- Constraints between components of kind `requires` and `excludes` (similar to those in feature models) can be read from another file.
  - The CSV formal for constraints is as follows: `name, constraintKind, name`, e.g. `i3, requires, ASUS` or `i3, excludes, MSI`. Where `constraintKind` is either `requires` or `excludes`.
  - Constraint `requires` means that the first component requires the second one, and `excludes` means that each component excludes the other.
- Every valid PC needs at least one component from each of these categories: `CPU`, `motherboard`, `RAM`, and `storage`. Components of other categories are optional (subject to other constraints).
- Users provide a budget on the console.
- The selected components of a configuration are listed by the program, if a configuration within the budget exists.

> [!TIP]
> A video going through the code is available from [https://youtu.be/Di2HA49KdLw](https://youtu.be/Di2HA49KdLw).

## Task 2: Redundancy Analysis

To assess a quality aspect of the PC configuration solver, you are asked to analyze the generated formulas for redundant assertions.

1. Your code automatically generates example scenario formulas. Analyze these formulas for redundant assertions:

   - Run `./gradlew build` to build the project (or simply run the test cases).
   - This will generate SMT scripts for different budgets in the [task-2/](task-2/) folder.
   - Check these scripts in the [FM Playground](https://play.formal-methods.net/?check=SMT) for redundant assertions using the redundancy analysis feature.

2. Reflect on the results of the analysis. Write a short summary of the results in the [explanation.txt](task-2/explanation.txt) file. Consider whether you want to improve your encoding from Task 1 based on the results of the redundancy analysis. If so, implement these improvements and document them (also inside [explanation.txt](task-2/explanation.txt)).
