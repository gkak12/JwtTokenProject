DELETE FROM USER;

INSERT INTO USER
    (ID, PASSWORD, NAME, AUTH)
VALUES
    ('user', '$2a$10$iQ1Sx4DJSfql/9NRLK8npe9GO6NdRD1a8UaJWKezU7DFnBwpAciey', 'park', 'ROLE_USER');   -- raw password: "password"