package com.example.testappium;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.AppiumBy;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.junit.Test;
import java.net.URL;
import java.time.Duration;

public class LoginTest {

    @Test
    public void miPrimerTest() throws Exception {
        // 1. CONFIGURACIÓN
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("platformName", "Android");
        caps.setCapability("appium:automationName", "UiAutomator2");
        caps.setCapability("appium:deviceName", "emulator-5554");
        //caps.setCapability("appium:app", "C:\\Users\\navas\\Downloads\\mda-2.0.2-23 (1).apk");/*"C:\\Users\\navas\\Downloads\\mda-2.0.2-23 (1).apk"*/

        // Ayuda a que Appium no se desespere con el SplashActivity
        caps.setCapability("appium:appWaitActivity", "com.saucelabs.mydemoapp.android.view.activities.MainActivity, com.saucelabs.mydemoapp.android.view.activities.SplashActivity");

        // 2. INICIALIZACIÓN
        AndroidDriver driver = new AndroidDriver(new URL("http://127.0.0.1:4723/wd/hub"), caps);

        // 3. CREAR EL "ESPERADOR" (WebDriverWait)
        // Configuramos un tiempo máximo de 20 segundos
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        try {
            // --- PASOS CON ESPERAS EXPLÍCITAS ---

            // Paso 1: Esperar a que el menú sea cliqueable y hacer click
            wait.until(ExpectedConditions.elementToBeClickable(AppiumBy.accessibilityId("View menu"))).click();
            System.out.println("Clic en el menú realizado");

            // Paso 2: Esperar el Log In (Usando tu XPATH)
            String xpathLogin = "//android.widget.TextView[@resource-id=\"com.saucelabs.mydemoapp.android:id/itemTV\" and @text=\"Log In\"]";
            wait.until(ExpectedConditions.visibilityOfElementLocated(AppiumBy.xpath(xpathLogin))).click();
            System.out.println("Clic en Log In realizado");

            // Paso 3: Esperar el campo de Usuario y escribir
            wait.until(ExpectedConditions.visibilityOfElementLocated(AppiumBy.id("com.saucelabs.mydemoapp.android:id/nameET"))).sendKeys("bob@example.com");

            // Paso 4: Escribir contraseña
            driver.findElement(AppiumBy.id("com.saucelabs.mydemoapp.android:id/passwordET")).sendKeys("10203040");

            // Paso 5: Click en el botón de entrar
            wait.until(ExpectedConditions.elementToBeClickable(
                    AppiumBy.xpath("//android.widget.Button[@content-desc=\"Tap to login with given credentials\"]")
            )).click();
            System.out.println("Test finalizado con éxito");

            // Espera visual para el humano
            Thread.sleep(5000);

        } catch (Exception e) {
            System.out.println("El test falló en algún paso: " + e.getMessage());
            throw e;
        } finally {
           // driver.quit();
        }
    }
}