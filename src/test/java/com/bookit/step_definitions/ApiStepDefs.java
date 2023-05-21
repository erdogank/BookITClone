package com.bookit.step_definitions;

import com.bookit.pages.SelfPage;
import com.bookit.utilities.BookItApiUtil;
import com.bookit.utilities.ConfigurationReader;
import com.bookit.utilities.DBUtils;
import com.bookit.utilities.Environment;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.Assert;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.*;

public class ApiStepDefs {
    String token;
    Response response;
    String emailGlobal;
    String studentEmail;
    String studentPassword;

    @Given("I logged Bookit api using {string} and {string}")
    public void i_logged_Bookit_api_using_and(String email, String password) {

        Response response = given().accept(ContentType.JSON)
                .queryParam("email", email)
                .queryParam("password", password)
                .when()
                .get(ConfigurationReader.get("qa2api.url") + "/sign");
        token = response.path("accessToken");
        System.out.println(token);


//        token = BookItApiUtil.generateToken(email,password);
        emailGlobal = email;
    }

    @When("I get the current user information from api")
    public void i_get_the_current_user_information_from_api() {
        System.out.println("token = " + token);

        //send a GET request "/api/users/me" endpoint to get current user info

        response = given().accept(ContentType.JSON)
                .and()
                .header("Authorization", token)
                .when()
                .get(ConfigurationReader.get("qa2api.url") + "/api/users/me");


//         response = given().accept(ContentType.JSON)
//                .and()
//                .header("Authorization", token)
//                .when()
//                .get(Environment.BASE_URL + "/api/users/me");

    }

    @Then("status code should be {int}")
    public void status_code_should_be(int statusCode) {
//        //verify status code matches with the feature file expected status code

        //Assert.assertTrue(response.statusCode()==statusCode);
       Assert.assertEquals(statusCode,response.statusCode());

    }

    @Then("the information about current user from api and database should match")
    public void theInformationAboutCurrentUserFromApiAndDatabaseShouldMatch() throws SQLException {
        System.out.println("we will compare database and api in this step");

        String dbUrl = ConfigurationReader.get("qa2dbUrl");
        String dbUsername = ConfigurationReader.get("dbUsername");
        String dbPassword = ConfigurationReader.get("dbPassword");

        String query = "select firstname,lastname,role from users\n" +
                "where email = '"+emailGlobal+"'";
        System.out.println("query = " + query);

        Connection connection = DriverManager.getConnection(dbUrl,dbUsername,dbPassword);
        Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ResultSet resultSet = statement.executeQuery(query);


        Map<String,Object> row = new LinkedHashMap<>();
        ResultSetMetaData rsmd = resultSet.getMetaData();

        System.out.println("rsmd.getColumnCount() = " + rsmd.getColumnCount());

        //I put information from database into a map
        while(resultSet.next()) {
            for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                row.put(rsmd.getColumnName(i), resultSet.getString(i));
            }
        }
        System.out.println("row = " + row);

        //expected infotmation from database
        String expectedFirstName = (String) row.get("firstname");
        String expectedLastName = (String) row.get("lastname");
        String expectedRole = (String) row.get("role");

        //I get information from api
        Map<String,Object> mapApi = response.as(Map.class);
        System.out.println("mapApi = " + mapApi);

        //actual information from api
        String actualFirstName = (String) mapApi.get("firstName");
        String actualLasttName = (String) mapApi.get("lastName");
        String actualRole = (String) mapApi.get("role");

        //compare database with api

        Assert.assertEquals(expectedFirstName,actualFirstName);
        Assert.assertEquals(expectedLastName,actualLasttName);
        Assert.assertEquals(expectedRole,actualRole);



