-- Parking lot / Level / Spot / Ticket / RateCard schema
CREATE TABLE parking_lot (
                             id VARCHAR(100) PRIMARY KEY,
                             name VARCHAR(255) NOT NULL
);

CREATE TABLE level (
                       id BIGSERIAL PRIMARY KEY,
                       level_number INT NOT NULL,
                       parking_lot_id VARCHAR(100) NOT NULL,
                       CONSTRAINT fk_level_lot FOREIGN KEY (parking_lot_id) REFERENCES parking_lot(id) ON DELETE CASCADE
);

CREATE TABLE parking_spot (
                              id VARCHAR(100) PRIMARY KEY,
                              level_id BIGINT NOT NULL,
                              number INT NOT NULL,
                              spot_type VARCHAR(50) NOT NULL,
                              occupied BOOLEAN NOT NULL DEFAULT false,
                              current_ticket_id VARCHAR(100),
                              version BIGINT NOT NULL DEFAULT 0,
                              CONSTRAINT fk_spot_level FOREIGN KEY (level_id) REFERENCES level(id) ON DELETE CASCADE
);

CREATE TABLE parking_ticket (
                                ticket_id VARCHAR(100) PRIMARY KEY,
                                parking_lot_id VARCHAR(100),
                                spot_id VARCHAR(100),
                                vehicle_plate VARCHAR(100),
                                vehicle_type VARCHAR(50),
                                entry_time TIMESTAMP WITH TIME ZONE,
                                exit_time TIMESTAMP WITH TIME ZONE,
                                cost NUMERIC(12,2),
                                status VARCHAR(50),
                                CONSTRAINT fk_ticket_lot FOREIGN KEY (parking_lot_id) REFERENCES parking_lot(id),
                                CONSTRAINT fk_ticket_spot FOREIGN KEY (spot_id) REFERENCES parking_spot(id)
);

CREATE TABLE rate_card (
                           id BIGSERIAL PRIMARY KEY,
                           tenant_id VARCHAR(100),
                           spot_type VARCHAR(50),
                           rate_per_hour NUMERIC(12,2) NOT NULL
);

-- Global default rates
INSERT INTO rate_card (tenant_id, spot_type, rate_per_hour)
VALUES
    (NULL, 'MOTORCYCLE', 10.00),
    (NULL, 'COMPACT', 20.00),
    (NULL, 'REGULAR', 25.00),
    (NULL, 'EV', 30.00),
    (NULL, 'LARGE', 40.00);

-- Tenant-specific override example
INSERT INTO rate_card (tenant_id, spot_type, rate_per_hour)
VALUES
    ('tenant_abc', 'REGULAR', 35.00),
    ('tenant_abc', 'EV', 50.00);

-- Indexes to help queries
CREATE INDEX idx_level_lot ON level(parking_lot_id);
CREATE INDEX idx_spot_level ON parking_spot(level_id);
CREATE INDEX idx_spot_occupied ON parking_spot(occupied);
