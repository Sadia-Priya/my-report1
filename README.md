\# PF Report System



Spring Boot microservices (Eureka, Gateway, Auth, Report), Next.js and PostgreSQL.

The PF report is shown on the website and can be downloaded as PDF and Excel in Bangla.



\## Setup



\### 1. Database

Create two databases in pgAdmin: `pf\_auth\_db` and `pf\_report\_db`.

Open the Query Tool on `pf\_report\_db` and run `database/setup.sql`.



\### 2. Database password

The password is not stored in the code. It is read from the `DB\_PASSWORD` environment variable.



In IntelliJ: Run → Edit Configurations → `AuthServiceApplication` → Modify options → Environment variables:



```

DB\_PASSWORD=your\_password

```



Replace `your\_password` with your own PostgreSQL password. Do the same for `ReportServiceApplication`.



\### 3. Run the backend

Start the services in this order:

1\. `DiscoveryServerApplication` (8761)

2\. `AuthServiceApplication` (8081) and `ReportServiceApplication` (8082)

3\. `ApiGatewayApplication` (8080)



Wait about 30 seconds.



\### 4. Run the frontend

```

cd frontend

npm install

npm run dev

```

Open http://localhost:3000, sign up and open the report page.

