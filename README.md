# ToolRentalApplication(sm0413)
  
To get started using the application after cloning the repo take the following steps:  
1. Open a terminal in the project directory  
2. Run the following commands:   
    1. ./mvnw clean package
    2. ./mvnw spring-boot:run
3. You are ready to start calling the service using requests like the one below  
**curl --location --request GET "localhost:8080/rental/tool?ToolCode=JAKD&CheckoutDate=07/02/2020&RentalDays=5&Discount=10"**
4. To shut down the application use the request below  
   **curl -X POST localhost:8080/actuator/shutdown**

The application takes four required parameters: **ToolCode**, **CheckoutDate**, **RentalDays**, and **Discount**  
**CheckoutDate** must be in the following format: MM/DD/YYYY