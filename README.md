# AURUM Conversion Engine

AURUM is a luxury conversion system with a single-file frontend and polyglot backend endpoints.

## Product Structure

```tree
aurum-conversion-platform/
├── index.html
├── backend/
│   ├── python/
│   │   ├── main.py
│   │   └── requirements.txt
│   ├── typescript/
│   │   ├── server.ts
│   │   ├── package.json
│   │   └── tsconfig.json
│   └── java/
│       ├── pom.xml
│       └── src/main/java/com/aurum/ledger/LedgerApplication.java
└── README.md
```

## Frontend

Open `index.html` in a browser.

To connect the frontend to a running backend, use the `api` query parameter:

```text
index.html?api=http://localhost:8000
```

## Python API

```bash
cd backend/python
python -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt
uvicorn main:app --port 8000
```

Endpoints:

```text
GET  /healthz
POST /api/leads
GET  /api/leads/{lead_id}
```

## TypeScript API

```bash
cd backend/typescript
npm install
npm run dev
```

Endpoints:

```text
GET  /healthz
POST /api/leads
GET  /api/leads/:id
```

## Java API

```bash
cd backend/java
mvn spring-boot:run
```

Endpoints:

```text
GET  /api/healthz
POST /api/leads
GET  /api/leads/{id}
```
```

References

Express. (n.d.). *Express - Node.js web application framework*. Retrieved September 2, 2026, from https://expressjs.com/

Microsoft. (n.d.). *TypeScript documentation*. Retrieved September 2, 2026, from https://www.typescriptlang.org/docs/

Mozilla Developer Network. (n.d.). *CSS: Cascading Style Sheets*. Retrieved September 2, 2026, from https://developer.mozilla.org/en-US/docs/Web/CSS

Mozilla Developer Network. (n.d.). *HTML: HyperText Markup Language*. Retrieved September 2, 2026, from https://developer.mozilla.org/en-US/docs/Web/HTML

Mozilla Developer Network. (n.d.). *JavaScript*. Retrieved September 2, 2026, from https://developer.mozilla.org/en-US/docs/Web/JavaScript

Python Software Foundation. (n.d.). *Python 3 documentation*. Retrieved September 2, 2026, from https://docs.python.org/3/

Ramírez, S. (n.d.). *FastAPI*. Retrieved September 2, 2026, from https://fastapi.tiangolo.com/

Spring. (n.d.). *Spring Boot reference documentation*. Retrieved September 2, 2026, from https://docs.spring.io/spring-boot/index.html
