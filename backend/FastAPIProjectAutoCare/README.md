
---
## 💾 Create a PostgreSQL database and set up the environment

   - Create a database (e.g., `autocare_db`) in PostgreSQL and copy/paste datas of `AutoCare.sql` to your own database :
     ```sql
     CREATE DATABASE autocare_db;
     ```

   - Modify the `config.py` and connect to your own server:
 
   - Modify the `database.py`
      ``` bash
     SQLALCHEMY_DATABASE_URL = "postgresql://your_user:your_password@localhost:5432/autocare_db"

   - Make sure to install the dependency:
     ```bash
     
     pip install ...
     
     pip freeze > requirements.txt
     
     ```


## 🔐 Authentication

Authentication is **not** based on JWT. Instead, each endpoint uses the `email`  field to identify the current user, without password verification.

All data (Cars, ServiceEvents, Notifications, ~~ServiceHistory~~) is linked to the current user via their email.

---

## 🧩 Modules Overview

### 🔸 Users (`users.py`)
- Info user, update, delete user.
- Fields: `email`, `full_name`, `user_id`, `notiffy_on`, etc.

### 🔸 Cars (`cars.py`)
- Add, update, delete and list user vehicles.
- Fields: `brand`, `model`, `mileage`, `year`, etc.

### 🔸 Services (`services.py`)
- Add maintenance events, plan future services, view history.
- Fields: `service_type`, `due_date`, `due_mileage`.

### 🔸 Notifications (`notifications.py`)
- Automatic reminders for important dates (e.g. technical inspection).
- Fields: `event_id`, `message`, `notify_time`, `is_sent`.

### 🔸 Auth (`auth.py`)
- Register (`full_name`, `email`, `password`)
- Login (`email`, `password`)
- Token generation for password reset.
- No JWT or session-based authentication.
- Identity validation is done via email only.

---

## 🛠️ Pydantic Schemas (`schemas.py`)

Defines request/response models used in the API:
- `UserCreate`, `UserRenameResquest`, `CarCreate`, `ServiceEventCreate`, etc.
- Clear separation of base, create, and response schemas.

---

## 🔧 Utils (`auth/utils.py`)

- Password hashing (though not required in endpoints).
- Reset token generation.
- User identity helpers (email/ password match).
 
---

## 💾 Database — PostgreSQL

- Each table is linked to the `users` table via the `email` field.
- ~~- UUIDs used for IDs.~~
- ~~- GIN indexing and performance optimization.~~
- Managed via SQLAlchemy ORM.

---

## 🚀 Getting Started

1. Clone the repository:
   ```bash
   git clone https://github.com/your-user/autocare.git
   cd autocare
