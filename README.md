# Web Application Automation Using Selenium & TestNG (Cross Browsers)

This project serves as a boilerplate for automating web applications across various browsers and environments using TestNG and the Selenium library.

[![Selenium Java Web UI Automation CI](https://github.com/osandadeshan/selenium-java-web-automation-demo/actions/workflows/selenium-java-ci.yml/badge.svg?branch=master)](https://github.com/osandadeshan/selenium-java-web-automation-demo/actions/workflows/selenium-java-ci.yml)

## Prerequisites
1. Java
2. Maven
3. Chrome

## How to run tests
1. Using IntelliJ IDEA
    * Go to Maven Profiles
    * Select `chrome`, `headless-chrome`, `firefox`, `headless-firefox`, `edge`, `ie` or `safari` Maven Profile as the browser
    * Select `dev`, `qa`, `uat`, `pre-prod` or `prod` Maven Profile as the environment
    * Select the test classes on the `src/test/java` folder
    * Right-click and click on `Run`


2. Using Command Line
    * To run the smoke test suite in Firefox browser against the QA environment

      `mvn clean test -Pfirefox,qa,smoke-test`
    * To run the regression test suite in Safari browser against the UAT environment

      `mvn clean test -Psafari,uat,regression-test`

**Note**: By default, if no Maven profiles are selected, the tests will be executed on the `chrome` browser and in the `dev` environment.
# Selenium_E2E
