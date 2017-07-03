# countWebSiteUsers
Counts the number of users accesing the web site.

Shows how many people viewed the page in the last 5 minutes.
Uses two methods to keep track of last access time: a custom implementation and a built-in one.

A built-in implementation may be inaccurate, since the current time is not equal to the time that will be written as the last access time for the next request.

## Keyword

- count online users
- servlet
- java
- maven
- tomcat7
- jetty
- junit


## Run

  $ mvn tomcat7:run

or 

  $ mvn jetty:run

## Page Url

http://localhost:8080/

