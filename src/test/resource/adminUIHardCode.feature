#Author: XARUNHA
Feature: Admin UI Test

  @tag1
  Scenario: Measure time taken by  adapter sets to parse complete input files in a ROP  by using Admin UI
    Given Get the list of input directories from the server and Interface details for each folder
    When Open Chrome and start application and naviagte to adminUI URL And I enter valid username and valid password And click on LogIn button
    Then Measure time taken by  adapter sets to parse complete input files in a ROP

  @tag2
  Scenario: Validate the START and STOP time with AdminUI
    Given Get the START and STOP time of adapter set details from previous scenario
    When Read the START and STOP time from server
    Then Validate the START and STOP time
