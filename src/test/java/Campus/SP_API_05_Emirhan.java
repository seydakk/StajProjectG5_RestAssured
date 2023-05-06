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

public class SP_API_05_Emirhan {

    Faker faker = new Faker();
    RequestSpecification reqSpec;
    String positionShortName;
    String positionName;
    String positionID;

    @BeforeClass
    public void login(){
        baseURI="https://test.mersys.io";

        Map<String, String> userCredential = new HashMap<>();
        userCredential.put("username", "turkeyts");
        userCredential.put("password", "TechnoStudy123");
        userCredential.put("rememberMe", "true");
        Cookies cookies =
                given()
                        .when()
                        .contentType(ContentType.JSON)
                        .body(userCredential)

                        .post("/auth/login")
                        .then()
                        .log().all()
                        .statusCode(200)
                        .extract().response().getDetailedCookies();

        reqSpec= new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .addCookies(cookies)
                .build();
    }
    @Test
    public void createPositions() {

        Map<String, String> positions = new HashMap<>();
        positionName = faker.company().profession() + faker.number().digits(3);
        positions.put("name", positionName);
        positionShortName = faker.company().profession() + faker.number().digits(3);
        positions.put("shortName", positionShortName);
        positions.put("tenantId", "6390ef53f697997914ec20c2");

        positionID =
                given()
                        .spec(reqSpec)
                        .body(positions)
                        .log().body()

                        .when()
                        .post("/school-service/api/employee-position")

                        .then()
                        .log().body()
                        .statusCode(201)
                        .extract().path("id");

        System.out.println("countryID = " + positionID);
    }

    @Test(dependsOnMethods = "createPositions")
    public void createPositionsNegative(){

        Map<String, String> positions = new HashMap<>();
        positions.put("name", positionName);
        positions.put("shortName", positionShortName);
        positions.put("tenantId", "6390ef53f697997914ec20c2");

        given()
                .spec(reqSpec)
                .body(positions)
                .log().body()

                .when()
                .post("/school-service/api/employee-position")

                .then()
                .log().body()
                .statusCode(400)
                .body("message", containsString("already"))
        ;

        System.out.println("positionID = " + positionID);
    }

    @Test(dependsOnMethods = "createPositionsNegative")
    public void updatePositions(){

        Map<String, String > positions = new HashMap<>();
        positions.put("id", positionID);
        positionName = "Erd" + faker.number().digits(3);
        positions.put("name", positionName);
        positions.put("shortName", positionShortName);
        positions.put("tenantId", "6390ef53f697997914ec20c2");

        given()
                .spec(reqSpec)
                .body(positions)
                //.log().body()

                .when()
                .put("/school-service/api/employee-position")

                .then()
                .log().body()
                .statusCode(200)
                .body("name", equalTo(positionName))
        ;
    }

    @Test(dependsOnMethods = "updatePositions")
    public void deletePositions(){
        given()
                .spec(reqSpec)
                .pathParam("positionID",positionID)
                .log().uri()

                .when()
                .delete("/school-service/api/employee-position/{positionID}")

                .then()
                .log().body()
                .statusCode(204)
        ;
    }


    @Test(dependsOnMethods = "deletePositions")
    public void deletePositionsNegative(){
        given()
                .spec(reqSpec)
                .pathParam("positionID",positionID)
                .log().uri()

                .when()
                .delete("/school-service/api/employee-position/{positionID}")

                .then()
                .log().body()
                .statusCode(400)
        ;
    }

    }
