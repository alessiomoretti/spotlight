CREATE TABLE users (
  id SERIAL,
  username VARCHAR(100) NOT NULL,
  password CHAR(32) NOT NULL,
  firstname VARCHAR(100) NOT NULL,
  lastname VARCHAR (100) NOT NULL,
  email VARCHAR(100) NOT NULL,
  department VARCHAR(100) NOT NULL,
  role VARCHAR(100) NOT NULL
);