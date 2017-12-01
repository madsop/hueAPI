FROM java:openjdk-8-jdk

ENV MAVEN_VERSION 3.3.9
ENV MAVEN_HOME /usr/lib/mvn
ENV PATH $MAVEN_HOME/bin:$PATH

RUN wget http://archive.apache.org/dist/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.tar.gz && \
  tar -zxvf apache-maven-$MAVEN_VERSION-bin.tar.gz && \
  rm apache-maven-$MAVEN_VERSION-bin.tar.gz && \
  mv apache-maven-$MAVEN_VERSION /usr/lib/mvn

# add source code to images
ADD . /hue

# switch working directory
WORKDIR /hue

# expose port 8080
EXPOSE 8080

# install and run
RUN mvn wildfly-swarm:run


# run ng serve on localhost
#CMD ["ng","serve", "--host", "0.0.0.0", "--disable-host-check"]
