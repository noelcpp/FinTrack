# FinTrack — Personal Finance Management System

A desktop-based personal finance management application built with **Java Swing and SQLite**. FinTrack is designed to help users record financial transactions, organize income and expenses, manage budgets, and understand their financial activity through a graphical interface.

> **Project type:** Academic project — Object-Oriented Programming with Java  
> **Application:** Desktop GUI  
> **Status:** Under development

---

## Features

- **Dashboard** — View a summary of financial activity and recent transactions.
- **Transaction management** — Record and manage income and expenses.
- **Category management** — Organize transactions into categories.
- **Account management** — Maintain financial account records.
- **Budget management** — Create and manage budgets.
- **Reports and insights** — Review financial activity and related summaries.
- **Desktop interface** — Graphical user interface built using Java Swing.
- **Local database** — Store application data using SQLite.

*Feature availability may depend on the current implementation.*

## Screenshots

Screenshots of the application will be added here.

<!-- Add screenshots after uploading them to a screenshots/ directory.
Example:
![Dashboard](screenshots/dashboard.png)
![Transactions](screenshots/transactions.png)
-->

---

## Technology Stack

| Technology | Purpose |
|---|---|
| Java | Core programming language |
| Java Swing | Desktop graphical user interface |
| SQLite | Local relational database |
| SQLite JDBC | Java-to-SQLite connectivity |
| Apache Maven | Dependency management and build automation |
| JUnit 5 | Unit testing |
| Git and GitHub | Version control and source code hosting |

## Project Structure

```text
FinTrack/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/fintrack/
│   │   │       ├── Main.java
│   │   │       ├── app/
│   │   │       ├── model/
│   │   │       ├── dao/
│   │   │       ├── service/
│   │   │       ├── ui/
│   │   │       ├── database/
│   │   │       ├── util/
│   │   │       └── exception/
│   │   └── resources/
│   └── test/
│       └── java/
│           └── com/fintrack/
├── data/                 # Local application data, if configured
├── pom.xml
├── .gitignore
└── README.md
```

The structure above illustrates the intended organization. Some directories may be absent if they have not yet been implemented. Maven generates the `target/` directory during builds; it is not part of the source code.

### Architecture

FinTrack follows a layered organization to separate responsibilities:

- **Model:** Represents application entities such as transactions, categories, accounts, and budgets.
- **DAO (Data Access Object):** Handles database operations.
- **Service:** Contains application logic and validation workflows.
- **UI:** Implements screens, tables, dialogs, and navigation using Swing.
- **Database:** Manages SQLite connectivity and database initialization.
- **Utilities and exceptions:** Provide reusable formatting, validation, date handling, and error handling.

This separation supports maintainability, testing, and the application of object-oriented programming principles.

---

## Requirements

To build and run the application from source, you need:

- A compatible Java Development Kit (JDK).
- Apache Maven.
- Git (optional, for cloning the repository).

The current Maven configuration targets **Java 27**. Use a compatible JDK for the current build, or change the compiler release to a supported Java version after verifying compatibility.

## Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/YOUR_USERNAME/FinTrack.git
cd FinTrack
```

Replace `YOUR_USERNAME` with your GitHub username and adjust the repository URL if you chose a different name.

### 2. Build the project

```bash
mvn clean package
```

Maven downloads the required dependencies, compiles the application, runs configured tests, and packages the application.

### 3. Run the application from source

```bash
mvn exec:java -Dexec.mainClass="com.fintrack.Main"
```

### 4. Run the packaged JAR

The Maven Shade Plugin is configured to create a JAR containing the application and its dependencies.

```bash
java -jar target/fintrack-1.0-SNAPSHOT.jar
```

The generated JAR filename depends on the Maven artifact ID and version. A compatible Java runtime must be installed to run this JAR.

---

## Running Tests

Run the test suite with:

```bash
mvn test
```

To clean previous build output, execute the tests, and package the application:

```bash
mvn clean package
```

Tests are maintained under `src/test/java/`.

## Database and Data Privacy

FinTrack uses SQLite for local data persistence.

- Database files should remain local and should not be committed if they contain personal financial information.
- Temporary SQLite files, including `-wal` and `-shm` files, should not be committed.
- The `data/` directory may be used for local databases, depending on the application's database configuration.
- Database initialization and schema management are handled by the application's database layer.

**Important:** Confirm the actual database location in `DatabaseConnection.java` before distributing the application. For a packaged desktop application, a writable per-user application-data directory is preferable to a location inside the installation directory.

---

## Building for Distribution

The shaded JAR can be shared with users who have a compatible Java runtime installed.

For a more convenient installation experience, platform-specific packages can be created using Java's `jpackage` tool:

- Windows: `.exe` or `.msi`
- macOS: `.dmg` or `.pkg`
- Linux: platform-appropriate package or application image

Native packages must be built for their target operating systems. Bundling a Java runtime can remove the need for users to install Java separately.

---

## Object-Oriented Programming Concepts

FinTrack is intended to demonstrate practical application of object-oriented programming concepts, including:

- **Encapsulation:** Keeping data and behavior organized within classes.
- **Abstraction:** Separating database operations and business logic from the user interface.
- **Inheritance and polymorphism:** Where appropriate in the UI, model, or service implementations.
- **Composition:** Connecting application components through well-defined responsibilities.
- **Exception handling:** Managing validation and database errors.
- **Separation of concerns:** Organizing code into model, DAO, service, UI, and database layers.

The exact use of each principle should be evaluated against the implementation.

## Future Improvements

Potential improvements include:

- Exporting reports to CSV or PDF.
- Recurring transactions.
- Improved reporting and date-range filtering.
- Data backup and restore.
- Enhanced input validation and error handling.
- Cross-platform installers with a bundled Java runtime.
- Additional automated tests and documentation.

## Contributing

This is an academic project. Suggestions, bug reports, and constructive feedback are welcome.

1. Fork the repository.
2. Create a feature branch.
3. Make your changes.
4. Run the test suite.
5. Submit a pull request with a clear description.

## License

No license has been specified yet. Unless a license is added to the repository, others should not assume they have permission to redistribute or modify the project.

## Author

**Noel Clement**

GitHub: [YOUR_GITHUB_USERNAME](https://github.com/YOUR_GITHUB_USERNAME)

---

*FinTrack — Organize your finances, one transaction at a time.*
