#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.scenario.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;
import java.util.Map;

/**
 * Represents a single Scenario in the JSON-Driven Architecture.
 *
 * Ported from PHP Sportello SCAI system.
 *
 * A Scenario defines:
 * - Route and navigation
 * - Component type (custom-grid, custom-form, custom-view)
 * - Schema reference
 * - Actions (top, row, bottom)
 * - Role-based authorization
 * - Breadcrumb navigation
 *
 * @author Sportello Archetype Generator
 * @version 1.0.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Scenario {

    private String key;
    private String title;
    private String breadcrumb;
    private String route;
    private String component;
    private List<String> roles;
    private Boolean hideInMaintenance;
    private Boolean showInMaintenance;
    private MenuConfig menu;
    private ScenarioOptions options;
    private List<BreadcrumbEntry> breadcrumbs;

    // Constructors
    public Scenario() {}

    public Scenario(String key, String title, String component) {
        this.key = key;
        this.title = title;
        this.component = component;
    }

    // Getters and Setters
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBreadcrumb() {
        return breadcrumb;
    }

    public void setBreadcrumb(String breadcrumb) {
        this.breadcrumb = breadcrumb;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public Boolean getHideInMaintenance() {
        return hideInMaintenance;
    }

    public void setHideInMaintenance(Boolean hideInMaintenance) {
        this.hideInMaintenance = hideInMaintenance;
    }

    public Boolean getShowInMaintenance() {
        return showInMaintenance;
    }

    public void setShowInMaintenance(Boolean showInMaintenance) {
        this.showInMaintenance = showInMaintenance;
    }

    public MenuConfig getMenu() {
        return menu;
    }

    public void setMenu(MenuConfig menu) {
        this.menu = menu;
    }

    public ScenarioOptions getOptions() {
        return options;
    }

    public void setOptions(ScenarioOptions options) {
        this.options = options;
    }

    public List<BreadcrumbEntry> getBreadcrumbs() {
        return breadcrumbs;
    }

    public void setBreadcrumbs(List<BreadcrumbEntry> breadcrumbs) {
        this.breadcrumbs = breadcrumbs;
    }

    @Override
    public String toString() {
        return "Scenario{" +
                "key='" + key + ${symbol_escape}'' +
                ", title='" + title + ${symbol_escape}'' +
                ", component='" + component + ${symbol_escape}'' +
                ", route='" + route + ${symbol_escape}'' +
                '}';
    }

    /**
     * Menu configuration for scenario
     */
    public static class MenuConfig {
        private Map<String, MenuEntry> entries;

        public Map<String, MenuEntry> getEntries() {
            return entries;
        }

        public void setEntries(Map<String, MenuEntry> entries) {
            this.entries = entries;
        }
    }

    /**
     * Single menu entry
     */
    public static class MenuEntry {
        private String order;
        private String title;
        private String icon;
        private String description;

        public String getOrder() {
            return order;
        }

        public void setOrder(String order) {
            this.order = order;
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

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    /**
     * Breadcrumb navigation entry
     */
    public static class BreadcrumbEntry {
        private String title;
        private String route;

        public BreadcrumbEntry() {}

        public BreadcrumbEntry(String title, String route) {
            this.title = title;
            this.route = route;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getRoute() {
            return route;
        }

        public void setRoute(String route) {
            this.route = route;
        }
    }
}
