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

public class SP_API_08_Omer {

    Faker faker = new Faker();
    String posDepID;
    String posDepName;

    Map<String, String> department;
    String posDepCode;
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
    public void createDepartment() {

        department = new HashMap<>();
        posDepName = faker.company().profession() + faker.number().digits(2);
        posDepCode = faker.company().profession() + faker.number().digits(3);
        department.put("name", posDepName);
        department.put("code", posDepCode);
        department.put("school", "6390f3207a3bcb6a7ac977f9");

        posDepID =
                given()
                        .spec(reqSpec)
                        .body(department)
                        .log().body()

                        .when()
                        .post("/school-service/api/department")

                        .then()
                        .log().body()
                        .statusCode(201)
                        .extract().path("id");

        System.out.println("locationID = " + posDepID);
    }
    @Test(dependsOnMethods = "createDepartment")
    public void createDepartmentNegative() {

        department = new HashMap<>();
        department.put("name", posDepName);
        department.put("code", posDepCode);
        department.put("school", "6390f3207a3bcb6a7ac977f9");

        given()
                .spec(reqSpec)
                .body(department)
                .log().body()

                .when()
                .post("/school-service/api/department")

                .then()
                .log().body()
                .statusCode(400)
                .body("message", containsString("already"))
        ;
    }
    @Test(dependsOnMethods = "createDepartmentNegative")
    public void updateDepartment() {

        posDepName = "omrFrk" + faker.number().digits(2);
        department.put("name", posDepName);
        department.put("id", posDepID);

        given()
                .spec(reqSpec)
                .body(department)

                .when()
                .put("/school-service/api/department")

                .then()
                .log().body()
                .statusCode(200)
                .body("name", equalTo(posDepName))
        ;
    }
    @Test(dependsOnMethods = "updateDepartment")
    public void deleteDepartment() {

        given()
                .spec(reqSpec)
                .pathParam("posDepID", posDepID)
                .log().uri()

                .when()
                .delete("/school-service/api/department/{posDepID}")

                .then()
                .log().body()
                .statusCode(204)
        ;
    }
    @Test(dependsOnMethods = "deleteDepartment")
    public void deleteDepartmentNegative() {

        given()
                .spec(reqSpec)
                .pathParam("posDepID", posDepID)
                .log().uri()

                .when()
                .delete("/school-service/api/department/{posDepID}")

                .then()
                .log().body()
                .statusCode(400)
                .body("message", equalTo("School Department not found"))
        ;
    }
}