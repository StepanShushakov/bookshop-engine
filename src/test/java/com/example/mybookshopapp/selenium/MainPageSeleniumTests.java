package com.example.mybookshopapp.selenium;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author karl
 */

@SpringBootTest
class MainPageSeleniumTests {

    private static ChromeDriver driver;

    @BeforeAll
    static void setup() {
        System.setProperty("webdriver.chrome.driver","/home/karl/chromedriver-linux64/chromedriver");
        driver = new ChromeDriver();
        driver.manage().timeouts().pageLoadTimeout(5, TimeUnit.SECONDS);
    }

    @AfterAll
    static void tearDown() {
        driver.quit();
    }

    private boolean pageContains(String text) {
        return driver.getPageSource().contains(text);
    }

    @Test
    public void testMainPageAccess() throws InterruptedException {
        MainPage mainPage = new MainPage(driver);
        mainPage
                .callPage()
                .pause();

        assertTrue(pageContains("BOOKSHOP"));
    }

    @Test
    public void testMainPageSearchByQuery() throws InterruptedException {
        MainPage mainPage = new MainPage(driver);
        mainPage
                .callPage()
                .pause()
                .setUpSearchToken("Sudden")
                .pause()
                .submit()
                .pause();

        assertTrue(pageContains("Sudden Manhattan"));
    }

    @Test
    public void testNavigationMove() throws InterruptedException {
        MainPage mainPage = new MainPage(driver);
        mainPage
                .callPage()
                .pause()
                .click("//*[@id=\"navigate\"]/ul/li[2]/a")  //Genres
                .pause();
        assertTrue(pageContains("Лёгкое чтение"));
        mainPage.click("/html/body/div/div/main/div/div[2]/div[2]/a").pause();  //Биографии
        assertTrue(pageContains("I Am Bruce Lee"));
        mainPage.click("//*[@id=\"navigate\"]/ul/li[3]/a").pause(); //News
        assertTrue((pageContains("from") && pageContains("to")) ||
                            (pageContains("c") && pageContains("по")));
        mainPage.click("//*[@id=\"navigate\"]/ul/li[4]/a").pause(); //Popular
        assertTrue(pageContains("Musketeers of Pig Alley, The"));
        mainPage.click("/html/body/div/div/main/div/div/div[1]/div[2]/div[2]/strong/a").pause();   //Musketeers of Pig Alley, The
        assertTrue(pageContains("Rate the book") || pageContains("Оцените книгу"));
        mainPage.click("//*[@id=\"navigate\"]/ul/li[5]/a").pause(); //Authors
        assertTrue(pageContains("Rhonda Peterson"));
        mainPage.click("/html/body/div/div/main/div/div/div[6]/div/div[2]/a").pause();  //Deeanne Rivitt
        assertTrue(pageContains("2010: Moby Dick"));
        mainPage.click("//*[@id=\"navigate\"]/ul/li[1]/a").pause(); //Main
        assertTrue((pageContains("Recommended") && pageContains("New Books") && pageContains("Popular")) ||
                (pageContains("Рекомендуемое") && pageContains("Новинки") && pageContains("Популярное")));
    }
}