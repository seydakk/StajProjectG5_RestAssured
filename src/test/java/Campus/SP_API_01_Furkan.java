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

public class  SP_API_01_Furkan {

    Faker faker = new Faker();
    String posCatID;
    String posCatName;
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
    public void createpositionCategory() {

        Map<String, String> positionCategory = new HashMap<>();
        posCatName = faker.company().profession() + faker.number().digits(2);
        positionCategory.put("name", posCatName);

        posCatID =
                given()
                        .spec(reqSpec)
                        .body(positionCategory)
                        .log().body()

                        .when()
                        .post("/school-service/api/position-category")

                        .then()
                        .log().body()
                        .statusCode(201)
                        .extract().path("id");

        System.out.println("countryID = " + posCatID);
    }

    @Test(dependsOnMethods = "createpositionCategory")
    public void createpositionCategoryNegative() {

        Map<String, String> country = new HashMap<>();
        country.put("name", posCatName);


        given()
                .spec(reqSpec)
                .body(country)
                .log().body()

                .when()
                .post("/school-service/api/position-category")

                .then()
                .log().body()
                .statusCode(400)
                .body("message", containsString("already"))
        ;
    }

    @Test(dependsOnMethods = "createpositionCategoryNegative")
    public void updatepositionCategory() {

        Map<String, String> country = new HashMap<>();
        country.put("id", posCatID);
        posCatName = "frkn" + faker.number().digits(5);
        country.put("name", posCatName);


        given()
                .spec(reqSpec)
                .body(country)
                //  .log().body()

                .when()
                .put("/school-service/api/position-category")

                .then()
                .log().body() //gelen body i log olarak göster
                .statusCode(200)
                .body("name", equalTo(posCatName))
        ;

    }


    @Test(dependsOnMethods = "updatepositionCategory")
    public void deletepositionCategory() {


        given()
                .spec(reqSpec)
                .pathParam("posCatID", posCatID)
                .log().uri()

                .when()
                .delete("/school-service/api/position-category/{posCatID}")

                .then()
                .log().body() //gelen body i log olarak göster
                .statusCode(204)

        ;

    }

    @Test(dependsOnMethods = "deletepositionCategory")
    public void deletepositionCategoryNegative() {

        given()
                .spec(reqSpec)
                .pathParam("posCatID", posCatID)
                .log().uri()

                .when()
                .delete("/school-service/api/position-category/{posCatID}")

                .then()
                .log().body() //gelen body i log olarak göster
                .statusCode(400)
                .body("message", equalTo("PositionCategory not  found"))

        ;
    }
}
