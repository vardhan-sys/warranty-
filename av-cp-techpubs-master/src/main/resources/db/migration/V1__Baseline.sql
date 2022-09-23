CREATE TABLE "role"
(
    name              VARCHAR NOT NULL CONSTRAINT role_pk PRIMARY KEY,
    label             VARCHAR NOT NULL,
    description       VARCHAR NOT NULL,
    policy            JSONB   NOT NULL,
    created_by        VARCHAR,
    creation_date     TIMESTAMP DEFAULT now(),
    last_updated_by   VARCHAR,
    last_updated_date TIMESTAMP DEFAULT now()
);
CREATE INDEX role_name_idx ON role (name);

CREATE TABLE "user"
(
    sso               VARCHAR NOT NULL CONSTRAINT user_pk PRIMARY KEY,
    created_by        VARCHAR,
    creation_date     TIMESTAMP DEFAULT now(),
    last_updated_by   VARCHAR,
    last_updated_date TIMESTAMP DEFAULT now()
);
CREATE INDEX user_sso_idx ON "user" (sso);

CREATE TABLE "user_role"
(
    sso               VARCHAR NOT NULL CONSTRAINT user_role_user_fk REFERENCES "user",
    role              VARCHAR NOT NULL CONSTRAINT user_role_role_fk REFERENCES role,
    attributes        JSONB   NOT NULL,
    created_by        VARCHAR,
    creation_date     TIMESTAMP DEFAULT now(),
    last_updated_by   VARCHAR,
    last_updated_date TIMESTAMP DEFAULT now(),
    CONSTRAINT user_role_pk PRIMARY KEY (sso, role)
);

CREATE TABLE "resource"
(
    name              VARCHAR NOT NULL CONSTRAINT resource_pk PRIMARY KEY,
    type              VARCHAR NOT NULL,
    created_by        VARCHAR,
    creation_date     TIMESTAMP DEFAULT now(),
    last_updated_by   VARCHAR,
    last_updated_date TIMESTAMP DEFAULT now()
);
CREATE INDEX resource_name_idx ON resource (type);

CREATE TABLE "action"
(
    name              VARCHAR NOT NULL CONSTRAINT action_pk PRIMARY KEY,
    created_by        VARCHAR,
    creation_date     TIMESTAMP DEFAULT now(),
    last_updated_by   VARCHAR,
    last_updated_date TIMESTAMP DEFAULT now()
);
CREATE INDEX action_name_idx ON action (name);

CREATE TABLE "permission"
(
    resource          VARCHAR NOT NULL CONSTRAINT permission_resource_fk REFERENCES resource,
    action            VARCHAR NOT NULL CONSTRAINT permission_action_fk REFERENCES action,
    created_by        VARCHAR,
    creation_date     TIMESTAMP DEFAULT now(),
    last_updated_by   VARCHAR,
    last_updated_date TIMESTAMP DEFAULT now(),
    CONSTRAINT permission_pk PRIMARY KEY (resource, action)
);

CREATE TABLE "role_permission"
(
    role              VARCHAR NOT NULL CONSTRAINT role_permission_role_fk REFERENCES role,
    resource          VARCHAR NOT NULL,
    action            VARCHAR NOT NULL,
    created_by        VARCHAR,
    creation_date     TIMESTAMP DEFAULT now(),
    last_updated_by   VARCHAR,
    last_updated_date TIMESTAMP DEFAULT now(),
    CONSTRAINT role_permission_pk PRIMARY KEY (role, resource, action),
    CONSTRAINT role_permission_resource_action_fk FOREIGN KEY (resource, action) REFERENCES permission
);

CREATE TABLE "bookcase"
(
    id           UUID DEFAULT public.gen_random_uuid() NOT NULL CONSTRAINT bookcase_pkey PRIMARY KEY,
    bookcase_key VARCHAR                               NOT NULL CONSTRAINT bookcase_bookcase_key_key UNIQUE,
    title        VARCHAR                               NOT NULL,
    foldername   VARCHAR                               NOT NULL,
    sb_model     VARCHAR,
    info         VARCHAR
);

CREATE TABLE "bookcase_version_status"
(
    code        VARCHAR NOT NULL CONSTRAINT bookcase_version_status_pkey PRIMARY KEY,
    description VARCHAR
);

CREATE TABLE "bookcase_version"
(
    id                           UUID         DEFAULT public.gen_random_uuid() NOT NULL CONSTRAINT bookcase_version_pkey PRIMARY KEY,
    bookcase_id                  UUID                                          NOT NULL CONSTRAINT bookcase_version_bookcase_id_fk REFERENCES bookcase,
    bookcase_version             VARCHAR                                       NOT NULL,
    offline_filename             VARCHAR,
    offline_filepath             VARCHAR,
    bookcase_version_status_code VARCHAR                                       NOT NULL CONSTRAINT bookcase_version_status_code_fk REFERENCES bookcase_version_status,
    version_timestamp            TIMESTAMP,
    created_by                   VARCHAR,
    creation_date                TIMESTAMP(0) DEFAULT now(),
    last_updated_by              VARCHAR,
    last_updated_date            TIMESTAMP(0) DEFAULT now(),
    CONSTRAINT bookcase_version_bookcase_id_bookcase_version_key UNIQUE (bookcase_id, bookcase_version)
);

CREATE TABLE "book"
(
    id          UUID DEFAULT public.gen_random_uuid() NOT NULL CONSTRAINT book_pkey PRIMARY KEY,
    book_key    VARCHAR                               NOT NULL,
    book_type   VARCHAR                               NOT NULL,
    bookcase_id UUID                                  NOT NULL CONSTRAINT book_bookcase_id_fk REFERENCES bookcase,
    CONSTRAINT book_bookcase_id_book_key_key UNIQUE (bookcase_id, book_key)
);
CREATE INDEX book_book_key_idx ON book (book_key);

CREATE TABLE "book_version"
(
    id                UUID         DEFAULT public.gen_random_uuid() NOT NULL CONSTRAINT book_version_pkey PRIMARY KEY,
    book_id           UUID                                          NOT NULL CONSTRAINT book_version_book_id_fk REFERENCES book,
    title             VARCHAR                                       NOT NULL,
    bookcase_version  VARCHAR                                       NOT NULL,
    book_order        SMALLINT,
    created_by        VARCHAR,
    creation_date     TIMESTAMP(0) DEFAULT now(),
    last_updated_by   VARCHAR,
    last_updated_date TIMESTAMP(0) DEFAULT now(),
    revision          VARCHAR,
    revision_date     VARCHAR,
    CONSTRAINT book_version_book_id_bookcase_version_key UNIQUE (book_id, bookcase_version)
);

CREATE TABLE "book_section"
(
    id                UUID DEFAULT public.gen_random_uuid() NOT NULL CONSTRAINT book_section_pkey PRIMARY KEY,
    tree_depth        SMALLINT                              NOT NULL,
    parent_section_id UUID CONSTRAINT section_parent_section_id_fk REFERENCES book_section,
    section_key       VARCHAR                               NOT NULL,
    book_id           UUID                                  NOT NULL CONSTRAINT section_book_id_fk REFERENCES book,
    title             VARCHAR,
    CONSTRAINT book_section_section_key_book_id_title_key UNIQUE (section_key, book_id, title)
);
COMMENT ON COLUMN book_section.tree_depth IS 'Start numbering sections from 1; reserve 0 for pageblk that fall under a book directly and do not belong to a section';

CREATE TABLE "book_section_version"
(
    id                 UUID         DEFAULT public.gen_random_uuid() NOT NULL CONSTRAINT book_section_version_pkey PRIMARY KEY,
    book_section_id    UUID                                          NOT NULL CONSTRAINT book_section_version_book_section_id_fk REFERENCES book_section,
    bookcase_version   VARCHAR                                       NOT NULL,
    book_section_order SMALLINT,
    created_by         VARCHAR,
    creation_date      TIMESTAMP(0) DEFAULT now(),
    last_updated_by    VARCHAR,
    last_updated_date  TIMESTAMP(0) DEFAULT now(),
    CONSTRAINT book_section_version_book_section_id_bookcase_version_key UNIQUE (book_section_id, bookcase_version)
);

