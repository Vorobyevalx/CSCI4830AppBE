# Problem Statement and Project Proposal
## SecureBank Hub
**Aleksey Vorobyev, Ahmed Hasan, Saleh Hayati**  
*Sept 18 2025*

## Abstract
**Summary of Project (maximum 200 words)**

This project focuses on developing a robust backend infrastructure for a secure banking application with emphasis on API security, fraud detection capabilities, and reliable data management. The backend system will serve as the foundation for user authentication, transaction processing, and real-time fraud monitoring while maintaining strict security protocols and data integrity standards.

## Description

Current banking applications face significant security challenges in protecting user financial data and detecting fraudulent activities in real-time. Many existing systems struggle with scalable backend architectures that can handle high transaction volumes while maintaining security and preventing unauthorized access. Traditional banking backends often lack sophisticated fraud detection mechanisms that can identify suspicious patterns and alert users promptly.

To address these critical issues, my role in this project involves developing a comprehensive backend system that prioritizes security, scalability, and fraud prevention. The backend will implement secure API endpoints using industry-standard authentication protocols, including JWT token-based authentication and role-based access control. This system will manage all database interactions, ensuring data integrity and implementing proper encryption for sensitive financial information.

The backend architecture will feature loosely coupled design principles, operating as a separate repository from the frontend to promote modularity and maintainability. RESTful API endpoints will handle all communication between the frontend and backend, including user authentication, account balance retrieval, transaction history queries, and settings management. Each endpoint will implement comprehensive input validation, rate limiting, and security headers to prevent common attacks such as SQL injection and cross-site scripting.

A key innovation of this backend system is the integration of a real-time fraud detection service. This component will analyze transaction patterns, monitor for unusual account activity, and flag potentially fraudulent transactions using predefined algorithms and machine learning techniques. The fraud detection system will evaluate factors such as transaction amounts, frequency, location data, and historical user behavior to identify anomalies that warrant investigation.

The technical implementation will utilize Java Spring Boot for robust API development, providing built-in security features and extensive middleware support. Database connectivity will be managed through JPA/Hibernate for production environments using SQL databases, with H2 database integration for local development and testing phases. AWS cloud services will be leveraged for scalable deployment, including EC2 for application hosting and RDS for managed database services.

Security implementation will include bcrypt password hashing, secure session management, HTTPS enforcement, and comprehensive audit logging for all financial transactions. The system will also implement proper error handling that prevents information leakage while providing meaningful feedback for debugging and user experience optimization.

For local development, the backend will be configured to run independently, allowing frontend developers to work simultaneously without dependencies. The fraud detection functionality will initially focus on rule-based detection algorithms, with the potential for future machine learning integration as the project evolves and more transaction data becomes available for training purposes.