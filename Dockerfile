# temp container to build using gradle
FROM gradle:6.9.0-jdk11
ENV OCPP_SERVER_SRC=/home/gradle/ocpp-server/
ENV OCPP_SERVER_HOME=/home/gradle/ocpp-server-home
RUN mkdir -p $OCPP_SERVER_HOME
ADD /ocpp-server.properties $OCPP_SERVER_HOME
RUN git clone https://github.com/v-bodnar/ocpp-server.git ocpp-server
RUN git clone  https://github.com/v-bodnar/GroovyOcppSupplier.git ocpp-server-home/groovy
WORKDIR $OCPP_SERVER_SRC
RUN gradle clean build installDist|| return 0
EXPOSE 8887
EXPOSE 8084
ENTRYPOINT exec $OCPP_SERVER_SRC/build/install/ocpp-server/bin/ocpp-server