-- SQLite: System logs table for application monitoring
-- Automatically executed during webapp creation with TomEEx

-- Create system_logs table (idempotent)
CREATE TABLE IF NOT EXISTS system_logs (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    log_level TEXT NOT NULL,
    category TEXT,
    message TEXT NOT NULL,
    details TEXT,
    created_at TEXT DEFAULT CURRENT_TIMESTAMP,
    created_by TEXT
);

-- Insert initial log entry to confirm successful database initialization (idempotent)
INSERT OR IGNORE INTO system_logs (id, log_level, category, message, details, created_by) VALUES
(1, 'INFO', 'SYSTEM', 'Database initialized successfully',
 '{"database_type": "SQLite", "schema_version": "1.0.0", "initialized_by": "TomEEx Archetype", "app_name": "${artifactId}"}',
 'system');

-- Create indexes for log querying performance
CREATE INDEX IF NOT EXISTS idx_system_logs_level ON system_logs(log_level);
CREATE INDEX IF NOT EXISTS idx_system_logs_category ON system_logs(category);
CREATE INDEX IF NOT EXISTS idx_system_logs_created_at ON system_logs(created_at DESC);

-- Display confirmation message
SELECT 'System logs table created successfully with initial entry' AS status;
