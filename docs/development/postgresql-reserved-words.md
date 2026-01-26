# PostgreSQL Reserved Words - Best Practices

## Current Approach

We use **explicit quoting** for reserved words rather than global quoting:

1. **Removed `globally_quoted_identifiers: true`** - This was quoting ALL identifiers unnecessarily
2. **Quote reserved words explicitly** - Only the `user` table is quoted: `@Table(name = "\"user\"")`
3. **Keep quotes in SQL migrations** - Flyway scripts use `CREATE TABLE "user"` for consistency

## Why This Approach?

### ✅ Advantages
- **Better portability** - Not all databases need quoting
- **Cleaner SQL** - Only reserved words are quoted, making SQL more readable
- **Explicit intent** - Makes it clear which identifiers are reserved words
- **Tool compatibility** - Some database tools have issues with globally quoted identifiers

### ⚠️ Trade-offs
- Must remember to quote reserved words in new entities
- Need to check PostgreSQL reserved words list when creating new tables

## PostgreSQL Reserved Words

Common reserved words to avoid or quote:
- `user` (we use this - quoted)
- `order`
- `group`
- `table`
- `select`
- `where`
- `index`
- `key`

Full list: https://www.postgresql.org/docs/current/sql-keywords-appendix.html

## Best Practice: Avoid Reserved Words

**Long-term recommendation**: Consider renaming `user` to `app_user` or `users`:
- More portable across databases
- No need for quotes
- Clearer intent

### Migration Path (Future)

If we decide to rename `user` to `app_user`:

1. Create migration: `V006__rename_user_to_app_user.sql`
   ```sql
   ALTER TABLE "user" RENAME TO app_user;
   ALTER TABLE user_tenant RENAME COLUMN user_id TO app_user_id;
   -- Update all indexes, constraints, etc.
   ```

2. Update JPA entity:
   ```java
   @Table(name = "app_user")  // No quotes needed!
   ```

3. Update all references in code

## Current Implementation

### JPA Entity
```java
@Entity
@Table(name = "\"user\"")  // Explicitly quoted
public class User { ... }
```

### SQL Migration
```sql
CREATE TABLE "user" ( ... );  -- Quoted in SQL
```

### Configuration
```yaml
spring:
  jpa:
    properties:
      hibernate:
        # globally_quoted_identifiers: true  # REMOVED - quote explicitly instead
```

## Summary

✅ **Current approach is good** - Explicit quoting is better than global quoting
📝 **Future improvement** - Consider renaming `user` to `app_user` for better portability
🔍 **When adding new entities** - Check if table/column names are reserved words and quote if needed
