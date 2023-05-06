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

public class  SP_API_12_Seyda {

    Faker faker = new Faker();

    RequestSpecification reqSpec;

    Map<String, String> nationality = new HashMap<>();

    String nationalityName;

    String nationalityId;

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
    public void createNationality() {
        nationalityName = faker.nation().nationality();
        nationality.put("name", nationalityName);

        nationalityId = given()
                .spec(reqSpec)
                .body(nationality)
                .log().body()

                .when()
                .post("/school-service/api/nationality")

                .then()
                .statusCode(201)
                .extract().path("id");

        System.out.println("nationalityId = " + nationalityId);
    }

    @Test(dependsOnMethods = "createNationality")
    public void createNationalityNegative() {

        given()
                .spec(reqSpec)
                .body(nationality)
                .log().body()

                .when()
                .post("/school-service/api/nationality")

                .then()
                .statusCode(400)
                .extract().path("id");

    }

    @Test(dependsOnMethods = "createNationalityNegative")
    public void updateNationality() {
        nationalityName = faker.nation().nationality();
        nationality.put("name", nationalityName);
        nationality.put("id", nationalityId);

        given()
                .spec(reqSpec)
                .body(nationality)

                .when()
                .put("/school-service/api/nationality")

                .then()
                .statusCode(200)
                .body("name", equalTo(nationalityName))
                .log().body();

    }


    @Test(dependsOnMethods = "updateNationality")
    public void deleteNationality() {
        given()
                .spec(reqSpec)
                .pathParam("id", nationalityId)

                .when()
                .delete("/school-service/api/nationality/{id}")

                .then()
                .log().body()
                .statusCode(200);
    }


    @Test(dependsOnMethods = "deleteNationality")
    public void deleteNationalityNegative() {
        given()
                .spec(reqSpec)
                .pathParam("id", nationalityId)

                .when()
                .delete("/school-service/api/nationality/{id}")

                .then()
                .log().body()
                .statusCode(400)
                .body("message",containsString("not  found"));

    }
}
