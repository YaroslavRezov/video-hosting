-- =====================================================
-- V1: Initial database schema
-- =====================================================

-- 1. Sequences
CREATE SEQUENCE topic_id_seq;
CREATE SEQUENCE video_id_seq;
CREATE SEQUENCE users_id_seq;


-- 2. Topic table
CREATE TABLE topic
(
    id BIGINT DEFAULT nextval('topic_id_seq'::regclass) NOT NULL,
    name VARCHAR(250) NOT NULL UNIQUE,
    description TEXT,
    released_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_topic PRIMARY KEY (id)
);


-- 3. Video table
CREATE TABLE video
(
    id BIGINT DEFAULT nextval('video_id_seq'::regclass) NOT NULL,
    title VARCHAR(250) NOT NULL,
    episode_number INTEGER NOT NULL,
    description TEXT,
    file_path VARCHAR(500) NOT NULL,
    topic_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_video PRIMARY KEY (id),

    CONSTRAINT fk_video_topic
        FOREIGN KEY (topic_id)
        REFERENCES topic (id)
        ON DELETE CASCADE,

    CONSTRAINT uq_video_topic_episode
        UNIQUE (topic_id, episode_number)
);


-- 4. Users table
CREATE TABLE users
(
    id BIGINT DEFAULT nextval('users_id_seq'::regclass) NOT NULL,
    username VARCHAR NOT NULL UNIQUE,
    password VARCHAR NOT NULL,

    CONSTRAINT pk_users PRIMARY KEY (id)
);


-- 5. Search index
CREATE INDEX idx_video_title_lower
    ON video (LOWER(title));


-- 6. Associate sequences with their columns
ALTER SEQUENCE topic_id_seq OWNED BY topic.id;
ALTER SEQUENCE video_id_seq OWNED BY video.id;
ALTER SEQUENCE users_id_seq OWNED BY users.id;

