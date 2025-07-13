# Mailing-App-On-Console
A mailing application that employs a multi-threaded server to concurrently handle multiple users, implemented using Java socket programming with a custom, self-defined protocol. The backend communicates with a PostgreSQL database through JDBC.

Users must first log in to access the application's functionalities. There are two types of users: non-admin users and admin users.
Non-admin users can send emails, view their inbox and outbox, and log out. In addition to these capabilities, admin users have access to user management features, including creating, viewing, updating, and deleting users (CRUD operations).
