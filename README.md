Cinema Network Management
This project is a Cinema Network Management System, designed to manage cinema operations across multiple locations. The system supports various functionalities like managing movie listings, handling customer service, and managing users with different roles.

Features
User Authentication: Supports different user roles such as Customer, Manager, Admin, and Customer Service.
Movie Management: Allows managers to manage movie listings, including adding, editing, and removing movies.
Customer Service: Provides tools for customer service representatives to handle customer inquiries and complaints.
Reporting: Generates reports on cinema operations for managers and administrators.
Getting Started
Prerequisites
Java 11 or higher
Maven
Git
Installation
Clone the repository:

bash
Copy code
git clone https://github.com/nayefzedane/CinemaNetworkManagement.git
Navigate to the project directory:

bash
Copy code
cd CinemaNetworkManagement
Build the project using Maven:

bash
Copy code
mvn clean install
Run the server:

bash
Copy code
mvn exec:java -Dexec.mainClass="il.cshaifasweng.OCSFMediatorExample.server.SimpleServer"
Run the client:

bash
Copy code
mvn exec:java -Dexec.mainClass="il.cshaifasweng.OCSFMediatorExample.client.App"
Usage
Login: Start the client, connect to the server, and log in with the appropriate credentials.
Navigate: Depending on the user role, the user will be directed to the appropriate dashboard.
Manage Movies: Managers can manage movie listings.
Handle Customer Service: Customer Service representatives can respond to customer inquiries.
Contributing
If you'd like to contribute, please fork the repository and use a feature branch. Pull requests are welcome.

License
This project is open-source and available under the MIT License.
