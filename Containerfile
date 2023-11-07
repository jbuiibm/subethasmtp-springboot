FROM openjdk
WORKDIR /
ADD emailsreceiver-0.0.1-SNAPSHOT.jar emailsreceiver-0.0.1-SNAPSHOT.jar
EXPOSE 4665
CMD java -jar emailsreceiver-0.0.1-SNAPSHOT.jar