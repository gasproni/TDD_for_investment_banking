
CREATE TABLE SecurityData (
 id INTEGER IDENTITY,
 creationTime VARCHAR(30),
 security VARCHAR(100),
 spot DOUBLE,
 volatility DOUBLE,
 currency VARCHAR(3));

 CREATE INDEX securityTime ON SecurityData (security, creationTime);

