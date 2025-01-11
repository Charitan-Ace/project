CREATE EXTENSION IF NOT EXISTS pgcrypto;


DROP TABLE IF EXISTS project CASCADE;
CREATE TABLE project (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    -- Assuming an ID column for primary key
    title VARCHAR(255) NOT NULL,
    description TEXT,
    goal DOUBLE PRECISION NOT NULL,
    -- Changed to DOUBLE PRECISION,
    -- Supports monetary values
    start_time TIMESTAMP WITH TIME ZONE NOT NULL,
    end_time TIMESTAMP WITH TIME ZONE NOT NULL,
    status_type VARCHAR(50) NOT NULL,
    category_type VARCHAR(50) NOT NULL,
    -- Updated enum values for CategoryType
    country_iso_code VARCHAR(50) NOT NULL,
    -- ISO 3166-1 alpha-3 format
    charity_id VARCHAR(50)
    -- created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    -- -- created_at
    -- updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP -- updated_at
);