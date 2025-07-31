create table the_entity
(
    text_attribute varchar(8) not null,
    id             uuid       not null,
    primary key (id)
);

CREATE TABLE issue
(
    id          BIGINT NOT NULL,
    title       VARCHAR(255),
    description VARCHAR(65535),
    CONSTRAINT pk_issue PRIMARY KEY (id)
);

CREATE TABLE issue_votes
(
    issue_id BIGINT NOT NULL,
    votes_id UUID   NOT NULL
);

CREATE TABLE person
(
    sub                UUID    NOT NULL,
    email_verified     BOOLEAN NOT NULL,
    name               VARCHAR(255),
    preferred_username VARCHAR(255),
    given_name         VARCHAR(255),
    family_name        VARCHAR(255),
    email              VARCHAR(255),
    CONSTRAINT pk_user PRIMARY KEY (sub)
);

CREATE TABLE user_authorities
(
    user_id     UUID,
    authorities VARCHAR(255),
    CONSTRAINT pk_authorities PRIMARY KEY (user_id, authorities)
);

CREATE TABLE vote
(
    id       UUID    NOT NULL,
    user_sub UUID,
    vote     INTEGER NOT NULL,
    CONSTRAINT pk_vote PRIMARY KEY (id)
);

ALTER TABLE issue_votes
    ADD CONSTRAINT uc_issue_votes_votes UNIQUE (votes_id);

ALTER TABLE vote
    ADD CONSTRAINT fk_vote_on_user_sub FOREIGN KEY (user_sub) REFERENCES person (sub);

ALTER TABLE issue_votes
    ADD CONSTRAINT fk_issvot_on_issue FOREIGN KEY (issue_id) REFERENCES issue (id);

ALTER TABLE issue_votes
    ADD CONSTRAINT fk_issvot_on_vote FOREIGN KEY (votes_id) REFERENCES vote (id);

ALTER TABLE user_authorities
    ADD CONSTRAINT fk_person_authorities FOREIGN KEY (user_id) REFERENCES person (sub);
