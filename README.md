## debug
* `./mvnw exec:java -Dexec.mainClass=net.johnsonlau.jproxy.App`

## package and run
* package `./mvnw clean package`
* run `java -DproxyPort=8118 -DlocalListening=true -jar target/jproxy-http.jar`

## reference
* https://blog.csdn.net/dotalee/article/details/77838659