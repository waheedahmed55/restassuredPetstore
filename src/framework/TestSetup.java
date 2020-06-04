package framework;

import com.aventstack.extentreports.AnalysisStrategy;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.testng.annotations.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class TestSetup {
    public static Date testStartTime = new Date();
    public static Exception lastException;
    protected static String applicationURL;
    private static String reportDir;

    public static ExtentHtmlReporter htmlReporter;
    public static ExtentReports reports;
    public static ExtentTest methodInfo;
    public static ExtentTest testInfo;
    public static String testName;

    @BeforeSuite
    @Parameters ({"App_URL", "Rep_Dir"})
    public static void setUp (@Optional String App_URL,
                              @Optional String Rep_Dir) {

        // initialize optional variables
        applicationURL = TestData.getSuiteProperty("App_URL", App_URL, "");
        reportDir = TestData.getSuiteProperty("Report_Directory", Rep_Dir, "reports");

        // initialize report system

        String reportName = new SimpleDateFormat("yyyyMMddhhmmss").format(testStartTime);
        reportName = reportDir + "/" + reportName + ".html";

        htmlReporter = new ExtentHtmlReporter(reportName);
        htmlReporter.config().setDocumentTitle("Automation Report");
        htmlReporter.config().setReportName("Smoke Test Report");
        htmlReporter.config().setTheme(Theme.STANDARD);
        reports = new ExtentReports();
        reports.setAnalysisStrategy(AnalysisStrategy.TEST);
        reports.attachReporter(htmlReporter);
        reports.setSystemInfo("Environment", "QA");
        reports.setSystemInfo("Hostname", System.getProperty("user.name"));
        reports.setSystemInfo("OS Version", System.getProperty("os.name"));
        reports.setSystemInfo("OS Architecture", System.getProperty("os.arch"));
        reports.setSystemInfo("Java Version", System.getProperty("java.runtime.version"));
        reports.setSystemInfo("Tester Name", "Automation Tester");
    }

    @AfterSuite
    public static void tearDown() {
        reports.flush();
    }

}
