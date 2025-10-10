#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.scenario.servlet;

import ${package}.scenario.processor.ScenarioProcessor;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;

/**
 * Servlet for Scenario Configuration API.
 *
 * Exposes the JSON-Driven Architecture configuration via HTTP.
 * Similar to PHP SportelloApiController::getPage()
 *
 * Architecture: WebApp = Single Module
 * - Configuration file: WEB-INF/scenarios/json/main.json
 * - Schema files: WEB-INF/scenarios/json/forms/*.json
 *
 * URL Patterns:
 * - GET /api/scenario/config              - Get full webapp configuration
 * - GET /api/scenario/list                - List all scenarios
 * - GET /api/scenario/{scenarioKey}       - Get specific scenario
 *
 * @author Sportello Archetype Generator
 */
@WebServlet(
    name = "ScenarioServlet",
    urlPatterns = {"/api/scenario/*"},
    loadOnStartup = 1
)
public class ScenarioServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(ScenarioServlet.class);
    private static final long serialVersionUID = 1L;

    private ScenarioProcessor processor;
    private ObjectMapper objectMapper;
    private String appName;

    @Override
    public void init() throws ServletException {
        super.init();

        // Initialize ObjectMapper
        objectMapper = new ObjectMapper();

        // Get application name from context
        appName = getServletContext().getInitParameter("app.name");
        if (appName == null) {
            // Default to "main" - configuration is always in main.json
            appName = "main";
        }

        // Initialize processor with scenarios base path
        String scenariosPath = getServletContext().getInitParameter("scenarios.path");
        if (scenariosPath == null) {
            // Default path: WEB-INF/scenarios
            scenariosPath = getServletContext().getRealPath("/WEB-INF/scenarios");
        }

        logger.info("Initializing ScenarioProcessor for app '{}' with path: {}",
            appName, scenariosPath);
        this.processor = new ScenarioProcessor(Paths.get(scenariosPath));
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Set response type
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Parse URL path
        String pathInfo = request.getPathInfo(); // e.g., /config or /SCENARIO_KEY

        if (pathInfo == null || pathInfo.equals("/")) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST,
                "Invalid request. Usage: /api/scenario/{config|list|scenarioKey}");
            return;
        }

        String[] pathParts = pathInfo.substring(1).split("/");

        try {
            if (pathParts.length == 1 && "config".equals(pathParts[0])) {
                // GET /api/scenario/config
                handleGetConfiguration(request, response);

            } else if (pathParts.length == 1 && "list".equals(pathParts[0])) {
                // GET /api/scenario/list
                handleListScenarios(request, response);

            } else if (pathParts.length == 1) {
                // GET /api/scenario/{scenarioKey}
                handleGetScenario(request, response, pathParts[0]);

            } else {
                sendError(response, HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid URL pattern");
            }

        } catch (Exception e) {
            logger.error("Error processing request", e);
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                "Error: " + e.getMessage());
        }
    }

    /**
     * Handle GET /api/scenario/config
     * Returns full processed configuration for this webapp
     */
    private void handleGetConfiguration(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        logger.info("Loading configuration for webapp: {}", appName);

        // Get parameters
        String role = request.getParameter("role");
        boolean maintenance = "true".equals(request.getParameter("maintenance"));

        // Determine role from session if not provided
        if (role == null) {
            role = (String) request.getSession().getAttribute("active_role");
        }

        // Build JSON path: json/{appName}.json
        String jsonPath = String.format("json/%s.json", appName);

        // Process configuration
        JsonNode config = processor.processConfiguration(
            jsonPath,
            role,
            true, // validationTerms - TODO: implement proper validation
            maintenance
        );

        // Send response
        response.setStatus(HttpServletResponse.SC_OK);
        PrintWriter out = response.getWriter();
        objectMapper.writeValue(out, config);
        out.flush();
    }

    /**
     * Handle GET /api/scenario/list
     * Returns list of all scenarios for this webapp
     */
    private void handleListScenarios(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        logger.info("Listing scenarios for webapp: {}", appName);

        String role = (String) request.getSession().getAttribute("active_role");
        String jsonPath = String.format("json/%s.json", appName);

        JsonNode config = processor.processConfiguration(jsonPath, role, true, false);

        if (config.has("scenarios")) {
            response.setStatus(HttpServletResponse.SC_OK);
            PrintWriter out = response.getWriter();
            objectMapper.writeValue(out, config.get("scenarios"));
            out.flush();
        } else {
            sendError(response, HttpServletResponse.SC_NOT_FOUND,
                "No scenarios found for webapp: " + appName);
        }
    }

    /**
     * Handle GET /api/scenario/{scenarioKey}
     * Returns specific scenario details
     */
    private void handleGetScenario(
            HttpServletRequest request,
            HttpServletResponse response,
            String scenarioKey
    ) throws IOException {

        logger.info("Loading scenario {} for webapp {}", scenarioKey, appName);

        String role = (String) request.getSession().getAttribute("active_role");
        String jsonPath = String.format("json/%s.json", appName);

        JsonNode config = processor.processConfiguration(jsonPath, role, true, false);

        if (config.has("scenarios") &&
            config.get("scenarios").has(scenarioKey)) {

            response.setStatus(HttpServletResponse.SC_OK);
            PrintWriter out = response.getWriter();
            objectMapper.writeValue(out, config.get("scenarios").get(scenarioKey));
            out.flush();

        } else {
            sendError(response, HttpServletResponse.SC_NOT_FOUND,
                "Scenario not found: " + scenarioKey);
        }
    }

    /**
     * Send error response as JSON
     */
    private void sendError(HttpServletResponse response, int statusCode, String message)
            throws IOException {
        response.setStatus(statusCode);
        PrintWriter out = response.getWriter();
        objectMapper.writeValue(out, new ErrorResponse(message));
        out.flush();
    }

    /**
     * Error response wrapper
     */
    private static class ErrorResponse {
        public final String error;

        ErrorResponse(String error) {
            this.error = error;
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        logger.info("ScenarioServlet destroyed");
    }
}
