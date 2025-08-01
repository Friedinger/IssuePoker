truncate the_entity;

INSERT INTO the_entity (text_attribute, id)
VALUES ('Alpha', '123e4567-e89b-12d3-a456-426614174000'),
       ('Bravo', '123e4567-e89b-12d3-a456-426614174001'),
       ('Charlie', '123e4567-e89b-12d3-a456-426614174002'),
       ('Delta', '123e4567-e89b-12d3-a456-426614174003'),
       ('Echo', '123e4567-e89b-12d3-a456-426614174004');

TRUNCATE issue CASCADE;
TRUNCATE person CASCADE;
TRUNCATE vote CASCADE;
TRUNCATE issue_votes CASCADE;
TRUNCATE user_authorities CASCADE;

INSERT INTO issue (id, title, description)
VALUES (1, 'Bug Report: Login Failure', 'Users are unable to log in using their credentials.'),
       (2, 'Feature Request: Dark Mode', 'Add a dark mode option for better usability at night.'),
       (3, 'UI Glitch: Button Overlap', 'The submit button overlaps with the cancel button on mobile devices.'),
       (4, 'Performance Issue: Slow Loading', 'The homepage takes too long to load, affecting user experience.'),
       (5, 'Security Concern: Data Exposure', 'Sensitive user data is exposed in the source code.'),
       (6, 'Usability Issue: Confusing Navigation', 'Navigation menu is not intuitive for new users.'),
       (7, 'Compatibility Issue: Browser Support', 'The application does not work on older versions of Safari.'),
       (8, 'Feature Request: Export to CSV', 'Allow users to export their data in CSV format.'),
       (9, 'Bug Report: Email Notifications', 'Email notifications are not sent for important updates.'),
       (10, 'UI Improvement: Font Size', 'Increase the font size for better readability.'),
       (11, 'Feature Request: Multi-language Support', 'Add support for multiple languages in the application.'),
       (12, 'Bug Report: Image Upload Error', 'Users encounter an error when uploading images.'),
       (13, 'Performance Issue: High CPU Usage', 'The application consumes excessive CPU resources.'),
       (14, 'Accessibility Issue: Screen Reader Support', 'The website is not fully compatible with screen readers.'),
       (15, 'Feature Request: User Profiles', 'Enable users to create and edit their profiles.'),
       (16, 'Bug Report: Payment Processing', 'Payments are failing for some users during checkout.'),
       (17, 'UI Glitch: Color Contrast', 'Some text does not have sufficient contrast against the background.'),
       (18, 'Feature Request: Push Notifications', 'Implement push notifications for real-time updates.'),
       (19, 'Usability Issue: Search Functionality', 'The search feature does not return accurate results.'),
       (20, 'Security Concern: Password Strength', 'Enforce stronger password requirements for user accounts.'),
       (21, 'Bug Report: Session Timeout', 'Users are logged out unexpectedly after a short period of inactivity.');

INSERT INTO person (sub, email_verified, name, preferred_username, given_name, family_name, email)
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