CREATE TABLE "publication_type"
(
    code        VARCHAR NOT NULL CONSTRAINT publication_type_pkey PRIMARY KEY,
    description VARCHAR
);

CREATE TABLE "technology_level"
(
    id                UUID         DEFAULT public.gen_random_uuid() NOT NULL CONSTRAINT technology_level_pkey PRIMARY KEY,
    level             VARCHAR                                       NOT NULL CONSTRAINT technology_level_level_key UNIQUE,
    description       VARCHAR,
    created_by        VARCHAR,
    creation_date     TIMESTAMP(0) DEFAULT now(),
    last_updated_by   VARCHAR,
    last_updated_date TIMESTAMP(0) DEFAULT now()
);

CREATE TABLE "pageblk"
(
    id                    UUID DEFAULT public.gen_random_uuid() NOT NULL CONSTRAINT pageblk_pkey PRIMARY KEY,
    publication_type_code VARCHAR                               NOT NULL CONSTRAINT pageblk_publication_type_code_fk REFERENCES publication_type,
    pageblk_key           VARCHAR                               NOT NULL,
    title                 VARCHAR                               NOT NULL,
    toc_title             VARCHAR,
    book_section_id       UUID                                  NOT NULL CONSTRAINT pageblk_book_section_id_fk REFERENCES book_section,
    metadata              JSONB,
    CONSTRAINT pageblk_book_section_id_publication_type_code_title_pageblk_key UNIQUE (book_section_id, publication_type_code, title, pageblk_key)
);
CREATE INDEX pageblk_pageblk_key_idx ON pageblk (pageblk_key);

CREATE TABLE "pageblk_version"
(
    id                  UUID         DEFAULT public.gen_random_uuid() NOT NULL CONSTRAINT pageblk_version_pkey PRIMARY KEY,
    pageblk_id          UUID                                          NOT NULL CONSTRAINT pageblk_version_pageblk_id_fk REFERENCES pageblk,
    bookcase_version    VARCHAR                                       NOT NULL,
    online_filename     VARCHAR                                       NOT NULL,
    filepath            VARCHAR                                       NOT NULL,
    offline_filename    VARCHAR,
    ded_filename        VARCHAR,
    technology_level_id UUID CONSTRAINT pageblk_fk REFERENCES technology_level,
    pageblk_order       SMALLINT,
    revision            VARCHAR,
    revision_date       VARCHAR,
    release_date        VARCHAR,
    created_by          VARCHAR,
    creation_date       TIMESTAMP(0) DEFAULT now(),
    last_updated_by     VARCHAR,
    last_updated_date   TIMESTAMP(0) DEFAULT now(),
    CONSTRAINT pageblk_version_pageblk_id_bookcase_version_revision_date_key UNIQUE (pageblk_id, bookcase_version, revision_date)
);
CREATE INDEX pageblk_version_pk_idx ON pageblk_version (pageblk_id);
CREATE INDEX pageblk_version_online_filename_bcversion_idx ON pageblk_version (online_filename, bookcase_version);

CREATE TABLE "company"
(
    icao_code         VARCHAR NOT NULL CONSTRAINT company_pkey PRIMARY KEY,
    created_by        VARCHAR,
    creation_date     TIMESTAMP(0) DEFAULT now(),
    last_updated_by   VARCHAR,
    last_updated_date TIMESTAMP(0) DEFAULT now()
);
CREATE INDEX icao_code_idx ON company (icao_code);

CREATE TABLE "engine_model"
(
    model           VARCHAR NOT NULL CONSTRAINT engine_model_pkey PRIMARY KEY,
    family          VARCHAR NOT NULL,
    created_by      VARCHAR,
    created_at      TIMESTAMP(0) DEFAULT now(),
    last_updated_by VARCHAR,
    last_updated_at TIMESTAMP(0) DEFAULT now()
);
CREATE INDEX engine_model_model_idx ON engine_model (model);

CREATE TABLE "engine_program"
(
    bookcase_key    VARCHAR NOT NULL CONSTRAINT engine_program_pkey PRIMARY KEY,
    program         VARCHAR NOT NULL,
    created_by      VARCHAR,
    created_at      TIMESTAMP(0) DEFAULT now(),
    last_updated_by VARCHAR,
    last_updated_at TIMESTAMP(0) DEFAULT now()
);
CREATE INDEX engine_program_bookcase_key_idx ON engine_program (bookcase_key);

CREATE TABLE "company_engine_model"
(
    icao_code         VARCHAR NOT NULL CONSTRAINT company_engine_model_icao_code_fk REFERENCES company,
    engine_model      VARCHAR NOT NULL CONSTRAINT company_engine_model_engine_model_fk REFERENCES engine_model,
    created_by        VARCHAR,
    creation_date     TIMESTAMP(0) DEFAULT now(),
    last_updated_by   VARCHAR,
    last_updated_date TIMESTAMP(0) DEFAULT now(),
    CONSTRAINT company_engine_model_pk PRIMARY KEY (icao_code, engine_model)
);

CREATE TABLE "engine_model_program"
(
    engine_model    VARCHAR NOT NULL CONSTRAINT engine_model_program_model_fk REFERENCES engine_model,
    bookcase_key    VARCHAR NOT NULL CONSTRAINT engine_model_program_bookcase_key_fk REFERENCES engine_program,
    created_by      VARCHAR,
    created_at      TIMESTAMP(0) DEFAULT now(),
    last_updated_by VARCHAR,
    last_updated_at TIMESTAMP(0) DEFAULT now(),
    CONSTRAINT engine_model_program_pk PRIMARY KEY (engine_model, bookcase_key)
);

CREATE TABLE "company_engine_book_enablement"
(
    icao_code       VARCHAR NOT NULL,
    engine_model    VARCHAR NOT NULL,
    book_id         UUID    NOT NULL CONSTRAINT company_engine_book_enablement_book_id_fk REFERENCES book,
    created_by      VARCHAR,
    created_at      TIMESTAMP(0) DEFAULT now(),
    last_updated_by VARCHAR,
    last_updated_at TIMESTAMP(0) DEFAULT now(),
    CONSTRAINT company_engine_book_enablement_pk PRIMARY KEY (icao_code, engine_model, book_id),
    CONSTRAINT company_engine_book_enablement_company_model_fk FOREIGN KEY (icao_code, engine_model) REFERENCES company_engine_model
);

CREATE TABLE "company_engine_pageblk_enablement"
(
    icao_code       VARCHAR NOT NULL,
    engine_model    VARCHAR NOT NULL,
    section_id      UUID    NOT NULL CONSTRAINT company_engine_pageblk_enablement_section_fk REFERENCES book_section,
    pageblk_key     VARCHAR NOT NULL,
    created_by      VARCHAR,
    created_at      TIMESTAMP(0) DEFAULT now(),
    last_updated_by VARCHAR,
    last_updated_at TIMESTAMP(0) DEFAULT now(),
    CONSTRAINT company_engine_pageblk_enablement_pk PRIMARY KEY (icao_code, engine_model, section_id, pageblk_key),
    CONSTRAINT company_engine_pageblk_enablement_company_model_fk FOREIGN KEY (icao_code, engine_model) REFERENCES company_engine_model
);

CREATE TABLE "company_engine_techlv_enablement"
(
    icao_code       VARCHAR NOT NULL,
    engine_model    VARCHAR NOT NULL,
    techlv_id       UUID    NOT NULL CONSTRAINT company_engine_techlv_enablement_techlv__id_fk REFERENCES technology_level,
    bookcase_key    VARCHAR NOT NULL,
    created_by      VARCHAR,
    created_at      TIMESTAMP DEFAULT now(),
    last_updated_by VARCHAR,
    last_updated_at TIMESTAMP DEFAULT now(),
    CONSTRAINT company_engine_techlv_enablement_pk PRIMARY KEY (icao_code, engine_model, techlv_id, bookcase_key),
    CONSTRAINT company_engine_techlv_enablement_company_model_fk FOREIGN KEY (icao_code, engine_model) REFERENCES company_engine_model
);

