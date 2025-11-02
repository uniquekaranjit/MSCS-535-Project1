const fs = require('fs');
const https = require('https');
const express = require('express');
const helmet = require('helmet');
const rateLimit = require('express-rate-limit');
const { body, validationResult } = require('express-validator');
const bcrypt = require('bcrypt');
const db = require('./db');

const app = express();
app.use(helmet());
app.use(express.json());

// Rate limiter
app.use(rateLimit({ windowMs: 15 * 60 * 1000, max: 100 }));

// Register user
app.post('/api/register',
  body('username').isLength({ min: 3, max: 50 }).matches(/^[A-Za-z0-9._@-]+$/),
  body('password').isLength({ min: 8, max: 100 }),
  async (req, res) => {
    const errors = validationResult(req);
    if (!errors.isEmpty()) return res.status(400).json({ errors: errors.array() });

    const { username, password } = req.body;
    try {
      const hash = await bcrypt.hash(password, 12);
      const stmt = db.prepare('INSERT INTO users (username, password_hash) VALUES (?, ?)');
      stmt.run(username, hash);
      res.status(201).json({ message: 'User registered successfully!' });
    } catch (err) {
      if (err.code === 'SQLITE_CONSTRAINT') {
        return res.status(409).json({ error: 'Username already exists' });
      }
      console.error(err);
      res.status(500).json({ error: 'Internal server error' });
    }
  }
);

// Login user
app.post('/api/login',
  body('username').notEmpty(),
  body('password').notEmpty(),
  async (req, res) => {
    const { username, password } = req.body;
    const errors = validationResult(req);
    if (!errors.isEmpty()) return res.status(400).json({ errors: errors.array() });

    const row = db.prepare('SELECT id, username, password_hash FROM users WHERE username = ?').get(username);
    if (!row) return res.status(401).json({ error: 'Invalid credentials' });

    const match = await bcrypt.compare(password, row.password_hash);
    if (!match) return res.status(401).json({ error: 'Invalid credentials' });

    res.json({ message: 'Login successful', username: row.username });
  }
);

// HTTPS setup
const PORT = 8443;
const options = {
  key: fs.readFileSync('./cert/key.pem'),
  cert: fs.readFileSync('./cert/cert.pem')
};

https.createServer(options, app).listen(PORT, () => {
  console.log(`✅ Secure server running at https://localhost:${PORT}`);
});

