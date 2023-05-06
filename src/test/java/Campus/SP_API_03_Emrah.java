package Campus;

import com.github.javafaker.Faker;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.http.Cookies;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.w3c.dom.DocumentType;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class SP_API_03_Emrah {

    Faker faker = new Faker();
    String docID;
    String docName;
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
    public void createDocumentTypes() {

        Map<String, Object> doctype = new HashMap<>();
        docName = faker.name().fullName();
        String[] attachmentStages = new String[2];
        attachmentStages[0] = "EXAMINATION";
        attachmentStages[1] = "DISMISSAL";

        doctype.put("name", docName);
        doctype.put("schoolId", "6390f3207a3bcb6a7ac977f9");
        doctype.put("attachmentStages", attachmentStages);

        docID =
                given()
                        .spec(reqSpec)
                        .body(doctype)
                        //  .log().body()

                        .when()
                        .post("/school-service/api/attachments/create")

                        .then()
                        .log().body()

                        .statusCode(201)
                        .extract().path("id");

        System.out.println("docID = " + docName);
    }

    @Test(dependsOnMethods = "createDocumentTypes")
    public void updateDocumentTypes() {

        Map<String, Object> doctype = new HashMap<>();

        docName = faker.name().fullName();

        String[] attachmentStages = new String[2];
        attachmentStages[0] = "EXAMINATION";


        doctype.put("id", docID);
        doctype.put("name", docName);
        doctype.put("schoolId", "6390f3207a3bcb6a7ac977f9");
        doctype.put("attachmentStages", attachmentStages);

        given()
                .spec(reqSpec)
                .body(doctype)
                .log().body()

                .when()
                .put("/school-service/api/attachments")

                .then()
                .log().body()
                .statusCode(200)
                .body("name", equalTo(docName));
    }

    @Test(dependsOnMethods = "updateDocumentTypes")
    public void deleteDocumentTypes() {

        given()
                .spec(reqSpec)
                .pathParam("id", docID)
                .log().uri()

                .when()
                .delete("/school-service/api/attachments/{id}")

                .then()
                .log().body()

                .statusCode(200);
    }

    @Test(dependsOnMethods = "deleteDocumentTypes")
    public void deleteDocumentTypesNegative() {

        given()
                .spec(reqSpec)
                .pathParam("id", docID)
                .log().uri()

                .when()
                .delete("/school-service/api/attachments/{id}")

                .then()
                .log().body()

                .statusCode(400)
                .body("message", equalTo("Attachment Type not found"));
    }
}