-- Lookup Tables --
INSERT INTO technology_level (level, description, created_by, creation_date, last_updated_by, last_updated_date)
    VALUES ('M', null, '212462850', now(), '212462850', now());
INSERT INTO technology_level (level, description, created_by, creation_date, last_updated_by, last_updated_date)
    VALUES ('0', null, '212462850', now(), '212462850', now());
INSERT INTO technology_level (level, description, created_by, creation_date, last_updated_by, last_updated_date)
    VALUES ('1', null, '212462850', now(), '212462850', now());
INSERT INTO technology_level (level, description, created_by, creation_date, last_updated_by, last_updated_date)
    VALUES ('2', null, '212462850', now(), '212462850', now());
INSERT INTO technology_level (level, description, created_by, creation_date, last_updated_by, last_updated_date)
    VALUES ('3', null, '212462850', now(), '212462850', now());
INSERT INTO technology_level (level, description, created_by, creation_date, last_updated_by, last_updated_date)
    VALUES ('4', null, '212462850', now(), '212462850', now());
INSERT INTO technology_level (level, description, created_by, creation_date, last_updated_by, last_updated_date)
    VALUES ('5', null, '212462850', now(), '212462850', now());

-- Populate bookcase_version_status table
INSERT INTO bookcase_version_status (code, description)
VALUES ('online', 'Viewable by Customers'),
       ('offline', 'Not viewable by customers, updates applied'),
       ('suspended', 'Not viewable by customers, updates not applied'),
       ('archived', 'Not viewable by Admin, update not applied, flag to be removed');

-- Populate the publication_type table
INSERT INTO publication_type (code, description)
VALUES ('ic', 'Incremental Change'),
       ('tr', 'Temporary Revision'),
       ('sb', 'Service Bulletin'),
       ('manual', 'Manual Data'),
       ('doc', 'Other Documents'),
       ('lr', 'Licensed Repair');

-- Populate engine_model table
INSERT INTO engine_model (model, "family", created_by, created_at, last_updated_by, last_updated_at)
	VALUES('CF34-1', 'CF34', '212462850', now(), '212462850', now());
INSERT INTO engine_model (model, "family", created_by, created_at, last_updated_by, last_updated_at)
	VALUES('CF34-10', 'CF34', '212462850', now(), '212462850', now());
INSERT INTO engine_model (model, "family", created_by, created_at, last_updated_by, last_updated_at)
	VALUES('CF34-10A', 'CF34', '212462850', now(), '212462850', now());
INSERT INTO engine_model (model, "family", created_by, created_at, last_updated_by, last_updated_at)
	VALUES('CF34-10E', 'CF34', '212462850', now(), '212462850', now());
INSERT INTO engine_model (model, "family", created_by, created_at, last_updated_by, last_updated_at)
	VALUES('CF34-3A', 'CF34', '212462850', now(), '212462850', now());
INSERT INTO engine_model (model, "family", created_by, created_at, last_updated_by, last_updated_at)
	VALUES('CF34-3A1', 'CF34', '212462850', now(), '212462850', now());
INSERT INTO engine_model (model, "family", created_by, created_at, last_updated_by, last_updated_at)
	VALUES('CF34-3', 'CF34', '212462850', now(), '212462850', now());
INSERT INTO engine_model (model, "family", created_by, created_at, last_updated_by, last_updated_at)
	VALUES('CF34-3B', 'CF34', '212462850', now(), '212462850', now());
INSERT INTO engine_model (model, "family", created_by, created_at, last_updated_by, last_updated_at)
	VALUES('CF34-8C', 'CF34', '212462850', now(), '212462850', now());
INSERT INTO engine_model (model, "family", created_by, created_at, last_updated_by, last_updated_at)
	VALUES('CF34-8E', 'CF34', '212462850', now(), '212462850', now());
INSERT INTO engine_model (model, "family", created_by, created_at, last_updated_by, last_updated_at)
	VALUES('CF6-45', 'CF6', '212462850', now(), '212462850', now());
INSERT INTO engine_model (model, "family", created_by, created_at, last_updated_by, last_updated_at)
	VALUES('CF6-50', 'CF6', '212462850', now(), '212462850', now());
INSERT INTO engine_model (model, "family", created_by, created_at, last_updated_by, last_updated_at)
	VALUES('CF6-6', 'CF6', '212462850', now(), '212462850', now());
INSERT INTO engine_model (model, "family", created_by, created_at, last_updated_by, last_updated_at)
	VALUES('CF6-80A', 'CF6', '212462850', now(), '212462850', now());
INSERT INTO engine_model (model, "family", created_by, created_at, last_updated_by, last_updated_at)
	VALUES('CF6-80C2', 'CF6', '212462850', now(), '212462850', now());
INSERT INTO engine_model (model, "family", created_by, created_at, last_updated_by, last_updated_at)
	VALUES('CF6-80E', 'CF6', '212462850', now(), '212462850', now());
INSERT INTO engine_model (model, "family", created_by, created_at, last_updated_by, last_updated_at)
	VALUES('CFE738', 'CFE', '212462850', now(), '212462850', now());
INSERT INTO engine_model (model, "family", created_by, created_at, last_updated_by, last_updated_at)
	VALUES('CFM56-2A', 'CFM56', '212462850', now(), '212462850', now());
INSERT INTO engine_model (model, "family", created_by, created_at, last_updated_by, last_updated_at)
	VALUES('CFM56-2B', 'CFM56', '212462850', now(), '212462850', now());
INSERT INTO engine_model (model, "family", created_by, created_at, last_updated_by, last_updated_at)
	VALUES('CFM56-2C', 'CFM56', '212462850', now(), '212462850', now());
INSERT INTO engine_model (model, "family", created_by, created_at, last_updated_by, last_updated_at)
	VALUES('CFM56-3', 'CFM56', '212462850', now(), '212462850', now());
INSERT INTO engine_model (model, "family", created_by, created_at, last_updated_by, last_updated_at)
	VALUES('CFM56-5A', 'CFM56', '212462850', now(), '212462850', now());
INSERT INTO engine_model (model, "family", created_by, created_at, last_updated_by, last_updated_at)
	VALUES('CFM56-5B', 'CFM56', '212462850', now(), '212462850', now());
INSERT INTO engine_model (model, "family", created_by, created_at, last_updated_by, last_updated_at)
	VALUES('CFM56-5C', 'CFM56', '212462850', now(), '212462850', now());
INSERT INTO engine_model (model, "family", created_by, created_at, last_updated_by, last_updated_at)
	VALUES('CFM56-7B', 'CFM56', '212462850', now(), '212462850', now());
INSERT INTO engine_model (model, "family", created_by, created_at, last_updated_by, last_updated_at)
	VALUES('CJ610', 'CJ610', '212462850', now(), '212462850', now());
INSERT INTO engine_model (model, "family", created_by, created_at, last_updated_by, last_updated_at)
	VALUES('CT58-100', 'CT58', '212462850', now(), '212462850', now());
INSERT INTO engine_model (model, "family", created_by, created_at, last_updated_by, last_updated_at)
	VALUES('CT58-110', 'CT58', '212462850', now(), '212462850', now());
INSERT INTO engine_model (model, "family", created_by, created_at, last_updated_by, last_updated_at)
	VALUES('CT58-140', 'CT58', '212462850', now(), '212462850', now());
INSERT INTO engine_model (model, "family", created_by, created_at, last_updated_by, last_updated_at)
	VALUES('CT7-2', 'CT7', '212462850', now(), '212462850', now());
INSERT INTO engine_model (model, "family", created_by, created_at, last_updated_by, last_updated_at)
	VALUES('CT7-5', 'CT7', '212462850', now(), '212462850', now());
