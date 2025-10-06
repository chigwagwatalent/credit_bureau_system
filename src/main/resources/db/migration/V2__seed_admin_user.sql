INSERT INTO users (username, email, password, role, active)
VALUES (
  'admin',
  'admin@datumbureau.local',
  -- password = Admin@123
  '$2a$10$Jb7v8c9pQf8C1Tt7kqIOgO8J8D2nM9m4O2pVv8x2Gq0JzH7Jt1k6K',
  'ADMIN',
  true
)
ON DUPLICATE KEY UPDATE email=VALUES(email);
