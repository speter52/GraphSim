#Use build.xml to run ANT and build source code, then add required modules to class path and run main
#Note: Also need to give build script run permissions(chmod)
ant
java -cp Modules/java-json.jar:out/production/SeniorThesis/ com.Main

