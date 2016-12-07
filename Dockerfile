FROM fedora:23

EXPOSE 10389
ADD pom.xml /ldap/
ADD src/ /ldap/src/

RUN dnf -y install wget

RUN wget http://repos.fedorapeople.org/repos/dchen/apache-maven/fedora-apache-maven.repo -O /etc/yum.repos.d/fedora-apache-maven.repo

RUN dnf -y install java-1.8.0-openjdk-devel apache-maven which && dnf clean all

RUN cd ldap && mvn clean package