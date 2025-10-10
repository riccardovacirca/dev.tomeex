#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.scenario.processor;

import ${package}.scenario.core.Scenario;
import ${package}.scenario.core.ScenarioOptions;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * ScenarioProcessor - Core processor for JSON-Driven Scenario Architecture.
 *
 * Ported from PHP SportelloHelpers::readConfiguration()
 * Location: app/Helpers/SportelloHelpers.php:324-424
 *
 * This class replicates the PHP processing pipeline:
 * 1. Load module JSON configuration
 * 2. Process each scenario:
 *    - Load and inline schema files
 *    - Apply schema transformations based on mode (insert/update)
 *    - Execute dynamic field functions (enums, selects)
 *    - Apply role-based authorization
 *    - Generate breadcrumb navigation
 *
 * @author TomEEx Dev Team
 * @version 1.0.0
 */
public class ScenarioProcessor {

    private static final Logger logger = LoggerFactory.getLogger(ScenarioProcessor.class);
    private final ObjectMapper objectMapper;
    private final Path modulesBasePath;

#if($enableRoleAuthorization == "true")
    private final RoleAuthorizationFilter roleFilter;
#end
#if($enableBreadcrumbs == "true")
    private final BreadcrumbGenerator breadcrumbGenerator;
#end
#if($enableSchemaValidation == "true")
    private final SchemaValidator schemaValidator;
#end

    public ScenarioProcessor(Path modulesBasePath) {
        this.modulesBasePath = modulesBasePath;
        this.objectMapper = new ObjectMapper();
#if($enableRoleAuthorization == "true")
        this.roleFilter = new RoleAuthorizationFilter();
#end
#if($enableBreadcrumbs == "true")
        this.breadcrumbGenerator = new BreadcrumbGenerator();
#end
#if($enableSchemaValidation == "true")
        this.schemaValidator = new SchemaValidator();
#end
    }

    /**
     * Main processing method - equivalent to PHP readConfiguration()
     *
     * @param jsonPath Path to module JSON (e.g., "SCAI/Config/json/SCAI.json")
     * @param activeRole Current user role
     * @param validationTerms Terms validation status
     * @param maintenanceMode System maintenance flag
     * @return Processed configuration as JsonNode
     */
    public JsonNode processConfiguration(
            String jsonPath,
            String activeRole,
            boolean validationTerms,
            boolean maintenanceMode
    ) throws IOException {

        logger.info("Processing configuration: {}", jsonPath);

        // 1. Load JSON file (PHP: file_get_contents + json_decode)
        Path fullPath = modulesBasePath.resolve(jsonPath);
        JsonNode jsonData = loadJsonFile(fullPath);

        // 2. Process scenarios if present
        if (jsonData.has("scenarios")) {
            JsonNode scenariosNode = jsonData.get("scenarios");
            processScenarios((ObjectNode) jsonData, scenariosNode, jsonPath);
        }

        // 3. Handle validation terms (PHP: lines 368-371)
        String effectiveRole = activeRole;
        if (activeRole != null &&
            jsonData.has("options") &&
            jsonData.get("options").has("serviceTermsRequired") &&
            jsonData.get("options").get("serviceTermsRequired").asBoolean() &&
            !validationTerms) {
            effectiveRole = "@"; // Unvalidated user role
        }

#if($enableRoleAuthorization == "true")
        // 4. Apply role-based authorization (PHP: fixRoleAuthorization)
        jsonData = roleFilter.filterByRole(jsonData, effectiveRole, maintenanceMode);
#end

#if($enableBreadcrumbs == "true")
        // 5. Generate breadcrumbs (PHP: lines 377-418)
        if (jsonData.has("scenarios")) {
            jsonData = breadcrumbGenerator.addBreadcrumbs((ObjectNode) jsonData);
        }
#end

        logger.info("Configuration processed successfully");
        return jsonData;
    }

    /**
     * Process all scenarios - equivalent to PHP foreach loop (lines 332-364)
     */
    private void processScenarios(ObjectNode jsonData, JsonNode scenariosNode, String modulePath) {

        Iterator<Map.Entry<String, JsonNode>> scenarios = scenariosNode.fields();

        while (scenarios.hasNext()) {
            Map.Entry<String, JsonNode> entry = scenarios.next();
            String scenarioKey = entry.getKey();
            JsonNode scenario = entry.getValue();

            logger.debug("Processing scenario: {}", scenarioKey);

            if (scenario.has("options")) {
                JsonNode options = scenario.get("options");

                // Process schema if present (PHP: lines 337-361)
                if (options.has("schema")) {
                    String schemaName = options.get("schema").asText();
                    String schemaMode = options.has("schemaMode") ?
                        options.get("schemaMode").asText() : "";
                    String component = scenario.has("component") ?
                        scenario.get("component").asText() : "";

                    processSchema(
                        (ObjectNode) jsonData,
                        scenarioKey,
                        schemaName,
                        schemaMode,
                        component,
                        modulePath
                    );
                }

#if($enableDynamicEnums == "true")
                // Process dynamic select fields (PHP: selectFields - line 336)
                processDynamicFields((ObjectNode) options);
#end
            }
        }
    }

