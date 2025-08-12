# Task Manager Demo

## Testing Guide

### Testing Flow Overview

1. Create a user
2. Create a task
3. Assign the user to the task
4. Search for the task
5. Change the task status
6. Search again to verify the status change
7. Edit the task (description and deadline)
8. Delete the task
9. Delete the user

### Prerequisites

- The application should be running on `http://localhost:8080`
- install `jq` for better json display

### Step-by-Step Testing

#### 1. Create a User

First, create a user that we'll assign to tasks later.

```bash
curl -i -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Alice",
    "lastName": "Smith",
    "email": "Alice.Smith@example.com"
  }'
```

```bash
curl -i -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Bob",
    "lastName": "Nakamoto",
    "email": "bob@example.com"
  }'
```

#### 2. Search for Users

Verify the user was created successfully.

```bash
curl GET "http://localhost:8080/api/users" | jq
```

### 3. Create a Task

Create a task and assign the previously created user to it.

```bash
curl -i -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Fix bugs",
    "description": "Fix everything and then drop database",
    "deadline": "2025-09-01T23:59:59",
    "users": ["USER_ID_HERE"]
  }'
```

### 4. Search for Tasks

Search for all tasks to verify the task was created.

```bash
curl GET "http://localhost:8080/api/tasks" | jq
```
Or:

```bash
curl GET "http://localhost:8080/api/tasks?title=bugs" | jq
```

### 5. Assign Additional Users to Task (Optional)

Assign more users to the existing task (first you need to create one more user).

```bash
curl -X PATCH http://localhost:8080/api/tasks/TASK_ID_HERE/assign \
  -H "Content-Type: application/json" \
  -d '{
    "userIds": ["USER_ID_HERE"]
  }'
```

### 6. Change Task Status

```bash
curl -X PATCH http://localhost:8080/api/tasks/TASK_ID_HERE/status \
  -H "Content-Type: application/json" \
  -d '{
    "status": "Completed"
  }'
```

### 7. Search for Tasks Again

```bash
curl GET "http://localhost:8080/api/tasks?taskStatus=Completed" | jq
```

### 8. Edit Task

```bash
curl -i -X PUT http://localhost:8080/api/tasks/TASK_ID_HERE \
  -H "Content-Type: application/json" \
  -d '{
    "description": "Learn some SQL",
    "deadline": "2026-01-15T18:00:00"
  }'
```

### 9. Verify Task Update

```bash
curl -X GET "http://localhost:8080/api/tasks/TASK_ID_HERE"
```

### 10. Delete the Task

```bash
curl -i -X DELETE http://localhost:8080/api/tasks/f0ce932a-92eb-4b7c-9f00-3ad73f5b5b0f
```

### 11. Verify Task Deletion

```bash
curl -X GET "http://localhost:8080/api/tasks" | jq
```

### 12. Delete the User

```bash
curl -i -X DELETE http://localhost:8080/api/users/USER_ID_HERE
```

### 13. Verify User Deletion


```bash
curl -X GET "http://localhost:8080/api/users"
```

### Sort functionality examples

```bash

# Get 1st page with 10 items, sorted by title
curl -X GET "http://localhost:8080/api/tasks?page=0&size=10&sort=title,asc"
```

## Available Task Statuses

- `New`
- `Active`
- `Completed`

### Required functionality for demo project:
- [x] domain models: User, Task
- [x] task can be assigned to more than 1 user
- [ ] task API: 
  - [ ] search with filter and sort
  - [ ] adding
  - [ ] editing
  - [ ] deleting
  - [ ] status change
  - [ ] user assigning
- [ ] user API:
  - [ ] search with filter and sort
  - [ ] adding
  - [ ] deleting

### other todos:
- [ ] ci
- [ ] todos with missing pieces