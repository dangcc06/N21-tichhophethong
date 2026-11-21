# Finance Service

Spring Boot REST service for student tuition records stored in the PostgreSQL
`tuition` table. Requires Java 25 and PostgreSQL.

## Run

Start PostgreSQL locally. For example, with Docker:

```powershell
docker run --name finance-postgres -e POSTGRES_DB=finance -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres -p 5432:5432 -d postgres:17-alpine
```

From the repository root, enter the service folder, create a local `.env` file
from the template, then edit its values to match your PostgreSQL connection:

```powershell
cd finance-service
Copy-Item .env.example .env
notepad .env
mvn spring-boot:run
```

`.env` is loaded automatically by Spring Boot and is excluded from Git. The
template uses port `3006` for the PostgreSQL server in this development setup;
change it to the host and port shown by your pgAdmin connection. Do not commit
the `.env` file or put real credentials in `.env.example`.

Hibernate creates or updates the `tuition` table at startup.

## API

| Method | Path | Purpose |
| --- | --- | --- |
| GET | `/api/tuition` | List tuition records |
| GET | `/api/tuition/{studentId}` | Get a student's tuition |
| POST | `/api/tuition` | Create tuition |
| PUT | `/api/tuition/{studentId}` | Replace the amount paid |
| DELETE | `/api/tuition/{studentId}` | Delete tuition |
| GET | `/api/tuition/statistics` | Count records by payment status |

Create request:

```json
{
  "studentId": "SV001",
  "tuitionAmount": 12000000,
  "paidAmount": 3000000
}
```

Update request:

```json
{
  "paidAmount": 6000000
}
```

`tuitionAmount` must be positive, `paidAmount` must be non-negative and no
greater than the tuition amount, and each student can have one tuition record.
The `status` field is derived as `UNPAID`, `PARTIALLY_PAID`, or `PAID`.
Records previously stored in MongoDB are not migrated automatically.