INSERT INTO engine_model (model, "family", created_by, created_at, last_updated_by, last_updated_at)
	VALUES('CT7-5A', 'CT7', '212462850', now(), '212462850', now());
INSERT INTO engine_model (model, "family", created_by, created_at, last_updated_by, last_updated_at)
	VALUES('CT7-6', 'CT7', '212462850', now(), '212462850', now());
INSERT INTO engine_model (model, "family", created_by, created_at, last_updated_by, last_updated_at)
	VALUES('CT7-7', 'CT7', '212462850', now(), '212462850', now());
INSERT INTO engine_model (model, "family", created_by, created_at, last_updated_by, last_updated_at)
	VALUES('CT7-8', 'CT7', '212462850', now(), '212462850', now());
INSERT INTO engine_model (model, "family", created_by, created_at, last_updated_by, last_updated_at)
	VALUES('CT7-9', 'CT7', '212462850', now(), '212462850', now());
INSERT INTO engine_model (model, "family", created_by, created_at, last_updated_by, last_updated_at)
	VALUES('CT7-9B', 'CT7', '212462850', now(), '212462850', now());
INSERT INTO engine_model (model, "family", created_by, created_at, last_updated_by, last_updated_at)
	VALUES('CT7-9C', 'CT7', '212462850', now(), '212462850', now());
INSERT INTO engine_model (model, "family", created_by, created_at, last_updated_by, last_updated_at)
	VALUES('CT7-TS', 'CT7', '212462850', now(), '212462850', now());
INSERT INTO engine_model (model, "family", created_by, created_at, last_updated_by, last_updated_at)
	VALUES('GE90', 'GE90', '212462850', now(), '212462850', now());
INSERT INTO engine_model (model, "family", created_by, created_at, last_updated_by, last_updated_at)
	VALUES('GE90-90', 'GE90', '212462850', now(), '212462850', now());
INSERT INTO engine_model (model, "family", created_by, created_at, last_updated_by, last_updated_at)
	VALUES('GE90-100', 'GE90', '212462850', now(), '212462850', now());
INSERT INTO engine_model (model, "family", created_by, created_at, last_updated_by, last_updated_at)
	VALUES('GENX-1B', 'GENX', '212462850', now(), '212462850', now());
INSERT INTO engine_model (model, "family", created_by, created_at, last_updated_by, last_updated_at)
	VALUES('GENX-2B', 'GENX', '212462850', now(), '212462850', now());
INSERT INTO engine_model (model, "family", created_by, created_at, last_updated_by, last_updated_at)
	VALUES('GP7200', 'GP7000', '212462850', now(), '212462850', now());
INSERT INTO engine_model (model, "family", created_by, created_at, last_updated_by, last_updated_at)
	VALUES('H80', 'H80', '212462850', now(), '212462850', now());
INSERT INTO engine_model (model, "family", created_by, created_at, last_updated_by, last_updated_at)
	VALUES('CT64-1', 'CT64', '212462850', now(), '212462850', now());
INSERT INTO engine_model (model, "family", created_by, created_at, last_updated_by, last_updated_at)
	VALUES('CT64-820-3', 'CT64', '212462850', now(), '212462850', now());
INSERT INTO engine_model (model, "family", created_by, created_at, last_updated_by, last_updated_at)
	VALUES('CT64-820-4', 'CT64', '212462850', now(), '212462850', now());
INSERT INTO engine_model (model, "family", created_by, created_at, last_updated_by, last_updated_at)
	VALUES('CT64-820', 'CT64', '212462850', now(), '212462850', now());
INSERT INTO engine_model (model, "family", created_by, created_at, last_updated_by, last_updated_at)
	VALUES('HF120', 'HF', '212462850', now(), '212462850', now());
INSERT INTO engine_model (model, "family", created_by, created_at, last_updated_by, last_updated_at)
	VALUES('PASSPORT20', 'PASSPORT', '212462850', now(), '212462850', now());

-- Populate engine_program table
INSERT INTO engine_program (bookcase_key, "program", created_by, created_at, last_updated_by, last_updated_at)
	VALUES('gek108750', 'CF34 BJ', '212462850', now(), '212462850', now());
INSERT INTO engine_program (bookcase_key, "program", created_by, created_at, last_updated_by, last_updated_at)
	VALUES('gek108751', 'CF34 RJ', '212462850', now(), '212462850', now());
INSERT INTO engine_program (bookcase_key, "program", created_by, created_at, last_updated_by, last_updated_at)
	VALUES('gek112090', 'CF34-10A', '212462850', now(), '212462850', now());
INSERT INTO engine_program (bookcase_key, "program", created_by, created_at, last_updated_by, last_updated_at)
	VALUES('gek112080', 'CF34-10E', '212462850', now(), '212462850', now());
INSERT INTO engine_program (bookcase_key, "program", created_by, created_at, last_updated_by, last_updated_at)
	VALUES('gek108752', 'CF34-8C', '212462850', now(), '212462850', now());
INSERT INTO engine_program (bookcase_key, "program", created_by, created_at, last_updated_by, last_updated_at)
	VALUES('gek112030', 'CF34-8E', '212462850', now(), '212462850', now());
INSERT INTO engine_program (bookcase_key, "program", created_by, created_at, last_updated_by, last_updated_at)
	VALUES('gek112149', 'CF34BJ-1A_3A_3A2', '212462850', now(), '212462850', now());
INSERT INTO engine_program (bookcase_key, "program", created_by, created_at, last_updated_by, last_updated_at)
	VALUES('gek108745', 'CF6-50', '212462850', now(), '212462850', now());
INSERT INTO engine_program (bookcase_key, "program", created_by, created_at, last_updated_by, last_updated_at)
	VALUES('gek108744', 'CF6-6', '212462850', now(), '212462850', now());
INSERT INTO engine_program (bookcase_key, "program", created_by, created_at, last_updated_by, last_updated_at)
	VALUES('gek108747-01', 'CF6-80A Boeing', '212462850', now(), '212462850', now());
INSERT INTO engine_program (bookcase_key, "program", created_by, created_at, last_updated_by, last_updated_at)
	VALUES('gek108747-10', 'CF6-80A Airbus', '212462850', now(), '212462850', now());
INSERT INTO engine_program (bookcase_key, "program", created_by, created_at, last_updated_by, last_updated_at)
	VALUES('gek108746', 'CF6-80C2 All', '212462850', now(), '212462850', now());
INSERT INTO engine_program (bookcase_key, "program", created_by, created_at, last_updated_by, last_updated_at)
	VALUES('gek108746-02', 'CF6-80C2 Boeing', '212462850', now(), '212462850', now());
INSERT INTO engine_program (bookcase_key, "program", created_by, created_at, last_updated_by, last_updated_at)
	VALUES('gek108746-20', 'CF6-80C2 Airbus', '212462850', now(), '212462850', now());
INSERT INTO engine_program (bookcase_key, "program", created_by, created_at, last_updated_by, last_updated_at)
	VALUES('gek108746-30', 'CF6-80C2 McDonnell', '212462850', now(), '212462850', now());
INSERT INTO engine_program (bookcase_key, "program", created_by, created_at, last_updated_by, last_updated_at)
	VALUES('gek113959', 'CF6-80C2K1F', '212462850', now(), '212462850', now());
INSERT INTO engine_program (bookcase_key, "program", created_by, created_at, last_updated_by, last_updated_at)
	VALUES('gek113960', 'CF6-80C2L1F', '212462850', now(), '212462850', now());
INSERT INTO engine_program (bookcase_key, "program", created_by, created_at, last_updated_by, last_updated_at)
	VALUES('gek108748', 'CF6-80E1', '212462850', now(), '212462850', now());
INSERT INTO engine_program (bookcase_key, "program", created_by, created_at, last_updated_by, last_updated_at)
	VALUES('gek109944', 'CT58', '212462850', now(), '212462850', now());
