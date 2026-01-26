# Java 21 Configuration

This project requires **Java 21** for building, testing, and running.

## Automatic Configuration

Maven is configured to use Java 21 via:
- `.mvn/toolchains.xml` - Maven toolchains configuration
- `pom.xml` - Compiler plugin set to Java 21

## Manual Setup

If Maven is not using Java 21 automatically, you can:

### Option 1: Use the wrapper script (Recommended)
```bash
cd backend
./mvn-java21.sh test
./mvn-java21.sh clean install
```

### Option 2: Set JAVA_HOME in your shell
Add to `~/.zshrc` or `~/.bash_profile`:
```bash
export JAVA_HOME=/opt/homebrew/Cellar/openjdk@21/21.0.10/libexec/openjdk.jdk/Contents/Home
export PATH="$JAVA_HOME/bin:$PATH"
```

Then reload:
```bash
source ~/.zshrc  # or source ~/.bash_profile
```

### Option 3: Set JAVA_HOME for a single command
```bash
export JAVA_HOME=/opt/homebrew/Cellar/openjdk@21/21.0.10/libexec/openjdk.jdk/Contents/Home
cd backend
mvn test
```

## Verify Java Version

Check that Maven is using Java 21:
```bash
mvn -version
```

You should see:
```
Java version: 21.0.10, vendor: Homebrew
```

## Troubleshooting

If you see errors about Mockito not being able to mock classes, it's likely because Maven is using Java 25 instead of Java 21. Use one of the options above to ensure Java 21 is used.
