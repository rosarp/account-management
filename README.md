# account-management
### Simple REST service with very basic functionality to manage accounts and its balance.

##### URL :
	
	http://localhost:18080

##### End Points :
	
###### GET

###### 1.
```
/v1/accounts/Id-123
	Where Id-123 is account id
```
###### POST

###### 1.
```
/v1/accounts
```
###### 2.
```
/v1/money-transfer
```

##### TODO:
```
To add spring security & user management
To handle generic/common errors properly.
To cover business scenarios & all test scenarios, cucumber feature files to be added.
To add basic UI to test existing functionality.
To add mutation coverage.
To add actuators for production server health monitoring.
```
##### Download and prepare source code
Required files to compile the application are bundled with this source code.

File: gradle-wrapper.jar is installed with the source.

File: gradlew (Unix Shell script)

File: gradlew.bat (Windows batch file)

You can download the source with following command.
```
git clone https://github.com/rosarp/account-management.git
```

##### How to compile
You can run following tasks on command line (demo given for Unix Shell script)

Note: $ is the command prompt. Please ignore it while copy pasting on the command line.

Following commands will clean, build and package the jar file.
```
$cd account-management
$./gradlew clean
$./gradlew build
$./gradlew bootRepackage
```

##### How to get production ready:

Then to run the spring boot server, run the below commands
```
$cd ./build/libs
$java -jar account-management-0.0.2-SNAPSHOT.jar
```
