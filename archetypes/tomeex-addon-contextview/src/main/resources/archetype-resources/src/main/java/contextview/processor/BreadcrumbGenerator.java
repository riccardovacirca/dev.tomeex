#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.contextview.processor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Breadcrumb navigation generator for contextviews.

 * Generates hierarchical breadcrumb navigation by analyzing contextview relationships
 * through goto-contextview actions.
 *
 * @author TomEEx Dev Team
 */
#if($enableBreadcrumbs == "true")
public class BreadcrumbGenerator {

    private static final Logger logger = LoggerFactory.getLogger(BreadcrumbGenerator.class);
    private static final int MAX_RECURSION_DEPTH = 10;

    /**
     * Add breadcrumbs to all contextviews - equivalent to PHP lines 377-418
     */
    public ObjectNode addBreadcrumbs(ObjectNode jsonData) {
        if (!jsonData.has("contextviews")) {
            return jsonData;
        }

        ObjectNode contextviews = (ObjectNode) jsonData.get("contextviews");
        Map<String, List<BreadcrumbEntry>> breadcrumbMap = new HashMap<>();

        // Generate breadcrumbs for each contextview
        Iterator<String> contextviewKeys = contextviews.fieldNames();
        while (contextviewKeys.hasNext()) {
            String contextviewKey = contextviewKeys.next();
            List<BreadcrumbEntry> breadcrumb = generateBreadcrumb(contextviewKey, contextviews, 0);
            breadcrumbMap.put(contextviewKey, breadcrumb);
        }

        // Add breadcrumbs to contextviews
        for (Map.Entry<String, List<BreadcrumbEntry>> entry : breadcrumbMap.entrySet()) {
            String contextviewKey = entry.getKey();
            List<BreadcrumbEntry> breadcrumb = entry.getValue();

            if (!breadcrumb.isEmpty()) {
                ObjectNode contextview = (ObjectNode) contextviews.get(contextviewKey);
                String scenarioTitle = contextview.has("title") ?
                    contextview.get("title").asText() : contextviewKey;
                String scenarioBreadcrumbTitle = contextview.has("breadcrumb") ?
                    contextview.get("breadcrumb").asText() : scenarioTitle;

                // Check if first breadcrumb matches current contextview (PHP: line 391)
                if (!breadcrumb.isEmpty() &&
                    (breadcrumb.get(0).title.equals(scenarioTitle) ||
                     breadcrumb.get(0).title.equals(scenarioBreadcrumbTitle))) {

                    // Build breadcrumb object (PHP: lines 402-412)
                    ArrayNode breadcrumbArray = contextview.objectNode().arrayNode();
                    StringBuilder pathString = new StringBuilder();

                    // Reverse breadcrumb (PHP: array_reverse)
                    Collections.reverse(breadcrumb);

                    for (BreadcrumbEntry bc : breadcrumb) {
                        ObjectNode bcNode = contextview.objectNode();
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

                    // Add breadcrumbs array to contextview
                    ArrayNode breadcrumbsContainer = contextview.objectNode().arrayNode();
                    ObjectNode breadcrumbObject = contextview.objectNode();
                    breadcrumbObject.set("to_array", breadcrumbArray);
                    breadcrumbObject.put("to_string", pathString.toString());
                    breadcrumbsContainer.add(breadcrumbObject);

                    contextview.set("breadcrumbs", breadcrumbsContainer);

                    logger.debug("Added breadcrumb to contextview '{}': {}",
                        contextviewKey, pathString);
                }
            }
        }

        return jsonData;
    }

    /**
     * Generate breadcrumb for a contextview - equivalent to PHP breadcrumb() recursive function
     * (PHP: lines 170-322)
     */
    private List<BreadcrumbEntry> generateBreadcrumb(
            String contextviewKey,
            ObjectNode allScenarios,
            int depth
    ) {
        List<BreadcrumbEntry> breadcrumb = new ArrayList<>();

        // Protection against infinite recursion (PHP: lines 172-176)
        if (depth > MAX_RECURSION_DEPTH) {
            logger.error("ATTENZIONE: superati i limiti della ricorsione durante " +
                "la chiamata allo contextview {}!!!", contextviewKey);
            return breadcrumb;
        }

        if (!allScenarios.has(contextviewKey)) {
            return breadcrumb;
        }

        JsonNode contextview = allScenarios.get(contextviewKey);

        if (!contextview.has("title")) {
            return breadcrumb;
        }

        String scenarioTitle = contextview.has("breadcrumb") ?
            contextview.get("breadcrumb").asText() :
            contextview.get("title").asText();
        String scenarioRoute = contextview.has("route") ?
            contextview.get("route").asText() : null;

        // Initialize breadcrumb with current contextview (PHP: lines 186-190)
        if (breadcrumb.isEmpty()) {
            breadcrumb.add(new BreadcrumbEntry(scenarioTitle, scenarioRoute));
        }

        // Search for parent contextview (PHP: lines 193-318)
        String targetScenario = contextviewKey.toLowerCase();
        boolean parentFound = false;

        Iterator<Map.Entry<String, JsonNode>> contextviews = allScenarios.fields();
        while (contextviews.hasNext() && !parentFound) {
            Map.Entry<String, JsonNode> entry = contextviews.next();
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

                            if (item.has("goto-contextview")) {
                                String gotoScenario = item.get("goto-contextview").asText();

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

                                // List contextview match (PHP: lines 260-312)
                                if ("_list".equals(gotoScenario.toLowerCase()) &&
                                    item.has("list-contextview")) {

                                    JsonNode listScenarios = item.get("list-contextview");
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
