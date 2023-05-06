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

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class SP_API_11_Zehra {

    Faker faker = new Faker();
    RequestSpecification reqSpec;

    Map<String, String> newDiscount = new HashMap<>();
    String description;
    String integrationCode;
    String priorityNo;
    String discountID;

    @BeforeClass
    public void login() {

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
                        .log().all()
                        .statusCode(200)
                        .extract().response().getDetailedCookies();

        reqSpec = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .addCookies(cookies)
                .build();

    }


    @Test
    public void createNewDiscount() {

        description = faker.name()+faker.number().digits(4);
        newDiscount.put("description", description);

        integrationCode = faker.number()+faker.number().digits(2);
        newDiscount.put("code", integrationCode);

        priorityNo = faker.number().digits(1);
        newDiscount.put("priority", priorityNo);

      //  newDiscount.put("tenantId","6390ef53f697997914ec20c2");

        discountID =
                given()
                        .spec(reqSpec)
                        .body(newDiscount)
                        .log().body()

                        .when()
                        .post("/school-service/api/discounts")

                        .then()
                        .log().body()
                        .statusCode(201)
                        .extract().path("id");


        System.out.println("id = " + discountID);
    }


    @Test(dependsOnMethods = "createNewDiscount")
    public void createNewDiscountNegative() {


        given()
                .spec(reqSpec)
                .body(newDiscount)
                .log().body()
                .when()
                .post("/school-service/api/discounts")
                .then()
                .statusCode(400)
                .extract().path("integrationCode");
    }


    @Test(dependsOnMethods = "createNewDiscountNegative")
    public void updateNewDiscount() {

        description=faker.number().digits(2);
        newDiscount.put("description", description);
        newDiscount.put("id",discountID);


        given()
                .spec(reqSpec)
                .body(newDiscount)
                .when()
                .put("/school-service/api/discounts")
                .then()
                .log().body()
                .statusCode(200)
                .body("description", equalTo(description));

    }

    @Test   (dependsOnMethods = "updateNewDiscount")
    public void deleteNewDiscount() {
        given()
                .spec(reqSpec)
                .pathParam("id", discountID)
                .when()
                .delete("/school-service/api/discounts/{id}")
                .then()
                .log().body()
                .statusCode(200);

    }

    @Test(dependsOnMethods = "deleteNewDiscount")
    public void deleteNewDiscountNegative() {
        given()
                .spec(reqSpec)
                .pathParam("id", discountID)
                .when()
                .delete("/school-service/api/discounts/{id}")
                .then()
                .log().body()
                .statusCode(400);
    }

}
