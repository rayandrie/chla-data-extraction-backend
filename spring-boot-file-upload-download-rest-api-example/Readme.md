
 [Uploading an Downloading files with Spring Boot](https://www.callicoder.com/spring-boot-file-upload-download-rest-api-example/)

**1. Clone the repository** 

```bash
git clone https://github.com/callicoder/spring-boot-file-upload-download-rest-api-example.git
```

**2. Specify the file uploads directory**

Open `src/main/resources/application.properties` file and change the property `file.upload-dir` to the path where you want the uploaded files to be stored.

```
file.upload-dir=/Users/SophiaHu/uploads
```

**2. Run the app using maven**

```bash
cd spring-boot-file-upload-download-rest-api-example
mvn spring-boot:run
```

Go to: `http://localhost:8080` 
