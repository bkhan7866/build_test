package build_test;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

/**
 * 
 * @author Bilal Khan
 *
 */
public class BuildTest {

	private static final String SEARCH_ID_INPUT_HOME_PAGE = "search_txt";
	private static final String SEARCH_CLASS_BUTTON_HOME_PAGE = "search-site-search";
	private static final String SEARCH_ID_INPUT_CART_PAGE = "headerSearchInput";
	private static final String SEARCH_ID_BUTTON_CART_PAGE = "headerSearch";

	private static final String URL = "http://www.build.com";
	
	private DecimalFormat numberFormat;

	private WebDriver driver;

	@Before
	public void configure(){
		driver = new FirefoxDriver();
		driver.get(URL);
		driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
		
		//Setup the format of Decimal as a RoundingMode = DOWN
		numberFormat = new DecimalFormat("#.00");
		numberFormat.setRoundingMode(RoundingMode.DOWN);
	}

	@Test
	public void test() {
		searchItemHomePage("Suede Kohler K­6626­6U");
		addIntoCart();
		searchItemCartPage("Cashmere Kohler K­6626­6U");
		addIntoCart();
		searchItemCartPage("Kohler K­6066­ST");
		setQuantity("2");
		addIntoCart();
		
		driver.findElement(By.className("icon-shopping-cart")).click();
		driver.findElement(By.className("icon-secure")).click();
		driver.findElement(By.name("guestLoginSubmit")).click();
		
		fillGuestFields();
		
		driver.findElement(By.className("js-checkout-review")).click();
		
		//Getting the subtotal amount
		String subTotalAmount = driver.findElement(By.id("subtotalamount")).getText();
		Double subTotalDouble = replaceMonetaryCharacters(subTotalAmount);
		Double taxCharged = subTotalDouble * 0.075;
		String taxChargedFormatted = numberFormat.format(taxCharged);
		
		//Getting the tax amount
		String taxAmount = driver.findElement(By.id("taxAmount")).getText();
		Double taxAmountDouble = replaceMonetaryCharacters(taxAmount);
		String taxAmoutFormatted = numberFormat.format(taxAmountDouble);

		//Verifying if the calculated tax is the same that tax amount showed on the page 
		assertThat(taxChargedFormatted).isEqualTo(taxAmoutFormatted);
		
		//Sum the subTotal and the tax charged
		Double subTotalPlusTax = subTotalDouble + taxCharged;
		String subTotalPlusTaxFormatted = numberFormat.format(subTotalPlusTax);
		
		//Getting the total amount
		String grandtotalamount = driver.findElement(By.id("grandtotalamount")).getText();
		Double grandtotalamountDouble = replaceMonetaryCharacters(grandtotalamount);
		String grandtotalamountFormatted = numberFormat.format(grandtotalamountDouble);
		
		//Verifying if the calculated total is the same that total amount showed on the page
		assertThat(grandtotalamountFormatted).isEqualTo(subTotalPlusTaxFormatted);
		
	}

	private double replaceMonetaryCharacters(String subTotalAmount) {
		return Double.parseDouble(subTotalAmount.replace("$","").replace(",",""));
	}

	private void fillGuestFields() {
		driver.findElement(By.id("shippingfirstname")).sendKeys("Jim");
		driver.findElement(By.id("shippinglastname")).sendKeys("Jones");
		driver.findElement(By.id("shippingaddress1")).sendKeys("5555 East Ave.");
		driver.findElement(By.id("shippingpostalcode")).sendKeys("95928");
		driver.findElement(By.id("shippingcity")).sendKeys("Chico");
		driver.findElement(By.id("shippingstate_1")).sendKeys("California");
		driver.findElement(By.id("shippingphonenumber")).clear();
		driver.findElement(By.id("shippingphonenumber")).sendKeys("5555555555");
		driver.findElement(By.id("creditCardNumber")).sendKeys("4111111111111111");
		driver.findElement(By.id("creditCardMonth")).sendKeys("10");
		driver.findElement(By.id("creditCardYear")).sendKeys("2023");
		driver.findElement(By.id("creditcardname")).clear();
		driver.findElement(By.id("creditcardname")).sendKeys("Jim Jones");
		driver.findElement(By.id("creditCardCVV2")).sendKeys("331");
		driver.findElement(By.id("emailAddress")).sendKeys("johndoe@gmail.com");
	}

	private void addIntoCart() {
		driver.findElement(By.className("addToCart")).click();
	}

	private void searchItemHomePage(String itemDescription) {
		WebElement headerSearchInput = driver.findElement(By.id(SEARCH_ID_INPUT_HOME_PAGE));
		headerSearchInput.sendKeys(itemDescription);
		driver.findElement(By.className(SEARCH_CLASS_BUTTON_HOME_PAGE)).click();
	}
	
	private void searchItemCartPage(String itemDescription) {
		WebElement headerSearchInput = driver.findElement(By.id(SEARCH_ID_INPUT_CART_PAGE));
		headerSearchInput.sendKeys(itemDescription);
		driver.findElement(By.id(SEARCH_ID_BUTTON_CART_PAGE)).click();
	}
	
	private void setQuantity(String qty){
		WebElement qtyInput = driver.findElement(By.id("qtyselected"));
		qtyInput.clear();
		qtyInput.sendKeys(qty);
	}

	@After
	public void close(){
		driver.close();
	}

}
