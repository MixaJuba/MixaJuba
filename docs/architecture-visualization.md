# 🎨 Architecture Visualization | Візуалізація Архітектури

## 📊 Системна Архітектура

### 1. Загальна Трирівнева Архітектура

```mermaid
graph TB
    subgraph "Presentation Layer"
        A[MainActivity]
        B[Compose UI]
        C[Navigation]
        D[ViewModels]
    end
    
    subgraph "Business Logic Layer"
        E[Use Cases]
        F[Domain Models]
        G[Repositories]
        H[OBD Protocol Handlers]
    end
    
    subgraph "Data Layer"
        I[Room Database]
        J[Hardware Transport]
        K[Network API]
    end
    
    subgraph "External"
        L[Vehicle ECU]
        M[Cloud Server]
    end
    
    A --> B
    B --> C
    C --> D
    D --> E
    E --> F
    E --> G
    G --> H
    G --> I
    H --> J
    J --> L
    G --> K
    K --> M
    
    style A fill:#00BCD4,stroke:#006064,color:#000
    style B fill:#00BCD4,stroke:#006064,color:#000
    style E fill:#4CAF50,stroke:#1B5E20,color:#000
    style F fill:#4CAF50,stroke:#1B5E20,color:#000
    style I fill:#FF9800,stroke:#E65100,color:#000
    style J fill:#FF9800,stroke:#E65100,color:#000
    style L fill:#F44336,stroke:#B71C1C,color:#fff
```

### 2. Потік Даних (Data Flow)

```mermaid
sequenceDiagram
    participant User
    participant UI as UI Screen
    participant VM as ViewModel
    participant UC as UseCase
    participant Repo as Repository
    participant OBD as OBD Protocol
    participant HW as Hardware
    participant ECU as Vehicle ECU
    
    User->>UI: Натискає "Read DTC"
    UI->>VM: readDtcCodes()
    VM->>UC: execute()
    UC->>Repo: getDtcCodes()
    Repo->>OBD: sendCommand("03")
    OBD->>HW: write("03\r")
    HW->>ECU: Bluetooth/USB
    ECU-->>HW: "43 02 01 33 02 45"
    HW-->>OBD: Response
    OBD-->>Repo: List<DtcCode>
    Repo-->>UC: Result.Success
    UC-->>VM: DtcUiState.Success
    VM-->>UI: Update UI
    UI-->>User: Показує коди
```

### 3. Модульна Структура

```mermaid
graph LR
    subgraph "App Module"
        APP[app]
    end
    
    subgraph "Core Modules"
        DOMAIN[core:domain]
        DATA[core:data]
    end
    
    subgraph "Feature Modules"
        DTC[features:dtc]
        LIVE[features:live]
    end
    
    subgraph "Infrastructure"
        HW[hardware:transport]
        PROTO[protocols:obd]
        SEC[security]
        UPD[updates]
    end
    
    APP --> DTC
    APP --> LIVE
    APP --> DATA
    
    DTC --> DOMAIN
    DTC --> DATA
    LIVE --> DOMAIN
    LIVE --> PROTO
    
    DATA --> DOMAIN
    
    PROTO --> HW
    PROTO --> DOMAIN
    
    style APP fill:#E1F5FE,stroke:#01579B
    style DOMAIN fill:#E8F5E9,stroke:#1B5E20
    style DATA fill:#E8F5E9,stroke:#1B5E20
    style DTC fill:#FFF3E0,stroke:#E65100
    style LIVE fill:#FFF3E0,stroke:#E65100
    style HW fill:#F3E5F5,stroke:#4A148C
    style PROTO fill:#F3E5F5,stroke:#4A148C
```

### 4. DTC Feature Архітектура

