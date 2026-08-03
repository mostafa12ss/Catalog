# Verification Rules
- After any code change, run the relevant Gradle build/compile task
  (e.g. `./gradlew :app:shared:compileKotlinJvm`) and confirm it succeeds
  before declaring the task complete.
- Never mark a task as done without actually running a build command.
- If a build fails, paste the exact compiler error and fix it before continuing
  to the next task — don't guess blindly.

# Code Accuracy Rules
- Never invent class names, method names, or import paths for third-party
  libraries (Supabase, SQLDelight, Ktor, Koin). If unsure of an exact API name,
  check the actual generated code or official docs first.
- Do not hardcode dependency version numbers from memory — check the current
  libs.versions.toml first and stay consistent with it.

# Generated Files Rule
- Never manually edit files inside any `build/` or `generated/` directory.
  These are regenerated automatically and manual edits will be lost or cause
  confusing errors.

# Multiplatform Source Set Rules
- iOS dependencies/actuals must be added to BOTH `iosArm64Main` and
  `iosSimulatorArm64Main` explicitly — `iosMain` is not auto-created in this project.
- Compose resource file names (fonts, drawables, string keys) must be lowercase
  snake_case only — no uppercase letters, no spaces.

# Scope & Communication Rules
- If a requested change requires modifying more than 3 files, list the files
  and a one-line summary of each change before starting.
- If a fix requires a library version upgrade, state the old and new version
  explicitly and ask for confirmation before applying it.

# Secrets Rule
- Never hardcode Supabase keys, API secrets, or credentials directly in shared
  source files that could be committed to git.