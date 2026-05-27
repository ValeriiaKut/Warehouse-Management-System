Warehouse Inventory Management System
Project Description

Warehouse Inventory Management System is a fullstack warehouse management application based on a microservices architecture.

The project consists of:

Android mobile frontend written in Kotlin
Backend developed using FastAPI
PostgreSQL database
Docker infrastructure
JWT Authentication and role-based authorization

The system allows product management, order creation, inventory control, and secure user authorization.

Technologies
Backend
Python
FastAPI
SQLAlchemy
PostgreSQL
Docker
JWT Authentication
bcrypt
SlowAPI (Rate Limiting)
Frontend
Android
Kotlin
Retrofit
Project Architecture

The backend is divided into independent microservices.

Auth Service

Responsible for:

user registration
login
JWT token generation
authorization
user roles management
Endpoints
POST /auth/register
POST /auth/login
GET /auth/me
Inventory Service

Responsible for:

product management
inventory control
CRUD operations
Endpoints
GET    /products
POST   /products
PUT    /products/{id}
DELETE /products/{id}
Orders Service

Responsible for:

order creation
order management
status updates
automatic product quantity reduction
Endpoints
POST   /orders
GET    /orders
PUT    /orders/{id}/status
DELETE /orders/{id}
Security Mechanisms

The project includes multiple security mechanisms:

JWT Authentication
Password hashing using bcrypt
Password validation
Role-based authorization
Rate limiting for login endpoint
Audit logging
Docker container logging
User Roles
Worker

Permissions:

browse products
search products
create orders
view orders
Admin

Additional permissions:

create products
edit products
delete products
update order statuses
delete orders
Docker Infrastructure

The entire backend runs in a Docker Compose environment.

Containers
auth_service
inventory_service
orders_service
warehouse_db
Running the Backend
docker compose up --build
Swagger Documentation

Each microservice contains automatically generated Swagger API documentation.

Swagger Links
Auth Service
http://127.0.0.1:8001/docs
Inventory Service
http://127.0.0.1:8002/docs
Orders Service
http://127.0.0.1:8003/docs
Android Emulator Configuration

Android Emulator uses the address:

10.0.2.2
Example
http://10.0.2.2:8001/
Database Structure
Main Tables
users
products
orders
order_items
Relationships
User        → Orders
Order       → OrderItems
Product     → OrderItems
Git Workflow

The project uses Git workflow with:

main branch
develop branch
feature branches
pull requests
Example Branches
feature/microservices
feature/security-improvements
Main Features
Microservices architecture
Android mobile application
JWT Authentication
Docker infrastructure
PostgreSQL database
REST API
Swagger documentation
Security mechanisms
Role-based access control
Future Improvements

Possible future extensions:

API Gateway
Refresh Tokens
Email Verification
Notification system
Admin dashboard
Kubernetes deployment
Summary

Warehouse Inventory Management System is a modern warehouse management platform built using scalable backend technologies and Android mobile development.

The system implements a microservices architecture, secure authentication mechanisms, RESTful APIs, and Docker-based deployment, making it scalable, maintainable, and production-ready.
