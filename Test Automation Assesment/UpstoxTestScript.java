package Test1;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class UpstoxTestScript {

    WebDriver driver;

    @BeforeClass
    public void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get("https://upstox.com/");
    }

    @AfterClass
    public void tearDown() {
       // driver.quit();
    }

    @Test
    public void scriptFunction() throws InterruptedException {
        //Close the pop pup
        try {
            WebElement closeButton = driver.findElement(By.className("close-modal"));
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].click();", closeButton);
        } catch (Exception e) {
            System.out.println("Pop Not Found");
        }

        //Hover The Element
        WebElement menuLink = driver.findElement(By.id("mega-menu-item-28609"));
        Actions actions = new Actions(driver);
        actions.moveToElement(menuLink).build().perform();

        //Click SIP Calculator
        driver.findElement(By.id("menu-item-49219")).click();

        //Sending Monthly monthlySIPAmount
        double monthlyInvestment = 10000;
        WebElement monthlySIPAmount = driver.findElement(By.id("Monthly SIP amount"));
        monthlySIPAmount.clear();
        monthlySIPAmount.sendKeys("10000");

        //sending Monthly expectedReturnRate
        // Generate a random value between 16 and 24 (inclusive)
        Random random = new Random();
        int min = 16;
        int max = 24;
        int annualRate = random.nextInt((max - min) + 1) + min;
        WebElement expectedReturnRate = driver.findElement(By.id("expected-return-rate"));
        expectedReturnRate.clear();
        expectedReturnRate.sendKeys(annualRate + "");

        //Sending numbers of years Investing

        int years = 10;
        WebElement sipTenure = driver.findElement(By.id("sip-tenure"));
        sipTenure.clear();
        sipTenure.sendKeys(years + "");

        //Extracting totalValueOfInvestment
        WebElement amount = driver.findElement(By.cssSelector(".whitespace-nowrap.text-xl.font-semibold.text-success-1"));
        String str = amount.getText();
        String cleanedString = str.replaceAll("[₹,]", "").trim();
        double totalValueOfInvestment = Double.parseDouble(cleanedString);

        //Extracting Invested and return Ammount
        List<WebElement> elements = driver.findElements(By.cssSelector(".font-sm.whitespace-nowrap.font-semibold.text-dark-6"));
        String str1 = elements.get(0).getText().replaceAll("[₹,]", "").trim();;
        String str2 = elements.get(1).getText().replaceAll("[₹,]", "").trim();;

        double investedAmount = Double.parseDouble(str1);
        double totalReturns = Double.parseDouble(str2);

        //Checking the Test Case is right or Wrong
        double[] values = calculateSIPDetails(monthlyInvestment, annualRate, years);
        if(roundToTwoDecimals(values[0]).equals(roundToTwoDecimals(investedAmount)) && roundToTwoDecimals(values[1]).equals(roundToTwoDecimals(totalReturns)) && roundToTwoDecimals(values[2]).equals(roundToTwoDecimals(totalValueOfInvestment))) {
            System.out.print("This is Correct Result Test Case Success " + "\n" + "Total Invested Amount: " + totalValueOfInvestment + "\n" + "Total Return You Gained: " + totalReturns+ "\n" + "Invested Amount: " + investedAmount);
        } else {
            System.out.println(totalValueOfInvestment + "  " + roundToTwoDecimals(values[2]));
            System.out.println(totalReturns + "  "+ roundToTwoDecimals(values[1]));
            System.out.println(roundToTwoDecimals(investedAmount) + "  " + roundToTwoDecimals(values[0]));
            System.out.print("Test Case Failed");
        }

        System.out.println();

        //Start Investing Button Colour
        WebElement startInvesting = driver.findElement(By.xpath("//*[contains(@class, 'cursor-pointer') and contains(@class, 'px-4')]"));
        String initialColor = startInvesting.getCssValue("background-color");
        String hexColor = rgbToHex(initialColor);
        System.out.println("Initial Button Background Color: " + hexColor);

        //Browser will automatically close
        //tearDown();

    }

    //RGB TO #Colour
    public static String rgbToHex(String rgb) {
        String[] rgbValues = rgb.replaceAll("[^0-9,]", "").split(",");

        int r = Integer.parseInt(rgbValues[0].trim());
        int g = Integer.parseInt(rgbValues[1].trim());
        int b = Integer.parseInt(rgbValues[2].trim());

        String hexColor = String.format("#%02X%02X%02X", r, g, b);

        return hexColor;
    }

    //SIP Calculator
    public static double[] calculateSIPDetails(double monthlyInvestment, double annualRate, int years) {
        int months = years * 12;
        double monthlyRate = annualRate / 12 / 100;

        double futureValue = monthlyInvestment * (Math.pow(1 + monthlyRate, months) - 1) / monthlyRate * (1 + monthlyRate);

        double totalInvested = monthlyInvestment * months;

        double totalReturns = futureValue - totalInvested;

        return new double[] {totalInvested, totalReturns, futureValue};

//        var investment = 800; //principal amount
//        var annualRate = 2;
//        var monthlyRate = annualRate / 12 / 100;  //Rate of interest
//        var years = 30;
//        var months = years * 12;  //Time period
//        var futureValue = 0; //Final Value
//
//        futureValue = investment * (Math.pow(1 + monthlyRate, months) - 1) /
               // monthlyRate;
    }

    public static BigDecimal roundToTwoDecimals(double value) {
        String s = Double.toString(value);
        for (int i = 0; i < s.length(); i++) {
            if(s.charAt(i) == '.') {
                if(i + 2 == s.length()) {
                    s = s + '0';
                }
            }
        }

        BigDecimal bd = new BigDecimal(s);
        bd = bd.setScale(2, RoundingMode.HALF_UP); // Rounding to 2 decimal places
        return bd;
    }
}
