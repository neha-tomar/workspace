1. Requirements :
	- Set Hadoop in Pseudo mode
	- Configure input and output directories using app.properties file
2. Run : 
	- Go to project root directory in command prompt
	- Run "mvn spring-boot:run"  (this command would start Tomcat server and execute MR job to process files given under input directory)
	- Open browser and hit URL "localhost:8080/word/count?query=<word>"

Note : This application has been developed for input/output location in local FileSystem and not HDFS.