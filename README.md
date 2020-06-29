# minesweeper

~~~
           _                                                   
          (_)                                                  
 _ __ ___  _ _ __   ___  _____      _____  ___ _ __   ___ _ __ 
| '_ ` _ \| | '_ \ / _ \/ __\ \ /\ / / _ \/ _ \ '_ \ / _ \ '__|
| | | | | | | | | |  __/\__ \\ V  V /  __/  __/ |_) |  __/ |   
|_| |_| |_|_|_| |_|\___||___/ \_/\_/ \___|\___| .__/ \___|_|   
                                              | |              
                                              |_|          
~~~

# Architecture Specifications and Notes

  - **Web Server:** It is implemented on scala using akka http framework. It implements a rest api. 
    You can find the specification on https://stoplight.io/p/docs/gh/rrobledo/minesweeper, It is
     documented using swagger (https://github.com/rrobledo/minesweeper/blob/master/docs/api.yaml) 
  - **Persistence:** It is over mongodb database, and the service is using the scala mongodb driver.
  - **Client Library:** A javascript client library has been implemented in order to be integrated with your html5 UI.
    (https://github.com/rrobledo/minesweeper/blob/master/client/lib/minesweeper.js) 
  - **User Interface:** A web page for testing the application has been implemented and deployed on 
    http://44.231.173.225
    
# Running

1. Starting Docker compose and dependencies
    ```
    cd /compose
    docker-compose up -d
    ```

2. Execute Application
    ```
    sbt run
    ```

3. Execute Testing
    ```
    sbt test
    ```

4. Execute Coverage Report
    ```
    sbt coverageReport
    ```

