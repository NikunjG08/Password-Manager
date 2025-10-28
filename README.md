# Password-Manager
Built an encrypted password manager that securely stores all credentials locally, ensuring complete user privacy without reliance on cloud servers. The app features categorized vaults for seperate accounts. 

# Features

- Secure Login & Registration** using hashed passwords (BCrypt)
- Local Encryption for stored credentials (AES via `CryptoUtils.java`)
- Password Strength Checker (`StrengthChecker.java`)
- Password Generator with customizable rules (symbols, numbers, etc.)
- Modern GUI built with Swing (Dashboard, Add/Edit forms, etc.)
- Local MySQL Database for user and credential storage
- Lightweight & Offline – all data stored securely on your device

---

Setup Instructions

Requirements
- Java **17+**
- MySQL **8.0+**
- IDE such as IntelliJ IDEA, Eclipse, or VS Code with Java extensions
- (Optional) Maven for dependency management

---


Database Setup

Open MySQL and run:

CREATE DATABASE password;
USE password;

CREATE TABLE users (
  id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50) UNIQUE NOT NULL,
  hashed_password VARCHAR(255) NOT NULL,
  email VARCHAR(100) UNIQUE NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE password_entries (
  id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT NOT NULL,
  service_name VARCHAR(100) NOT NULL,
  encrypted_username TEXT NOT NULL,
  encrypted_password TEXT NOT NULL,
  notes TEXT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id)
);

Configure Database Credentials:

Edit src/PasswordManager/DBConnection.java to match your local or remote MySQL setup:

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/password";
    private static final String USER = "your_mysql_username";
    private static final String PASS = "your_mysql_password";
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}

Additionally, for each of the java files, wherever the password, set as 123456 right now, exists, should be replaced to your_mysql_password.

Install Dependencies:
If using Maven, include these in your pom.xml
<dependency>
  <groupId>mysql</groupId>
  <artifactId>mysql-connector-j</artifactId>
  <version>8.0.33</version>
</dependency>

<dependency>
  <groupId>org.mindrot</groupId>
  <artifactId>jbcrypt</artifactId>
  <version>0.4</version>
</dependency>

The project can be run after the files are complied.