INSERT INTO engine_program (bookcase_key, "program", created_by, created_at, last_updated_by, last_updated_at)
	VALUES('gek112212', 'CT7-8', '212462850', now(), '212462850', now());
INSERT INTO engine_program (bookcase_key, "program", created_by, created_at, last_updated_by, last_updated_at)
	VALUES('gek112212-01', 'CT7-8', '212462850', now(), '212462850', now());
INSERT INTO engine_program (bookcase_key, "program", created_by, created_at, last_updated_by, last_updated_at)
	VALUES('gek112212-10', 'CT7-8', '212462850', now(), '212462850', now());
INSERT INTO engine_program (bookcase_key, "program", created_by, created_at, last_updated_by, last_updated_at)
	VALUES('gek108753', 'CT7-TP 5-7', '212462850', now(), '212462850', now());
INSERT INTO engine_program (bookcase_key, "program", created_by, created_at, last_updated_by, last_updated_at)
	VALUES('gek112181', 'CT7-TP 9', '212462850', now(), '212462850', now());
INSERT INTO engine_program (bookcase_key, "program", created_by, created_at, last_updated_by, last_updated_at)
	VALUES('gek108749', 'GE90', '212462850', now(), '212462850', now());
INSERT INTO engine_program (bookcase_key, "program", created_by, created_at, last_updated_by, last_updated_at)
	VALUES('gek108786', 'GE90-100', '212462850', now(), '212462850', now());
INSERT INTO engine_program (bookcase_key, "program", created_by, created_at, last_updated_by, last_updated_at)
	VALUES('gek112865', 'GEnx-1B', '212462850', now(), '212462850', now());
INSERT INTO engine_program (bookcase_key, "program", created_by, created_at, last_updated_by, last_updated_at)
	VALUES('gek112865_lr', 'GEnx-1B (LR)', '212462850', now(), '212462850', now());
INSERT INTO engine_program (bookcase_key, "program", created_by, created_at, last_updated_by, last_updated_at)
	VALUES('gek114118', 'GEnx-2B', '212462850', now(), '212462850', now());
INSERT INTO engine_program (bookcase_key, "program", created_by, created_at, last_updated_by, last_updated_at)
	VALUES('gek114118_lr', 'GEnx-2B (LR)', '212462850', now(), '212462850', now());
INSERT INTO engine_program (bookcase_key, "program", created_by, created_at, last_updated_by, last_updated_at)
	VALUES('gek108792', 'Standard Practice Manual (SPM)', '212462850', now(), '212462850', now());
INSERT INTO engine_program (bookcase_key, "program", created_by, created_at, last_updated_by, last_updated_at)
	VALUES('gek114154', 'CT7-2', '212462850', now(), '212462850', now());
INSERT INTO engine_program (bookcase_key, "program", created_by, created_at, last_updated_by, last_updated_at)
	VALUES('gek114155', 'CT7-6', '212462850', now(), '212462850', now());
INSERT INTO engine_program (bookcase_key, "program", created_by, created_at, last_updated_by, last_updated_at)
	VALUES('gek114152', 'CT64-820-3', '212462850', now(), '212462850', now());
INSERT INTO engine_program (bookcase_key, "program", created_by, created_at, last_updated_by, last_updated_at)
	VALUES('gek114153', 'CT64-820-4', '212462850', now(), '212462850', now());
INSERT INTO engine_program (bookcase_key, "program", created_by, created_at, last_updated_by, last_updated_at)
	VALUES('gek117477', 'CT64', '212462850', now(), '212462850', now());
INSERT INTO engine_program (bookcase_key, "program", created_by, created_at, last_updated_by, last_updated_at)
	VALUES('gek117478', 'CT7-TS', '212462850', now(), '212462850', now());
INSERT INTO engine_program (bookcase_key, "program", created_by, created_at, last_updated_by, last_updated_at)
	VALUES('gek114154-10', 'CT7-2AD Shop', '212462850', now(), '212462850', now());
INSERT INTO engine_program (bookcase_key, "program", created_by, created_at, last_updated_by, last_updated_at)
	VALUES('gek114154-01', 'CT7-2AD Maintenance', '212462850', now(), '212462850', now());
INSERT INTO engine_program (bookcase_key, "program", created_by, created_at, last_updated_by, last_updated_at)
	VALUES('gek114154-20', 'CT7-2E Shop', '212462850', now(), '212462850', now());
INSERT INTO engine_program (bookcase_key, "program", created_by, created_at, last_updated_by, last_updated_at)
	VALUES('gek114154-02', 'CT7-2E Maintenance', '212462850', now(), '212462850', now());
INSERT INTO engine_program (bookcase_key, "program", created_by, created_at, last_updated_by, last_updated_at)
	VALUES('gek114154-30', 'CT7-2F Shop', '212462850', now(), '212462850', now());
INSERT INTO engine_program (bookcase_key, "program", created_by, created_at, last_updated_by, last_updated_at)
	VALUES('gek114154-03', 'CT7-2F Maintenance', '212462850', now(), '212462850', now());
INSERT INTO engine_program (bookcase_key, "program", created_by, created_at, last_updated_by, last_updated_at)
	VALUES('gek117479', 'CF6-80C2K1F Middle River', '212462850', now(), '212462850', now());
INSERT INTO engine_program (bookcase_key, "program", created_by, created_at, last_updated_by, last_updated_at)
	VALUES('gek117480', 'CF6-80C2L1F Middle River ', '212462850', now(), '212462850', now());
INSERT INTO engine_program (bookcase_key, "program", created_by, created_at, last_updated_by, last_updated_at)
	VALUES('gek112121', 'HF120-H1A Dealer', '212462850', now(), '212462850', now());
INSERT INTO engine_program (bookcase_key, "program", created_by, created_at, last_updated_by, last_updated_at)
	VALUES('gek112110', 'HF120-H1A Overhaul', '212462850', now(), '212462850', now());
INSERT INTO engine_program (bookcase_key, "program", created_by, created_at, last_updated_by, last_updated_at)
	VALUES('gek119360', 'Honda-SPM', '212462850', now(), '212462850', now());
INSERT INTO engine_program (bookcase_key, "program", created_by, created_at, last_updated_by, last_updated_at)
	VALUES('gek112059', 'PASSPORT20', '212462850', now(), '212462850', now());
INSERT INTO engine_program (bookcase_key, "program", created_by, created_at, last_updated_by, last_updated_at)
	VALUES('gek112060', 'PASSPORT20', '212462850', now(), '212462850', now());

-- Populate engine_model_program table
INSERT INTO engine_model_program (engine_model, bookcase_key, created_by, created_at, last_updated_by, last_updated_at)
	VALUES('CF34-1', 'gek112149', '212462850', now(), '212462850', now());
INSERT INTO engine_model_program (engine_model, bookcase_key, created_by, created_at, last_updated_by, last_updated_at)
	VALUES('CF34-10', 'gek112080', '212462850', now(), '212462850', now());
INSERT INTO engine_model_program (engine_model, bookcase_key, created_by, created_at, last_updated_by, last_updated_at)
	VALUES('CF34-10', 'gek112090', '212462850', now(), '212462850', now());
INSERT INTO engine_model_program (engine_model, bookcase_key, created_by, created_at, last_updated_by, last_updated_at)
	VALUES('CF34-10A', 'gek112090', '212462850', now(), '212462850', now());
INSERT INTO engine_model_program (engine_model, bookcase_key, created_by, created_at, last_updated_by, last_updated_at)
	VALUES('CF34-10E', 'gek112080', '212462850', now(), '212462850', now());
INSERT INTO engine_model_program (engine_model, bookcase_key, created_by, created_at, last_updated_by, last_updated_at)
	VALUES('CF34-3A', 'gek112149', '212462850', now(), '212462850', now());
INSERT INTO engine_model_program (engine_model, bookcase_key, created_by, created_at, last_updated_by, last_updated_at)
	VALUES('CF34-3A1', 'gek108750', '212462850', now(), '212462850', now());
