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

####Some Extra Notes (I put some thought into this):
- I would of liked to handle the email verification on the frontend using another micro service so that I only 
need to make one POST call to /account. However I needed a way to not allow a user to log in if they had registered without
verifying their email and show the feedback on the frontend. So to handle that I did 1 API call with the flag (emailVerify) false
then a secondary follow up API call that updated the flag after they've verified from the register screen.
- Note that the register frame on the frontend automatically shows verify email elements without routing, that was my
way of emulating a persisting session without having to route with objects.
- When you log in, the client carries over the username to the url route, that is how I'm currently doing email/password updates,
This is by no means a solid solution, I didn't put any emphasis on security nor user sessions for this challenge.
- So having no session cookie was a pretty bad thing, obviously in a normal environment, a session cookie would be put
in place to prevent external parties from doing harm to user accounts.
- I found sending emails quite challenging, I must have spent a bit of time trying to get SSL working for emails but cut
my losses in the end, which is why emails are sent using TLS and why it may be blocked by your company firewall.
 