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
    String gradeLevel;  // name
    String shortName;
    String  ngl;
    int order;



    //{
//    "id": null,
//    "name": "merhat",
//    "shortName": "mir",
//    "nextGradeLevel": {
//    "id": "63c059debff29b76b07e28f3"
//    }

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

        //gradeLevel =  faker.number().toString();


        gradeLevel.put("name", "gradeLevel");
        gradeLevel.put("id", null);
        gradeLevel.put("name", "gradeLevelName");
        gradeLevel.put("shortName","");
        gradeLevel.put("order","1");
        gradeLevel.put("nextGradeLevel","ngl");


        gradeId = given()
                .spec(reqSpec)
                .body(gradeLevel)
                .log().body()

                .when()
                .post("/school-service/api/grade-levels")

                .then()
                .statusCode(201)
                .extract().path("id");

        System.out.println("gradeID= " + gradeId);
    }

    @Test(dependsOnMethods = "CreateGradeLevel")
    public void CreateGradeLevelNegative() {
        Map<String, String>gradeLevel = new HashMap<>();

        gradeLevel.put("id", null);
        gradeLevel.put("name", "gradeLevelName");
        gradeLevel.put("shortName","");
        gradeLevel.put("nextGradeLevel","ngl");

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

        gradeLevel.put("id", null);
        gradeLevel.put("name", "bahaLevel");
        gradeLevel.put("shortName","taz");
        gradeLevel.put("nextGradeLevel","tx");


        given()
                .spec(reqSpec)
                .body(gradeLevel)

                .when()
                .put("/school-service/api/grade-levels")

                .then()
                .statusCode(200)
                .body("name", equalTo(gradeLevel))
                .log().body();

    }


    @Test(dependsOnMethods = "UpdateGradeLevel")
    public void DeleteGradeLevel() {
        given()
                .spec(reqSpec)
                .pathParam("id", gradeId)

                .when()
                .delete("/school-service/api/grade-levels/{gradeId}")

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
                .delete("/school-service/api/bank-accounts/{gradeId}")

                .then()
                .log().body()
                .statusCode(400)
                .body("message",containsString("not  found"));

    }
}
