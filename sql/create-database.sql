\c devicedb;

DROP TABLE IF EXISTS device_reading;

CREATE TABLE IF NOT EXISTS device_reading (
  id BIGSERIAL,
  device_id VARCHAR(50) NOT NULL,
  current_value NUMERIC(22,2) NOT NULL,
  unit VARCHAR(50) NOT NULL,
  "timestamp" timestamp NOT NULL,
  "version" smallint,
  PRIMARY KEY(id)
);