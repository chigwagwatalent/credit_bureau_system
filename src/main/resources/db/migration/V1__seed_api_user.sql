INSERT INTO api_users (username, password, active)
VALUES (
  'api_admin',
  -- password: Api@1234
  '$2a$10$zZ8s7t8qX2Vw0v2n2yOZgOjQ8n3z3r/8VCTbqVf4m1Nq5O4b7mW9i',
  true
)
ON DUPLICATE KEY UPDATE active=VALUES(active);
