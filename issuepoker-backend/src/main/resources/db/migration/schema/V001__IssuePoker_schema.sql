CREATE SEQUENCE IF NOT EXISTS issue_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE issue
(
    id          BIGINT         NOT NULL,
    owner       VARCHAR(255)   NOT NULL,
    repository  VARCHAR(255)   NOT NULL,
    title       VARCHAR(255)   NOT NULL,
    description VARCHAR(65535) NOT NULL,
    revealed    BOOLEAN        NOT NULL,
    vote_result INTEGER,
    CONSTRAINT pk_issue PRIMARY KEY (id)
);

CREATE TABLE issue_votes
(
    issue_id BIGINT NOT NULL,
    votes_id UUID   NOT NULL
);

CREATE TABLE vote
(
    id       UUID         NOT NULL,
    username VARCHAR(255) NOT NULL,
    voting   INTEGER      NOT NULL,
    CONSTRAINT pk_vote PRIMARY KEY (id)
);

ALTER TABLE issue_votes
    ADD CONSTRAINT uc_issue_votes_votes UNIQUE (votes_id);

ALTER TABLE issue_votes
    ADD CONSTRAINT fk_issvot_on_issue FOREIGN KEY (issue_id) REFERENCES issue (id);

ALTER TABLE issue_votes
    ADD CONSTRAINT fk_issvot_on_vote FOREIGN KEY (votes_id) REFERENCES vote (id);