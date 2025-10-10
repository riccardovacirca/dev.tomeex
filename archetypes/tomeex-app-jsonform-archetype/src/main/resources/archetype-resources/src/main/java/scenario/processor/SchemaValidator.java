#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.scenario.processor;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JSON Schema validator for scenario schemas.
 *
 * @author TomEEx Dev Team
 */
#if($enableSchemaValidation == "true")
public class SchemaValidator {

    private static final Logger logger = LoggerFactory.getLogger(SchemaValidator.class);

    /**
     * Validate a schema against JSON Schema specification
     */
    public boolean validateSchema(JsonNode schema) {
        // TODO: Implement JSON Schema validation using everit-json-schema
        logger.debug("Schema validation placeholder");
        return true;
    }
}
#else
public class SchemaValidator {
    // Schema validation disabled
    public boolean validateSchema(com.fasterxml.jackson.databind.JsonNode schema) {
        return true;
    }
}
#end
