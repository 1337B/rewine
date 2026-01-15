-- =============================================================================
-- V7__events_geolocation_indexes.sql - Add geolocation indexes and event enhancements
-- =============================================================================

-- Add composite index for geolocation-based queries (bounding box)
CREATE INDEX IF NOT EXISTS idx_events_lat_lng ON events(latitude, longitude);

-- Add contact information columns if not exist
ALTER TABLE events ADD COLUMN IF NOT EXISTS contact_email VARCHAR(255);
ALTER TABLE events ADD COLUMN IF NOT EXISTS contact_phone VARCHAR(50);
ALTER TABLE events ADD COLUMN IF NOT EXISTS website_url VARCHAR(500);

-- Add organizer type column for distinguishing between partner and business organizers
ALTER TABLE events ADD COLUMN IF NOT EXISTS organizer_type VARCHAR(50) DEFAULT 'PARTNER';

-- Comment for documentation
COMMENT ON COLUMN events.organizer_type IS 'Type of organizer: PARTNER or BUSINESS';
COMMENT ON COLUMN events.latitude IS 'Event location latitude for geolocation queries';
COMMENT ON COLUMN events.longitude IS 'Event location longitude for geolocation queries';

