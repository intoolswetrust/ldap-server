# Docker ldap-server image

Distroless Java Docker image with Apache DS based LDAP server

Use the `latest` tag to have up-to-date version.

## How to run it

### Default LDIF

```bash
docker run -it --rm -p 10389:10389 -p 10636:10636 kwart/ldap-server 
```

### Custom LDIF

If the LDIF is located in the current directory and named `custom.ldif`

```bash
docker run -it --rm \
    -p 10389:10389 \
    -v `pwd`:/mnt \
    kwart/ldap-server \
    /mnt/custom.ldif
```
