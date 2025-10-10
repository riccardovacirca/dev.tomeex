#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.scenario.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

/**
 * Options configuration for a Scenario.
 * Contains schema reference, API endpoints, and action configurations.
 *
 * @author TomEEx Dev Team
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ScenarioOptions {

    private String schema;
    private String schemaMode; // "update" or "insert"
    private JsonNode schemaData; // Processed schema (inline after processing)
    private String schemaMessage; // Error message if schema not found
    private String title;
    private Boolean hideSearchButtons;
    private List<ApiEndpoint> api;
    private ActionGroup actionsTop;
    private ActionGroup actionsRow;
    private ActionGroup actionsBottom;

    // Getters and Setters
    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getSchemaMode() {
        return schemaMode;
    }

    public void setSchemaMode(String schemaMode) {
        this.schemaMode = schemaMode;
    }

    public JsonNode getSchemaData() {
        return schemaData;
    }

    public void setSchemaData(JsonNode schemaData) {
        this.schemaData = schemaData;
    }

    public String getSchemaMessage() {
        return schemaMessage;
    }

    public void setSchemaMessage(String schemaMessage) {
        this.schemaMessage = schemaMessage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getHideSearchButtons() {
        return hideSearchButtons;
    }

    public void setHideSearchButtons(Boolean hideSearchButtons) {
        this.hideSearchButtons = hideSearchButtons;
    }

    public List<ApiEndpoint> getApi() {
        return api;
    }

    public void setApi(List<ApiEndpoint> api) {
        this.api = api;
    }

    public ActionGroup getActionsTop() {
        return actionsTop;
    }

    public void setActionsTop(ActionGroup actionsTop) {
        this.actionsTop = actionsTop;
    }

    public ActionGroup getActionsRow() {
        return actionsRow;
    }

    public void setActionsRow(ActionGroup actionsRow) {
        this.actionsRow = actionsRow;
    }

    public ActionGroup getActionsBottom() {
        return actionsBottom;
    }

    public void setActionsBottom(ActionGroup actionsBottom) {
        this.actionsBottom = actionsBottom;
    }

    /**
     * API Endpoint configuration
     */
    public static class ApiEndpoint {
        private String operation;
        private String method;
        private String url;
        private List<String> roles;

        public String getOperation() {
            return operation;
        }

        public void setOperation(String operation) {
            this.operation = operation;
        }

        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public List<String> getRoles() {
            return roles;
        }

        public void setRoles(List<String> roles) {
            this.roles = roles;
        }
    }

    /**
     * Group of actions (top, row, bottom)
     */
    public static class ActionGroup {
        private List<Action> items;

        public List<Action> getItems() {
            return items;
        }

        public void setItems(List<Action> items) {
            this.items = items;
        }
    }

    /**
     * Single action configuration
     */
    public static class Action {
        private String code;
        private String title;
        private String icon;
        private String cssClass;
        private List<String> roles;
        private ActionConfig action;
        private String gotoScenario;
        private Boolean maintainEntityId;
        private Boolean showResultAsMessage;
        private Boolean downloadResult;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public String getCssClass() {
            return cssClass;
        }

        public void setCssClass(String cssClass) {
            this.cssClass = cssClass;
        }

        public List<String> getRoles() {
            return roles;
        }

        public void setRoles(List<String> roles) {
            this.roles = roles;
        }

        public ActionConfig getAction() {
            return action;
        }

        public void setAction(ActionConfig action) {
            this.action = action;
        }

        public String getGotoScenario() {
            return gotoScenario;
        }

        public void setGotoScenario(String gotoScenario) {
            this.gotoScenario = gotoScenario;
        }

        public Boolean getMaintainEntityId() {
            return maintainEntityId;
        }

        public void setMaintainEntityId(Boolean maintainEntityId) {
            this.maintainEntityId = maintainEntityId;
        }

        public Boolean getShowResultAsMessage() {
            return showResultAsMessage;
        }

        public void setShowResultAsMessage(Boolean showResultAsMessage) {
            this.showResultAsMessage = showResultAsMessage;
        }

        public Boolean getDownloadResult() {
            return downloadResult;
        }

        public void setDownloadResult(Boolean downloadResult) {
            this.downloadResult = downloadResult;
        }
    }

    /**
     * Action type configuration
     */
    public static class ActionConfig {
        private String type; // "goto-page", "download-file", "download-datatable", etc.
        private String filename;
        private String downloadType;
        private String errorMessage;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getFilename() {
            return filename;
        }

        public void setFilename(String filename) {
            this.filename = filename;
        }

        public String getDownloadType() {
            return downloadType;
        }

        public void setDownloadType(String downloadType) {
            this.downloadType = downloadType;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }
    }
}
