#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.contextview.servlet;

import ${package}.contextview.processor.ContextViewProcessor;
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
 * Servlet for ContextView Configuration API.
 *
 * Exposes the JSON-Driven Architecture configuration via HTTP.
 *
 * Architecture: WebApp = Single Module
 * - Configuration file: WEB-INF/contextviews/json/main.json
 * - Schema files: WEB-INF/contextviews/json/forms/*.json
 *
 * URL Patterns:
 * - GET /api/contextview/config              - Get full webapp configuration
 * - GET /api/contextview/list                - List all contextviews
 * - GET /api/contextview/{contextviewKey}       - Get specific contextview
 *
 * @author TomEEx Dev Team
 */
@WebServlet(
    name = "ContextViewServlet",
    urlPatterns = {"/api/contextview/*"},
    loadOnStartup = 1
)
public class ContextViewServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(ContextViewServlet.class);
    private static final long serialVersionUID = 1L;

    private ContextViewProcessor processor;
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

        // Initialize processor with contextviews base path
        String contextviewsPath = getServletContext().getInitParameter("contextviews.path");
        if (contextviewsPath == null) {
            // Default path: WEB-INF/contextviews
            contextviewsPath = getServletContext().getRealPath("/WEB-INF/contextviews");
        }

        logger.info("Initializing ContextViewProcessor for app '{}' with path: {}",
            appName, contextviewsPath);
        this.processor = new ContextViewProcessor(Paths.get(contextviewsPath));
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
                "Invalid request. Usage: /api/contextview/{config|list|contextviewKey}");
            return;
        }

        String[] pathParts = pathInfo.substring(1).split("/");

        try {
            if (pathParts.length == 1 && "config".equals(pathParts[0])) {
                // GET /api/contextview/config
                handleGetConfiguration(request, response);

            } else if (pathParts.length == 1 && "list".equals(pathParts[0])) {
                // GET /api/contextview/list
                handleListScenarios(request, response);

            } else if (pathParts.length == 1) {
                // GET /api/contextview/{contextviewKey}
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
     * Handle GET /api/contextview/config
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
     * Handle GET /api/contextview/list
     * Returns list of all contextviews for this webapp
     */
    private void handleListScenarios(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        logger.info("Listing contextviews for webapp: {}", appName);

        String role = (String) request.getSession().getAttribute("active_role");
        String jsonPath = String.format("json/%s.json", appName);

        JsonNode config = processor.processConfiguration(jsonPath, role, true, false);

        if (config.has("contextviews")) {
            response.setStatus(HttpServletResponse.SC_OK);
            PrintWriter out = response.getWriter();
            objectMapper.writeValue(out, config.get("contextviews"));
            out.flush();
        } else {
            sendError(response, HttpServletResponse.SC_NOT_FOUND,
                "No contextviews found for webapp: " + appName);
        }
    }

    /**
     * Handle GET /api/contextview/{contextviewKey}
     * Returns specific contextview details
     */
    private void handleGetScenario(
            HttpServletRequest request,
            HttpServletResponse response,
            String contextviewKey
    ) throws IOException {

        logger.info("Loading contextview {} for webapp {}", contextviewKey, appName);

        String role = (String) request.getSession().getAttribute("active_role");
        String jsonPath = String.format("json/%s.json", appName);

        JsonNode config = processor.processConfiguration(jsonPath, role, true, false);

        if (config.has("contextviews") &&
            config.get("contextviews").has(contextviewKey)) {

            response.setStatus(HttpServletResponse.SC_OK);
            PrintWriter out = response.getWriter();
            objectMapper.writeValue(out, config.get("contextviews").get(contextviewKey));
            out.flush();

        } else {
            sendError(response, HttpServletResponse.SC_NOT_FOUND,
                "ContextView not found: " + contextviewKey);
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
        logger.info("ContextViewServlet destroyed");
    }
}
