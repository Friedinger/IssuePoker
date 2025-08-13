# Development

This project is based on the [RefArch](https://refarch.oss.muenchen.de/).

For the initial development setup see the [Getting Started](https://refarch.oss.muenchen.de/templates/getting-started.html)
and [Develop](https://refarch.oss.muenchen.de/templates/develop.html) documentations of the RefArch.

## Starting the application

1. Start the frontend development server:

   ```shell
   cd issuepoker-frontend
   npm run dev
   ```

2. Start the stack:

   ```shell
   cd stack
   podman compose up -d
   ```

3. Start the backend:

   ```shell
   cd issuepoker-backend
   mvn clean verify
   mvn spring-boot:run -Dspring-boot.run.profiles=local
   ```

4. Open your browser and visit [http://localhost:8083](http://localhost:8083)

5. Login one of the local accounts from keycloak migration:

   - Username: `admin` Password: `admin`
   - Username: `user1` Password: `user1`
   - Username: `user2` Password: `user2`

   All users can view issues and vote for them, but only admins can create new issues, reveal the voting and set its result.

## Contributing

Contributions are what make the open source community such an amazing place to learn, inspire, and create. Any contributions you make are
**greatly appreciated**.

To learn more about how you can contribute, please read our [Code of Conduct](https://github.com/Friedinger/IssuePoker/blob/main/.github/CODE_OF_CONDUCT.md).
