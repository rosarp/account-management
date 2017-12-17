# account-management
Simple REST service with very basic functionality to manage accounts and its balance.

URL :
	
	http://localhost:18080

End Points :
	
	GET
		/v1/accounts/Id-123
			Where Id-123 is account id
	
	POST
		/v1/accounts
		/v1/money-transfer

TODO:
	
	To handle generic/common errors properly.
	To cover business scenarios & all test scenarios, cucumber feature files to be added.
	To add basic UI to test existing functionality.

How to get production ready:

	gradle wrapper is installed with the source.
	gradlew (Unix Shell script)
	gradlew.bat (Windows batch file)

	You can run following tasks on command line (demo given for Unix Shell script)
	It will clean, build and package the jar file.
	And then it will run the spring boot server
	Note: $ is the command prompt. Please ignore it while copy pasting on the command line.
	
	$cd account-management
	$./gradlew clean
	$./gradlew build
	$./gradlew bootRepackage
	$cd ./build/libs
	$java -jar account-management-0.0.2-SNAPSHOT.jar

