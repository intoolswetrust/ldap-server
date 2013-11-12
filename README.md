# ldap-server

Simple all-in-one LDAP server, which uses in `dc=jboss,dc=org` partition.

## Development

You can simply build the software yourself.

### How to get the sources

You should have [git](http://git-scm.com/) installed

	$ git clone git://github.com/kwart/ldap-server.git

or you can download [current sources as a zip file](https://github.com/kwart/ldap-server/archive/master.zip)

### How to build it

You need to have [Maven](http://maven.apache.org/) installed

	$ cd ldap-server
	$ mvn clean package

### How to run it

	$ java -jar ldap-server.jar [data.ldif]

## Default LDIF

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

## License

* [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0)
 