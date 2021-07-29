FROM gcr.io/distroless/java:8
MAINTAINER Josef (kwart) Cacek <josef.cacek@gmail.com>

COPY target/ldap-server.jar /ldap-server.jar
ENTRYPOINT ["/usr/bin/java", "-jar", "/ldap-server.jar"]
EXPOSE 389 636
CMD ["-p", "389", \
     "-sp", "636", \
     "-b", "0.0.0.0" ]
