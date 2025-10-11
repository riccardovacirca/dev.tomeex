package ${package}.servlet;

import dev.tomeex.tools.Database;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class SystemLogServlet extends HttpServlet {

    private ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        super.init();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        setupCorsHeaders(response);
        response.setContentType("application/json");

        String pathInfo = request.getPathInfo();
        Database db = null;

        try {
            db = new Database("jdbc/${artifactId}DB");
            db.open();

            if (pathInfo == null || pathInfo.equals("/")) {
                // Get all logs
                Database.Recordset logs = db.select("SELECT * FROM system_logs ORDER BY created_at DESC");
                objectMapper.writeValue(response.getWriter(), logs);
            } else {
                // Get specific log by ID
                String idStr = pathInfo.substring(1);
                Long id = Long.valueOf(idStr);
                Database.Recordset logs = db.select("SELECT * FROM system_logs WHERE id = ?", id);

                if (logs != null && !logs.isEmpty()) {
                    objectMapper.writeValue(response.getWriter(), logs.get(0));
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().write("{\"error\": \"Log entry not found\"}");
                }
            }

            db.close();
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"Internal server error: " + e.getMessage() + "\"}");
            if (db != null) {
                try { db.close(); } catch (Exception ex) {}
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        setupCorsHeaders(response);
        response.setContentType("application/json");
        Database db = null;

        try {
            @SuppressWarnings("unchecked")
            java.util.Map<String, Object> logData = objectMapper.readValue(request.getReader(), java.util.Map.class);

            db = new Database("jdbc/${artifactId}DB");
            db.open();
            db.query("INSERT INTO system_logs (log_level, category, message, details, created_by) VALUES (?, ?, ?, ?, ?)",
                    logData.get("logLevel"),
                    logData.get("category"),
                    logData.get("message"),
                    logData.get("details"),
                    logData.get("createdBy"));

            long id = db.lastInsertId();
            logData.put("id", id);
            db.close();

            response.setStatus(HttpServletResponse.SC_CREATED);
            objectMapper.writeValue(response.getWriter(), logData);
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Invalid data: " + e.getMessage() + "\"}");
            if (db != null) {
                try { db.close(); } catch (Exception ex) {}
            }
        }
    }

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        setupCorsHeaders(response);
        response.setStatus(HttpServletResponse.SC_OK);
    }

    private void setupCorsHeaders(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
    }
}
