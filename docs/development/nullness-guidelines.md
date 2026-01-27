## Nullness Guidelines

This project uses Eclipse null analysis to catch null-safety issues. Spring Framework APIs are not fully annotated
for nullness, so false positives can appear at framework boundaries.

### Principles
- Annotate our own code consistently and prefer defaults at package level.
- Treat external libraries as unknown unless they are annotated.
- Suppress nullness warnings only at the boundary, not throughout business logic.
- Add a brief comment explaining why the suppression is safe.

### Recommended Pattern
When calling a Spring API that expects a non-null parameter but lacks proper annotations, suppress the warning at
the boundary method:

```java
@Bean
@SuppressWarnings("null") // Spring API lacks nullness annotations; boundary suppression.
public LocaleResolver localeResolver() {
    AcceptHeaderLocaleResolver localeResolver = new AcceptHeaderLocaleResolver();
    localeResolver.setSupportedLocales(List.of(Locale.GERMAN, Locale.FRENCH, Locale.ITALIAN, Locale.ENGLISH));
    return localeResolver;
}
```

### Avoid
- Scattering `@SuppressWarnings("null")` inside domain or application logic.
- Adding casts or redundant null checks just to satisfy the analyzer.

### Notes
- Prefer package-level `@NonNullApi` / `@NonNullFields` for our own code.
- Apply the same defaults to test packages to keep analysis consistent across sources.
- Revisit suppressions when Spring/JSpecify annotations become available in the APIs we use.
