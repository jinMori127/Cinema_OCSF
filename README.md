# Cinema Site Project

## Overview
This project was developed as part of the Software Engineering course. It involves creating a management system for a movie theater chain called **"LunaAura."** The system enables users to browse movies, purchase tickets, buy streaming links for home viewing, and improve customer service.

## Features
The system supports multiple user roles, including:
- **Network Manager**
- **Branch Manager**
- **Customer Service Worker**
- **Content Manager**
- **Customers**
- **Guests**

---

## Guest & Customer Operations

### 1. Movie Browsing
- View a list of movies currently showing in theaters.
- View upcoming movies.
- Access details such as the movie’s name, producer, main actors, summary, and an image.

![Recording 2024-09-29 010005](https://github.com/user-attachments/assets/b31772db-f89b-49db-8e00-7eab4f787b57)


### 2. Ticket Purchase
- Purchase tickets for movies shown in theaters.
- Select specific seats from a map of the theater hall.
- Receive a digital ticket via email with movie details, theater location, and seating information.

![purchaseticket](https://github.com/user-attachments/assets/be65ca1a-274a-4834-b47c-8bff3c82c594)

Email Recived:

![image](https://github.com/user-attachments/assets/024100d7-ac81-49f7-a75b-87a0083f8691)



### 3. Home Viewing Links
- Purchase links for limited-time access to movies for home viewing.
- Receive a streaming link via email with the movie’s name and link.

![homwlinkpurchase](https://github.com/user-attachments/assets/6cee8add-7ddc-4eae-848f-809cc1fb5afc)

Email Recived:

![image](https://github.com/user-attachments/assets/b03aae95-b6ff-4b28-8aa2-b54d8cecb9b2)



### 4. Ticket Pass
- Purchase a pass for 20 movie tickets at a discounted rate.
- Use the pass at any theater in the chain, subject to seat availability.

![MultiPurchase](https://github.com/user-attachments/assets/e59774c6-4197-47a5-8bcc-9ec515474e18)

Purchase using MultiTicket:

![purchaselinkmulti](https://github.com/user-attachments/assets/733b8090-6496-4ff0-802f-03ef68296e56)






###  When a guest makes a purchase, they are converted to a customer.


---

## Customer Operations

### 1. Customer Identification
- Customers can be identified by their ID.
![LOGINUSER (2)](https://github.com/user-attachments/assets/5c888371-e5ad-490f-bdde-5cd8d214b0a2)


### 2. Customer Service
- Submit complaints via the system.
- Possible financial compensation for valid complaints.
  
![Complain](https://github.com/user-attachments/assets/e3dd70ca-6686-4d98-b721-a41436f53594)


### 3. Purchase Cancellation
- Cancel movie tickets up to three hours before the showtime for a full refund.
- Cancel movie tickets between three hours and one hour before showtime for a 50% refund.
- Cancel home viewing packages up to one hour before the link becomes active for a 50% refund.

![cancel purchase](https://github.com/user-attachments/assets/7764fe9d-43c1-4f4a-938e-9492c8093817)


---






### Employee Access:
- Employees log in with a username and password to access the full system.

![WORKERLOGIN](https://github.com/user-attachments/assets/5fa71399-19a0-46b6-a467-c9761f1b7ee2)



---

## Branch Manager Operations
### 1. Operational Tracking
- Monthly reports on ticket sales by theater, pass sales, and home viewing package sales.
- Complaint status reports available to both theater and network managers.

---

## Network Manager Operations

### 1. Price Approval
- Review and approve or deny pricing changes proposed by the content manager.

### 2. Operational Tracking
- Generate monthly reports on ticket sales categorized by theater, as well as pass sales and home viewing package sales.
- Access complaint status reports, which are available to both theater and network managers for effective oversight.


---
## Content Manager

### 1. Edit Movie Details
- Update and modify the details of movies, including titles, descriptions, and cast information.

![EDITINGDETAILS](https://github.com/user-attachments/assets/2d361139-0025-49b0-bad3-95b817608caf)


### 2. Price Modification
- Adjust the prices of movies, which requires approval from the network manager before implementation.

![EditPrice](https://github.com/user-attachments/assets/9a645fa4-076b-46d1-b8ba-7b3eef1915b3)



---

## Customer Service Operations

### 1. Handling Complaints
- Customer service representatives are responsible for addressing complaints from customers.

### 2. Compensation Decisions
- Customer service representatives have the authority to decide on financial compensation for customers based on the validity of the complaint. 
- Compensation includes refunds.

![RespondComplain](https://github.com/user-attachments/assets/d90c4597-e9e8-498a-b89b-829d03c809e6)

---

## Development Details
The system is to be developed in Java, with a distributed architecture. The first version will be operational via local network (LAN) and will not include an internet-based user interface. The project will be implemented using a client-server architecture with a relational database backend.

---

## Team Collaboration
The project is to be developed collaboratively by all group members.


### Setup Instructions

1. **Clone the repository:**
   ```bash
   git clone https://github.com/jinMori127/Cinema_OCSF.git
   ```

2. **Navigate to the project directory:**
   ```bash
   cd <project-directory>
   ```

3. **Start the server:**
   ```bash
   java -jar <server-file>.jar
   ```
   - **Note:** Insert your SQL password and host for the HTTP server configuration.

4. **Run the client:**

5. **Choose your host and IP:**
   - If running locally, keep it as the default:
     ```bash
     localhost:3000
     ```
   - If using ngrok, set the host and IP the same as your ngrok instance.
     For example:
     ```
     Host: 0.tcp.eu.ngrok.io
     Port: 10160
     ```
6. **You're now ready to go!**
---

---

## Contributing
- Fork the repository and create a new branch for your feature or bugfix.
- Commit your changes and open a pull request for review.

---

## Contact
**email**: samii.serhan@gmail.com  
**linkedin**: [Sami-serhan](https://www.linkedin.com/in/sami-serhan-101906311/)
