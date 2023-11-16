#### 💯Points: ![Points bar](../../blob/badges/.github/badges/points-bar.svg)

#### 📝 [Report](../../blob/badges/report.md)
---

# Automated PC configuration using an SMT solver

For this assignment you are asked to build a PC configuration SMT problem and extract a solution from the model using JavaSMT.

As a first step see the introduction video for using JavaSMT for our purposes [https://youtu.be/9ptEo4apVcU](https://youtu.be/9ptEo4apVcU) (36min).

Instead of encoding a fixed set of components, their prices, and constraints, (as shown in the JavaSMT video [after minute 19:50](https://youtu.be/9ptEo4apVcU?t=1190)) we are going to read them using an API provided in the project.

- Components, their categories, and their prices can be read from files, i.e., they may change. 
  - The CSV formal for components is as follows: `category, name, price`, e.g. `CPU, i3, 113` or `motherboard, ASUS, 100`.
- Constraints between components of kind `requires` and `excludes` (similar to those in feature models) can be read from another file. 
  - The CSV formal for constraints is as follows: `name, constraintKind, name`, e.g. `i3, requires, ASUS` or `i3, excludes, MSI`. Where `constraintKind` is either `requires` or `excludes`. 
  - Constraint `requires` means that the first component requires the second one, and `excludes` means that each component excludes the other.
- Every valid PC needs at least one component from each of these categories: `CPU`, `motherboard`, `RAM`, and `storage`. Components of other categories are optional (subject to other constraints).
- Users provide a budget on the console.
- The selected components of a configuration are listed by the program, if a configuration within the budget exists.

A video going through the code is available from [https://youtu.be/Di2HA49KdLw](https://youtu.be/Di2HA49KdLw).
