package Booking_Project;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.github.javafaker.Faker;
import com.github.javafaker.service.FakeValuesService;
import com.github.javafaker.service.RandomService;
import org.junit.Assert;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.RestAssured.*;
import io.restassured.matcher.RestAssuredMatchers.*;
import org.hamcrest.Matchers.*;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.testng.AssertJUnit.assertEquals;


@Test
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Test_Get {
    Faker faker = new Faker();

    // Generate a number between 1(inclusive) to 999 (exclusive)
    public int randomNum = faker.number().numberBetween(1, 999);
    public  String token = "";
    public  String bookingId = "";
    public  String BookingFirstName = ""+randomNum;
    public  String BookingLastName = ""+randomNum;
    public  String CheckinDate = "";
    public  String CheckOutDate = "";
    public String updatedFirstName = "Update_Test"+randomNum;
    public String updatedLastName = "Update_Book"+randomNum;
    public String updatedTotalPrice = "220";
    public String updatedDepositPaidFlag = "False";
    public String updatedCheckInDate = "2021-10-03";
    public String updatedCheckOutDate = "2021-10-04";
    public String updatedAdditionalNeeds = "Dinner";





    // Performing a simple check if the API End point is up and running
    public void Test01_APIHealthCheck()
    {
        System.out.println("*****************************************************************");
        System.out.println("Performing a simple check if the API End point is up and running");
        System.out.println("*****************************************************************");
        Response res = given()
                .when()
                .get("https://restful-booker.herokuapp.com/ping");
        Assert.assertEquals(res.getStatusCode(),201);
        System.out.println("URL is up and running fine");
    }

    //Creating an authentication token, to use in future requests
    public void Test02_getAuthCode() {
        System.out.println("*****************************************************************");
        System.out.println("Retrieving an Authentication code");
        System.out.println("*****************************************************************");
        RestAssured.baseURI  = "https://restful-booker.herokuapp.com/auth";
        Response res = given()
                .contentType("application/json")
                .body("{\"username\":\"admin\" , \"password\":\"password123\"}")
                .when()
                .post("");
        Assert.assertEquals(res.getStatusCode(),200);
        String token = res.getBody().asString();
        System.out.println("Authentication Token :" + token);
    }

    public void Test03_CreateBooking()
    {
        System.out.println("*****************************************************************");
        System.out.println("Create a new Booking");
        System.out.println("*****************************************************************");
        RestAssured.baseURI  = "https://restful-booker.herokuapp.com/booking";
        Response res = given()
                .header("Authorization", "Bearer "+token)
                .accept("application/json")
                .contentType("application/json")
                .body("{\"firstname\": \"Test\"  , \"lastname\":\"Booking\" , \"totalprice\":\"110\" , \"depositpaid\":\"true\" , \"bookingdates\":{\"checkin\":\"2021-09-03\" , \"checkout\":\"2021-09-04\"} , \"additionalneeds\":\"Breakfast\"}")
                 .when()
                .post("");
        JsonPath jsonPathEvaluator = res.jsonPath();
        Assert.assertEquals(res.getStatusCode(),200);
        System.out.println("Booking Created successfully!!" +  "/n" +  "Details are as below");
        String body = res.getBody().asString();
        bookingId = jsonPathEvaluator.get("bookingid").toString();
        BookingFirstName = jsonPathEvaluator.get("booking.firstname").toString();
        BookingLastName = jsonPathEvaluator.get("booking.lastname").toString();
        CheckinDate = jsonPathEvaluator.get("booking.bookingdates.checkin").toString();
        CheckOutDate = jsonPathEvaluator.get("booking.bookingdates.checkout").toString();
        System.out.println("Booking ID : " + jsonPathEvaluator.get("bookingid"));
        System.out.println("First name : " + jsonPathEvaluator.get("booking.firstname"));
        System.out.println("Last name : " + jsonPathEvaluator.get("booking.lastname"));
        System.out.println("Total Price : " + jsonPathEvaluator.get("booking.totalprice"));
        System.out.println("Is Deposit Paid : " + jsonPathEvaluator.get("booking.depositpaid"));
        System.out.println("CheckIn Date : " + jsonPathEvaluator.get("booking.bookingdates.checkin"));
        System.out.println("CheckOut Date : " + jsonPathEvaluator.get("booking.bookingdates.checkout"));
        System.out.println("Additional Needs : " + jsonPathEvaluator.get("booking.additionalneeds"));
        System.out.println("Booking details created : " + body);
    }

    public void Test04_GetBookingByID()
    {
        System.out.println("*****************************************************************");
        System.out.println("Retrieving Booking details based on booking ID created for new Booking");
        System.out.println("*****************************************************************");
        Response res = given()
                .header("Authorization", "Bearer "+token)
                .header("Accept","application/json")
                .pathParam("ID",bookingId)
                .when()
                .get("https://restful-booker.herokuapp.com/booking/{ID}");
        String body = res.getBody().asString();
        JsonPath jsonPathEvaluator = res.jsonPath();
        Assert.assertEquals(res.getStatusCode(),200);
        Assert.assertTrue(jsonPathEvaluator.get("firstname").toString().contains(BookingFirstName));
        Assert.assertTrue(jsonPathEvaluator.get("lastname").toString().contains(BookingLastName));
        System.out.println("*****************************************************************");
        System.out.println("Booking Details retrieved match correctly!!");
        System.out.println("*****************************************************************");
        System.out.println("Booking details : "+body);
    }

    // This API restricts any updates to be performed on the data
    public void Test05_PartialUpdateBooking(){
        System.out.println("*****************************************************************");
        System.out.println("Placing back the original first and last name for the above updated booking");
        System.out.println("*****************************************************************");
        Response res = given()
                .header("Authorization", "Bearer "+token)
                .accept("application/json")
                .cookie("token="+token)
                .contentType("application/json")
                .pathParam("ID",bookingId)
                .body("{\"firstname\":\"updated_Test\", \"lastname\":\"updated_Booking\"}")
                .when()
                .patch("https://restful-booker.herokuapp.com/booking/{ID}");
        String body = res.getBody().asString();
        JsonPath jsonPathEvaluator = res.jsonPath();
        Assert.assertEquals(res.getStatusCode(),403);
        System.out.println("*****************************************************************");
        System.out.println("Booking Details are restricted to eb updated!!");
        System.out.println("*****************************************************************");
    }

    public void Test06_DeleteBooking(){
        System.out.println("*****************************************************************");
        System.out.println("Deleting the above created Booking");
        System.out.println("*****************************************************************");
        Response res = given()
                .header("Authorization", "Bearer "+token)
                .accept("application/json")
                .contentType("application/json")
                .pathParam("ID","25")
                .when()
                .delete("https://restful-booker.herokuapp.com/booking/{ID}");
        String body = res.getBody().asString();
        System.out.println("Booking details are deleted : " + body);
    }


    public void Test07_getAllBookingIds(){
        System.out.println("*****************************************************************");
        System.out.println("Retrieving all Booking Ids");
        System.out.println("*****************************************************************");
        RestAssured.baseURI  = "https://restful-booker.herokuapp.com/booking";
        Response res = given()
                .header("Authorization", "Bearer "+token)
                .when()
                .get("");
        String body = res.getBody().asString();
        System.out.println("Booking IDs : "+body);
    }
}