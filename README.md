# 🚗 CarSaathi – Car Dealership Management System

CarSaathi is a desktop-based Car Dealership Management System built using Java (Swing), MySQL, and JDBC.
It simulates real-world dealership operations including inventory management, bookings, purchases, and analytics.

The project emphasizes strong DBMS concepts, clean layered architecture, and practical workflow implementation.

---

🌟 Key Highlights

- Real-world dealership workflow simulation
- Fully normalized relational database design
- Booking → Inventory → Purchase lifecycle
- Role-based system (Admin & Dealer)
- Advanced SQL (Joins, Functions, Procedures, Triggers)
- Analytics dashboards for decision making

---

🎯 Objective

- Digitize dealership operations
- Maintain structured and consistent data
- Track inventory and sales in real time
- Provide meaningful analytics
- Apply DBMS concepts in a practical system

---

🏗️ System Architecture

The system follows a layered architecture:

UI Layer (Swing)

Handles all user interactions (Login, Dashboard, Inventory, Analytics)

Service Layer

Contains business logic (booking validation, purchase handling, analytics)

DAO Layer

Handles database operations using JDBC (queries, joins, updates)

Database Layer (MySQL)

Stores structured relational data with proper constraints

---

👥 User Roles

Admin

- Manage dealers
- View system-wide analytics
- Monitor overall performance

Dealer

- Manage inventory
- Register customers
- Handle bookings and purchases
- View dealership analytics

---

🚀 Core Features

Authentication

- Email-based login
- Role-based access

---

Car Management

- CarModel → defines car type
- CarInstance → represents actual unit

✔ Ensures scalability and accurate stock tracking

---

Inventory System

- Add car instances
- Track availability per dealer
- Real-time updates

---

Booking System

- Customers can book cars
- Status: Available / Unavailable

✔ Automatically updated via triggers

---

Purchase System

- Converts booking → purchase
- Stores final price
- Marks car as SOLD

✔ Prevents invalid purchases

---

Analytics Dashboard

Dealer

- Total Revenue
- Monthly Revenue
- Top Selling Model
- Available Stock

Admin

- Total System Revenue
- Dealer Ranking
- Most Popular Model
- Total Sales

---

🔄 System Workflow

Inventory → Booking → Purchase

1. Dealer adds car to inventory
2. Car becomes available
3. Customer booking is created
4. Booking marked unavailable
5. Dealer completes purchase
6. Car marked as SOLD
7. Analytics updated

---

Automation Logic

- New car added → booking becomes Available
- Purchase attempted → validated before execution
- After purchase → car automatically marked Sold

---

Analytics Flow

- Data processed using:
  
  - JOIN
  - GROUP BY
  - SUM / COUNT

- Procedures generate:
  
  - Monthly revenue
  - Yearly revenue
  - Top models

---

🧠 DBMS Concepts Used

DML Operations

- INSERT, UPDATE, DELETE

Joins

- Purchase + CarInstance
- CarInstance + CarModel
- Dealer + Sales

Aggregation

- SUM → revenue
- COUNT → sales
- GROUP BY → statistics

Stored Procedures

- getMonthlyRevenue
- getMonthlySales
- getSystemMonthlySales
- getTopSellingModel
- getYearlySales

Stored Functions

- getTotalSales
- getAvailableStock

Triggers

- after_car_added
- before_purchase_insert
- after_purchase_insert_update_car

---

🗃️ Database Design

Tables:

- USER
- ADMIN
- DEALER
- CUSTOMER
- USER_PHONE
- CARMODEL
- CARINSTANCE
- BOOKING
- PURCHASE

✔ Fully normalized
✔ Foreign key relationships maintained

---

📂 Project Structure

src/
│
├── ui/
│   ├── admin/
│   ├── dealer/
│   └── customer/
│
├── service/
├── dao/
├── model/
├── db/
└── util/

resources/
lib/

---

⚙️ Technologies Used

- Java (Core + Swing)
- MySQL
- JDBC
- SQL (Procedures, Functions, Triggers)

---

▶️ How to Run

1. Clone the repository
2. Open in any Java IDE (VS Code / IntelliJ / Eclipse)
3. Setup MySQL database
4. Execute "schema.sql"
5. Configure database credentials
6. Run "Main.java"

---
