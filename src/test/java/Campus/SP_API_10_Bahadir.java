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

public class SP_API_10_Bahadir {

    Faker faker = new Faker();

    RequestSpecification reqSpec;

    String gradeId;  // id
    String gradeName;  // name
    String shortName;
    String nextGradeLevel ;
    int order;



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
    public void CreateGradeLevel() {
        Map<String, String> gradeLevel = new HashMap<>();
        gradeName=faker.name().title().concat("12K");

        gradeLevel.put("id", null);   // id
        gradeLevel.put("name", gradeName);  //name
        gradeLevel.put("shortName",faker.app().name()); //shortName
        gradeLevel.put("order",faker.number().digits(1)); //order
        gradeLevel.put("nextGradeLevel",gradeLevel.put("GL","GL+1"));
        //{
//    "id": null,
//    "name": "merhat",
//    "shortName": "mir",
//    "nextGradeLevel": {
//    "id": "63c059debff29b76b07e28f3"
//    }    "id": null,
//  "name": "{{$randomFullName}}",
//  "shortName": "{{$randomUserName}}",
//  "nextGradeLevel": null,
//  "order": "{{$randomInt}}",

        gradeId = given()
                .spec(reqSpec)
                .body(gradeLevel)
                .log().body()

                .when()
                .post("/school-service/api/grade-levels")

                .then()
                .log().body()
                .statusCode(201)
                .extract().path("id");

        System.out.println("gradeID= " + gradeId);
    }

    @Test(dependsOnMethods = "CreateGradeLevel")
    public void CreateGradeLevelNegative() {
        Map<String, String>gradeLevel = new HashMap<>();


        gradeLevel.put("id", null);   // id
        gradeLevel.put("name", gradeName);  //name
        gradeLevel.put("shortName",faker.app().name()); //shortName
        gradeLevel.put("order",faker.number().digits(1)); //order
        gradeLevel.put("nextGradeLevel",gradeLevel.put("GL","GL+1"));

        given()
                .spec(reqSpec)
                .body(gradeLevel)
                .log().body()

                .when()
                .post("/school-service/api/grade-levels")

                .then()
                .statusCode(400)
                .body("message", containsString("already"))
        ;

    }

    @Test(dependsOnMethods = "CreateGradeLevelNegative")
    public void UpdateGradeLevel() {
        Map<String, String> gradeLevel = new HashMap<>();

        gradeLevel.put("id", gradeId);   // id
        gradeLevel.put("name", gradeName);  //name
        gradeLevel.put("shortName",faker.app().name()+faker.number().digits(2)); //shortName
        gradeLevel.put("order",faker.number().digits(1)); //order
        gradeLevel.put("nextGradeLevel",gradeLevel.put("GL","GNL+1"));


        given()
                .spec(reqSpec)
                .body(gradeLevel)

                .when()
                .put("/school-service/api/grade-levels")

                .then()
                .log().body()
                .statusCode(200)
                .body("name", equalTo(gradeName));


    }


    @Test(dependsOnMethods = "UpdateGradeLevel")
    public void DeleteGradeLevel() {
        given()
                .spec(reqSpec)
                .pathParam("id", gradeId)

                .when()
                .delete("/school-service/api/grade-levels/{id}")

                .then()
                .log().body()
                .statusCode(200);
    }


    @Test(dependsOnMethods = "DeleteGradeLevel")
    public void DeleteGradeLevelNegative() {
        given()
                .spec(reqSpec)
                .pathParam("id", gradeId)

                .when()
                .delete("/school-service/api/grade-levels/{id}")

                .then()
                .log().body()
                .statusCode(400)
                .body("message",containsString("Grade Level not found."));

    }
}
