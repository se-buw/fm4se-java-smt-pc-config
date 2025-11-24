package de.buw.fm4se.java_smt.pcconfig;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;

import java.nio.file.Files;
import java.nio.file.Path;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class T2RedundancyCheckingTest {
    String[] args = {};

    /**
     * Test redundancy checking by generating SMT-LIB scripts for different budgets
     */
    @Test
    @Order(1)
    void testDifferentBudgets() {
        try {
            PcConfigGeneratorAndSolver.configSolver(args, 266, true);
            PcConfigGeneratorAndSolver.configSolver(args, 267, true);
            PcConfigGeneratorAndSolver.configSolver(args, 500, true);
            PcConfigGeneratorAndSolver.configSolver(args, 1000, true);
        } catch (Exception e) {
            fail("Unable to generate SMTLIB2 scripts for redundancy analysis: " + e.getMessage());
        }
    }

    /**
     * Test smt-lib dump output
     * 
     * @throws Exception
     */
    @Test
    @Order(2)
    void testExplanationExists() throws Exception {
        assertTrue(Files.exists(Path.of("task-2/explanation.txt")),
                "Explanation file for redundancy checking does not exist.");
        // check that the file is not empty
        assertFalse(Files.size(Path.of("task-2/explanation.txt")) < 10,
                "Explanation file for redundancy checking is likely too short.");
    }
}
