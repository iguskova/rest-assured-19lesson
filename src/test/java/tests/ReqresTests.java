package tests;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static helpers.CustomAllureListener.withCustomTemplates;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.is;

public class ReqresTests {

    @BeforeAll
    static void setup(){
        RestAssured.baseURI = "https://reqres.in";
    }

    @Test
    @DisplayName("Создать пользователя")
    void createUser(){
        String data = "{ \"name\": \"morpheus\", " +
                "\"job\": \"leader\"}";


        given()
                .filter(withCustomTemplates())
                .contentType(JSON)
                .body(data)
                .log().uri()
                .log().body()
                .when()
                .post("/api/users")
                .then()
                .log().status()
                .log().body()
                .statusCode(201)
                .body("name", is("morpheus"), "job", is("leader"));
    }

    @Test
    @DisplayName("Найти пользователя")
    void findUser(){

        given()
                .filter(withCustomTemplates())
                .log().all()
                .when()
                .get("/api/users/2")
                .then()
                .log().status()
                .log().body()
                .statusCode(200)
                .body("data.id", is(2),
                        "data.email", is("janet.weaver@reqres.in"));
    }

    @Test
    @DisplayName("Пользователь не найден")
    void userNotFound(){

        given()
                .filter(withCustomTemplates())
                .log().all()
                .when()
                .get("/api/users/23")
                .then()
                .log().all()
                .statusCode(404);
    }

    @Test
    @DisplayName("Успешная регистрация")
    void registerSuccessful(){
        String data= "{ \"email\": \"eve.holt@reqres.in\", "+
                "\"password\": \"pistol\"}";

        given()
                .filter(withCustomTemplates())
                .contentType(JSON)
                .body(data)
                .log().body()
                .when()
                .post("/api/register")
                .then()
                .log().status()
                .log().body()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/generateToken_response_shema.json"))
                .body("id",is(4),"token",is("QpwL5tke4Pnpja7X4"));
    }

    @Test
    @DisplayName("Неуспешная регистрация")
    void registerUnsuccessful(){

        String data = "{ \"email\": \"sydney@fife\"}";

        given()
                .filter(withCustomTemplates())
                .contentType(JSON)
                .body(data)
                .log().all()
                .when()
                .post("/api/register")
                .then()
                .log().all()
                .statusCode(400)
                .body("error",is("Missing password"));
    }
}
