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

public class SP_API_06_Emirhan {
    Faker faker = new Faker();
    RequestSpecification reqSpec;
    String subCatName;
    String subCatID;



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
        ;

        reqSpec= new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .addCookies(cookies)
                .build();

    }
    @Test
    public void createSubjectCategory() {

        Map<String, String> subCat = new HashMap<>();
        subCatName = faker.company().profession() + faker.number().digits(3);
        subCat.put("name", subCatName);
        subCat.put("code", faker.address().countryCode()+faker.number().digits(3));

        subCatID =
                given()
                        .spec(reqSpec)
                        .body(subCat)
                        .log().body()

                        .when()
                        .post("/school-service/api/subject-categories")

                        .then()
                        .log().body()
                        .statusCode(201)
                        .extract().path("id");

        System.out.println("countryID = " + subCatID);
    }


    @Test(dependsOnMethods = "createSubjectCategory")
    public void createSubjectCategoryNeegative(){
        HashMap<String,String> subCat = new HashMap<>();
        subCat.put("name" , subCatName);
        subCat.put("code",faker.address().countryCode()+faker.number().digits(5) );

        given()
                .spec(reqSpec)
                .body(subCat)
                .log().body()

                .when()
                .post("/school-service/api/subject-categories")

                .then()
                .log().body()
                .statusCode(400)
                .body("message", containsString("already"))
        ;
    }

    @Test(dependsOnMethods = "createSubjectCategoryNeegative")
    public void updateSubjectCategory(){

        Map<String ,String> subCat = new HashMap<>();
        subCat.put("id", subCatID);
        subCatName = "Emirhan" + faker.number().digits(5);
        subCat.put("name", subCatName);
        subCat.put("code", faker.address().countryCode()+faker.number().digits(5));

        given()
                .spec(reqSpec)
                .body(subCat)
                //.log().body()

                .when()
                .put("/school-service/api/subject-categories")

                .then()
                .log().body()
                .statusCode(200)
                .body("name", equalTo(subCatName))
        ;
    }

    @Test(dependsOnMethods = "updateSubjectCategory")
    public void deleteSubjectCategory(){

        given()
                .spec(reqSpec)
                .pathParam("subCatID", subCatID)
                .log().uri()

                .when()
                .delete("/school-service/api/subject-categories/{subCatID}")

                .then()
                .log().body()
                .statusCode(200)
        ;

    }


    @Test(dependsOnMethods = "deleteSubjectCategory")
    public void deleteSubjectCategoryNegative(){
        given()
                .spec(reqSpec)
                .pathParam("subCatID", subCatID)
                .log().uri()

                .when()
                .delete("/school-service/api/subject-categories/{subCatID}")

                .then()
                .log().body()
                .statusCode(400)
        ;
    }

}
