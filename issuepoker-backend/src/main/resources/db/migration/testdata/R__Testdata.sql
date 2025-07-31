truncate the_entity;

INSERT INTO the_entity (text_attribute, id)
VALUES ('Alpha', '123e4567-e89b-12d3-a456-426614174000'),
       ('Bravo', '123e4567-e89b-12d3-a456-426614174001'),
       ('Charlie', '123e4567-e89b-12d3-a456-426614174002'),
       ('Delta', '123e4567-e89b-12d3-a456-426614174003'),
       ('Echo', '123e4567-e89b-12d3-a456-426614174004');

TRUNCATE issue CASCADE;
TRUNCATE "user" CASCADE;
TRUNCATE vote CASCADE;
TRUNCATE issue_votes CASCADE;
TRUNCATE user_authorities CASCADE;

INSERT INTO issue (id, title, description)
VALUES (1, 'Test Issue 1', 'Description for test issue 1'),
       (2, 'Test Issue 2', 'Description for test issue 2'),
       (3, 'Test Issue 3', 'Description for test issue 3');

INSERT INTO "user" (sub, email_verified, name, preferred_username, given_name, family_name, email)
VALUES ('123e4567-e89b-12d3-a456-426614174005', TRUE, 'John Doe', 'johndoe', 'John', 'Doe', 'john.doe@example.com'),
       ('123e4567-e89b-12d3-a456-426614174006', FALSE, 'Jane Smith', 'janesmith', 'Jane', 'Smith', 'jane.smith@example.com');

INSERT INTO vote (id, user_sub, vote)
VALUES ('123e4567-e89b-12d3-a456-426614174007', '123e4567-e89b-12d3-a456-426614174005', 1),
       ('123e4567-e89b-12d3-a456-426614174008', '123e4567-e89b-12d3-a456-426614174006', -1),
       ('123e4567-e89b-12d3-a456-426614174009', '123e4567-e89b-12d3-a456-426614174005', 1);

INSERT INTO issue_votes (issue_id, votes_id)
VALUES (1, '123e4567-e89b-12d3-a456-426614174007'),
       (1, '123e4567-e89b-12d3-a456-426614174008'),
       (2, '123e4567-e89b-12d3-a456-426614174009');

INSERT INTO user_authorities (user_id, authorities)
VALUES ('123e4567-e89b-12d3-a456-426614174005', 'ROLE_USER'),
       ('123e4567-e89b-12d3-a456-426614174006', 'ROLE_ADMIN'),
       ('123e4567-e89b-12d3-a456-426614174005', 'ROLE_MODERATOR');
