# account-management
### Simple REST service with very basic functionality to manage accounts and its balance.

##### URL :
	
```
http://localhost:8080
```

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

e.g. payload
{
	"accountId" : "Id-123",
	"balance" : 2000
}
```
###### 2.

```
/v1/money-transfer

e.g. payload
{
	"accountFrom" : "Id-123",
	"accountTo" : "Id-234",
	"amount" : 300
}
```

### How to get production ready:

##### ToDo:

```
Use real database such as postgresql instead of in memory db/map/list functionality.
Handle generic/common errors properly.
Add spring security & user management
Enable HTTPS by providing EmbeddedServletContainerCustomizer.
Cover business scenarios & all test scenarios using cucumber feature files.
Add mutation coverage.
Add actuators for production server health monitoring.
Add basic UI to test existing functionality.
Setup CI server.
Setup test & prod server.
```

##### application jar:
[github releases](https://github.com/rosarp/account-management/releases)

##### Download and prepare source code:
Required files to compile the application are bundled with this source code.

File: gradle-wrapper.jar is installed with the source.

File: gradlew (Unix Shell script)

File: gradlew.bat (Windows batch file)

You can download the source with following command.
```
git clone https://github.com/rosarp/account-management.git
```

##### How to compile:
You can run following tasks on command line (demo given for Unix Shell script)

Note: $ is the command prompt. Please ignore it while copy pasting on the command line.

Following commands will clean, build and package the jar file.

```
$cd account-management
$./gradlew clean
$./gradlew build
$./gradlew bootRepackage
```

##### How to run the server:

To run the spring boot server, run the below commands.
If HTTPS is configured using customizer then add -Dkeystore.file as below. Otherwise skip it.

```
$cd ./build/libs
$java -Dspring.profiles.active=production -Dkeystore.file=file:///$PWD/src/main/resources/keystore.p12 -jar account-management-0.0.3.jar
```
