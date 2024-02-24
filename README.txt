SMART PORTABLES

Tomcat - Read Me

Getting Started

To run the Best Deal Tomcat application, follow the steps below:

1. Start Tomcat Server

Navigate to the Tomcat installation directory and start the server using the following command:

```
C:\apache-tomcat-7.0.34\bin\startup
```

2. Deploy the Project

Place the entire project folder inside the Tomcat webapps directory:

```
C:\apache-tomcat-7.0.34\webapps
```

3. Run the Application

Once the project is deployed, you can access the application through the browser using the following URL:

```
https://localhost/{PORT}
```

Replace `{PORT}` with the appropriate port number.

4. Compile Java Source Code

If you need to recompile the Java source code, use the following command in the project directory:

```
javac *.java
```
Notes : 

- Ensure that the Tomcat server is properly configured and running before deploying the project.
- Make sure to replace `{PORT}` in the URL with the actual port number configured for your Tomcat server.
