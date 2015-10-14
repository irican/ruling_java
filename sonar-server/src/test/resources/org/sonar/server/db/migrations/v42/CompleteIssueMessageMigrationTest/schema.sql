-- 4.2
CREATE TABLE "RULES" (
  "ID" INTEGER NOT NULL GENERATED BY DEFAULT AS IDENTITY (START WITH 1, INCREMENT BY 1),
  "PLUGIN_RULE_KEY" VARCHAR(200) NOT NULL,
  "PLUGIN_NAME" VARCHAR(255) NOT NULL,
  "DESCRIPTION" VARCHAR(16777215),
  "PRIORITY" INTEGER,
  "CARDINALITY" VARCHAR(10),
  "PARENT_ID" INTEGER,
  "PLUGIN_CONFIG_KEY" VARCHAR(500),
  "NAME" VARCHAR(200),
  "STATUS" VARCHAR(40),
  "LANGUAGE" VARCHAR(20),
  "CREATED_AT" TIMESTAMP,
  "UPDATED_AT" TIMESTAMP,
  "NOTE_CREATED_AT" TIMESTAMP,
  "NOTE_UPDATED_AT" TIMESTAMP,
  "NOTE_USER_LOGIN" VARCHAR(40),
  "NOTE_DATA" CLOB(2147483647)
);

CREATE TABLE "ISSUES" (
  "ID" BIGINT NOT NULL GENERATED BY DEFAULT AS IDENTITY (START WITH 1, INCREMENT BY 1),
  "KEE" VARCHAR(50) UNIQUE NOT NULL,
  "COMPONENT_ID" INTEGER NOT NULL,
  "ROOT_COMPONENT_ID" INTEGER,
  "RULE_ID" INTEGER,
  "SEVERITY" VARCHAR(10),
  "MANUAL_SEVERITY" BOOLEAN NOT NULL,
  "MESSAGE" VARCHAR(4000),
  "LINE" INTEGER,
  "EFFORT_TO_FIX" DOUBLE,
  "TECHNICAL_DEBT" INTEGER,
  "STATUS" VARCHAR(20),
  "RESOLUTION" VARCHAR(20),
  "CHECKSUM" VARCHAR(1000),
  "REPORTER" VARCHAR(40),
  "ASSIGNEE" VARCHAR(40),
  "AUTHOR_LOGIN" VARCHAR(100),
  "ACTION_PLAN_KEY" VARCHAR(50) NULL,
  "ISSUE_ATTRIBUTES" VARCHAR(4000),
  "ISSUE_CREATION_DATE" TIMESTAMP,
  "ISSUE_CLOSE_DATE" TIMESTAMP,
  "ISSUE_UPDATE_DATE" TIMESTAMP,
  "CREATED_AT" TIMESTAMP,
  "UPDATED_AT" TIMESTAMP
);
