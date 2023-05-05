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

public class SP_API_03_Emrah {

    Faker faker = new Faker();
    String DocumentTypeID;
    String DocumentTypeName;
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
    public void createdocumentTypes () {

        Map<String, String> DocumentType = new HashMap<>();
        DocumentTypeName = faker.company().profession() + faker.number().digits(2);
        DocumentType.put("name", DocumentTypeName);

        DocumentTypeID =
                given()
                        .spec(reqSpec)
                        .body(DocumentType)
                        .log().body()

                        .when()
                        .post("/school-service/api/attachments")

                        .then()
                        .log().body()
                        .statusCode(201)
                        .extract().path("id");

        System.out.println("countryID = " + DocumentTypeName);
    }

    @Test(dependsOnMethods = "createdocumentTypesNegative")
    public void updatedocumentTypes() {

        Map<String, String> country = new HashMap<>();
        country.put("id", DocumentTypeName);
        DocumentTypeName = "emre" + faker.number().digits(5);
        country.put("name", DocumentTypeName);

        given()
                .spec(reqSpec)
                .body(country)
                //  .log().body()

                .when()
                .put("/school-service/api/attachments")

                .then()
                .log().body() //gelen body i log olarak göster
                .statusCode(200)
                .body("name", equalTo(DocumentTypeName))
        ;

    }

    @Test(dependsOnMethods = "updatedocumentTypes")
    public void deletedocumentTypes() {


        given()
                .spec(reqSpec)
                .pathParam("DocumentTypeID", DocumentTypeID)
                .log().uri()

                .when()
                .delete("/school-service/api/attachments/{documentID}")

                .then()
                .log().body() //gelen body i log olarak göster
                .statusCode(204)
        ;

    }

    @Test(dependsOnMethods = "deletedocumentTypes")
    public void deletedocumentTypesNegative() {

        given()
                .spec(reqSpec)
                .pathParam("DocumentTypeID", DocumentTypeID)
                .log().uri()

                .when()
                .delete("/school-service/api/document-type/{attachments/{documentID}")

                .then()
                .log().body() //gelen body i log olarak göster
                .statusCode(400)
                .body("message", equalTo("Attachment Type not found"))
        ;
    }
}





