INSERT INTO engine_model_program (engine_model, bookcase_key, created_by, created_at, last_updated_by, last_updated_at)
	VALUES('CF34-3A1', 'gek108751', '212462850', now(), '212462850', now());
INSERT INTO engine_model_program (engine_model, bookcase_key, created_by, created_at, last_updated_by, last_updated_at)
	VALUES('CF34-3', 'gek108750', '212462850', now(), '212462850', now());
INSERT INTO engine_model_program (engine_model, bookcase_key, created_by, created_at, last_updated_by, last_updated_at)
	VALUES('CF34-3', 'gek108751', '212462850', now(), '212462850', now());
INSERT INTO engine_model_program (engine_model, bookcase_key, created_by, created_at, last_updated_by, last_updated_at)
	VALUES('CF34-3B', 'gek108750', '212462850', now(), '212462850', now());
INSERT INTO engine_model_program (engine_model, bookcase_key, created_by, created_at, last_updated_by, last_updated_at)
	VALUES('CF34-8C', 'gek108752', '212462850', now(), '212462850', now());
INSERT INTO engine_model_program (engine_model, bookcase_key, created_by, created_at, last_updated_by, last_updated_at)
	VALUES('CF34-8E', 'gek112030', '212462850', now(), '212462850', now());
INSERT INTO engine_model_program (engine_model, bookcase_key, created_by, created_at, last_updated_by, last_updated_at)
	VALUES('CF6-50', 'gek108745', '212462850', now(), '212462850', now());
INSERT INTO engine_model_program (engine_model, bookcase_key, created_by, created_at, last_updated_by, last_updated_at)
	VALUES('CF6-6', 'gek108744', '212462850', now(), '212462850', now());
INSERT INTO engine_model_program (engine_model, bookcase_key, created_by, created_at, last_updated_by, last_updated_at)
	VALUES('CF6-80A', 'gek108747-01', '212462850', now(), '212462850', now());
INSERT INTO engine_model_program (engine_model, bookcase_key, created_by, created_at, last_updated_by, last_updated_at)
	VALUES('CF6-80A', 'gek108747-10', '212462850', now(), '212462850', now());
INSERT INTO engine_model_program (engine_model, bookcase_key, created_by, created_at, last_updated_by, last_updated_at)
	VALUES('CF6-80C2', 'gek108746', '212462850', now(), '212462850', now());
INSERT INTO engine_model_program (engine_model, bookcase_key, created_by, created_at, last_updated_by, last_updated_at)
	VALUES('CF6-80C2', 'gek108746-02', '212462850', now(), '212462850', now());
INSERT INTO engine_model_program (engine_model, bookcase_key, created_by, created_at, last_updated_by, last_updated_at)
	VALUES('CF6-80C2', 'gek108746-20', '212462850', now(), '212462850', now());
INSERT INTO engine_model_program (engine_model, bookcase_key, created_by, created_at, last_updated_by, last_updated_at)
	VALUES('CF6-80C2', 'gek108746-30', '212462850', now(), '212462850', now());
INSERT INTO engine_model_program (engine_model, bookcase_key, created_by, created_at, last_updated_by, last_updated_at)
	VALUES('CF6-80C2', 'gek113959', '212462850', now(), '212462850', now());
INSERT INTO engine_model_program (engine_model, bookcase_key, created_by, created_at, last_updated_by, last_updated_at)
	VALUES('CF6-80C2', 'gek117479', '212462850', now(), '212462850', now());
INSERT INTO engine_model_program (engine_model, bookcase_key, created_by, created_at, last_updated_by, last_updated_at)
	VALUES('CF6-80C2', 'gek113960', '212462850', now(), '212462850', now());
INSERT INTO engine_model_program (engine_model, bookcase_key, created_by, created_at, last_updated_by, last_updated_at)
	VALUES('CF6-80C2', 'gek117480', '212462850', now(), '212462850', now());
INSERT INTO engine_model_program (engine_model, bookcase_key, created_by, created_at, last_updated_by, last_updated_at)
	VALUES('CF6-80E', 'gek108748', '212462850', now(), '212462850', now());
INSERT INTO engine_model_program (engine_model, bookcase_key, created_by, created_at, last_updated_by, last_updated_at)
	VALUES('CT58-100', 'gek109944', '212462850', now(), '212462850', now());
INSERT INTO engine_model_program (engine_model, bookcase_key, created_by, created_at, last_updated_by, last_updated_at)
	VALUES('CT58-110', 'gek109944', '212462850', now(), '212462850', now());
INSERT INTO engine_model_program (engine_model, bookcase_key, created_by, created_at, last_updated_by, last_updated_at)
	VALUES('CT58-140', 'gek109944', '212462850', now(), '212462850', now());
INSERT INTO engine_model_program (engine_model, bookcase_key, created_by, created_at, last_updated_by, last_updated_at)
	VALUES('CT7-2', 'gek114154', '212462850', now(), '212462850', now());
INSERT INTO engine_model_program (engine_model, bookcase_key, created_by, created_at, last_updated_by, last_updated_at)
	VALUES('CT7-2', 'gek114154-01', '212462850', now(), '212462850', now());
INSERT INTO engine_model_program (engine_model, bookcase_key, created_by, created_at, last_updated_by, last_updated_at)
	VALUES('CT7-2', 'gek114154-02', '212462850', now(), '212462850', now());
INSERT INTO engine_model_program (engine_model, bookcase_key, created_by, created_at, last_updated_by, last_updated_at)
	VALUES('CT7-2', 'gek114154-03', '212462850', now(), '212462850', now());
INSERT INTO engine_model_program (engine_model, bookcase_key, created_by, created_at, last_updated_by, last_updated_at)
	VALUES('CT7-2', 'gek114154-10', '212462850', now(), '212462850', now());
INSERT INTO engine_model_program (engine_model, bookcase_key, created_by, created_at, last_updated_by, last_updated_at)
	VALUES('CT7-2', 'gek114154-20', '212462850', now(), '212462850', now());
INSERT INTO engine_model_program (engine_model, bookcase_key, created_by, created_at, last_updated_by, last_updated_at)
	VALUES('CT7-2', 'gek114154-30', '212462850', now(), '212462850', now());
INSERT INTO engine_model_program (engine_model, bookcase_key, created_by, created_at, last_updated_by, last_updated_at)
	VALUES('CT7-5', 'gek108753', '212462850', now(), '212462850', now());
INSERT INTO engine_model_program (engine_model, bookcase_key, created_by, created_at, last_updated_by, last_updated_at)
	VALUES('CT7-5A', 'gek108753', '212462850', now(), '212462850', now());
INSERT INTO engine_model_program (engine_model, bookcase_key, created_by, created_at, last_updated_by, last_updated_at)
	VALUES('CT7-6', 'gek114155', '212462850', now(), '212462850', now());
INSERT INTO engine_model_program (engine_model, bookcase_key, created_by, created_at, last_updated_by, last_updated_at)
	VALUES('CT7-7', 'gek108753', '212462850', now(), '212462850', now());
INSERT INTO engine_model_program (engine_model, bookcase_key, created_by, created_at, last_updated_by, last_updated_at)
	VALUES('CT7-8', 'gek112212', '212462850', now(), '212462850', now());
INSERT INTO engine_model_program (engine_model, bookcase_key, created_by, created_at, last_updated_by, last_updated_at)
	VALUES('CT7-8', 'gek112212-01', '212462850', now(), '212462850', now());
INSERT INTO engine_model_program (engine_model, bookcase_key, created_by, created_at, last_updated_by, last_updated_at)
	VALUES('CT7-8', 'gek112212-10', '212462850', now(), '212462850', now());
INSERT INTO engine_model_program (engine_model, bookcase_key, created_by, created_at, last_updated_by, last_updated_at)
	VALUES('CT7-9', 'gek112181', '212462850', now(), '212462850', now());
INSERT INTO engine_model_program (engine_model, bookcase_key, created_by, created_at, last_updated_by, last_updated_at)
	VALUES('CT7-9B', 'gek112181', '212462850', now(), '212462850', now());
