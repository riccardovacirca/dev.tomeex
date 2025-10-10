#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.scenario.processor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Role-based authorization filter for scenarios and actions.
 *
 * Ported from PHP SportelloHelpers::fixRoleAuthorization()
 * Location: app/Helpers/SportelloHelpers.php:435-503
 *
 * Filters JSON configuration based on:
 * - User role authorization (role/roles properties)
 * - Backend-only flags
 * - Maintenance mode visibility
 * - Login status requirements
 *
 * @author Sportello Archetype Generator
 */
#if($enableRoleAuthorization == "true")
public class RoleAuthorizationFilter {

    private static final Logger logger = LoggerFactory.getLogger(RoleAuthorizationFilter.class);

    /**
     * Filter JSON data by role - equivalent to PHP fixRoleAuthorization()
     *
     * @param data JSON data to filter
     * @param role Active user role (or null for unauthenticated, "@" for unvalidated)
     * @param maintenanceMode System maintenance status
     * @return Filtered JSON data
     */
    public JsonNode filterByRole(JsonNode data, String role, boolean maintenanceMode) {
        logger.debug("Filtering by role: {}, maintenance: {}", role, maintenanceMode);

        ObjectNode mutableData = data.deepCopy();
        filterNode(mutableData, role, maintenanceMode);

        return mutableData;
    }

    /**
     * Recursively filter a JSON node
     */
    private void filterNode(ObjectNode node, String role, boolean maintenanceMode) {

        List<String> keysToRemove = new ArrayList<>();
        Iterator<Map.Entry<String, JsonNode>> fields = node.fields();

        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> entry = fields.next();
            String key = entry.getKey();
            JsonNode value = entry.getValue();

            boolean shouldRemove = false;

            if (value.isObject()) {
                ObjectNode objValue = (ObjectNode) value;

                // Check role authorization (PHP: lines 443-458)
                if (objValue.has("role") || objValue.has("roles")) {
                    shouldRemove = !isRoleAuthorized(objValue, role);
                }

                // Check backend flag (PHP: lines 467-469)
                if (!shouldRemove && objValue.has("backend") &&
                    objValue.get("backend").asBoolean()) {
                    shouldRemove = true;
                }

                // Check only-not-logged flag (PHP: lines 472-474)
                if (!shouldRemove && objValue.has("only-not-logged") &&
                    objValue.get("only-not-logged").asBoolean() &&
                    role != null && !role.isEmpty()) {
                    shouldRemove = true;
                }

                // Check maintenance mode visibility (PHP: lines 477-484)
                if (!shouldRemove && maintenanceMode &&
                    objValue.has("hide_in_maintenance") &&
                    objValue.get("hide_in_maintenance").asBoolean()) {
                    shouldRemove = true;
                }

                if (!shouldRemove && !maintenanceMode &&
                    objValue.has("show_in_maintenance") &&
                    objValue.get("show_in_maintenance").asBoolean()) {
                    shouldRemove = true;
                }

                // Handle table_join field_alias (PHP: lines 460-464)
                if (!shouldRemove && objValue.has("table_join") && objValue.has("data")) {
                    JsonNode tableJoin = objValue.get("table_join");
                    if (tableJoin.has("field_alias")) {
                        objValue.put("data", tableJoin.get("field_alias").asText());
                    }
                }

                if (shouldRemove) {
                    keysToRemove.add(key);
                } else {
                    // Recurse into nested object (PHP: line 494)
                    if (!"search_enum".equals(key)) {
                        filterNode(objValue, role, maintenanceMode);
                    }
                }
            } else if (value.isArray()) {
                // Filter array elements
                filterArray((ArrayNode) value, role, maintenanceMode);
            }
        }

        // Remove filtered keys
        keysToRemove.forEach(node::remove);
    }

    /**
     * Filter array nodes
     */
    private void filterArray(ArrayNode array, String role, boolean maintenanceMode) {
        List<Integer> indicesToRemove = new ArrayList<>();

        for (int i = 0; i < array.size(); i++) {
            JsonNode element = array.get(i);
            if (element.isObject()) {
                ObjectNode objElement = (ObjectNode) element;

                boolean shouldRemove = false;

                if (objElement.has("role") || objElement.has("roles")) {
                    shouldRemove = !isRoleAuthorized(objElement, role);
                }

                if (!shouldRemove && objElement.has("backend") &&
                    objElement.get("backend").asBoolean()) {
                    shouldRemove = true;
                }

                if (shouldRemove) {
                    indicesToRemove.add(i);
                } else {
                    filterNode(objElement, role, maintenanceMode);
                }
            }
        }

        // Remove in reverse order to maintain indices
        for (int i = indicesToRemove.size() - 1; i >= 0; i--) {
            array.remove(indicesToRemove.get(i));
        }
    }

    /**
     * Check if role is authorized - equivalent to PHP role check logic (lines 443-458)
     */
    private boolean isRoleAuthorized(ObjectNode node, String role) {
        JsonNode roleNode = node.has("role") ? node.get("role") : node.get("roles");

        if (roleNode == null) {
            return true;
        }

        if (roleNode.isArray()) {
            // Multiple roles allowed
            ArrayNode rolesArray = (ArrayNode) roleNode;

            // Check for wildcard (PHP: in_array('*', $d[$field]))
            for (JsonNode roleEntry : rolesArray) {
                String allowedRole = roleEntry.asText();
                if ("*".equals(allowedRole) && role != null && !role.isEmpty()) {
                    return true;
                }
                if (role != null && role.equals(allowedRole)) {
                    return true;
                }
                if ("!".equals(allowedRole) && (role == null || role.isEmpty())) {
                    return true;
                }
                if ("@".equals(allowedRole) && "@".equals(role)) {
                    return true;
                }
            }
            return false;

        } else {
            // Single role
            String allowedRole = roleNode.asText();

            if ("*".equals(allowedRole) && role != null && !role.isEmpty()) {
                return true;
            }
            if (role != null && role.equals(allowedRole)) {
                return true;
            }
            if ("!".equals(allowedRole) && (role == null || role.isEmpty())) {
                return true;
            }
            return "@".equals(allowedRole) && "@".equals(role);
        }
    }
}
#else
public class RoleAuthorizationFilter {
    // Role authorization disabled
    public com.fasterxml.jackson.databind.JsonNode filterByRole(
        com.fasterxml.jackson.databind.JsonNode data,
        String role,
        boolean maintenanceMode) {
        return data;
    }
}
#end
