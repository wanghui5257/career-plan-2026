-- Career Plan 2026 Database Migration V5
-- Add new fields for user profile and task confirmation
-- Date: 2026-03-19

-- Add background and goals fields to users table
ALTER TABLE users ADD COLUMN IF NOT EXISTS background TEXT;
ALTER TABLE users ADD COLUMN IF NOT EXISTS goals TEXT;

-- Add confirmed and confirmed_at fields to tasks table
ALTER TABLE tasks ADD COLUMN IF NOT EXISTS confirmed BOOLEAN DEFAULT FALSE;
ALTER TABLE tasks ADD COLUMN IF NOT EXISTS confirmed_at DATETIME;