INSERT INTO engine_model_program (engine_model, bookcase_key, created_by, created_at, last_updated_by, last_updated_at)
	VALUES('CT7-9C', 'gek112181', '212462850', now(), '212462850', now());
INSERT INTO engine_model_program (engine_model, bookcase_key, created_by, created_at, last_updated_by, last_updated_at)
	VALUES('CT7-TS', 'gek117478', '212462850', now(), '212462850', now());
INSERT INTO engine_model_program (engine_model, bookcase_key, created_by, created_at, last_updated_by, last_updated_at)
	VALUES('GE90', 'gek108749', '212462850', now(), '212462850', now());
INSERT INTO engine_model_program (engine_model, bookcase_key, created_by, created_at, last_updated_by, last_updated_at)
	VALUES('GE90-90', 'gek108749', '212462850', now(), '212462850', now());
INSERT INTO engine_model_program (engine_model, bookcase_key, created_by, created_at, last_updated_by, last_updated_at)
	VALUES('GE90-100', 'gek108786', '212462850', now(), '212462850', now());
INSERT INTO engine_model_program (engine_model, bookcase_key, created_by, created_at, last_updated_by, last_updated_at)
	VALUES('GENX-1B', 'gek112865', '212462850', now(), '212462850', now());
INSERT INTO engine_model_program (engine_model, bookcase_key, created_by, created_at, last_updated_by, last_updated_at)
	VALUES('GENX-1B', 'gek112865_lr', '212462850', now(), '212462850', now());
INSERT INTO engine_model_program (engine_model, bookcase_key, created_by, created_at, last_updated_by, last_updated_at)
	VALUES('GENX-2B', 'gek114118', '212462850', now(), '212462850', now());
INSERT INTO engine_model_program (engine_model, bookcase_key, created_by, created_at, last_updated_by, last_updated_at)
	VALUES('GENX-2B', 'gek114118_lr', '212462850', now(), '212462850', now());
INSERT INTO engine_model_program (engine_model, bookcase_key, created_by, created_at, last_updated_by, last_updated_at)
	VALUES('CT64-1', 'gek117477', '212462850', now(), '212462850', now());
INSERT INTO engine_model_program (engine_model, bookcase_key, created_by, created_at, last_updated_by, last_updated_at)
	VALUES('CT64-820-3', 'gek114152', '212462850', now(), '212462850', now());
INSERT INTO engine_model_program (engine_model, bookcase_key, created_by, created_at, last_updated_by, last_updated_at)
	VALUES('CT64-820-4', 'gek114153', '212462850', now(), '212462850', now());
INSERT INTO engine_model_program (engine_model, bookcase_key, created_by, created_at, last_updated_by, last_updated_at)
	VALUES('CT64-820', 'gek114152', '212462850', now(), '212462850', now());
INSERT INTO engine_model_program (engine_model, bookcase_key, created_by, created_at, last_updated_by, last_updated_at)
	VALUES('CT64-820', 'gek114153', '212462850', now(), '212462850', now());
INSERT INTO engine_model_program (engine_model, bookcase_key, created_by, created_at, last_updated_by, last_updated_at)
	VALUES('HF120', 'gek112110', '212462850', now(), '212462850', now());
INSERT INTO engine_model_program (engine_model, bookcase_key, created_by, created_at, last_updated_by, last_updated_at)
	VALUES('HF120', 'gek112121', '212462850', now(), '212462850', now());
INSERT INTO engine_model_program (engine_model, bookcase_key, created_by, created_at, last_updated_by, last_updated_at)
	VALUES('PASSPORT20', 'gek112059', '212462850', now(), '212462850', now());
INSERT INTO engine_model_program (engine_model, bookcase_key, created_by, created_at, last_updated_by, last_updated_at)
	VALUES('PASSPORT20', 'gek112060', '212462850', now(), '212462850', now());