```mermaid
graph TB
    subgraph "UI Layer"
        A[DtcScreen.kt]
    end
    
    subgraph "ViewModel"
        B[DtcViewModel]
        C[DtcUiState]
    end
    
    subgraph "Domain"
        D[ReadDtcCodesUseCase]
        E[ClearDtcCodesUseCase]
        F[DtcCode Model]
    end
    
    subgraph "Data"
        G[DtcRepository]
        H[DtcDao]
        I[Room Database]
    end
    
    subgraph "Protocols"
        J[ObdInterface]
        K[DtcParser]
    end
    
    subgraph "Hardware"
        L[Port]
        M[BluetoothPort]
        N[Vehicle ECU]
    end
    
    A --> B
    B --> C
    B --> D
    B --> E
    D --> F
    E --> F
    D --> G
    E --> G
    G --> H
    H --> I
    G --> J
    J --> K
    J --> L
    L --> M
    M --> N
    
    style A fill:#2196F3,color:#fff
    style B fill:#4CAF50,color:#fff
    style D fill:#FF9800,color:#000
    style G fill:#9C27B0,color:#fff
    style J fill:#F44336,color:#fff
    style L fill:#795548,color:#fff
```

### 5. Live Data Feature Архітектура

```mermaid
graph TB
    subgraph "UI Layer"
        A[LiveScreen.kt]
        B[ChartComposable]
    end
    
    subgraph "ViewModel"
        C[LiveDataViewModel]
        D[LiveDataState]
    end
    
    subgraph "Domain"
        E[StreamLiveDataUseCase]
        F[PidModel]
    end
    
    subgraph "Protocols"
        G[ObdInterface]
        H[PidParser]
        I[Real-time Stream]
    end
    
    subgraph "Hardware"
        J[Port]
        K[Connection Manager]
    end
    
    A --> B
    A --> C
    C --> D
    C --> E
    E --> F
    E --> G
    G --> H
    H --> I
    I --> G
    G --> J
    J --> K
    
    style A fill:#00BCD4,color:#000
    style C fill:#4CAF50,color:#fff
    style E fill:#FFC107,color:#000
    style G fill:#E91E63,color:#fff
    style I fill:#FF5722,color:#fff
```

### 6. Dependency Graph (Gradle)

```mermaid
graph TD
    APP[app] --> CORE_DOMAIN[core:domain]
    APP --> CORE_DATA[core:data]
    APP --> FEAT_DTC[features:dtc]
    APP --> FEAT_LIVE[features:live]
    
    CORE_DATA --> CORE_DOMAIN
    
    FEAT_DTC --> CORE_DOMAIN
    FEAT_DTC --> CORE_DATA
    FEAT_DTC --> PROTO_OBD[protocols:obd]
    
    FEAT_LIVE --> CORE_DOMAIN
    FEAT_LIVE --> PROTO_OBD
    
    PROTO_OBD --> CORE_DOMAIN
    PROTO_OBD --> HW_TRANS[hardware:transport]
    
    HW_TRANS --> CORE_DOMAIN
    
    SEC[security] --> CORE_DOMAIN
    UPD[updates] --> CORE_DOMAIN
    
    style APP fill:#1976D2,color:#fff
    style CORE_DOMAIN fill:#388E3C,color:#fff
    style CORE_DATA fill:#388E3C,color:#fff
    style FEAT_DTC fill:#F57C00,color:#000
    style FEAT_LIVE fill:#F57C00,color:#000
    style PROTO_OBD fill:#7B1FA2,color:#fff
    style HW_TRANS fill:#5D4037,color:#fff
```

### 7. CI/CD Pipeline

```mermaid
graph LR
    A[Git Push] --> B[GitHub Actions]
    B --> C{Branch?}
    C -->|main| D[Full CI]
    C -->|PR| E[Lint Check]
    
    D --> F[Build]
    D --> G[Unit Tests]
    D --> H[Lint]
    D --> I[Detekt]
    
    F --> J{Success?}
    G --> J
    H --> J
    I --> J
    
    J -->|Yes| K[Deploy to Staging]
    J -->|No| L[Notify Developer]
    
    K --> M[UI Tests]
    M --> N{Pass?}
    N -->|Yes| O[Release to Production]
    N -->|No| L
    
    style A fill:#4CAF50,color:#fff
    style B fill:#2196F3,color:#fff
    style D fill:#FF9800,color:#000
    style E fill:#FFC107,color:#000
    style K fill:#9C27B0,color:#fff
    style O fill:#4CAF50,color:#fff
    style L fill:#F44336,color:#fff
```

### 8. Ролі AI Агентів

