package Campus;


import com.github.javafaker.Faker;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.http.Cookies;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

public class SP_API_09_Bahadir {


    Faker faker = new Faker();

    RequestSpecification reqSpec;


    String accountName;  // name
    String currency;
    String  iban;
    String integrationCode;
    String BankId;  // id
    String schoolId; //schoolId

    @BeforeClass
    public void Setup() {
        baseURI = "https://test.mersys.io";

        Map<String, String> userCredential = new HashMap<>();
        userCredential.put("username", "turkeyts");
        userCredential.put("password", "TechnoStudy123");
        userCredential.put("rememberMe", "true");

        Cookies cookies =
                given()
                        .contentType(ContentType.JSON)
                        .body(userCredential)

                        .when()
                        .post("/auth/login")

                        .then()
                        .statusCode(200)
                        .extract().response().getDetailedCookies();

        reqSpec = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
               .addCookies(cookies)
                .build();

    }


    @Test
    public void CreateBankAccount() {
        Map<String, String> bankAccount = new HashMap<>();

       accountName = faker.finance().iban() ;

        bankAccount.put("id","BankId");
        bankAccount.put("name", accountName);
        bankAccount.put("iban","DE12911129");
        bankAccount.put("integrationCode","E12");
        bankAccount.put("currency","EUR");
        bankAccount.put("active", "true");
        bankAccount.put("schoolId","6390f3207a3bcb6a7ac977f9");


// {
//    "id": null,
//    "name": "haha",
//    "iban": "DE1294847",
//    "integrationCode": "France - 967",
//    "currency": "KGS",
//    "active": true,
//    "schoolId": "6390f3207a3bcb6a7ac977f9"
//}

        BankId = given()
                .spec(reqSpec)
                .body(bankAccount)
                .log().body()

                .when()
                .post("/school-service/api/bank-accounts")

                .then()
                .statusCode(201)
                .extract().path("id");

        System.out.println("BankNo = " + BankId);
    }

    @Test(dependsOnMethods = "CreateBankAccount")
    public void CreateBankAccountNegative() {
        Map<String, String> bankAccount = new HashMap<>();
        bankAccount.put("id","BankId");
        bankAccount.put("name", accountName);
        bankAccount.put("iban","DE12911129");
        bankAccount.put("integrationCode","E12");
        bankAccount.put("currency","EUR");
        bankAccount.put("active", "true");
        bankAccount.put("schoolId","6390f3207a3bcb6a7ac977f9");

        given()
                .spec(reqSpec)
                .body(bankAccount)
                .log().body()

                .when()
                .post("/school-service/api/bank-accounts")

                .then()
                .statusCode(400)
                .body("message", containsString("already"))
        ;

    }

   @Test(dependsOnMethods = "CreateBankAccountNegative")
    public void UpdateBankAccount() {
       Map<String, String> bankAccount = new HashMap<>();

       bankAccount.put("id","BankId");
       bankAccount.put("name", "bahaBank");
       bankAccount.put("iban","DE14531453");
       bankAccount.put("integrationCode","E12");
       bankAccount.put("currency","KZT");
       bankAccount.put("active", "true");
       bankAccount.put("schoolId", faker.idNumber().toString());


        given()
                .spec(reqSpec)
                .body(bankAccount)

                .when()
                .put("//school-service/api/bank-accounts")

                .then()
                .statusCode(200)
                .body("name", equalTo(accountName))
                .log().body();

    }


    @Test(dependsOnMethods = "UpdateBankAccount")
    public void DeleteBankAccount() {
        given()
                .spec(reqSpec)
                .pathParam("id", BankId)

                .when()
                .delete("/school-service/api/bank-accounts/{BankId}")

                .then()
                .log().body()
                .statusCode(200);
    }


    @Test(dependsOnMethods = "DeleteBankAccount")
    public void DeleteBankAccountNegative() {
        given()
                .spec(reqSpec)
                .pathParam("id", BankId)

                .when()
                .delete("/school-service/api/bank-accounts/{BankId}")

                .then()
                .log().body()
                .statusCode(400)
                .body("message",containsString("not  found"));

    }
}