        //get information from database
        //connection is from hooks and it will be ready
//        String query = "select firstname,lastname,role from users\n" +
//                "where email = '"+emailGlobal+"'";
//
//        Map<String,Object> dbMap = DBUtils.getRowMap(query);
//        System.out.println("dbMap = " + dbMap);
//        //save db info into variables
//        String expectedFirstName = (String) dbMap.get("firstname");
//        String expectedLastName = (String) dbMap.get("lastname");
//        String expectedRole = (String) dbMap.get("role");
//
//        //get information from api
//        JsonPath jsonPath = response.jsonPath();
//        //save api info into variables
//        String actualFirstName = jsonPath.getString("firstName");
//        String actualLastName = jsonPath.getString("lastName");
//        String actualRole = jsonPath.getString("role");
//
//        //compare database vs api
//        Assert.assertEquals(expectedFirstName,actualFirstName);
//        Assert.assertEquals(expectedLastName,actualLastName);
//        Assert.assertEquals(expectedRole,actualRole);

    }

    @Then("UI,API and Database user information must be match")
    public void uiAPIAndDatabaseUserInformationMustBeMatch() throws SQLException {

        String dbUrl = ConfigurationReader.get("qa2dbUrl");
        String dbUsername = ConfigurationReader.get("dbUsername");
        String dbPassword = ConfigurationReader.get("dbPassword");

        String query = "select firstname,lastname,role from users\n" +
                "where email = '"+emailGlobal+"'";
        System.out.println("query = " + query);

        Connection connection = DriverManager.getConnection(dbUrl,dbUsername,dbPassword);
        Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ResultSet resultSet = statement.executeQuery(query);


        Map<String,Object> row = new LinkedHashMap<>();
        ResultSetMetaData rsmd = resultSet.getMetaData();

        System.out.println("rsmd.getColumnCount() = " + rsmd.getColumnCount());



        //I put information from database into a map
        while(resultSet.next()) {
            for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                row.put(rsmd.getColumnName(i), resultSet.getString(i));
            }
        }
        System.out.println("row = " + row);

        //expected infotmation from database
        String expectedFirstName = (String) row.get("firstname");
        String expectedLastName = (String) row.get("lastname");
        String expectedRole = (String) row.get("role");

        //I get information from api
        Map<String,Object> mapApi = response.as(Map.class);
        System.out.println("mapApi = " + mapApi);

        //actual information from api
        String actualFirstName = (String) mapApi.get("firstName");
        String actualLastName = (String) mapApi.get("lastName");
        String actualRole = (String) mapApi.get("role");

//        //get information from database
//        //connection is from hooks and it will be ready
//        String query = "select firstname,lastname,role from users\n" +
//                "where email = '"+emailGlobal+"'";
//
//        Map<String,Object> dbMap = DBUtils.getRowMap(query);
//        System.out.println("dbMap = " + dbMap);
//        //save db info into variables
//        String expectedFirstName = (String) dbMap.get("firstname");
//        String expectedLastName = (String) dbMap.get("lastname");
//        String expectedRole = (String) dbMap.get("role");
//
//        //get information from api
//        JsonPath jsonPath = response.jsonPath();
//        //save api info into variables
//        String actualFirstName = jsonPath.getString("firstName");
//        String actualLastName = jsonPath.getString("lastName");
//        String actualRole = jsonPath.getString("role");

        //get information from UI
        SelfPage selfPage = new SelfPage();
        String actualUIName = selfPage.name.getText();
        String actualUIRole = selfPage.role.getText();

        System.out.println("actualUIName = " + actualUIName);
        System.out.println("actualUIRole = " + actualUIRole);

         //UI vs DB
        String expectedFullName = expectedFirstName+" "+expectedLastName;
        //verify ui fullname vs db fullname
        Assert.assertEquals(expectedFullName,actualUIName);
        Assert.assertEquals(expectedRole,actualUIRole);

        //UI vs API
        //Create a fullname for api

        String actualFullName = actualFirstName+" "+actualLastName;

        Assert.assertEquals(actualFullName,actualUIName);
        Assert.assertEquals(actualRole,actualUIRole);

    }

    @When("I send POST request to {string} endpoint with following information")
    public void i_send_POST_request_to_endpoint_with_following_information(String path, Map<String,String> studentInfo) {
        //why we prefer to get information as a map from feature file ?
        //bc we have queryParams method that takes map and pass to url as query key&value structure
        System.out.println("studentInfo = " + studentInfo);

        //assign email and password value to these variables so that we can use them later for deleting
        studentEmail = studentInfo.get("email");
        studentPassword = studentInfo.get("password");

        response = given().accept(ContentType.JSON)
                .queryParams(studentInfo)
                .and().header("Authorization",token)
                .log().all()
                .when()
                .post(ConfigurationReader.get("qa2api.url") + path)
        .then().log().all().extract().response();
        System.out.println("response.statusCode() = " + response.statusCode());


    }

    @Then("I delete previously added student")
    public void i_delete_previously_added_student() {
        BookItApiUtil.deleteStudent(studentEmail,studentPassword);
    }


    @Given("I logged Bookit api as {string}")
    public void iLoggedBookitApiAs(String role) {
       token= BookItApiUtil.getTokenByRole(role);
    }
}
