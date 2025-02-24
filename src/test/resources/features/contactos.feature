Feature: To add a new contact
  To add a new contact to the agenda

  Scenario: Add new contact
    Given I am in the contacts list page
    When II provide "Oscar" for the name
    And I provide "Belmonte" for the surname
    And I provide "123" for the nif
    And I click the New button
    Then The person with nif "123" is created in the agenda