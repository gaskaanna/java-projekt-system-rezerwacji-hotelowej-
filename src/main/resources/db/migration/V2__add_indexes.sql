-- Add indexes to improve query performance

-- Index for email lookups in users table
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);

-- Index for room lookups by number
CREATE INDEX IF NOT EXISTS idx_rooms_room_number ON rooms(room_number);

-- Index for reservation date ranges to improve availability searches
CREATE INDEX IF NOT EXISTS idx_reservations_dates ON reservations(check_in_date, check_out_date);

-- Index for reservation status to improve filtering
CREATE INDEX IF NOT EXISTS idx_reservations_status ON reservations(status);

-- Index for refresh token lookups
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_token ON refresh_tokens(token);

-- Index for user_id in reservations for faster user reservation lookups
CREATE INDEX IF NOT EXISTS idx_reservations_user_id ON reservations(user_id);

-- Index for room_id in reservations for faster room reservation lookups
CREATE INDEX IF NOT EXISTS idx_reservations_room_id ON reservations(room_id);