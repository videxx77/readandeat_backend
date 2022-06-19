# readandeat_backend

## Release Notes v0.1
NO PRODUCTION RELEASE

### Implemented
- Auth (signup/signin) API working
- JWT working
- Customer API working (without picture upload)
- Product API working (without picture upload)

### Missing
- picture upload
- better exception messages
- response with message and http status 
- https

## Steps for no production setuprec
1. set up database (MySql) without creating tables
2. change creadentials in src\main\resources\application.properties
3. in application.properties change spring.jpa.hibernate.ddl-auto=update to spring.jpa.hibernate.ddl-auto=create
4. open in intelij and start with ReadandeatBackendV2Application as main
5. (dont forget to change spring.jpa.hibernate.ddl-auto=create back to spring.jpa.hibernate.ddl-auto=update)
