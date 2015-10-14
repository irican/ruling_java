CREATE TABLE "USERS" (
  "ID" INTEGER NOT NULL GENERATED BY DEFAULT AS IDENTITY (START WITH 1, INCREMENT BY 1),
  "LOGIN" VARCHAR(255),
  "NAME" VARCHAR(200),
  "EMAIL" VARCHAR(100),
  "CRYPTED_PASSWORD" VARCHAR(40),
  "SALT" VARCHAR(40),
  "CREATED_AT" TIMESTAMP,
  "UPDATED_AT" TIMESTAMP,
  "CREATED_AT_MS" BIGINT,
  "UPDATED_AT_MS" BIGINT,
  "REMEMBER_TOKEN" VARCHAR(500),
  "REMEMBER_TOKEN_EXPIRES_AT" TIMESTAMP,
  "ACTIVE" BOOLEAN DEFAULT TRUE
);
