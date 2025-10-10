#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.scenario.processor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Breadcrumb navigation generator for scenarios.
 *
 * Ported from PHP SportelloHelpers::breadcrumb()
 * Location: app/Helpers/SportelloHelpers.php:170-322
 *
 * Generates hierarchical breadcrumb navigation by analyzing scenario relationships
 * through goto-scenario actions.
 *
 * @author Sportello Archetype Generator
 */
#if($enableBreadcrumbs == "true")
public class BreadcrumbGenerator {

    private static final Logger logger = LoggerFactory.getLogger(BreadcrumbGenerator.class);
    private static final int MAX_RECURSION_DEPTH = 10;

    /**
     * Add breadcrumbs to all scenarios - equivalent to PHP lines 377-418
     */
    public ObjectNode addBreadcrumbs(ObjectNode jsonData) {
        if (!jsonData.has("scenarios")) {
            return jsonData;
        }

        ObjectNode scenarios = (ObjectNode) jsonData.get("scenarios");
        Map<String, List<BreadcrumbEntry>> breadcrumbMap = new HashMap<>();

        // Generate breadcrumbs for each scenario
        Iterator<String> scenarioKeys = scenarios.fieldNames();
        while (scenarioKeys.hasNext()) {
            String scenarioKey = scenarioKeys.next();
            List<BreadcrumbEntry> breadcrumb = generateBreadcrumb(scenarioKey, scenarios, 0);
            breadcrumbMap.put(scenarioKey, breadcrumb);
        }

        // Add breadcrumbs to scenarios
        for (Map.Entry<String, List<BreadcrumbEntry>> entry : breadcrumbMap.entrySet()) {
            String scenarioKey = entry.getKey();
            List<BreadcrumbEntry> breadcrumb = entry.getValue();

            if (!breadcrumb.isEmpty()) {
                ObjectNode scenario = (ObjectNode) scenarios.get(scenarioKey);
                String scenarioTitle = scenario.has("title") ?
                    scenario.get("title").asText() : scenarioKey;
                String scenarioBreadcrumbTitle = scenario.has("breadcrumb") ?
                    scenario.get("breadcrumb").asText() : scenarioTitle;

                // Check if first breadcrumb matches current scenario (PHP: line 391)
                if (!breadcrumb.isEmpty() &&
                    (breadcrumb.get(0).title.equals(scenarioTitle) ||
                     breadcrumb.get(0).title.equals(scenarioBreadcrumbTitle))) {

                    // Build breadcrumb object (PHP: lines 402-412)
                    ArrayNode breadcrumbArray = scenario.objectNode().arrayNode();
                    StringBuilder pathString = new StringBuilder();

                    // Reverse breadcrumb (PHP: array_reverse)
                    Collections.reverse(breadcrumb);

                    for (BreadcrumbEntry bc : breadcrumb) {
                        ObjectNode bcNode = scenario.objectNode();
                        bcNode.put("title", bc.title);
                        if (bc.route != null) {
                            bcNode.put("route", bc.route);
                        }
                        breadcrumbArray.add(bcNode);

                        if (pathString.length() > 0) {
                            pathString.append(" > ");
                        }
                        pathString.append(bc.title);
                    }

                    // Add breadcrumbs array to scenario
                    ArrayNode breadcrumbsContainer = scenario.objectNode().arrayNode();
                    ObjectNode breadcrumbObject = scenario.objectNode();
                    breadcrumbObject.set("to_array", breadcrumbArray);
                    breadcrumbObject.put("to_string", pathString.toString());
                    breadcrumbsContainer.add(breadcrumbObject);

                    scenario.set("breadcrumbs", breadcrumbsContainer);

                    logger.debug("Added breadcrumb to scenario '{}': {}",
                        scenarioKey, pathString);
                }
            }
        }

        return jsonData;
    }

