# 🔐 Security Module | Безпека Додатку

## 📋 Призначення

Модуль **security** відповідає за безпеку даних, шифрування, аудит та політики логування. Захищає чутливі дані користувачів та автомобілів.

## 🏗️ Структура

```
security/
├── src/main/kotlin/com/quantumforce_code/security/
│   ├── ThreatModel.md          - Аналіз загроз (документація)
│   ├── SecurityPolicy.kt       - Політики шифрування та доступу
│   └── LoggerPolicy.kt         - Контроль чутливих логів
└── build.gradle.kts
```

## 🛡️ Компоненти

### ThreatModel.md
- **Роль**: Документація загроз безпеці
- **Зміст**:
  - Можливі атаки (MITM, injection, тощо)
  - Вразливості системи
  - Мітигація ризиків
  - Compliance вимоги

### SecurityPolicy.kt
```kotlin
// 1. File Purpose: Security policies and encryption rules
// 2. Role: Defines data protection strategies

object SecurityPolicy {
    // Шифрування даних
    fun encryptSensitiveData(data: String): String
    fun decryptSensitiveData(encrypted: String): String
    
    // Контроль доступу
    fun hasPermission(user: User, resource: Resource): Boolean
    
    // Валідація вводу
    fun sanitizeInput(input: String): String
}
```

**Функції:**
- AES-256 шифрування для VIN та особистих даних
- Secure storage (Android Keystore)
- Input validation для запобігання injection
- Permission management

### LoggerPolicy.kt
```kotlin
// 1. File Purpose: Logging policy for sensitive data
// 2. Role: Prevents leaking PII in logs

object LoggerPolicy {
    fun logSafe(tag: String, message: String)
    fun maskSensitiveData(data: String): String
    fun shouldLog(logLevel: LogLevel): Boolean
}
```

**Функції:**
- Маскування VIN в логах (XXXX...1234)
- Видалення особистих даних з crash reports
- Різні log levels для debug/release
- GDPR compliance

## 🔒 Типи Захисту

### 1. Data at Rest (Дані у Спокої)
- **Room Database**: Encrypted SQLite (SQLCipher)
- **Shared Preferences**: EncryptedSharedPreferences
- **Files**: Android FileProvider з encryption

### 2. Data in Transit (Дані в Русі)
- **Bluetooth**: Paired device authentication
- **Network API**: HTTPS з certificate pinning
- **USB**: Device permission requirements

### 3. Data in Use (Дані у Використанні)
- **Memory**: Secure memory cleanup після використання
- **Screen**: Screenshot prevention для sensitive screens
- **Clipboard**: Clear clipboard після copy

## 🚨 Загрози та Мітигація

| Загроза | Ризик | Мітигація |
|---------|-------|-----------|
| MITM Attack | High | HTTPS, Certificate Pinning |
| SQL Injection | Medium | Parameterized queries, Room |
| Data Leakage in Logs | Medium | LoggerPolicy, masking |
| Unauthorized Access | High | Android Keystore, Biometrics |
| Reverse Engineering | Medium | ProGuard, R8, code obfuscation |
| Bluetooth Sniffing | Low | Encryption, paired devices only |

## 🔗 Залежності

```kotlin
dependencies {
    implementation(libs.kotlin.stdlib)
    
    // Encryption
    implementation(libs.androidx.security.crypto)
    implementation("net.zetetic:android-database-sqlcipher:4.5.4")
    
    // Biometrics
    implementation(libs.androidx.biometric)
    
    // Internal
    implementation(project(":core:domain"))
}
```

## 🧪 Тестування

### Security Tests
- Encryption/Decryption round-trip
- Input validation (SQL injection, XSS)
- Permission checks
- Log masking verification

```bash
./gradlew :security:test
```

## 📜 Compliance

### GDPR (EU)
- ✅ Right to be forgotten (clear user data)
- ✅ Data portability (export reports)
- ✅ Consent management
- ✅ Privacy by design

### CCPA (California)
- ✅ Data transparency
- ✅ Opt-out mechanisms
- ✅ Data deletion requests

## 🎯 Приклади Використання

### Шифрування VIN
```kotlin
val vin = "1HGBH41JXMN109186"
val encrypted = SecurityPolicy.encryptSensitiveData(vin)
// Save to database: encrypted value

// On read
val decrypted = SecurityPolicy.decryptSensitiveData(encrypted)
```

### Safe Logging
```kotlin
// ❌ Небезпечно
Log.d("Vehicle", "VIN: $vin")

// ✅ Безпечно
LoggerPolicy.logSafe("Vehicle", "VIN: ${LoggerPolicy.maskSensitiveData(vin)}")
// Output: "VIN: XXXX...9186"
```

### Permission Check
```kotlin
if (SecurityPolicy.hasPermission(currentUser, Resource.CLEAR_DTC)) {
    clearDtcCodes()
} else {
    showUnauthorizedError()
}
```

## 🔧 Конфігурація

### ProGuard Rules (`proguard-rules.pro`)
```
# Security classes
-keep class com.quantumforce_code.security.** { *; }

# Crypto
-keep class javax.crypto.** { *; }
-dontwarn javax.crypto.**
```

### AndroidManifest.xml
```xml
<!-- Prevent screenshots on sensitive screens -->
<activity android:windowSoftInputMode="stateHidden">
    <meta-data
        android:name="android.app.screenshot"
        android:value="false" />
</activity>
```

## 📊 Security Audit Log

```kotlin
data class SecurityEvent(
    val timestamp: Long,
    val eventType: SecurityEventType,
    val userId: String?,
    val details: String,
    val severity: Severity
)

enum class SecurityEventType {
    UNAUTHORIZED_ACCESS,
    DATA_BREACH_ATTEMPT,
    SUSPICIOUS_ACTIVITY,
    PERMISSION_VIOLATION
}
```

## 🚀 Roadmap

- [ ] Implement biometric authentication
- [ ] Add certificate pinning for API
- [ ] Integrate with Firebase App Check
- [ ] Add anomaly detection (unusual DTC patterns)
- [ ] Implement secure element for key storage

---

**Пакет**: `com.quantumforce_code.security`  
**Стандарти**: OWASP MASVS, GDPR, CCPA  
**Encryption**: AES-256, Android Keystore
