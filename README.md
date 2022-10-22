# Invoice API Application

## Invoice API - Challenge
   As a user I would like to import the invoice and generate the invoice PDF. Below are the detail requirements for the microservice(s) to be created.

## Sample invoice to be imported as json 
	{
	  "InvoiceId":"12345678",
	  "Customer" : {
		"Name": "Jim",
		 "Email": "asd@gmail.com",
		"Id": "1234"
	  },
	  "Products":[
		 {
		  "ProductId":"12345",
		  "Name":"Internet Package",
		  "Price": 100,
		  "Discount":10
		},
	  {
		  "ProductId":"45678",
		  "Name":"Test",
		  "Price": 100,
		  "Discount":10
		}],
	  }

## R1: Rest end point should be provided to import the invoice 
	Below are the validation and business rules that must be implemented

	•	InvoiceId, Customer (All child elements) , Net Price, Product (All child elements) are mandatory and while importing the invoice if they are missing then we should reject the import 
	•	There can be many products in the same invoice json but no two products must have same productid. 
	•	One product is mandatory in the invoice.
	•	If invoiceId is already imported then duplicate Id should not be imported.
	•	Invoice Id should be of length 8 and customer id should be of length 4 and product id should be of length 5.
	•	One customer can have many invoices but one invoice id will belong to one customer

## R2: Rest End point to view the invoice by invoiceId.
	As a user I would like to view the invoice details as below based upon invoiceId.
	For example, if I query InvoiceId: 12345678 should return the below response

	{
	  "InvoiceId":"12345678",
	  "Customer" : {
		"Name": "Jim",
		 "Email": "asd@gmail.com",
		"Id": "1234"
	  },
	  "Products":[
		 {
		  "ProductId":"12345",
		  "Name":"Internet Package",
		  "Price": 100,
		  "Discount":10
		},
	  {
		  "ProductId":"45678",
		  "Name":"Test",
		  "Price": 100,
		  "Discount":10
		}],
	  }

## R3: Rest endpoint to delete the invoice by invoiceId.
	As a user I should be able to delete the invoice by invoiceId.

## R4: Rest endpoint to update the invoice
	As a user I would like to update the invoice by invoice Id but only products can be updated.
	For example, if Invoice has product with Id 12345 the with below request I should be able to update the product details
	{
	  "InvoiceId":"12345678",
	"Products":[
		 {
		  "ProductId":"55555",
		  "Name":"Mobile Sim",
		  "Price": 100,
		  "Discount":10
		}
	]
	}
	For the invoice if product id is found then update the product else add the product.


## R5: As a user there should a possibility to configure discount based upon productid and customerId while generating the pdf.
	For example, if we configure productids [12345,5555] with discount 10 euro then a 10 euro discount must be applied on the product while generating the pdf bill.
	Same for Customer Ids customerIds[1234,3333] with 20 euro discount. This discount should be applicable only while generating the pdf. 


## R6: Rest end point to generate the PDF by invoice ID.
	As a user I would like to generate invoice pdf for example as Below

	Dear <CustomerName>,
	Below are you invoice details
	<Name of the product>: <Price> €
	<Name of the product>: <Price> €
	<Name of the product>: <Price> €

	<Total>: <Price> €



## Technical Requirements:
	The code should be of highest quality and should be deployable in production. Use any open source api to generate pdf.


## Assignment:
	-	Please highlight design and conceptual issues of those requirements
	-	Please add to all requirements how the response of the service(s) look like
	-	Write a list of all assumptions you took to come up with the responses
	-	Create some sample code using Spring Boot (there is no need to cover all requirements as time is short)

   
   
## Design Appraoch 
    1. Identified 5 REST api endpoints - 
        1. importInvoice, 
        2. viewInvoice
        3. deleteInvoice
		4. updateInvoice
		5. generateInvoicePdf
    2. Model class for Customer with following attributes:
        Name: String
		Email: String
		Id: String
	3. Model class for Products with following attributes:
        ProductId: String
		Name: String
		Price: Long
		Discount: Double
	4. Model class for Invoice with following attributes:
        InvoiceId: String
		ProductIds: List<String>
		customerId: String
		isImported: Boolean
    5. Used H2 in-memory DB as a storage
    6. validation and business rules are enforced using Validator  