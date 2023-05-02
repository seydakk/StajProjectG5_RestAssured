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

public class SP_API_02_Furkan {

    Faker faker = new Faker();
    String attestationID;
    String attestationName;
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
    public void createAttestation() {

        Map<String, String> positionCategory = new HashMap<>();
        attestationName = faker.backToTheFuture().character() + faker.number().digits(2);
        positionCategory.put("name", attestationName);

        attestationID =
                given()
                        .spec(reqSpec)
                        .body(positionCategory)
                        .log().body()

                        .when()
                        .post("/school-service/api/attestation")

                        .then()
                        .log().body()
                        .statusCode(201)
                        .extract().path("id");

        System.out.println("countryID = " + attestationID);
    }

    @Test(dependsOnMethods = "createAttestation")
    public void createAttestationNegative() {

        Map<String, String> country = new HashMap<>();
        country.put("name", attestationName);


        given()
                .spec(reqSpec)
                .body(country)
                .log().body()

                .when()
                .post("/school-service/api/attestation")

                .then()
                .log().body()
                .statusCode(400)
                .body("message", containsString("already"))
        ;
    }

    @Test(dependsOnMethods = "createAttestationNegative")
    public void updateAttestation() {

        Map<String, String> country = new HashMap<>();
        country.put("id", attestationID);
        attestationName = "frkn" + faker.number().digits(5);
        country.put("name", attestationName);


        given()
                .spec(reqSpec)
                .body(country)
                //  .log().body()

                .when()
                .put("/school-service/api/attestation")

                .then()
                .log().body() //gelen body i log olarak göster
                .statusCode(200)
                .body("name", equalTo(attestationName))
        ;

    }


    @Test(dependsOnMethods = "updateAttestation")
    public void deleteAttestation() {


        given()
                .spec(reqSpec)
                .pathParam("attestationID", attestationID)
                .log().uri()

                .when()
                .delete("/school-service/api/attestation/{attestationID}")

                .then()
                .log().body() //gelen body i log olarak göster
                .statusCode(204)

        ;

    }

    @Test(dependsOnMethods = "deleteAttestation")
    public void deleteAttestationNegative() {

        given()
                .spec(reqSpec)
                .pathParam("attestationID", attestationID)
                .log().uri()

                .when()
                .delete("/school-service/api/attestation/{attestationID}")

                .then()
                .log().body() //gelen body i log olarak göster
                .statusCode(400)
                .body("message", equalTo("attestation not found"))

        ;
    }
}

