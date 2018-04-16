CREATE TABLE users (
  id                     SERIAL,
  username               VARCHAR NOT NULL PRIMARY KEY,
  password               CHAR(32) NOT NULL,
  firstname              VARCHAR NOT NULL,
  lastname               VARCHAR NOT NULL,
  email                  VARCHAR NOT NULL,
  department             VARCHAR NOT NULL,
  role                   VARCHAR NOT NULL
);

CREATE TABLE rooms (
  id                     SERIAL,
  roomID                 VARCHAR NOT NULL PRIMARY KEY,
  name                   VARCHAR NOT NULL,
  department             VARCHAR NOT NULL,
  capacity               INTEGER NOT NULL,
  projector              INTEGER NOT NULL,
  whiteboard             INTEGER NOT NULL,
  int_whiteboard         INTEGER NOT NULL,
  videocall_capable      INTEGER NOT NULL,
  microphone             INTEGER NOT NULL
);

CREATE TABLE reservations (
  id                     SERIAL,
  roomID                 VARCHAR NOT NULL REFERENCES rooms(roomID),
  start_timestamp        TIMESTAMP,
  end_timestamp          TIMESTAMP
);