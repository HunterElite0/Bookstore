SQLite Version: This application recommends using SQLite version 3.45.2 or later.

# Project Overview:

This project aims to create a functional online bookstore application using a client-server architecture and socket programming with multithreading. Users can browse, search, borrow, and lend books through an interactive interface.

# Technical Specifications:

Client-Server Architecture: The application utilizes a client-server architecture for communication and data management. The server manages book inventory and user requests, while clients interact with the bookstore and each other.
Socket Programming: Java SE sockets are used to establish communication between the server and clients, facilitating request and response exchange.
Multithreading: The server implements multithreading to handle concurrent requests from multiple clients efficiently.
Features:

Server-Client Communication:

Java SE sockets enable communication between the server and clients.
Users' requests and server responses are handled effectively.
Book Inventory Management:

The server maintains a database (recommended: SQLite) of available books.
Book details include title, author, genre, price, quantity, and a list of current borrowers.
User Authentication:

Secure user access is ensured through login and registration mechanisms:
Login: Username and password are sent for verification.
Registration: Name, username, and password are sent for new user creation.
Error handling for login/registration:
Incorrect password: 401 Unauthorized error.
Non-existent username: 404 Not Found error.
Duplicate username: Custom error indicating username unavailability.
Browse and Search Books:

Users can browse the bookstore's catalog.
Search functionalities allow filtering by title, author, and genre.
Detailed book information can be viewed.
Add and Remove Books:

Users can add books they wish to lend, specifying details.
Removing books from the lending list is also supported.
Submit a Request:

Borrowers can submit borrowing requests to lenders.
Accepted requests establish a chat for communication regarding book exchange.
Accept/Reject Requests:

Borrower requests can be reviewed by lenders.
Users can accept or reject borrowing requests.
Users can act as both borrowers and lenders.
Request History:

Users can access their request history, viewing statuses (accepted, rejected, pending).
Library Statistics (Admin Only):

Admins can view overall library statistics:
Currently borrowed books.
Available books.
Accepted/rejected/pending requests.
Error Handling:

The application implements error handling mechanisms to address various scenarios, including invalid user inputs.
Getting Started:

# How to initialize and run the applicaion

Ensure you have Java SE Development Kit (JDK) installed.
Download the application source code.
Configure the database connection details in the server code (point to your SQLite database).
Run the app.java file to initialize the database.
Compile and run the server application.
Compile and run the client application on separate machines or terminals.

* NOTE:
Admin account credentials: admin admin
