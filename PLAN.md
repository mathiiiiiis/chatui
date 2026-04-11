# PLAN.md - Terminal Chat System

## 1. Goal
Build a real-time terminal chat system with:
- Java backend (TCP server)
- Go client using Bubble Tea TUI
- Multi-user messaging
- Room support (later phase)

---

## 2. Backend (Java) - Phase 1

### 2.1 Research
- TCP sockets (ServerSocket, Socket)
- Blocking I/O behavior
- Thread-per-client model
- Basic message formats (plain text → JSON later)

---

### 2.2 Minimal Server
- Create ServerSocket on port
- Accept client connections
- Read incoming messages
- Print messages to console (debug)

---

### 2.3 Multi-client support
- Create ClientHandler class
- Run each client in a separate thread
- Maintain list of active clients

---

### 2.4 Broadcasting
- Implement message broadcast to all connected clients
- Exclude sender from echo (optional)

---

### 2.5 Connection handling
- Detect client disconnects
- Remove dead clients from list
- Prevent server crashes on I/O errors

---

## 3. Protocol Design

### Phase 1 (simple)
- Raw text messages

### Phase 2 (recommended)
Format:
TYPE|USER|ROOM|CONTENT

or JSON:
{
  "type": "message",
  "user": "",
  "room": "",
  "content": ""
}

---

## 4. Rooms System (after core works)
- Map rooms → list of clients
- Route messages only inside room
- Add join/leave room actions

---

## 5. Client (Go + Bubble Tea)

### 5.1 Research
- Bubble Tea architecture (Model / Update / View)
- Goroutines for network listening
- TCP client (net.Dial)

---

### 5.2 Core Client
- Connect to Java server via TCP
- Send messages from input
- Receive messages asynchronously

---

### 5.3 UI
- Message view (scrollable)
- Input field
- Basic layout (chat window style)

---

### 5.4 Async handling
- Separate goroutine for receiving messages
- Send updates to Bubble Tea via messages/channels

---

## 6. Stability Improvements (later)
- Thread pool instead of raw threads (Java)
- Logging system
- Better error handling
- Heartbeat/ping system (optional)

---

## 7. Future Extensions
- Authentication (username system)
- Message history
- Encryption layer (AES optional)
- Private messages (DMs)
- Persistence (database)

---

## 8. Order of Implementation

1. Java TCP server (single client)
2. Multiple clients
3. Broadcast system
4. Basic Go TCP client
5. Bubble Tea UI
6. Rooms
7. Protocol upgrade (JSON)
8. Stability improvements
