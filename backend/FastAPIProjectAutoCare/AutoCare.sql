-- User
CREATE TABLE users (
    user_id SERIAL PRIMARY KEY,
    full_name TEXT NOT NULL,
    email TEXT NOT NULL UNIQUE,
    password TEXT NOT NULL,
    notify_on BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Cars
CREATE TABLE cars (
    car_id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    brand TEXT NOT NULL,
    model TEXT NOT NULL,
    year INTEGER NOT NULL,
    mileage REAL NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ServiceEvent
CREATE TABLE service_events (
    event_id SERIAL PRIMARY KEY,
    car_id INTEGER NOT NULL REFERENCES cars(car_id) ON DELETE CASCADE,
    service_type TEXT NOT NULL,
    due_date DATE NOT NULL,
    due_mileage REAL NOT NULL,
    is_completed BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Notifications
CREATE TABLE notifications (
    notification_id SERIAL PRIMARY KEY,
    event_id INTEGER NOT NULL REFERENCES service_events(event_id) ON DELETE CASCADE,
    notify_time TIMESTAMP NOT NULL,
    is_sent BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ServiceHistory
CREATE TABLE service_history (
    event_id SERIAL PRIMARY KEY,
    car_id INTEGER NOT NULL REFERENCES cars(car_id) ON DELETE CASCADE,
    service_type TEXT NOT NULL,
    service_date DATE NOT NULL,
    mileage REAL NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tokens de réinitialisation de mot de passe
CREATE TABLE reset_tokens (
    id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES users(user_id) ON DELETE CASCADE,
    token TEXT UNIQUE NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Join Notifications
SELECT n.*
FROM notifications n
JOIN service_events s ON n.event_id = s.event_id
JOIN cars c ON s.car_id = c.car_id
WHERE c.user_id = 2;
Pizza base copy paste the word