```mermaid
graph TB
    subgraph "AI Agent Ecosystem"
        A[RepoBuilder]
        B[UI Agent]
        C[Data Agent]
        D[Protocols Agent]
        E[Transport Agent]
        F[Security Reviewer]
    end
    
    subgraph "Responsibilities"
        A --> A1[DevContainer Setup]
        A --> A2[CI/CD Config]
        A --> A3[Build Management]
        
        B --> B1[Compose UI]
        B --> B2[Navigation]
        B --> B3[Accessibility]
        
        C --> C1[Room DB]
        C --> C2[Repositories]
        C --> C3[Data Mappers]
        
        D --> D1[OBD Protocol]
        D --> D2[PID Parsing]
        D --> D3[DTC Parsing]
        
        E --> E1[Bluetooth]
        E --> E2[USB Serial]
        E --> E3[TCP/IP]
        
        F --> F1[Code Review]
        F --> F2[Threat Analysis]
        F --> F3[Audit]
    end
    
    style A fill:#00BCD4,color:#000
    style B fill:#4CAF50,color:#fff
    style C fill:#FF9800,color:#000
    style D fill:#9C27B0,color:#fff
    style E fill:#F44336,color:#fff
    style F fill:#795548,color:#fff
```

### 9. Користувацькі Сценарії

```mermaid
journey
    title User Journey - DTC Діагностика
    section Підключення
      Відкрити додаток: 5: User
      Вибрати адаптер: 4: User
      Підключитись: 3: App, Hardware
    section Діагностика
      Натиснути "Read DTC": 5: User
      Зчитати коди: 3: App, OBD
      Показати результати: 5: App
    section Аналіз
      Переглянути деталі: 5: User
      Побачити причини: 4: User
      Зберегти звіт: 5: User
    section Ремонт
      Очистити коди: 4: User
      Перевірити повторно: 5: User
```

### 10. Стек Технологій

```mermaid
mindmap
  root((AutoDiagPro))
    Android
      Kotlin
      Jetpack Compose
      Material3
      Hilt DI
      Navigation
    Architecture
      Clean Architecture
      MVVM
      Repository Pattern
      Use Cases
    Data
      Room Database
      SQLite
      Coroutines
      Flow
    Hardware
      Bluetooth RFCOMM
      USB Serial
      TCP/IP Socket
    Protocols
      OBD-II
      ISO 15765-4 CAN
      ELM327
      PID/DTC Parsing
    DevOps
      GitHub Actions
      Codespaces
      Gradle Kotlin DSL
      ktlint/detekt
    Testing
      JUnit5
      Compose Testing
      MockK
      Truth
```

---

## 🎨 Кольорова Палітра

### Primary Colors
- **Primary**: `#00BCD4` (Cyan) - Головні елементи
- **Primary Variant**: `#0097A7` - Акценти
- **Secondary**: `#4CAF50` (Green) - Успішні стани
- **Secondary Variant**: `#388E3C` - Підтвердження

### Functional Colors
- **Error**: `#F44336` (Red) - Помилки, критичні DTC
- **Warning**: `#FF9800` (Orange) - Попередження
- **Info**: `#2196F3` (Blue) - Інформація
- **Success**: `#4CAF50` (Green) - Успіх

### Background
- **Background**: `#121212` - Темний фон
- **Surface**: `#1E1E1E` - Картки
- **Surface Variant**: `#2C2C2C` - Вторинні поверхні

### Text
- **On Primary**: `#000000` - Текст на primary
- **On Background**: `#FFFFFF` - Основний текст
- **On Surface**: `#E0E0E0` - Текст на картках

---

## 📐 Типографія

### Font Families
- **Display**: Roboto Bold - Заголовки
- **Body**: Roboto Regular - Основний текст
- **Monospace**: Roboto Mono - Технічні дані, коди

### Sizes
- **H1**: 32sp - Головні заголовки
- **H2**: 24sp - Секційні заголовки
- **Body1**: 16sp - Основний текст
- **Body2**: 14sp - Вторинний текст
- **Caption**: 12sp - Підписи

---

**Оновлено**: 2025-01-07  
**Версія**: 1.0  
**Автор**: AI Agent Ecosystem
