# Hall Booking Management System

A comprehensive Java Swing application for managing hall bookings, maintenance scheduling, and customer issues. Built with MVC architecture and file-based data persistence.

---

## 📋 Table of Contents
- [Overview](#overview)
- [Features](#features)
- [System Architecture](#system-architecture)
- [Getting Started](#getting-started)
- [Default Credentials](#default-credentials)
- [User Workflows](#user-workflows)
- [Hall Types & Pricing](#hall-types--pricing)
- [Business Rules](#business-rules)
- [Project Structure](#project-structure)

---

## 🏢 Overview

The Hall Booking Management System is designed for organizations to manage their hall/venue rentals. It supports four user roles with distinct functionalities:

- **Customer** - Book halls, manage reservations, raise issues
- **Scheduler** - Manage halls and maintenance schedules
- **Administrator** - Manage users and all bookings
- **Manager** - View sales reports and handle customer issues

---

## ✨ Features

### Customer Features
- ✅ User registration and login
- ✅ Browse available halls with details
- ✅ Make hall bookings with date/time selection
- ✅ Process payments (Card/Cash/Online Banking)
- ✅ View and print booking receipts
- ✅ View upcoming and past bookings
- ✅ Cancel bookings (3-day cancellation policy)
- ✅ Raise issues/complaints for bookings

### Scheduler Features
- ✅ Dashboard with system statistics
- ✅ Add new halls to the system
- ✅ Edit hall details (name, type, capacity, price)
- ✅ Toggle hall availability
- ✅ Delete halls (with booking validation)
- ✅ Schedule maintenance for halls
- ✅ Track maintenance status (Scheduled → In Progress → Completed)

### Administrator Features
- ✅ Dashboard with user and booking statistics
- ✅ View all registered users
- ✅ Filter users by role and status
- ✅ Add new staff accounts (Scheduler/Admin/Manager)
- ✅ Activate/Deactivate user accounts
- ✅ Delete user accounts
- ✅ View all bookings in the system
- ✅ Confirm, cancel, or complete bookings
- ✅ Delete booking records

### Manager Features
- ✅ Dashboard with revenue and issue statistics
- ✅ View sales reports with period filtering
- ✅ Export sales reports
- ✅ View and respond to customer issues
- ✅ Update issue status (Open → In Progress → Done → Closed)

---

## 🏗️ System Architecture

```
┌─────────────────────────────────────────────────────────┐
│                    Presentation Layer                    │
│         (Swing GUI - LoginFrame, Dashboards)            │
├─────────────────────────────────────────────────────────┤
│                     Service Layer                        │
│  (AuthServices, BookingService, HallServices, etc.)     │
├─────────────────────────────────────────────────────────┤
│                    Repository Layer                      │
│  (UserRepository, BookingRepository, HallRepository)    │
├─────────────────────────────────────────────────────────┤
│                      Data Layer                          │
│            (Text Files - .txt persistence)              │
└─────────────────────────────────────────────────────────┘
```

**Design Patterns Used:**
- MVC (Model-View-Controller)
- Repository Pattern
- Singleton Pattern (Repositories)
- Factory Pattern (UI Components)

---

## 🚀 Getting Started

### Prerequisites
- Java JDK 8 or higher
- Any Java IDE (VS Code, IntelliJ, Eclipse) or command line

### Compilation
```bash
cd "/path/to/assignment final java"
find src -name "*.java" -print > sources.txt
javac -d out -sourcepath src @sources.txt
```

### Running the Application
```bash
java -cp out HotelMgmt.Main
```

### Quick Start (One Command)
```bash
cd "/path/to/assignment final java" && find src -name "*.java" -print > sources.txt && javac -d out -sourcepath src @sources.txt && java -cp out HotelMgmt.Main
```

---

## 🔐 Default Credentials

| Role | Email | Password |
|------|-------|----------|
| **Administrator** | admin@hallbooking.com | admin123 |
| **Manager** | manager@hallbooking.com | manager123 |
| **Scheduler** | scheduler@hallbooking.com | scheduler123 |

> **Note:** Customers can register through the registration page.

---

## 📊 User Workflows

### Customer Workflow
```
Register/Login → Browse Halls → Select Hall & Time → 
Make Payment → Receive Receipt → View My Bookings → 
(Optional) Cancel Booking / Raise Issue
```

1. **Registration**: New customers register with name, email, password, and phone
2. **Login**: Enter credentials to access customer dashboard
3. **Book a Hall**: 
   - Click "Book Hall" from dashboard
   - Select an available hall from the table
   - Choose date and time (8 AM - 6 PM)
   - Enter duration and remarks
   - Click "Proceed to Payment"
4. **Payment**: Select payment method (Card/Cash/Online) and confirm
5. **Receipt**: View and note down booking confirmation
6. **Manage Bookings**: View upcoming/past bookings, cancel if needed
7. **Raise Issues**: Report problems related to bookings

### Scheduler Workflow
```
Login → Dashboard → Manage Halls / Schedule Maintenance → 
Monitor Maintenance Status
```

1. **Hall Management**:
   - Add new halls with type, capacity, and pricing
   - Edit existing hall details
   - Toggle availability for temporary closure
   - Delete unused halls

2. **Maintenance Scheduling**:
   - Schedule maintenance periods for halls
   - Track maintenance progress
   - Mark maintenance as complete

### Administrator Workflow
```
Login → Dashboard → Manage Users / Manage Bookings → 
Monitor System Activity
```

1. **User Management**:
   - View all users with filtering options
   - Add new staff members
   - Activate/Deactivate accounts
   - Remove users from system

2. **Booking Management**:
   - View all bookings with status filter
   - Confirm pending bookings
   - Cancel or complete bookings
   - Delete booking records

### Manager Workflow
```
Login → Dashboard → View Sales Reports / Handle Issues → 
Generate Reports
```

1. **Sales Reports**:
   - View revenue statistics
   - Filter by period (Today/Week/Month/All Time)
   - Export detailed reports

2. **Issue Management**:
   - Review customer complaints
   - Respond to issues
   - Update issue status
   - Close resolved issues

---

## 🏛️ Hall Types & Pricing

| Hall Type | Capacity | Price per Hour |
|-----------|----------|----------------|
| **Auditorium** | 500 pax | RM 300.00 |
| **Banquet Hall** | 200 pax | RM 100.00 |
| **Meeting Room** | 30 pax | RM 50.00 |

---

## 📜 Business Rules

1. **Business Hours**: 8:00 AM - 6:00 PM daily
2. **Cancellation Policy**: Bookings can only be cancelled at least 3 days before the event
3. **Booking Conflicts**: System prevents double-booking of halls
4. **Maintenance Blocks**: Halls under maintenance cannot be booked
5. **Payment**: Booking is confirmed only after successful payment
6. **User Status**: Only active users can login to the system

---

## 📁 Project Structure

```
assignment final java/
├── data/                          # Data storage
│   ├── users.txt                  # User accounts
│   ├── halls.txt                  # Hall information
│   ├── bookings.txt               # Booking records
│   ├── payments.txt               # Payment records
│   ├── issues.txt                 # Customer issues
│   └── maintenance.txt            # Maintenance schedules
│
├── src/HotelMgmt/
│   ├── Main.java                  # Application entry point
│   │
│   ├── constants/                 # Enums and constants
│   │   ├── BookingStatus.java
│   │   ├── HallType.java
│   │   ├── IssueStatus.java
│   │   ├── MaintenanceStatus.java
│   │   ├── UserRole.java
│   │   └── UserStatus.java
│   │
│   ├── model/                     # Data models
│   │   ├── User.java (abstract)
│   │   ├── Customer.java
│   │   ├── Staff.java (abstract)
│   │   ├── Administrator.java
│   │   ├── Manager.java
│   │   ├── Scheduler.java
│   │   ├── Hall.java
│   │   ├── Booking.java
│   │   ├── Payment.java
│   │   ├── Issue.java
│   │   └── Maintenance.java
│   │
│   ├── repository/                # Data access layer
│   │   ├── Repository.java (interface)
│   │   ├── UserRepository.java
│   │   ├── HallRepository.java
│   │   ├── BookingRepository.java
│   │   ├── PaymentRepository.java
│   │   ├── IssueRepository.java
│   │   └── MaintenanceRepository.java
│   │
│   ├── services/                  # Business logic layer
│   │   ├── AuthServices.java
│   │   ├── UserService.java
│   │   ├── HallServices.java
│   │   ├── BookingService.java
│   │   ├── PaymentService.java
│   │   ├── IssueService.java
│   │   └── MaintenanceService.java
│   │
│   ├── util/                      # Utility classes
│   │   ├── FileUtil.java
│   │   ├── DateUtil.java
│   │   ├── IdGenerator.java
│   │   └── ValidationUtil.java
│   │
│   └── GUI/                       # User interface
│       ├── UIComponents.java      # Reusable UI components
│       ├── LoginFrame.java
│       ├── RegisterFrame.java
│       │
│       ├── customer/
│       │   ├── CustomerDashboard.java
│       │   ├── BookingFrame.java
│       │   ├── MyBookingFrame.java
│       │   └── IssueFrame.java
│       │
│       ├── Scheduler/
│       │   ├── SchedulerDashboard.java
│       │   ├── HallManagementFrame.java
│       │   └── MaintenanceFrame.java
│       │
│       ├── admin/
│       │   ├── AdminDashboard.java
│       │   ├── UserManagementFrame.java
│       │   └── BookingManagementFrame.java
│       │
│       └── manager/
│           ├── ManagerDashboard.java
│           ├── SalesReportFrame.java
│           └── IssueManagementFrame.java
│
└── out/                           # Compiled classes
```

---

## 🎨 UI Color Scheme

| Color | Hex Code | Usage |
|-------|----------|-------|
| Primary Blue | #2980B9 | Primary buttons, headers |
| Success Green | #27AE60 | Success buttons, positive status |
| Danger Red | #E74C3C | Delete/Cancel buttons, warnings |
| Secondary Gray | #34495E | Secondary buttons, sidebar |
| Background | #ECF0F1 | Page backgrounds |

---

## 📝 Data File Format

All data is stored in pipe-delimited (|) text files:

**users.txt**
```
ID|Name|Email|Password|Role|Status|Phone
```

**halls.txt**
```
ID|Name|Type|Capacity|PricePerHour|Description|Available
```

**bookings.txt**
```
ID|CustomerID|CustomerName|HallID|HallName|StartDateTime|EndDateTime|Amount|Status|Remarks|CreatedAt
```

---

## 🛠️ Technologies Used

- **Language**: Java 8+
- **GUI Framework**: Java Swing
- **Data Storage**: Text Files (.txt)
- **Architecture**: MVC with Repository Pattern

---

## 👥 Authors

Hall Booking System Development Team

---

## 📄 License

This project is for educational purposes.

---

*Last Updated: March 2026*
