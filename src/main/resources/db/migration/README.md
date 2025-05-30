# Database Migration with Flyway

This project uses Flyway for database schema migrations. Flyway is a database migration tool that allows for versioned, repeatable, and reliable database schema changes.

## Migration Scripts

Migration scripts are SQL files located in the `src/main/resources/db/migration` directory. They follow a specific naming convention:

- `V<version>__<description>.sql` - Versioned migrations
- `R__<description>.sql` - Repeatable migrations

For example:
- `V1__init_schema.sql` - Initial schema creation
- `V2__add_indexes.sql` - Adding indexes to improve performance

## Current Migrations

1. **V1__init_schema.sql**: Creates the initial database schema with all tables and relationships
2. **V2__add_indexes.sql**: Adds indexes to improve query performance

## How to Add New Migrations

To add a new migration:

1. Create a new SQL file in the `src/main/resources/db/migration` directory
2. Name it following the convention: `V<next_version>__<description>.sql`
   - For example, if the last version is `V2`, the next one should be `V3`
3. Write your SQL statements in the file
4. Flyway will automatically detect and apply the new migration when the application starts

## Best Practices

1. **Never modify existing migration scripts** - Once a migration has been applied to any environment, it should never be changed
2. **Keep migrations small and focused** - Each migration should do one thing
3. **Use descriptive names** - The name should clearly indicate what the migration does
4. **Include both "up" and "down" logic when possible** - This makes it easier to roll back changes if needed
5. **Test migrations thoroughly** - Ensure that migrations work correctly before deploying to production
6. **Use `IF NOT EXISTS` and `IF EXISTS` clauses** - This makes migrations more robust

## Configuration

Flyway is configured in the `application.properties` file:

```properties
spring.flyway.enabled=true
spring.flyway.baseline-on-migrate=true
spring.flyway.locations=classpath:db/migration
```

- `spring.flyway.enabled=true` - Enables Flyway
- `spring.flyway.baseline-on-migrate=true` - Allows Flyway to work with existing databases
- `spring.flyway.locations=classpath:db/migration` - Specifies where migration scripts are located

## Hibernate Configuration

To prevent conflicts between Flyway and Hibernate's schema generation, Hibernate is configured to validate the schema rather than modify it:

```properties
spring.jpa.hibernate.ddl-auto=validate
```

This ensures that Hibernate only validates that the database schema matches the entity classes, but doesn't try to create, update, or drop tables.