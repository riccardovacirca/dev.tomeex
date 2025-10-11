-- MariaDB: System logs table for application monitoring
-- Automatically executed during webapp creation with TomEEx

-- Create system_logs table (idempotent)
CREATE TABLE IF NOT EXISTS system_logs (
    id INT AUTO_INCREMENT PRIMARY KEY,
    log_level VARCHAR(20) NOT NULL,
    category VARCHAR(100),
    message TEXT NOT NULL,
    details JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insert initial log entry to confirm successful database initialization (idempotent)
INSERT INTO system_logs (log_level, category, message, details, created_by)
SELECT 'INFO', 'SYSTEM', 'Database initialized successfully',
       '{"database_type": "MariaDB", "schema_version": "1.0.0", "initialized_by": "TomEEx Archetype", "app_name": "${artifactId}"}',
       'system'
WHERE NOT EXISTS (
    SELECT 1 FROM system_logs WHERE category = 'SYSTEM' AND message = 'Database initialized successfully'
);

-- Create indexes for log querying performance
CREATE INDEX IF NOT EXISTS idx_system_logs_level ON system_logs(log_level);
CREATE INDEX IF NOT EXISTS idx_system_logs_category ON system_logs(category);
CREATE INDEX IF NOT EXISTS idx_system_logs_created_at ON system_logs(created_at DESC);

-- Display confirmation message
SELECT 'System logs table created successfully with initial entry' AS status;
