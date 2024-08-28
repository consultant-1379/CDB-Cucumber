package com.ericsson.eniq.Cucumber.adminUIHardCode;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.Select;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import remoteExecuter.RemoteExecutor;

public class Steps {
	WebDriver driver = null;
	List<WebElement> start_time = null;
	List<WebElement> stop_time = null;
	String PATH = "/eniq/data/pmdata/eniq_oss_1";
	HashMap<String, String> nodeTypes_dir = new HashMap<String, String>();
	HashMap<String, String> List_Of_NodeTypes_and_Interfaces = new HashMap<String, String>();

	@Given("^Get the list of input directories from the server and Interface details for each folder$")
	public void get_the_list_of_input_directories_from_the_server_and_Interface_details_for_each_folder()
			throws Throwable {
		ArrayList<String> al = new ArrayList<String>();
		ArrayList<String> node = new ArrayList<String>();
		ArrayList<String> interface_name = new ArrayList<String>();

		File f = new File("src/test/resource/nodeTypes_with_Interfaces.txt");
		Scanner r = null;
		r = new Scanner(f);

		while (r.hasNext()) {
			String current = r.nextLine();
			al.add(current);
		}
		for (int i = 0; i < al.size(); i++) {
			if (al.get(i).contains("inDir")) {
				String st2 = al.get(i).substring(0, al.get(i).length() - 1);
				node.add(st2);
			} else {
				if (al.get(i).contains("interfaceName")) {
					interface_name.add(al.get(i));
				}
			}
		}
		for (int j = 0; j < node.size(); j++) {
			List_Of_NodeTypes_and_Interfaces.put(node.get(j), interface_name.get(j));
		}
	}

	
	@When("^Open Chrome and start application and naviagte to adminUI URL And I enter valid username and valid password And click on LogIn button$")
	public void open_Chrome_and_start_application_and_naviagte_to_adminUI_URL_And_I_enter_valid_username_and_valid_password_And_click_on_LogIn_button()
			throws Throwable {
		String nodeTypeList = RemoteExecutor.executeComand("dcuser", "dcuser", "atvts4147.athtem.eei.ericsson.se",
				"cd /eniq/data/pmdata/eniq_oss_1/; find . -print | grep -i '.*[.]xml'  | cut -d '/' -f2  | grep -v xml");
		System.out.println("#################" + nodeTypeList);
		if (nodeTypeList.isEmpty()) {
			Assert.fail("ERROR: Found no input files in the path /eniq/data/pmdata/eniq_oss_1/ ");

		} else {
			String[] array = nodeTypeList.split("\n");

			System.out.println(Arrays.toString(array));

			String[] array1 = new HashSet<String>(Arrays.asList(array)).toArray(new String[0]);
			System.out.println("WWWWWWWWWWWWWWWWWWW" + array1.toString());

			// Folder and its interface
			int i = 0;
			for (String s : array1) {
				nodeTypes_dir.put(s, array1[i]);
				i++;
			}
			System.out.println("map1 output: *********** : " + nodeTypes_dir.keySet());

			for (String entry : nodeTypes_dir.keySet()) {
				for (String interfaces : List_Of_NodeTypes_and_Interfaces.keySet()) {
					if (interfaces.contains(entry)) {
						System.out.println("############################## Found");
						String value_of_key = List_Of_NodeTypes_and_Interfaces.get(interfaces);
						System.out.println("$$$$$$$$$$$$$$$$$$$" + value_of_key);
						String interface_of_node = value_of_key.substring(value_of_key.lastIndexOf("=") + 1,
								value_of_key.lastIndexOf(""));
						System.out.println("########" + interface_of_node);
						String filename = interface_of_node + "-eniq_oss_1";
						// Finding Interface

						System.out.println(filename);

						//System.setProperty("webdriver.firefox.bin", "/app/firefox/3.6.12/LMWP3/firefox");						
						//System.setProperty("webdriver.gecko.driver", "/usr/bin/firefox");
						//driver = new FirefoxDriver();
						//driver = new ChromeDriver();						
						//System.setProperty("webdriver.gecko.driver", "src/test/resource/WebDrivers/geckodriver");
						// DesiredCapabilities capability = DesiredCapabilities.firefox();
						//driver = new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), capability);
						
						
						System.setProperty("webdriver.gecko.driver", "/app/firefox/3.6.12/LMWP3/firefox");
						driver = new FirefoxDriver();
						
						
						
						driver.get("https://atvts4147.athtem.eei.ericsson.se:8443/");
						TimeUnit.SECONDS.sleep(1);
						if (driver.getCurrentUrl().contains("LoaderStatusServlet")) {
							driver.findElement(By.id("username")).sendKeys("eniq");
							driver.findElement(By.id("password")).sendKeys("eniq");
							driver.findElement(By.id("submit")).click();
						} else {
							Assert.fail("ERROR: Failed to Login as 'eniq' user");
						}

						driver.findElement(By.xpath("//a[@href='/adminui/servlet/ETLRunSetOnce']")).click();
						System.out.println("Redirected to 'ETLC Set Scheduling'");

						if (driver.getCurrentUrl().contains("ETLRunSetOnce")) {
							Select dropdown_setType = new Select(driver.findElement(By.name("settype")));
							dropdown_setType.selectByVisibleText("Interface");
							System.out.println("Selected 'Set Type' as : Interface \n");
							if (driver.getCurrentUrl().contains("Interface")) {
								Select dropdown_package = new Select(driver.findElement(By.name("packageSets")));
								dropdown_package.selectByVisibleText(filename);
								System.out.println("Selected 'Package' as : " + filename + "\n");
								if (driver.getCurrentUrl().contains(filename)) {
									List<WebElement> allCountrymvalue = driver
											.findElements(By.cssSelector("tbody>tr>td>table#ttable>tbody>tr"));
									List<WebElement> alllinks = driver.findElements(
											By.cssSelector("tbody>tr>td>table#ttable>tbody>tr>td>font>a"));
									for (int i1 = 1; i1 < allCountrymvalue.size(); i1++) {
										System.out.println("Value are : " + allCountrymvalue.get(i1).getText()
												+ "== Corresponding link is : " + alllinks.get(i1 - 1).getText()
												+ "\n");

										if (allCountrymvalue.get(i1).getText().contains("Adapter")) {
											System.out.println("Starting the Adpater...\n");
											alllinks.get(i1 - 1).click();
											System.out.println("Adapter Started...\n");
											break;
										} else {
											System.out.println(
													"ERROR: Adapter's corresponding link 'Start' link is worng or Unable to start the Adapter");
										}
									}
								} else {
									System.out.println("ERROR: Adapter page not found");
								}
							} else {
								System.out.println("ERROR: 'Interface' Page not found'");
							}
						} else {
							System.out.println("ERROR: ETLC Set Scheduling (ETLRunSetOnce) page is not found");
						}

						System.out.println("waiting for 60 sec");
						TimeUnit.SECONDS.sleep(5);
						System.out.println("Test");

						driver.findElement(By.xpath("//a[@href='/adminui/servlet/ETLHistory']")).click();
						System.out.println("Redirected to 'ETLC Set History'");

						if (driver.getCurrentUrl().contains("ETLHistory")) {
							Select dropdown_selectedpackage = new Select(driver.findElement(By.name("selectedpack")));
							dropdown_selectedpackage.selectByVisibleText(filename);
							System.out.println("Selected 'Package' as : " + filename + "\n");
							Select dropdown_selectedsettype = new Select(
									driver.findElement(By.name("selectedsettype")));
							dropdown_selectedsettype.selectByVisibleText("Adapter");
							System.out.println("Selected 'Set Type' as : Adapter \n");

							driver.findElement(By.xpath("//input[@type='submit']")).click();
							System.out.println("Clicked on Search");
						} else {
							System.out.println("ERROR: ETLHistory page is not found");
						}

						List<WebElement> link = driver.findElements(By.cssSelector(
								"body > table > tbody > tr:nth-child(1) > td:nth-child(2) > form > table:nth-child(3) > tbody > tr:nth-child(2) > td:nth-child(1) > font > a"));
						link.get(0).click();

						List<WebElement> start_time = driver.findElements(By.cssSelector(
								"body > table > tbody > tr:nth-child(1) > td:nth-child(2) > table:nth-child(3) > tbody > tr:nth-child(4) > td:nth-child(2)"));
						String start = start_time.get(0).getText();
						System.out.println("START TIME : " + start + "\n");

						List<WebElement> stop_time = driver.findElements(By.cssSelector(
								"body > table > tbody > tr:nth-child(1) > td:nth-child(2) > table:nth-child(3) > tbody > tr:nth-child(5) > td:nth-child(2)"));
						String stop = stop_time.get(0).getText();
						System.out.println("STOP TIME  : " + stop + "\n");

						DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

						try {

							Date d1 = sdf.parse(start);
							Date d2 = sdf.parse(stop);
							System.out.println(d1 + "   " + d2);
							// in milliseconds
							long diff = d2.getTime() - d1.getTime();

							long diffSeconds = diff / 1000 % 60;
							long diffMinutes = diff / (60 * 1000) % 60;
							long diffHours = diff / (60 * 60 * 1000) % 24;
							long diffDays = diff / (24 * 60 * 60 * 1000);

							System.out.println("\n Time difference : \n");

							System.out.print(diffDays + " days, ");
							System.out.print(diffHours + " hours, ");
							System.out.print(diffMinutes + " minutes, ");
							System.out.print(diffSeconds + " seconds.");

							System.out.println("\n\n");
							if (diffMinutes > 15 || driver.getPageSource().contains("WARNING")
									|| driver.getPageSource().contains("ERROR")) {
								System.out.println("Test case failed");
								driver.close();
								Assert.fail("ERROR: In " + filename
										+ " Either log has WARNING/ERRORmessages or exceeded the ROP time");
								continue;
							} else {
								System.out.println("Adapter sets is parsed complete input files in a ROP \n");
								System.out.println("Test case passed");
								driver.close();
							}

						} catch (Exception e) {
							e.printStackTrace();
						}

					}
				}

			}
		}
	}

	@Then("^Measure time taken by  adapter sets to parse complete input files in a ROP$")
	public void measure_time_taken_by_adapter_sets_to_parse_complete_input_files_in_a_ROP() throws Throwable {
	}

	@Given("^Get the START and STOP time of adapter set details from previous scenario$")
	public void get_the_START_and_STOP_time_of_adapter_set_details_from_previous_scenario() throws Throwable {
	}

	@When("^Read the START and STOP time from server$")
	public void read_the_START_and_STOP_time_from_server() throws Throwable {
	}

	@Test
	@Then("^Validate the START and STOP time$")
	public void validate_the_START_and_STOP_time() throws Throwable {
	}

}