    /**
     * Load and process schema file - equivalent to PHP lines 348-359
     */
    private void processSchema(
            ObjectNode jsonData,
            String scenarioKey,
            String schemaName,
            String schemaMode,
            String component,
            String modulePath
    ) {
        try {
            // Build schema path (PHP: lines 347-348)
            Path moduleDir = modulesBasePath.resolve(modulePath).getParent();
            Path schemaPath = moduleDir.resolve("forms").resolve(schemaName + ".json");

            if (Files.exists(schemaPath)) {
                // Load schema (PHP: file_get_contents + json_decode)
                JsonNode schemaJson = loadJsonFile(schemaPath);

                // Apply transformations (PHP: addAngularJSONClasses)
                schemaJson = applySchemaTransformations(schemaJson, schemaMode, component);

                // Inline schema into scenario (PHP: line 354)
                ObjectNode scenarioOptions = (ObjectNode) jsonData
                    .get("scenarios")
                    .get(scenarioKey)
                    .get("options");
                scenarioOptions.set("schema", schemaJson);

                logger.debug("Schema '{}' loaded and processed for scenario '{}'",
                    schemaName, scenarioKey);

            } else {
                // Schema not found - set error message (PHP: lines 356-358)
                ObjectNode scenarioOptions = (ObjectNode) jsonData
                    .get("scenarios")
                    .get(scenarioKey)
                    .get("options");
                scenarioOptions.putNull("schema");
                scenarioOptions.put("schemaMessage",
                    "Lo schema di configurazione '" + schemaName +
                    "' non esiste o contiene degli errori, controllare i file di configurazione");

                logger.warn("Schema file not found: {}", schemaPath);
            }

        } catch (IOException e) {
            logger.error("Error processing schema '{}' for scenario '{}'",
                schemaName, scenarioKey, e);
        }
    }

    /**
     * Apply schema transformations - equivalent to PHP addAngularJSONClasses()
     * (SportelloHelpers.php:125-144)
     */
    private JsonNode applySchemaTransformations(
            JsonNode schemaJson,
            String mode,
            String component
    ) {
        ObjectNode mutableSchema = schemaJson.deepCopy();

#if($enableCustomForm == "true")
        // For custom-form component (PHP: lines 128-134)
        if ("custom-form".equals(component)) {
            // Find conditional validations
            // Insert condition validations
            // Insert checkbox arrays
            mutableSchema = prepareJsonSchema(mutableSchema, mode);
        }
#end

#if($enableCustomGrid == "true" || $enableCustomForm == "true")
        // For custom-grid or custom-form (PHP: lines 136-138)
        if ("custom-grid".equals(component) || "custom-form".equals(component)) {
            mutableSchema = searchFields(mutableSchema);
        }
#end

        return mutableSchema;
    }

    /**
     * Prepare JSON schema based on mode - equivalent to PHP prepareJsonSchema()
     * (SportelloHelpers.php:520-558)
     */
    private ObjectNode prepareJsonSchema(ObjectNode schemaJson, String mode) {

        if (schemaJson.has("schema")) {
            ObjectNode schema = (ObjectNode) schemaJson.get("schema");
            Iterator<Map.Entry<String, JsonNode>> fields = schema.fields();

            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> field = fields.next();
                String fieldKey = field.getKey();
                ObjectNode fieldValue = (ObjectNode) field.getValue();

                // Add CSS class for field (PHP: lines 534-535)
                String existingClass = fieldValue.has("fieldHtmlClass") ?
                    fieldValue.get("fieldHtmlClass").asText() + " " : "";
                fieldValue.put("fieldHtmlClass", existingClass + fieldKey + "_olof");

                // Handle readOnly based on mode (PHP: lines 537-543)
                if ("update".equals(mode) &&
                    fieldValue.has("readOnly_for_update") &&
                    fieldValue.get("readOnly_for_update").asBoolean()) {
                    fieldValue.put("readOnly", true);
                }

                if ("insert".equals(mode) &&
                    fieldValue.has("readOnly_for_insert") &&
                    fieldValue.get("readOnly_for_insert").asBoolean()) {
                    fieldValue.put("readOnly", true);
                }
            }
        }

        return schemaJson;
    }

    /**
     * Process dynamic fields (enums, selects) - equivalent to PHP searchFields()
     * (SportelloHelpers.php:579-613)
     */
    private ObjectNode searchFields(ObjectNode node) {
        Iterator<Map.Entry<String, JsonNode>> fields = node.fields();

        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> entry = fields.next();
            String key = entry.getKey();
            JsonNode value = entry.getValue();

            // Handle enum_function (PHP: lines 598-603)
            if ("enum_function".equals(key) && value.isTextual()) {
                String functionName = value.asText();
                // TODO: Implement dynamic function execution
                // For now, log the function to be called
                logger.debug("Dynamic enum function to execute: {}", functionName);
            }

            // Handle search_enum_function (PHP: lines 592-597)
            if ("search_enum_function".equals(key) && value.isTextual()) {
                String functionName = value.asText();
                logger.debug("Dynamic search enum function to execute: {}", functionName);
            }

            // Recurse into nested objects
            if (value.isObject()) {
                searchFields((ObjectNode) value);
            }
        }

        return node;
    }

#if($enableDynamicEnums == "true")
    /**
     * Process dynamic select fields - equivalent to PHP selectFields()
     * (SportelloHelpers.php:616-638)
     */
    private void processDynamicFields(ObjectNode options) {
        // Similar to searchFields but for scenario options
        // Processes upload_select_content functions
        logger.debug("Processing dynamic fields in scenario options");
    }
#end

    /**
     * Load JSON file from path
     */
    private JsonNode loadJsonFile(Path path) throws IOException {
        try (InputStream is = Files.newInputStream(path)) {
            return objectMapper.readTree(is);
        }
    }

    /**
     * Get ObjectMapper instance for external use
     */
    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }
}
