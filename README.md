# Hotel Management System

## Technologies Used
- **Spring Boot** (main platform)
- **Spring Cloud Netflix Eureka** (service discovery)
- **API Gateway** (request management)
- **Lombok, MapStruct** (for DTO mapping)

## Application Launch

### Prerequisites
- JDK 17 or higher
- Maven 3.6+
- Any IDE (IntelliJ IDEA, Eclipse, VS Code)

### Service Launch Procedure
1. Launch Eureka Server
```bash
cd eureka-server
mvn spring-boot:run
```

2. Launch Hotel Management Service
```bash
cd hotel-service
mvn spring-boot:run
```

3. Launch Booking Service
```bash
cd booking-service
mvn spring-boot:run
```

4. Starting API Gateway
```bash
cd api-gateway
mvn spring-boot:run
```

**Important:** Services must be started in the specified order for correct registration in Eureka.

## Security

### Authentication and Authorization
- **JWT tokens** (expiration: 1 hour)
- **Roles:** USER, ADMIN
- Each microservice independently validates JWT as a Resource Server.

## Token Obtaining Example

### Registration
**POST** `/api/bookings/user/register`
```json
{
"username": "user@example.com",
"password": "password123",
"role": "USER"
}
```

### Authorization
**POST** `/api/bookings/user/auth`
```json
{
"username": "user@example.com",
"password": "password123"
}
```

## API Endpoints

### API Gateway (port 8080)
All requests go through the Gateway with the `/api` prefix.

### Booking Service
- **POST** `/api/bookings/booking` **USER** - Create a booking
- **GET** `/api/bookings/bookings` **USER** - Booking history
- **GET** `/api/bookings/booking/{id}` **USER** - Get a booking
- **DELETE** `/api/bookings/booking/{id}` **USER** - Cancel a booking
- **POST** `/api/bookings/user/register` **PUBLIC** - Register
- **POST** `/api/bookings/user/auth` **PUBLIC** - Authorize
- **POST** `/api/bookings/user` **ADMIN** - Create a user
- **PATCH** `/api/bookings/user` **ADMIN** - Update a user
- **DELETE** `/api/bookings/user` **ADMIN** - Delete user

### Hotel Management Service
- **GET** `/api/hotels/hotels` **USER** - List of hotels
- **POST** `/api/hotels/hotels` **ADMIN** - Add hotel
- **GET** `/api/hotels/rooms` **USER** - Available rooms
- **GET** `/api/hotels/rooms/recommend` **USER** - Recommended rooms
- **POST** `/api/hotels/rooms` **ADMIN** - Add room
- **POST** `/api/hotels/rooms/{id}/confirm-availability` **INTERNAL** - Confirm availability
- **POST** `/api/hotels/rooms/{id}/release` **INTERNAL** - Unlocking

## Database Structure

### Booking Service (H2 in-memory)
#### Users Table:
- `id` (PK)
- `username` (UNIQUE)
- `password` (encrypted)
- `role` (USER/ADMIN)

#### Bookings Table:
- `id` (PK)
- `user_id` (FK)
- `room_id`
- `start_date`
- `end_date`
- `status` (PENDING/CONFIRMED/CANCELLED)
- `created_at`
- `correlation_id` (for idempotency)

### Hotel Management Service (H2 in-memory)
#### Hotels Table:
- `id` (PK)
- `name`
- `address`

#### Rooms Table:
- `id` (PK)
- `hotel_id` (FK)
- `number` (room number)
- `available` (availability for booking)
- `times_booked` (booking counter for uniform occupancy)

## Algorithms and Features
