# sm0413
Tool Rental Application  
The application takes four required parameters: **ToolCode**, **CheckoutDate**, **RentalDays**, and **Discount**  
Sample CURL for calling locally:  
curl --location --request GET 'localhost:8080/rental/tool?ToolCode=JAKD&CheckoutDate=07/02/2020&RentalDays=5&Discount=10'
