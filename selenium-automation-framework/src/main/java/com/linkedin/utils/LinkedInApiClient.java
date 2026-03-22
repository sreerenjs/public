package com.linkedin.utils;

import com.linkedin.config.ConfigReader;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

/**
 * LinkedInApiClient - REST Assured wrapper for LinkedIn API calls
 *
 * Example use case: validate job postings via API before / after UI tests,
 * or use the API to set up test data.
 *
 * LinkedIn API docs: https://learn.microsoft.com/en-us/linkedin/
 */
public class LinkedInApiClient {

    private final RequestSpecification requestSpec;

    public LinkedInApiClient() {
        RestAssured.baseURI = ConfigReader.getApiBaseUrl();

        // Build a reusable request specification with common headers
        this.requestSpec = given()
            .header("Authorization", "Bearer " + ConfigReader.getApiToken())
            .header("Content-Type",  "application/json")
            .header("Accept",        "application/json")
            .log().ifValidationFails(); // Log request details only on assertion failures
    }

    // ─── GET Requests ────────────────────────────────────────────────────────

    /**
     * GET /jobSearch - searches for jobs by keyword and location
     * Returns the raw Response for flexible assertion in tests
     */
    public Response searchJobs(String keywords, String location, int start, int count) {
        return requestSpec
            .queryParam("keywords",    keywords)
            .queryParam("location",    location)
            .queryParam("start",       start)
            .queryParam("count",       count)
            .when()
            .get("/jobSearch")
            .then()
            .extract()
            .response();
    }

    /**
     * GET /jobs/{jobId} - retrieves a single job posting by ID
     */
    public Response getJobById(String jobId) {
        return requestSpec
            .pathParam("jobId", jobId)
            .when()
            .get("/jobs/{jobId}")
            .then()
            .extract()
            .response();
    }

    /**
     * GET /me - retrieves the authenticated user's profile
     */
    public Response getMyProfile() {
        return requestSpec
            .when()
            .get("/me")
            .then()
            .extract()
            .response();
    }

    // ─── Convenience assertion helpers ───────────────────────────────────────

    /**
     * Returns true if the API is reachable (status 200 or 401)
     * 401 = reachable but invalid token, still confirms connectivity
     */
    public boolean isApiReachable() {
        try {
            int status = requestSpec
                .when()
                .get("/me")
                .then()
                .extract()
                .statusCode();
            return status == 200 || status == 401;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Extracts a JSON path value from the response
     * Example: client.extract(response, "elements[0].title.text")
     */
    public String extractValue(Response response, String jsonPath) {
        try {
            return response.jsonPath().getString(jsonPath);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Returns the status code of a response
     */
    public int getStatusCode(Response response) {
        return response.getStatusCode();
    }
}
