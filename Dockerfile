FROM clojure:latest
MAINTAINER Richard Hull <rm_hull@yahoo.co.uk>
RUN mkdir -p /usr/src/app
WORKDIR /usr/src/app
COPY . /usr/src/app
RUN \
  lein deps && \
  lein ring uberjar && \
  rm -rf target/classes ~/.m2
  
EXPOSE 3000
ENTRYPOINT ["java", "-jar", "target/ods-search-appliance-0.0.1-SNAPSHOT-standalone.jar"]