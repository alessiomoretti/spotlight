INSERT INTO users VALUES (1, 'johndoe', 'd763ec748433fb79a04f82bd46133d55', 'John', 'Doe', 'john.doe@uni.com', 'History', 2);
INSERT INTO users VALUES (2, 'jennyseed', 'd63f0ccf73571e11d728612f478738f5', 'Jenny', 'Seed', 'jenny.seed@uni.com', 'Administration', 3);
INSERT INTO users VALUES (3, 'jackblack', '4e661d7c21bb2711960c00ad3f31ca65', 'Jack', 'Black', 'jack.black@uni.com', 'Infopoint', 1);

INSERT INTO rooms VALUES (1, 'A1 - Campus', 'History', 150, 1, 1, 1, 1, 1);
INSERT INTO rooms VALUES (2, 'A2 - Campus', 'History', 100, 1, 1, 0, 0, 1);
INSERT INTO rooms VALUES (3, 'B1 - Campus', 'Engineering', 300, 1, 0, 1, 1, 1);
INSERT INTO rooms VALUES (4, 'B2 - Campus', 'Engineering', 200, 1, 0, 0, 0, 0);
INSERT INTO rooms VALUES (5, 'C1 - Campus', 'Law School', 250, 1, 1, 1, 1, 1);
INSERT INTO rooms VALUES (6, 'C1 - Campus', 'Law School', 300, 1, 0, 1, 0, 1);
INSERT INTO rooms VALUES (7, 'K1 - Village', 'Economics', 320, 0, 1, 0, 0, 0);
INSERT INTO rooms VALUES (8, 'K2 - Village', 'Economics', 140, 1, 0, 0, 1, 1);

INSERT INTO events VALUES ('testevent - johndoe - 1524317641', 'testevent', to_timestamp(1524427257), to_timestamp(1524456057), 'johndoe', 'john.doe@uni.com');
INSERT INTO events VALUES ('testevent2 - johndoe - 1524317641', 'testevent2', to_timestamp(1524427450), to_timestamp(1524457000), 'johndoe', 'john.doe@uni.com');

INSERT INTO departments VALUES (1, 'History');
INSERT INTO departments VALUES (2, 'Engineering');
INSERT INTO departments VALUES (3, 'Law School');
INSERT INTO departments VALUES (4, 'Economics');