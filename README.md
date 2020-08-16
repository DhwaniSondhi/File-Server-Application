## File-Server-Application
This project is to create an end application with the HTTP server library. This remote file server manager is built according to following requirements:
- ***GET /*** returns a list of the current files in the data directory. It can return different type format such as JSON, XML, plain text, HTML according to the Accept key of the header of the request.  
- ***GET /foo*** returns the content of the file named foo in the data directory. If the content does not exist, it returns an appropriate status code (e.g. HTTP ERROR 404).
- ***POST /bar*** creates or overwrites the file named bar in the data directory with the content of the body of the request. 
- Used a mechanism to prevent the clients to read/write any file outside the file server working directory to remove the severe access vulnerability.
- Provides multi-requests, content-type, and content-disposition support.
- Handles the commands of the [httpc](https://github.com/DhwaniSondhi/cURL-like-Command-Line-Implementation) (a HTTP client implementation).

The command to HTTP server:<br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>httpfs [-v] [-p PORT] [-d PATH-TO-DIR]</code><br/><br/>
***-v*** &nbsp;&nbsp;&nbsp;&nbsp;Prints debugging messages.<br/>
***-p*** &nbsp;&nbsp;&nbsp;&nbsp;Specifies the port number that the server will listen and serve at. Default is 8080.<br/>
***-d*** &nbsp;&nbsp;&nbsp;&nbsp;Specifies the directory that the server will use to read/write requested files. Default is the current directory when launching the &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;application.<br/>

[Please click for more description.](https://github.com/DhwaniSondhi/File-Server-Application)

### How to run?
- Install Java 8+.
- Run the command given in the description. 
- Send the requests from the client. ([httpc](https://github.com/DhwaniSondhi/cURL-like-Command-Line-Implementation) client can also be used)

