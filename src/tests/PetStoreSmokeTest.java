package tests;

import framework.TestData;
import io.restassured.RestAssured;
import io.restassured.response.Response;

import framework.TestSetup;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.util.Date;

public class PetStoreSmokeTest extends TestSetup {
    private Date timeStamp;
    private String pet_name;
    private long pet_id;
    private String pet_json;

    @BeforeClass
    public void beforeClass() {  }

    @BeforeMethod
    public void beforeMethod() { timeStamp = new Date(); }

    @Parameters({"pet_name"})
    public PetStoreSmokeTest(String pet_name) {
        this.pet_name = pet_name;
    }

    @Test(alwaysRun = true, description = "Verify if the Test can be run in this environment.")
    public void ifTestCanBeRun() {
        // verify if correct test data provided by the test suite.
        if (TestData.getPet("photoUrl", pet_name).isEmpty())
            throw new SkipException("The test class is skipped because Pet test data is not available.");
    }

    @Test(dependsOnMethods = {"ifTestCanBeRun"}, description = "Verify if new Pet can be added.")
    public void ifPetCanBeAdded() {
        String ep_postNewPet = "/pet";
        String ep_body = "{\"name\":\"" + pet_name + "\",\"photoUrls\":[\"" + TestData.getPet("photoUrl", pet_name) + "\"]}";
        Response response;

        response = RestAssured
                .given()
                    .baseUri(applicationURL).header("Content-Type", "application/json").body(ep_body)
                .post(ep_postNewPet);

        Assert.assertEquals(response.statusCode(), 200,"Pet \\\"\" + pet_name + \"\\\" is not added. HTTP error: ");
        pet_id = TestData.getLong(response.getBody().print(), "id");
    }

    @Test(dependsOnMethods = {"ifPetCanBeAdded"}, description = "Verify if new Pet can be found by id.")
    public void ifPetCanBeFoundById() {
        String ep_getPetById = "/pet/{petId}";
        Response response;

        response = RestAssured
                .given()
                    .baseUri(applicationURL).pathParam("petId", pet_id)
                .get(ep_getPetById);
        Assert.assertEquals(response.statusCode(), 200,"Pet \"" + pet_name + "\" is not found by id " + pet_id + ". HTTP error: ");
        pet_json = response.getBody().print();
    }

    @Test(dependsOnMethods = {"ifPetCanBeFoundById"}, description = "Verify if existing Pet can be modified.")
    public void ifPetCanBeModified() {
        String ep_putPetById = "/pet";
        String oldStatus = TestData.getString(pet_json, "status");
        String ep_body; // = "{\"id\":" + pet_id + ",\"status\":\"available\"}";
        Response response;

        if (oldStatus.isEmpty())
            ep_body = pet_json.substring (0,pet_json.length()-1) + ",\"status\":\"available\"}";
        else
            ep_body = pet_json.replaceFirst("\"status\":[.]*\"" + oldStatus + "\"", "\"status\":\"available\"}");

        response = RestAssured
                .given()
                    .baseUri(applicationURL).header("Content-Type", "application/json").body(ep_body)
                .put(ep_putPetById);
        Assert.assertEquals(response.statusCode(), 200,"Pet \"" + pet_name + "\" is not updated. HTTP error: ");
    }

    @Test(dependsOnMethods = {"ifPetCanBeModified"}, description = "Verify if existing Pet can be deleted.")
    public void ifPetCanBeDeleted() {
        String ep_deletePetById = "/pet/{petId}";
        Response response;

        response = RestAssured
                .given()
                .baseUri(applicationURL).pathParam("petId", pet_id)
                .delete(ep_deletePetById);
        Assert.assertEquals(response.statusCode(), 200,"Pet \"" + pet_name + "\" is not deleted. HTTP error: ");
    }

}
