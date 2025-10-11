-- PostgreSQL: System logs table for application monitoring
-- Automatically executed during webapp creation with TomEEx

-- Create system_logs table (idempotent)
CREATE TABLE IF NOT EXISTS system_logs (
    id SERIAL PRIMARY KEY,
    log_level VARCHAR(20) NOT NULL,
    category VARCHAR(100),
    message TEXT NOT NULL,
    details JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100)
);

-- Insert initial log entry to confirm successful database initialization
INSERT INTO system_logs (log_level, category, message, details, created_by) VALUES
('INFO', 'SYSTEM', 'Database initialized successfully',
 '{"database_type": "PostgreSQL", "schema_version": "1.0.0", "initialized_by": "TomEEx Archetype", "app_name": "${artifactId}"}',
 'system')
ON CONFLICT DO NOTHING;

-- Create indexes for log querying performance
CREATE INDEX IF NOT EXISTS idx_system_logs_level ON system_logs(log_level);
CREATE INDEX IF NOT EXISTS idx_system_logs_category ON system_logs(category);
CREATE INDEX IF NOT EXISTS idx_system_logs_created_at ON system_logs(created_at DESC);

-- Display confirmation message
DO $$
BEGIN
    RAISE NOTICE 'System logs table created successfully with initial entry';
END $$;
