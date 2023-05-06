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

public class SP_API_07_Omer {
    Faker faker = new Faker();
    String posLocID;
    String posLocName;
    String posLocShName;

    Map<String, String> location;
    String capacity;
    RequestSpecification reqSpec;

    @BeforeClass
    public void Login() {
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
    public void createLocation() {

        location = new HashMap<>();
        posLocName = faker.company().profession() + faker.number().digits(2);
        posLocShName = faker.company().profession() + faker.number().digits(3);
        location.put("name", posLocName);
        location.put("shortName", posLocShName);
        location.put("capacity", capacity);
        location.put("type","CLASS");
        location.put("school", "6390f3207a3bcb6a7ac977f9");

        posLocID =
                given()
                        .spec(reqSpec)
                        .body(location)
                        .log().body()

                .when()
                        .post("/school-service/api/location")

                .then()
                        .log().body()
                        .statusCode(201)
                        .extract().path("id");

        System.out.println("locationID = " + posLocID);
    }
    @Test(dependsOnMethods = "createLocation")
    public void createLocationNegative() {

        location = new HashMap<>();
        location.put("name", posLocName);
        location.put("shortName", posLocShName);
        location.put("capacity", capacity);
        location.put("type","CLASS");
        location.put("school", "6390f3207a3bcb6a7ac977f9");

        given()
                .spec(reqSpec)
                .body(location)
                .log().body()

        .when()
                .post("/school-service/api/location")

        .then()
                .log().body()
                .statusCode(400)
                .body("message", containsString("already"))
        ;
    }
    @Test(dependsOnMethods = "createLocationNegative")
    public void updateLocation() {

       // location = new HashMap<>();

        posLocName = "omrFrk" + faker.number().digits(2);
        location.put("name", posLocName);
        location.put("id", posLocID);

        given()
                .spec(reqSpec)
                .body(location)

        .when()
                .put("/school-service/api/location")

        .then()
                .log().body()
                .statusCode(200)
                .body("name", equalTo(posLocName))
        ;
    }
    @Test(dependsOnMethods = "updateLocation")
    public void deleteLocation() {

        given()
                .spec(reqSpec)
                .pathParam("posLocID", posLocID)
                .log().uri()

        .when()
                .delete("/school-service/api/location/{posLocID}")

        .then()
                .log().body()
                .statusCode(200)
        ;
    }
    @Test(dependsOnMethods = "deleteLocation")
    public void deleteLocationNegative() {

        given()
                .spec(reqSpec)
                .pathParam("posLocID", posLocID)
                .log().uri()

        .when()
                .delete("/school-service/api/location/{posLocID}")

        .then()
                .log().body()
                .statusCode(400)
                .body("message", equalTo("School Location not found"))
        ;
    }
}