package de.buw.fm4se.java_smt.pcconfig;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import java.util.Arrays;
import java.util.Map;
import java.util.LinkedHashMap;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class T1PcConfigurationTest {
  String[] args = {};

  /**
   * Test if the budget is enough for a minimum configuration
   * 
   * @throws Exception
   */
  @Test
  @Order(1)
  void testMinimumBudget() throws Exception {
    String result = PcConfigGeneratorAndSolver.configSolver(args, 266);
    assertTrue(result.isEmpty(), "With minimum components, the budget needs to be at least 267 euro");

    String result2 = PcConfigGeneratorAndSolver.configSolver(args, 267);
    assertFalse(result2.isEmpty(), "With minimum components, 267 euro is enough");
  }

  /**
   * Test if budget is enough for i7 and Ryzen 7
   * 
   * @throws Exception
   */
  @Test
  @Order(2)
  void testCPUBudget() throws Exception {
    String result = PcConfigGeneratorAndSolver.configSolver(args, 267);
    Map<String, String> components = getComponents(result);

    assertFalse(components.containsKey("i7") && components.get("i7").equals("true"),
        "With €267, i7 is not possible");

    assertFalse(components.containsKey("ryzen7") && components.get("ryzen7").equals("true"),
        "With €267, Ryzen 7 is not possible");
  }

  /**
   * Test if the configuration contains required component CPU
   * 
   * @throws Exception
   */
  @Test
  @Order(3)
  void testContainsCPU() throws Exception {
    String result = PcConfigGeneratorAndSolver.configSolver(args, 267);
    Map<String, String> components = getComponents(result);
    try {
      assertTrue(components.get("i3").equals("true")
          || components.get("i5").equals("true")
          || components.get("ryzen7").equals("true"),
          "CPU is a minimum component");

    } catch (Exception e) {
      fail("The encoding is most likely unsatisfiable with the minimum budget");
    }
  }

  /**
   * Test if the configuration contains required component Motherboard
   * 
   * @throws Exception
   */
  @Test
  @Order(4)
  void testContainsMotherboard() throws Exception {
    String result = PcConfigGeneratorAndSolver.configSolver(args, 267);
    Map<String, String> components = getComponents(result);
    try {
      assertTrue(components.get("gigabyteIntel").equals("true")
          || components.get("msiAMD").equals("true"),
          "Motherboard is a minimum component");
    } catch (Exception e) {
      fail("The encoding is most likely unsatisfiable with the minimum budget");
    }
  }

  /**
   * Test if the configuration contains required component RAM
   * 
   * @throws Exception
   */
  @Test
  @Order(5)
  void testContainsRAM() throws Exception {
    String result = PcConfigGeneratorAndSolver.configSolver(args, 267);
    Map<String, String> components = getComponents(result);

    try {
      assertTrue(components.get("8GB").equals("true")
          || components.get("16GB").equals("true")
          || components.get("32GB").equals("true"),
          "RAM is a minimum component");
    } catch (Exception e) {
      fail("The encoding is most likely unsatisfiable with the minimum budget");
    }
  }

  /**
   * Test if the configuration contains required component Storage
   * 
   * @throws Exception
   */
  @Test
  @Order(6)
  void testContainsStorage() throws Exception {
    String result = PcConfigGeneratorAndSolver.configSolver(args, 267);
    Map<String, String> components = getComponents(result);
    try{
      assertTrue(components.get("hdd1tb").equals("true")
          || components.get("ssd1tb").equals("true")
          || components.get("ssd2tb").equals("true"),
          "Storage is a minimum component");
    }catch (Exception e) {
      fail("The encoding is most likely unsatisfiable with the minimum budget");
    }
  }

  /**
   * Test if appropriate motherboard is selected for the CPU
   * 
   * @throws Exception
   */
  @Test
  @Order(7)
  void testMotherboardConstraint() throws Exception {
    String result = PcConfigGeneratorAndSolver.configSolver(args, 1000);
    Map<String, String> components = getComponents(result);
    if (components.containsKey("i3") && components.get("i3").equals("true")) {
      assertTrue(components.get("gigabyteIntel").equals("true"),
          "Intel processor needs a Intel motherboard");
    } else if (components.containsKey("i7") && components.get("i7").equals("true")) {
      assertTrue(components.get("gigabyteIntel").equals("true"),
          "Intel processor needs a Intel motherboard");
    } else if (components.containsKey("ryzen7") && components.get("ryzen7").equals("true")) {
      assertTrue(components.get("msiAMD").equals("true"),
          "AMD processor needs a AMD motherboard");
    }
  }

  /**
   * Test if both RTX and GTX are not selected
   * 
   * @throws Exception
   */
  @Test
  @Order(8)
  void testGpuExclude() throws Exception {
    String result = PcConfigGeneratorAndSolver.configSolver(args, 1000);
    Map<String, String> components = getComponents(result);
    if (components.containsKey("RTX") && components.get("RTX").equals("true")) {
      assertFalse(components.containsKey("GTX") && components.get("GTX").equals("true"),
          "If RTX is selected, GTX should not be selected");
    } else if (components.containsKey("GTX") && components.get("GTX").equals("true")) {
      assertFalse(components.containsKey("RTX") && components.get("RTX").equals("true"),
          "If GTX is selected, RTX should not be selected");
    }
  }

  /**
   * Test if both MSI and Gigabyte are not selected
   * 
   * @throws Exception
   */
  @Test
  @Order(9)
  void testMotherboardExclude() throws Exception {
    String result = PcConfigGeneratorAndSolver.configSolver(args, 1000);
    Map<String, String> components = getComponents(result);
    if (components.containsKey("msiAMD") && components.get("msiAMD").equals("true")) {
      assertFalse(components.containsKey("gigabyteIntel") && components.get("gigabyteIntel").equals("true"),
          "If MSI is selected, Gigabyte should not be selected");
    } else if (components.containsKey("gigabyteIntel") && components.get("gigabyteIntel").equals("true")) {
      assertFalse(components.containsKey("msiAMD") && components.get("msiAMD").equals("true"),
          "If Gigabyte is selected, MSI should not be selected");
    }
  }

  /**
   * Get the components from the model
   * 
   * @param String
   * @return Map<String, String> componentDict
   * @throws Exception
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