-- Insert Roles
INSERT INTO "role" ("name", label, description, policy, created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('superuser', 'Superuser', 'Has access to all tabs and all engine models and airframes.', '{
  "technologyLevels": ["M", "0", "1", "2", "3", "4", "5"]
}', '212462850', now(), '212462850', now());
INSERT INTO "role" ("name", label, description, policy, created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('restricted-repair-provisioner', 'Restricted Repair Provisioner', 'Can enable all documents in the Enablement tabs.', '{
  "technologyLevels": ["M", "0", "1", "2", "3", "4", "5"]
}', '212462850', now(), '212462850', now());
INSERT INTO "role" ("name", label, description, policy, created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('provisioner', 'Provisioner', 'Can only enable unrestricted documents in the Enablement tabs.', '{
  "technologyLevels": ["M", "0", "1", "5"]
}', '212462850', now(), '212462850', now());
INSERT INTO "role" ("name", label, description, policy, created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('uploader', 'Document Uploader', 'Can upload new documents in the Uploader tab.', '{"technologyLevels": []}', '212462850', now(), '212462850', now());
INSERT INTO "role" ("name", label, description, policy, created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('publisher', 'Document Publisher', 'Can publish documents in the Publisher tab.', '{"technologyLevels": []}', '212462850', now(), '212462850', now());
INSERT INTO "role" ("name", label, description, policy, created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('reviewer', 'Document Reviewer', 'Can review documents in the Review Overlay.', '{"technologyLevels": []}', '212462850', now(), '212462850', now());
INSERT INTO "role" ("name", label, description, policy, created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('reporter', 'Reporter', 'View enabled document reports in the Reporting tab.', '{"technologyLevels": []}', '212462850', now(), '212462850', now());

-- Insert Resources
INSERT INTO resource ("name", type, created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('companies', 'tab', '212462850', now(), '212462850', now());
INSERT INTO resource ("name", type, created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('engine-models', 'tab', '212462850', now(), '212462850', now());
INSERT INTO resource ("name", type, created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('aircraft-platform', 'tab', '212462850', now(), '212462850', now());
INSERT INTO resource ("name", type, created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('admin-management', 'tab', '212462850', now(), '212462850', now());
INSERT INTO resource ("name", type, created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('publisher', 'tab', '212462850', now(), '212462850', now());
INSERT INTO resource ("name", type, created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('uploader', 'tab', '212462850', now(), '212462850', now());
INSERT INTO resource ("name", type, created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('audit-trail', 'tab', '212462850', now(), '212462850', now());
INSERT INTO resource ("name", type, created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('reporting', 'tab', '212462850', now(), '212462850', now());
INSERT INTO resource ("name", type, created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('review-overlay', 'widget', '212462850', now(), '212462850', now());

-- Insert Actions
INSERT INTO "action" ("name", created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('view', '212462850', now(), '212462850', now());
INSERT INTO "action" ("name", created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('edit', '212462850', now(), '212462850', now());

-- Insert Permissions
INSERT INTO permission (resource, "action", created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('companies', 'view', '212462850', now(), '212462850', now());
INSERT INTO permission (resource, "action", created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('companies', 'edit', '212462850', now(), '212462850', now());
INSERT INTO permission (resource, "action", created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('engine-models', 'view', '212462850', now(), '212462850', now());
INSERT INTO permission (resource, "action", created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('engine-models', 'edit', '212462850', now(), '212462850', now());
INSERT INTO permission (resource, "action", created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('aircraft-platform', 'view', '212462850', now(), '212462850', now());
INSERT INTO permission (resource, "action", created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('aircraft-platform', 'edit', '212462850', now(), '212462850', now());
INSERT INTO permission (resource, "action", created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('admin-management', 'view', '212462850', now(), '212462850', now());
INSERT INTO permission (resource, "action", created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('admin-management', 'edit', '212462850', now(), '212462850', now());
INSERT INTO permission (resource, "action", created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('publisher', 'view', '212462850', now(), '212462850', now());
INSERT INTO permission (resource, "action", created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('publisher', 'edit', '212462850', now(), '212462850', now());
INSERT INTO permission (resource, "action", created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('uploader', 'view', '212462850', now(), '212462850', now());
INSERT INTO permission (resource, "action", created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('uploader', 'edit', '212462850', now(), '212462850', now());
INSERT INTO permission (resource, "action", created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('audit-trail', 'view', '212462850', now(), '212462850', now());
INSERT INTO permission (resource, "action", created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('audit-trail', 'edit', '212462850', now(), '212462850', now());
INSERT INTO permission (resource, "action", created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('reporting', 'view', '212462850', now(), '212462850', now());
INSERT INTO permission (resource, "action", created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('reporting', 'edit', '212462850', now(), '212462850', now());
INSERT INTO permission (resource, "action", created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('review-overlay', 'view', '212462850', now(), '212462850', now());
INSERT INTO permission (resource, "action", created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('review-overlay', 'edit', '212462850', now(), '212462850', now());

-- Insert Superuser Role_Permissions
INSERT INTO role_permission ("role", "resource", "action", created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('superuser', 'companies', 'view', '212462850', now(), '212462850', now());
INSERT INTO role_permission ("role", "resource", "action", created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('superuser', 'companies', 'edit', '212462850', now(), '212462850', now());
INSERT INTO role_permission ("role", "resource", "action", created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('superuser', 'engine-models', 'view', '212462850', now(), '212462850', now());
INSERT INTO role_permission ("role", "resource", "action", created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('superuser', 'engine-models', 'edit', '212462850', now(), '212462850', now());
INSERT INTO role_permission ("role", "resource", "action", created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('superuser', 'aircraft-platform', 'view', '212462850', now(), '212462850', now());
INSERT INTO role_permission ("role", "resource", "action", created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('superuser', 'aircraft-platform', 'edit', '212462850', now(), '212462850', now());
INSERT INTO role_permission ("role", "resource", "action", created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('superuser', 'admin-management', 'view', '212462850', now(), '212462850', now());
INSERT INTO role_permission ("role", "resource", "action", created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('superuser', 'admin-management', 'edit', '212462850', now(), '212462850', now());
INSERT INTO role_permission ("role", "resource", "action", created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('superuser', 'audit-trail', 'view', '212462850', now(), '212462850', now());
INSERT INTO role_permission ("role", "resource", "action", created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('superuser', 'audit-trail', 'edit', '212462850', now(), '212462850', now());
INSERT INTO role_permission ("role", "resource", "action", created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('superuser', 'reporting', 'view', '212462850', now(), '212462850', now());
INSERT INTO role_permission ("role", "resource", "action", created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('superuser', 'reporting', 'edit', '212462850', now(), '212462850', now());
INSERT INTO role_permission ("role", "resource", "action", created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('superuser', 'publisher', 'view', '212462850', now(), '212462850', now());
INSERT INTO role_permission ("role", "resource", "action", created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('superuser', 'publisher', 'edit', '212462850', now(), '212462850', now());
INSERT INTO role_permission ("role", "resource", "action", created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('superuser', 'review-overlay', 'view', '212462850', now(), '212462850', now());
INSERT INTO role_permission ("role", "resource", "action", created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('superuser', 'review-overlay', 'edit', '212462850', now(), '212462850', now());

-- Insert Restricted Repair Provisioner Role_Permissions
INSERT INTO role_permission ("role", "resource", "action", created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('restricted-repair-provisioner', 'companies', 'view', '212462850', now(), '212462850', now());
INSERT INTO role_permission ("role", "resource", "action", created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('restricted-repair-provisioner', 'companies', 'edit', '212462850', now(), '212462850', now());
INSERT INTO role_permission ("role", "resource", "action", created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('restricted-repair-provisioner', 'engine-models', 'view', '212462850', now(), '212462850', now());
INSERT INTO role_permission ("role", "resource", "action", created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('restricted-repair-provisioner', 'engine-models', 'edit', '212462850', now(), '212462850', now());
INSERT INTO role_permission ("role", "resource", "action", created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('restricted-repair-provisioner', 'aircraft-platform', 'view', '212462850', now(), '212462850', now());
INSERT INTO role_permission ("role", "resource", "action", created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('restricted-repair-provisioner', 'aircraft-platform', 'edit', '212462850', now(), '212462850', now());
INSERT INTO role_permission ("role", "resource", "action", created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('restricted-repair-provisioner', 'audit-trail', 'view', '212462850', now(), '212462850', now());
INSERT INTO role_permission ("role", "resource", "action", created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('restricted-repair-provisioner', 'audit-trail', 'edit', '212462850', now(), '212462850', now());
INSERT INTO role_permission ("role", "resource", "action", created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('restricted-repair-provisioner', 'reporting', 'view', '212462850', now(), '212462850', now());
INSERT INTO role_permission ("role", "resource", "action", created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('restricted-repair-provisioner', 'reporting', 'edit', '212462850', now(), '212462850', now());

-- Insert Provisioner Role_Permissions
INSERT INTO role_permission ("role", "resource", "action", created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('provisioner', 'companies', 'view', '212462850', now(), '212462850', now());
INSERT INTO role_permission ("role", "resource", "action", created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('provisioner', 'companies', 'edit', '212462850', now(), '212462850', now());
INSERT INTO role_permission ("role", "resource", "action", created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('provisioner', 'engine-models', 'view', '212462850', now(), '212462850', now());
INSERT INTO role_permission ("role", "resource", "action", created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('provisioner', 'engine-models', 'edit', '212462850', now(), '212462850', now());
INSERT INTO role_permission ("role", "resource", "action", created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('provisioner', 'aircraft-platform', 'view', '212462850', now(), '212462850', now());
INSERT INTO role_permission ("role", "resource", "action", created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('provisioner', 'aircraft-platform', 'edit', '212462850', now(), '212462850', now());
INSERT INTO role_permission ("role", "resource", "action", created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('provisioner', 'audit-trail', 'view', '212462850', now(), '212462850', now());
INSERT INTO role_permission ("role", "resource", "action", created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('provisioner', 'audit-trail', 'edit', '212462850', now(), '212462850', now());
INSERT INTO role_permission ("role", "resource", "action", created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('provisioner', 'reporting', 'view', '212462850', now(), '212462850', now());
INSERT INTO role_permission ("role", "resource", "action", created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('provisioner', 'reporting', 'edit', '212462850', now(), '212462850', now());

-- Insert Publisher Role_Permissions
INSERT INTO role_permission ("role", "resource", "action", created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('publisher', 'publisher', 'view', '212462850', now(), '212462850', now());
INSERT INTO role_permission ("role", "resource", "action", created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('publisher', 'publisher', 'edit', '212462850', now(), '212462850', now());

-- Insert Uploader Role_Permissions
INSERT INTO role_permission ("role", "resource", "action", created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('uploader', 'uploader', 'view', '212462850', now(), '212462850', now());
INSERT INTO role_permission ("role", "resource", "action", created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('uploader', 'uploader', 'edit', '212462850', now(), '212462850', now());

-- Insert Reporter Role_Permissions
INSERT INTO role_permission ("role", "resource", "action", created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('reporter', 'reporting', 'view', '212462850', now(), '212462850', now());
INSERT INTO role_permission ("role", "resource", "action", created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('reporter', 'reporting', 'edit', '212462850', now(), '212462850', now());

-- Insert Reviewer Role_Permissions
INSERT INTO role_permission ("role", "resource", "action", created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('reviewer', 'review-overlay', 'view', '212462850', now(), '212462850', now());
INSERT INTO role_permission ("role", "resource", "action", created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('reviewer', 'review-overlay', 'edit', '212462850', now(), '212462850', now());

-- Insert Superuser
INSERT INTO "user" (sso, created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('212462850', '212462850', now(), '212462850', now());
INSERT INTO user_role (sso, "role", "attributes", created_by, creation_date, last_updated_by, last_updated_date)
VALUES ('212462850', 'superuser', '{
  "engineModels": [
    "all"
  ],
  "airFrames": [
    "all"
  ],
  "docTypes": [
    "all"
  ]
}', '212462850', now(), '212462850', now());