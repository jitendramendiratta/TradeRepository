## Trade repository:

### Problem Statement
There is a scenario where thousands of trades are flowing into one store, assume any way of transmission of trades. We need to create a one trade store, which stores the trade in the following order.

Trade Id	Version	Counter-Party Id	Book-Id	Maturity Date	Created Date	Expired
T1	1	CP-1	B1	20/05/2020	<today date>	N
T2	2	CP-2	B1	20/05/2021	<today date>	N
T2	1	CP-1	B1	20/05/2021	14/03/2015	N
T3	3	CP-3	B2	20/05/2014	<today date>	Y

There are couples of validation, we need to provide in the above assignment
1.	During transmission if the lower version is being received by the store it will reject the trade and throw an exception. If the version is same it will override the existing record.
2.	Store should not allow the trade which has less maturity date then today date.
3.	Store should automatically update the expire flag if in a store the trade crosses the maturity date.

### Solution
* This solution is built using springboot and H2 database.
* Provider thread will source the data using batch mode from a file and publish to a trade queue. 
* Trade Processor thread will process the trades received from trade queue. It will apply the checks mentioned in the problem statement and will save/updates trades if they qualify the checks.
* A scheduled Task will kick off every 23:59 hrs to find mature trades which have expired=false and will update the expired flag as true.
* We can build multiple Provider and Processors & they can be configured using properties file.


=====================================================================================
### How to set up and run project

* To Build the project :  ./gradlew build
* To Run tests : ./gradlew test
* To Generate Code coverage report : ./gradlew jacocoTestReport
