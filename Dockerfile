FROM nimmis/java-centos:openjdk-8-jre

ADD ml-bridge.jar ml-bridge.jar

EXPOSE 8080

# -Xdebug -Xrunjdwp:transport=dt_socket,server=y,address=8001,suspend=y
CMD java -jar ml-bridge.jar