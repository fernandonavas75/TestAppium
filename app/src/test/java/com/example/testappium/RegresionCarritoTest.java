package com.example.testappium;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.AppiumBy;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.Assert;
import java.net.URL;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
//Importaciones para la generacion del reporte en HTML para transformarlo en pdf//
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import org.junit.BeforeClass;
import org.junit.AfterClass;

@RunWith(Parameterized.class)
public class RegresionCarritoTest {

    private String producto;
    private int unidades;
    private AndroidDriver driver;
    private WebDriverWait wait;

    private static ExtentReports extent;
    private static ExtentSparkReporter spark;

    @BeforeClass
    public static void setupReport(){

        if (extent == null) {
            spark = new ExtentSparkReporter("build/reports/TestRegresion.html");
            extent = new ExtentReports();
            extent.attachReporter(spark);

            // Información adicional para que este mas bonito
            extent.setSystemInfo("Ambiente", "QA - Regresión");
            extent.setSystemInfo("App", "Sauce Labs Demo");
        }
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                { "Sauce Labs Backpack", 1 },
                { "Sauce Labs Bolt T-Shirt", 1 },
                { "Sauce Labs Bike Light", 2 }
        });
    }

    public RegresionCarritoTest(String producto, int unidades) {
        this.producto = producto;
        this.unidades = unidades;
    }

    @Test
    public void validarCarritoCompras() throws Exception {
        // Se crea un nodo de test nuevo para cada iteración del parámetro
        ExtentTest logger = extent.createTest("Validar Producto: " + producto);

        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("platformName", "Android");
        caps.setCapability("appium:automationName", "UiAutomator2");
        caps.setCapability("appium:deviceName", "emulator-5554");
        caps.setCapability("appium:app", "C:\\Users\\navas\\Downloads\\mda-2.0.2-23 (1).apk");
        caps.setCapability("appium:appWaitActivity", "com.saucelabs.mydemoapp.android.view.activities.MainActivity, com.saucelabs.mydemoapp.android.view.activities.SplashActivity");
        caps.setCapability("appium:noReset", false);

        driver = new AndroidDriver(new URL("http://127.0.0.1:4723/wd/hub"), caps);
        //Esto es porque mi compu es lenta y el emulador no ayuda
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        try {
            logger.info("Iniciando automatización para " + producto);

            wait.until(ExpectedConditions.visibilityOfElementLocated(AppiumBy.id("com.saucelabs.mydemoapp.android:id/productRV")));
            /*
            * Por un momento pense que no se leia el producto es por eso q hice un scrollable para que leyera todos los productos que estaban
            * en el catalogo pero al final ah sido error de la aplicacion mismo (es por este elemento)
            */
            String scrollCommand = "new UiScrollable(new UiSelector().scrollable(true)).scrollIntoView(new UiSelector().description(\"" + producto + "\"))";
            driver.findElement(AppiumBy.androidUIAutomator(scrollCommand)).click();

            if (unidades > 1) {
                WebElement btnPlus = wait.until(ExpectedConditions.elementToBeClickable(AppiumBy.accessibilityId("Increase item quantity")));
                for (int i = 1; i < unidades; i++) { btnPlus.click(); }
            }

            wait.until(ExpectedConditions.elementToBeClickable(AppiumBy.accessibilityId("Tap to add product to cart"))).click();

            WebElement badgeCarrito = wait.until(ExpectedConditions.visibilityOfElementLocated(AppiumBy.id("com.saucelabs.mydemoapp.android:id/cartTV")));
            String textoBadge = badgeCarrito.getText();

            Assert.assertEquals("ERROR: Unidades incorrectas", String.valueOf(unidades), textoBadge);
            //esto principialmente es para que se grabe en el Reporte final que se hace en los HTML.
            logger.pass("EXITOSO: Se agregaron " + textoBadge + " unidades de " + producto);

        } catch (Throwable e) {
            //Bueno esto es para que se guarde el fallo y se presente en el reporte
            logger.fail("FALLO EN APK: " + e.getMessage());
            Assert.fail("Fallo en el producto " + producto + ": " + e.getMessage());
        } finally {
            if (driver != null) { driver.quit(); }
        }
    }

    @AfterClass
    public static void tearDownReport() {
        // El flush() escribe TODOS los resultados acumulados en el archivo
        if (extent != null) {
            extent.flush();
        }
    }
}