# Registration API
SpringBoot application that uses Hibernate and JPA + Postgres.

- In order to run this project in the IDE, User must have a running postgres datasource exposed on localhost with the default port 5432. 
- The database used by default for docker compose is called `postgres`, so make sure you match your db name to the value in application properties. 
- This project has integration tests that are dependent on a datasource.
- The workflow for registration only works if Emails not being blocked by a firewall. 

**Best case is to hotspot off your phone if you're at work.** 

Most Classes in this project have JavaDocs

####Dockerfile
- Docker file uses a gradle base image
- Docker file is configured to build the project via :bootRun
- The image will be built then run

####DockerCompose
- Recommended start will be to execute `docker-compose up`
- This will create 2 running images (Postgre) + (Springboot App)
- Everything should be configured start from the getgo

After starting docker-compose, you can now start the frontend (Note that the frontend project needs to be build via `ng build --prod`) before running the docker image.
The frontend queries localhost:8080 (The api) so if you were to substitute the ip for something else make sure you reflect that in the FE codebase `post-data-to-api.service`.

####Endpoints
- POST /account: Creates an unverified user
- POST /account/verify: Re-sends email verification code
- PUT /account/verify: Updates the User email verification flag after they send the correct verification code
- POST /account/login: Authenticates Registered User (Must have verified email)
- PUT /account/email: Allows User to change email
- PUT /account/password: Allows user to change password

 