    /**
     * Generate breadcrumb for a scenario - equivalent to PHP breadcrumb() recursive function
     * (PHP: lines 170-322)
     */
    private List<BreadcrumbEntry> generateBreadcrumb(
            String scenarioKey,
            ObjectNode allScenarios,
            int depth
    ) {
        List<BreadcrumbEntry> breadcrumb = new ArrayList<>();

        // Protection against infinite recursion (PHP: lines 172-176)
        if (depth > MAX_RECURSION_DEPTH) {
            logger.error("ATTENZIONE: superati i limiti della ricorsione durante " +
                "la chiamata allo scenario {}!!!", scenarioKey);
            return breadcrumb;
        }

        if (!allScenarios.has(scenarioKey)) {
            return breadcrumb;
        }

        JsonNode scenario = allScenarios.get(scenarioKey);

        if (!scenario.has("title")) {
            return breadcrumb;
        }

        String scenarioTitle = scenario.has("breadcrumb") ?
            scenario.get("breadcrumb").asText() :
            scenario.get("title").asText();
        String scenarioRoute = scenario.has("route") ?
            scenario.get("route").asText() : null;

        // Initialize breadcrumb with current scenario (PHP: lines 186-190)
        if (breadcrumb.isEmpty()) {
            breadcrumb.add(new BreadcrumbEntry(scenarioTitle, scenarioRoute));
        }

        // Search for parent scenario (PHP: lines 193-318)
        String targetScenario = scenarioKey.toLowerCase();
        boolean parentFound = false;

        Iterator<Map.Entry<String, JsonNode>> scenarios = allScenarios.fields();
        while (scenarios.hasNext() && !parentFound) {
            Map.Entry<String, JsonNode> entry = scenarios.next();
            String currentScenarioKey = entry.getKey();
            JsonNode currentScenario = entry.getValue();

            if (!currentScenario.has("title") && !currentScenario.has("breadcrumb")) {
                continue;
            }

            // Check actions-top and actions-row (PHP: lines 201-317)
            String[] actionTypes = {"actions-top", "actions-row"};

            for (String actionType : actionTypes) {
                if (parentFound) break;

                if (currentScenario.has("options") &&
                    currentScenario.get("options").has(actionType)) {

                    JsonNode actionGroup = currentScenario.get("options").get(actionType);

                    if (actionGroup.has("items")) {
                        ArrayNode items = (ArrayNode) actionGroup.get("items");

                        for (JsonNode item : items) {
                            if (parentFound) break;

                            if (item.has("goto-scenario")) {
                                String gotoScenario = item.get("goto-scenario").asText();

                                // Direct match (PHP: lines 225-258)
                                if (gotoScenario.toLowerCase().equals(targetScenario)) {
                                    String currentTitle = currentScenario.has("breadcrumb") ?
                                        currentScenario.get("breadcrumb").asText() :
                                        currentScenario.get("title").asText();
                                    String currentRoute = currentScenario.has("route") ?
                                        currentScenario.get("route").asText() : null;

                                    breadcrumb.add(new BreadcrumbEntry(currentTitle, currentRoute));

                                    // Recursive call (PHP: line 250)
                                    List<BreadcrumbEntry> parentBreadcrumb =
                                        generateBreadcrumb(currentScenarioKey, allScenarios, depth + 1);
                                    breadcrumb.addAll(parentBreadcrumb);

                                    parentFound = true;
                                }

                                // List scenario match (PHP: lines 260-312)
                                if ("_list".equals(gotoScenario.toLowerCase()) &&
                                    item.has("list-scenario")) {

                                    JsonNode listScenarios = item.get("list-scenario");
                                    if (listScenarios.isArray()) {
                                        for (JsonNode listScenario : listScenarios) {
                                            if (listScenario.asText().toLowerCase().equals(targetScenario)) {
                                                String currentTitle = currentScenario.has("breadcrumb") ?
                                                    currentScenario.get("breadcrumb").asText() :
                                                    currentScenario.get("title").asText();
                                                String currentRoute = currentScenario.has("route") ?
                                                    currentScenario.get("route").asText() : null;

                                                breadcrumb.add(new BreadcrumbEntry(currentTitle, currentRoute));

                                                List<BreadcrumbEntry> parentBreadcrumb =
                                                    generateBreadcrumb(currentScenarioKey, allScenarios, depth + 1);
                                                breadcrumb.addAll(parentBreadcrumb);

                                                parentFound = true;
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return breadcrumb;
    }

    /**
     * Internal breadcrumb entry structure
     */
    private static class BreadcrumbEntry {
        String title;
        String route;

        BreadcrumbEntry(String title, String route) {
            this.title = title;
            this.route = route;
        }
    }
}
#else
public class BreadcrumbGenerator {
    // Breadcrumbs disabled
    public com.fasterxml.jackson.databind.node.ObjectNode addBreadcrumbs(
        com.fasterxml.jackson.databind.node.ObjectNode jsonData) {
        return jsonData;
    }
}
#end
