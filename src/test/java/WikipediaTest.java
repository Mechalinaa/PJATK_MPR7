import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class WikipediaTest {
    private WebDriver driver;
    private WebDriverWait wait;
    private static final By SEARCH_INPUT = By.name("search");
    private static final By ARTICLE_TITLE = By.id("firstHeading");
    private static final By SUGGESTIONS_DROPDOWN = By.cssSelector("#v-0");
    private static final By FIRST_ARTICLE_LINK = By.cssSelector("div.mw-parser-output p a"); // pierwszy link w artykule
    private static final By HOMEPAGE_LINK = By.cssSelector(".mw-logo"); //wraca do strony glownej
    private static final By TABLE_OF_CONTENTS = By.id("vector-toc"); // spis tresci
    private static final By ARTICLE_IMAGES = By.cssSelector("div.mw-parser-output img");
    private static final By REFERENCES_SECTION = By.cssSelector("ol.references, div.bibliography");


    @BeforeEach
    void setUp(){
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.get("https://pl.wikipedia.org/wiki/Wikipedia:Strona_główna"); //za kazdym razem otwieramy te strone

        //cookies
        try {
            WebElement acceptCookies = driver.findElement(By.cssSelector("button#acceptButton"));
            if (acceptCookies.isDisplayed()) {
                acceptCookies.click();
            }
        } catch (NoSuchElementException ignored) {}
    }

    @AfterEach
    void tearDown(){
        if(driver != null){
            driver.quit();
        }
    }

    @DisplayName("Should display search input on homepage")
    @Test
    void shouldDisplaySearchInputOnHomePage(){
        WebElement searchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(SEARCH_INPUT));

        assertThat(searchInput.isDisplayed()).isTrue(); //sprawdzamy czy element jest na stronie
    }

    @DisplayName("Should redirect to acrticle when searching for term")
    @Test
    void shouldRedirectToArticleWhenSearchingForTerm(){
        driver.findElement(SEARCH_INPUT).sendKeys("Java" + Keys.ENTER); //wpisujemy w wyszukiwarke i zatwierdzamy
        wait.until(ExpectedConditions.urlContains("/wiki/Java"));

        assertThat(driver.getCurrentUrl()).contains("/wiki/Java"); //sprawdzamy czy link zawiera w sobie to co wyszukiwalismy
    }

    @DisplayName("Header should contain searched term")
    @Test
    void headerShouldContainSearchedTerm(){
        driver.findElement(SEARCH_INPUT).sendKeys("Java" + Keys.ENTER);
        WebElement title = wait.until(ExpectedConditions.visibilityOfElementLocated(ARTICLE_TITLE));

        assertThat(title.getText()).containsIgnoringCase("Java"); //sprawdzamy czy header zawiera szukana fraze
    }
    /// w teorii powinno dzialac
    @DisplayName("Sould display suggest when typing in search")
    @Test
    void shouldDisplaySuggestWhenTypingInSearch(){
        WebElement searchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(SEARCH_INPUT));
        searchInput.sendKeys("Java");
        WebElement suggestions = wait.until(ExpectedConditions.visibilityOfElementLocated(SUGGESTIONS_DROPDOWN));

        assertThat(suggestions.isDisplayed()).isTrue();
    }

    @DisplayName("Should navigate to a linked article from main article")
    @Test
    void shouldNavigateToLinkedArticleFromMainArticle() {
        WebElement searchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(SEARCH_INPUT));
        searchInput.sendKeys("Java" + Keys.ENTER);
        WebElement firstLink = wait.until(ExpectedConditions.elementToBeClickable(FIRST_ARTICLE_LINK));
        String urlBefore = driver.getCurrentUrl();
        firstLink.click();
        wait.until(ExpectedConditions.not(ExpectedConditions.urlToBe(urlBefore)));

        assertThat(driver.getCurrentUrl()).isNotEqualTo(urlBefore);
    }

    @DisplayName("Should navigate back to homepage when clicking homepage link")
    @Test
    void shouldNavigateBackToHomepageWhenClickingHomepageLink() {
        WebElement searchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(SEARCH_INPUT));
        searchInput.sendKeys("Java" + Keys.ENTER);
        WebElement homepageLink = wait.until(ExpectedConditions.visibilityOfElementLocated(HOMEPAGE_LINK));
        homepageLink.click();
        wait.until(ExpectedConditions.urlContains("/Wikipedia:Strona_g%C5%82%C3%B3wna"));

        assertThat(driver.getCurrentUrl()).contains("/Wikipedia:Strona_g%C5%82%C3%B3wna");
    }

    @DisplayName("Should display table of contents in article")
    @Test
    void shouldDisplayTableOfContentsInArticle() {
        WebElement searchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(SEARCH_INPUT));
        searchInput.sendKeys("Java" + Keys.ENTER);
        WebElement toc = wait.until(ExpectedConditions.visibilityOfElementLocated(TABLE_OF_CONTENTS));

        assertThat(toc.isDisplayed()).isTrue();
    }

    @DisplayName("Article header should match searched term")
    @Test
    void articleHeaderShouldMatchSearchedTerm() {
        WebElement searchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(SEARCH_INPUT));
        searchInput.sendKeys("Java" + Keys.ENTER);
        WebElement header = wait.until(ExpectedConditions.visibilityOfElementLocated(ARTICLE_TITLE));

        assertThat(header.getText()).containsIgnoringCase("Java");
    }

    @DisplayName("Article should contain at least one image")
    @Test
    void articleShouldContainAtLeastOneImage() {
        WebElement searchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(SEARCH_INPUT));
        searchInput.sendKeys("Java" + Keys.ENTER);
        List<WebElement> images = driver.findElements(ARTICLE_IMAGES);

        assertThat(images).isNotEmpty();
    }

    @DisplayName("Article should contain references or bibliography section")
    @Test
    void articleShouldContainReferencesOrBibliography() {
        WebElement searchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(SEARCH_INPUT));
        searchInput.sendKeys("Java" + Keys.ENTER);
        List<WebElement> references = driver.findElements(REFERENCES_SECTION);

        assertThat(references)
                .isNotEmpty()
                .allMatch(WebElement::isDisplayed, "References or bibliography section should be visible");
    }



}
