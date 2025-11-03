# Secure HTTPS Spring Boot Web App (Java 21)

## Create HTTPS Keystore
```
keytool -genkeypair -alias myhttpskey -keyalg RSA -keysize 2048 -storetype PKCS12   -keystore src/main/resources/keystore.p12 -validity 365 -storepass changeit   -dname "CN=localhost"
```

## Run
```
mvn spring-boot:run
```

## Test 
```
curl -k -X POST https://localhost:8443/api/register -H "Content-Type: application/json"   -d '{"username":"alice","password":"S3curePassw0rd"}'
curl -k -X POST https://localhost:8443/api/login -H "Content-Type: application/json"   -d '{"username":"alice","password":"S3curePassw0rd"}'
```
