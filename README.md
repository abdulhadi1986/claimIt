# `CLAIMIT` The Lost And Found application

## Business purpose 
This application provide functionalities for: <br>

System Admin (Must login): 
- Add items
- View filed claims against items <br>

System User (Must login):
- File claims against items <br>

All users
- View items

## Functional Overview
1. ***Upload & Store Data:*** <br>
   - The application extracts and store the following information
   from the uploaded file:
   ***LostItem: ItemName, Quantity, Place***
   - The contents of the LostItem may be in any order.
   - Supported File types are : MsWord .docx, .pdf , and .txt
   - When item is added and in the DB there exists item with the same description and place then only quantity is updated
2. ***Read LostItems:*** read the saved Lost Items
3. ***Claim LostItem Data and save:*** users can claim the lost item.<br>
4. ***Retrieved LostItems claimed by people***: Admin can read all submitted claims for the Lost items and Users

### Out of scope 
- User-Data: call external service to get user data 
- User-Authentication: User login and get auth-Token from external service. <br> we only validate if that user is authenticated and authorized. 
## Technical Overview

### Technology Stack 
- Java 25
- Spring Boot 4.*
- PostgreSQL Database

### Local Development
#### To run the application locally:
1. Run the class: src/main/java/com/foundIt/claimIt/local/db/PostgresLauncher.java
<br> This will start the PostgreSQL database locally.<br>
2. Run the application: src/main/java/com/foundIt/claimIt/ClaimItApplication.java

***Note:*** Component Tests and @SpringBootTest classes will fail if the database is not running

#### Local User Authentication (will be replaced by external service on production)
In the package src/main/java/com/foundIt/claimIt/local** 
There are services that authenticate user and issue JWT token.
#### Local User Data (will be replaced by external service on production)
in the package src/main/java/com/foundIt/claimIt/local**
There are services that manage user data. in our service we only need userId and userName


### Local Testing
run the application locally (Application port 8080 , database port 5432)
Use postman collection : ClaimIT.postman_collection.json

For endpoints which require BearerToken with role ADMIN : call /login with one of the following credentials: 
- role_admin1@claimit.com password: role_admin1123
- role_admin2@claimit.com password: role_admin2123

For endpoints which require BearerToken with role USER : call /login with one of the following credentials:
- role_user1@claimit.com password: role_user1123
- role_user2@claimit.com password: role_user2123

For uploading file to add items:
- you can use one of the files in : src/test/resources/test-files
- or use your own.

Steps : 
1. Login as admin and use the token to:
2. Upload file to add items.
3. As normal user without Token Get items 
4. Login as user and use the toke to:
5. Submit Claim 
6. Use admin token to get submitted claims.
7. Feel free to try different input and explore the possible success/error responses
