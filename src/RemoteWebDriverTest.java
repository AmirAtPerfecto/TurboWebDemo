import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.*;
import org.openqa.selenium.html5.*;
import org.openqa.selenium.logging.*;
import org.openqa.selenium.remote.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.Cookie.Builder;

import com.perfecto.reportium.client.ReportiumClient;
import com.perfecto.reportium.client.ReportiumClientFactory;
import com.perfecto.reportium.model.Job;
import com.perfecto.reportium.model.PerfectoExecutionContext;
import com.perfecto.reportium.model.Project;
import com.perfecto.reportium.test.TestContext;
import com.perfecto.reportium.test.result.TestResult;
import com.perfecto.reportium.test.result.TestResultFactory;

/**
 * For programming samples and updated templates refer to the Perfecto GitHub at: https://github.com/PerfectoCode
 */
public class RemoteWebDriverTest {

    public static void main(String[] args) throws MalformedURLException, IOException {
        System.out.println("Run started");
        boolean fast = true;
        RemoteWebDriver driver = null;
        long startBrowser = 0,
        		scriptExecution = 0,
        		tempTimer = 0;
        String browserName = "mobileOS";
        DesiredCapabilities capabilities = new DesiredCapabilities(browserName, "", Platform.ANY);
        String host = System.getenv().get("PERFECTO_CLOUD");
        capabilities.setCapability("user", System.getenv().get("PERFECTO_CLOUD_USERNAME"));
        capabilities.setCapability("offline-token", System.getenv().get("PERFECTO_CLOUD_TOKEN"));
        capabilities.setCapability("password", System.getenv().get("PERFECTO_CLOUD_PASSWORD"));

        //TODO: Change your device ID
        //capabilities.setCapability("deviceName", "12345");
        capabilities.setCapability("platformName", "Windows");
        capabilities.setCapability("platformVersion", "10");
        capabilities.setCapability("browserName", "Chrome");
        capabilities.setCapability("browserVersion", "58");
        capabilities.setCapability("resolution", "1280x1024");
        capabilities.setCapability("location", "US East");

        // Use the automationName capability to define the required framework - Appium (this is the default) or PerfectoMobile.
        // capabilities.setCapability("automationName", "PerfectoMobile");

        // Call this method if you want the script to share the devices with the Perfecto Lab plugin.
        PerfectoLabUtils.setExecutionIdCapability(capabilities, host);

        // Add a persona to your script (see https://community.perfectomobile.com/posts/1048047-available-personas)
        //capabilities.setCapability(WindTunnelUtils.WIND_TUNNEL_PERSONA_CAPABILITY, WindTunnelUtils.GEORGIA);

        // Name your script
        // capabilities.setCapability("scriptName", "RemoteWebDriverTest");
        tempTimer = System.currentTimeMillis();
        if (fast)
        	driver = new RemoteWebDriver(new URL("https://" + host + "/nexperience/perfectomobile/wd/hub/fast"), capabilities);
        else
        	driver = new RemoteWebDriver(new URL("https://" + host + "/nexperience/perfectomobile/wd/hub"), capabilities);
        driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
        startBrowser = (System.currentTimeMillis() - tempTimer)/1000;
        // Reporting client. For more details, see https://github.com/perfectocode/samples/wiki/reporting
        PerfectoExecutionContext perfectoExecutionContext = new PerfectoExecutionContext.PerfectoExecutionContextBuilder()
                .withProject(new Project("My Project", "1.0"))
                .withJob(new Job("My Job", 45))
                .withContextTags("tag1")
                .withWebDriver(driver)
                .build();
        ReportiumClient reportiumClient = new ReportiumClientFactory().createPerfectoReportiumClient(perfectoExecutionContext);

        try {
            reportiumClient.testStart("Shelter Insurance", new TestContext("tag2", "tag3"));

// page 1            
            reportiumClient.testStep("step1"); // this is a logical step for reporting
            tempTimer = System.currentTimeMillis();
            driver.get("https://www.shelterinsurance.com/");
            driver.findElementById("home_usr_zipcode").sendKeys("60606");
            driver.findElement(By.xpath("//A[starts-with(@href, '/CA/quote/request')]")).click();
           
// page 2            
            reportiumClient.testStep("step2"); // this is a logical step for reporting
            selectRadioButton(driver, "hasActivePolicies", 2);
            
            driver.findElementById("firstName").sendKeys("Sean");
            driver.findElementById("lastName").sendKeys("Beard");
            driver.findElementById("dateOfBirth").sendKeys("04-23-1976");
            driver.findElementById("addressLineOne").sendKeys("1 main st.");
            selectDropDown(driver, "county", 2);
            driver.findElementById("dateOfBirth").sendKeys("06101970");
            driver.findElementByClassName("button").click();

// page 3            

            reportiumClient.testStep("step3"); // this is a logical step for reporting
            selectRadioButton(driver, "vehicleBean.showVinFields", 2);
            selectDropDown(driver, "vehicleYear", 9);
            selectDropDown(driver, "vehicleMake", 2);
            selectDropDown(driver, "vehicleModel", 1);
            selectDropDown(driver, "vehiclePackage", 1);
            selectDropDown(driver, "vehiclePassiveRestraint", 1);
            driver.findElementById("oneWayMiles").sendKeys("20");
            driver.findElementById("annualMiles").sendKeys("120000");
            selectDropDown(driver, "ownershipType", 1);
            driver.findElementByClassName("button").click();

// page 4            
            
            reportiumClient.testStep("step4"); // this is a logical step for reporting           
            driver.findElementById("emailAddress").sendKeys("dan@gmail.com");
            selectRadioButton(driver, "driverBean.gender", 2);
            selectDropDown(driver, "licenseType", 1);
            driver.findElementByClassName("button").click();

// page 5            
            reportiumClient.testStep("step5"); // this is a logical step for reporting            
            Thread.sleep(5000);
            driver.findElementByClassName("button").click();

// page 6            
            
            reportiumClient.testStep("step6"); // this is a logical step for reporting            
            scriptExecution = (System.currentTimeMillis() - tempTimer)/1000;

            if (null != driver.findElementById("vehicleBeans0.primaryDriverId")) 
            	reportiumClient.testStop(TestResultFactory.createSuccess());
            else
            	reportiumClient.testStop(TestResultFactory.createFailure("Object not found", new Throwable()));
            	
        } catch (Exception e) {
            reportiumClient.testStop(TestResultFactory.createFailure(e.getMessage(), e));
            e.printStackTrace();
        } finally {
            try {
                // Retrieve the URL of the Single Test Report, can be saved to your execution summary and used to download the report at a later point
                String reportURL = reportiumClient.getReportUrl();
                System.out.println("report URL: " + reportURL);
                System.out.println("StartBrowser: " + startBrowser+ " seconds, "+ "Script Execution: "+ scriptExecution+ " seconds");
                // For documentation on how to export reporting PDF, see https://github.com/perfectocode/samples/wiki/reporting
                // String reportPdfUrl = (String)(driver.getCapabilities().getCapability("reportPdfUrl"));

                driver.close();

                // In case you want to download the report or the report attachments, do it here.
                // PerfectoLabUtils.downloadAttachment(driver, "video", "C:\\test\\report\\video", "flv");
                // PerfectoLabUtils.downloadAttachment(driver, "image", "C:\\test\\report\\images", "jpg");

            } catch (Exception e) {
                e.printStackTrace();
            }

            driver.quit();
        }

        System.out.println("Run ended");
    }
    private static void selectDropDown(RemoteWebDriver driver, String ID, int index){
    	Select dropdown = new Select(driver.findElement(By.id(ID)));
    	dropdown.selectByIndex(index);

    }
    private static void selectRadioButton(RemoteWebDriver driver, String name, int index){
    	List<WebElement> radios = driver.findElements(By.name(name));
		JavascriptExecutor executor = (JavascriptExecutor) driver;
		if (radios.size()>1)
			if (index == 1)
				executor.executeScript("arguments[0].click();", radios.get(0));
			else
				executor.executeScript("arguments[0].click();", radios.get(1));
		else
			executor.executeScript("arguments[0].click();", radios.get(0));
			
/*    	
		for (WebElement radio : radios) {
    			if (1 == index){
    				executor.executeScript("arguments[0].click();", radio);
    				return;
    			 }
				executor.executeScript("arguments[0].click();", radio);
    			
    	}
*/
		}
    private static void switchToContext(RemoteWebDriver driver, String context) {
        RemoteExecuteMethod executeMethod = new RemoteExecuteMethod(driver);
        Map<String,String> params = new HashMap<String,String>();
        params.put("name", context);
        executeMethod.execute(DriverCommand.SWITCH_TO_CONTEXT, params);
    }

    private static String getCurrentContextHandle(RemoteWebDriver driver) {
        RemoteExecuteMethod executeMethod = new RemoteExecuteMethod(driver);
        String context =  (String) executeMethod.execute(DriverCommand.GET_CURRENT_CONTEXT_HANDLE, null);
        return context;
    }

    private static List<String> getContextHandles(RemoteWebDriver driver) {
        RemoteExecuteMethod executeMethod = new RemoteExecuteMethod(driver);
        List<String> contexts =  (List<String>) executeMethod.execute(DriverCommand.GET_CONTEXT_HANDLES, null);
        return contexts;
    }
}
