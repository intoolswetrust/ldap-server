# ldap-server

Simple all-in-one LDAP server (wrapped [ApacheDS](http://directory.apache.org/apacheds/)).

You don't need any configuration files to get it working. Just launch the JAR and that's it.

Server data are not persisted, they just live in memory.

## Download

Download latest tag from [GitHub releases](https://github.com/kwart/ldap-server/releases)

### Docker container

If you search a lightweight Docker container with LDAP server for your testing, you can use `kwart/ldap-server`.

```bash
docker pull kwart/ldap-server
docker run -it --rm kwart/ldap-server
```

## Development

You can simply build the software yourself.

### How to get the sources

You should have [git](http://git-scm.com/) installed

```
git clone git://github.com/kwart/ldap-server.git
```

or you can download [current sources as a zip file](https://github.com/kwart/ldap-server/archive/master.zip)

### How to build it

You need to have [Maven](http://maven.apache.org/) installed

```bash
mvn clean package
```

### How to run it

```bash
java -jar ldap-server.jar [data.ldif]
```

#### Help

```
$ java -jar target/ldap-server.jar --help
The ldap-server is a simple LDAP server implementation based on ApacheDS. It
creates one user partition with root 'dc=jboss,dc=org'.

Usage: java -jar ldap-server.jar [options] [LDIFs to import]
  Options:
    --allow-anonymous, -a
       allows anonymous bind to the server
       Default: false
    --bind, -b
       takes [bindAddress] as a parameter and binds the LDAP server on the
       address
       Default: 0.0.0.0
    --help, -h
       shows this help and exits
       Default: false
    --port, -p
       takes [portNumber] as a parameter and binds the LDAP server on that port
       Default: 10389
    --ssl-enabled-ciphersuite, -scs
       takes [sslCipherSuite] as argument and enables it for 'ldaps'. Can be
       used multiple times.
    --ssl-enabled-protocol, -sep
       takes [sslProtocolName] as argument and enables it for 'ldaps'. Can be
       used multiple times. If the argument is not provided following are used:
       TLSv1, TLSv1.1, TLSv1.2
    --ssl-keystore-file, -skf
       takes keystore [filePath] as argument. The keystore should contain
       privateKey to be used by LDAPs
    --ssl-keystore-password, -skp
       takes keystore [password] as argument
    --ssl-need-client-auth, -snc
       enables SSL 'needClientAuth' flag
       Default: false
    --ssl-port, -sp
       adds SSL transport layer (i.e. 'ldaps' protocol). It takes [portNumber]
       as a parameter and binds the LDAPs server on the port
    --ssl-want-client-auth, -swc
       enables SSL 'wantClientAuth' flag
       Default: false

Examples:

$ java -jar ldap-server.jar users.ldif
Starts LDAP server on port 10389 (all interfaces) and imports users.ldif

$ java -jar ldap-server.jar -sp 10636 users.ldif
Starts LDAP server on port 10389 and LDAPs on port 10636 and imports the LDIF

$ java -jar ldap-server.jar -b 127.0.0.1 -p 389
Starts LDAP server on address 127.0.0.1:389 and imports default data (one user
entry 'uid=jduke,ou=Users,dc=jboss,dc=org'
```

#### SSL/TLS

If you want to enable SSL/TLS ('ldaps') and use your own certificate, the generate (or import) the private key into a JKS keystore and provide path to it as argument. 

```bash
# generate a keypair
keytool -validity 365 -genkey -alias myserver -keyalg RSA -keystore /tmp/ldaps.keystore -storepass 123456 -keypass 123456 -dname cn=myserver.mycompany.com

# use the generated keypair (-skf) with given password (-skp)
# We also enable detail SSL debug information by setting javax.net.debug system property.
java -Djavax.net.debug=all -jar target/ldap-server.jar -sp 1038389 -skf /tmp/ldaps.keystore -skp 123456
```

## Default LDIF

```
version: 1

dn: dc=jboss,dc=org
dc: jboss
objectClass: top
objectClass: domain

dn: ou=Users,dc=jboss,dc=org
objectClass: organizationalUnit
objectClass: top
ou: Users

dn: uid=jduke,ou=Users,dc=jboss,dc=org
objectClass: top
objectClass: person
objectClass: inetOrgPerson
cn: Java Duke
sn: duke
uid: jduke
userPassword: theduke

dn: ou=Roles,dc=jboss,dc=org
objectclass: top
objectclass: organizationalUnit
ou: Roles

dn: cn=Admin,ou=Roles,dc=jboss,dc=org
objectClass: top
objectClass: groupOfNames
cn: Admin
member: uid=jduke,ou=Users,dc=jboss,dc=org
```

## License

* [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0)
 
