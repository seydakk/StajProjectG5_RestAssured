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

public class SP_API_04_Emrah {

    Faker faker = new Faker();
    String FieldsID;
    String FieldsName;
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
    public void createFields() {

        Map<String, String> Fields = new HashMap<>();
        FieldsName = faker.company().profession() + faker.number().digits(2);
        Fields.put ("name", FieldsName);

        FieldsID =
                given()
                        .spec(reqSpec)
                        .body(Fields)
                        .log().body()

                        .when()
                        .post("/school-service/api/entity-field")

                        .then()
                        .log().body()
                        .statusCode(201)
                        .extract().path("id");

        System.out.println("countryID = " + FieldsID);
    }

    @Test(dependsOnMethods = "createFields")
    public void createFieldsNegative() {

        Map<String, String> country = new HashMap<>();
        country.put("name", FieldsName);


        given()
                .spec(reqSpec)
                .body(country)
                .log().body()

                .when()
                .post("/school-service/api/entity-field")

                .then()
                .log().body()
                .statusCode(400)
                .body("message", containsString("already"))
        ;
    }

    @Test(dependsOnMethods = "createFieldNegative")
    public void updateField () {

        Map<String, String> country = new HashMap<>();
        country.put("id", FieldsID);
        FieldsName = "frkn" + faker.number().digits(5);
        country.put("name", FieldsName);


        given()
                .spec(reqSpec)
                .body(country)
                //  .log().body()

                .when()
                .put("/school-service/api/entity-field/{FieldID}")

                .then()
                .log().body() //gelen body i log olarak göster
                .statusCode(200)
                .body("name", equalTo(FieldsName))
        ;

    }

    @Test(dependsOnMethods = "updateFields")

    public void deleteFields () {


        given()
                .spec(reqSpec)
                .pathParam("FieldsID", FieldsID)
                .log().uri()

                .when()
                .delete("/school-service/api/entity-field/{FieldID}")

                .then()
                .log().body() //gelen body i log olarak göster
                .statusCode(204)

        ;

    }

    @Test(dependsOnMethods = "deleteFields")
    public void deleteFieldsNegative() {

        given()
                .spec(reqSpec)
                .pathParam("FieldsID", FieldsID)
                .log().uri()

                .when()
                .delete("/school-service/api/entity-field/{FieldID}")

                .then()
                .log().body() //gelen body i log olarak göster
                .statusCode(400)
                .body("message", equalTo("EntityFields not  found"))

        ;
    }
}


