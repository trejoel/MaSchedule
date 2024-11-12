# MaSchedule
Multi-agent based tool for simulation of heuristics

The main class executes our Multi-agent based architecture

We suggest to use the intelij framework to compile and execute. Before running the main file, please add the following open source libraries included in the tree of the current branch in the building properties of the project

-opencsv-5.9.jar
-commons-lang3-3.14.0.jar
-commons-lang3-3.14.0-javadoc.jar
-commons-lang3-3.14.0-sources.jar
-commons-lang3-3.14.0-test-sources.jar
-commons-lang3-3.14.0-tests.jar

Then, you can execute the code by running the main.java file. 

Notice that the user can customize the instances to be executed by changing the conf in the main file. 

The configuration includes the following fields: number of days, heuristic, number of hospitals ,number of beds ,number of patients , name of instance)

All the fields except the name of instance are of type integer. The of instance is an string. 

As an example in our main file we have:

 Conf conf=new Conf(20,1,3,100, 500,"Test.csv");

 That indicates that the simulation consists of 20 days, using the policy one (round robin), in three hospitals, with 100 number of beds and 500 patients. In this case, we have the name "Test.csv" as the name of file. In such a case, the file (in this case "Test.csv") contains all the information about each element in the simulation. Patients, beds, etc.

 If the name of instance is empty, the simulation generates a simulation environment with the specifications of the configuration file. The features of bed, and patients are included in a random fashion. 


d



