Feature: GateMate Home Page

  Scenario: Regular User Registers 
    Given the user is on the homepage
    When the user clicks on the login button
    Then the user is redirected to the login page
    Given the user doesnt have an account
    When the user clicks in register account
    Then the user is redirected to the register page
    Given the user enters "asdsa@gmail.com" as email to register
    And the user enters "123" as register password
    And the user enters "123" to confirm the password
    And the user selects "User" as role 
    When the user clicks to register
    Then the user is registed and redirected to the login

  Scenario: Old User Login
    Given the user is on the homepage
    When the user clicks on the login button
    Then the user is redirected to the login page
    Given the user enters "goncaloferreira07@gmail.com" as email
    And the user enters "123" as password
    When the user clicks to login
    Then the user loggedin and is redirected to the homepage


