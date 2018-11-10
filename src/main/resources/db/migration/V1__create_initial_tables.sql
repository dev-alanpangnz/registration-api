CREATE TABLE user_account (
  user_id BIGSERIAL NOT NULL PRIMARY KEY,
  username VARCHAR(20) NOT NULL,
  email VARCHAR(25) NOT NULL,
  email_verified VARCHAR(10),
  password VARCHAR(60) NOT NULL,
  password_salt VARCHAR(10) NOT NULL,
  session_active VARCHAR(10)
);