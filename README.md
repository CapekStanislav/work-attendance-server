# work-attendance-rest
The project is written as the bachelor work for the Faculty of the Informatics and Management University of Hradec Králové and is stil **in development**. The theme of the project is an attendance system. 

It's built upon REST architecture and expose entities in HAL format. It leverages the Spring Data Rest framework with an H2 database. The chosen database is only for development purposes.

## Client - React
For the client is used Reactjs library and for management global states and cache is used library React Query. The client application is written only by functional components and logic is separate in custom hooks. 

Run client in client directory run command:
```
npm start
```
## Server - Spring Boot Application

Data are stored in the H2 database. The database does not contain any default data. The server runs at URL localhost:8080/api/v1.To run the server application use the maven command or IDE.
```
mvnw spring-boot:run
```
