FROM openjdk:22

ARG JAR_FILE=target/*.jar
ARG JAR_LIB_FILE=target/classes/lib
ARG JAR_RU_FILE=target/classes/ru
ARG JAR_PROP_FILE=target/classes/*.properties
ARG JAR_MANIFEST=target/META-INF/

COPY ${JAR_FILE} app.jar
ADD ${JAR_LIB_FILE} lib/
ADD ${JAR_RU_FILE} ru/
ADD ${JAR_PROP_FILE} .
ADD ${JAR_MANIFEST} META-INF/

ENTRYPOINT ["java","-jar","/app.jar"]

