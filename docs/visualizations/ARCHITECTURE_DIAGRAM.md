# 🏗️ QuantumForce_Code Architecture Visualization | Архітектурна Візуалізація

## 📋 File Purpose | Призначення
Візуальне представлення архітектури проекту AutoDiagPro з діаграмами модулів, потоків даних та взаємодій.

## 🎯 Role | Роль
Надає швидке розуміння структури проекту для нових розробників та AI-агентів.

---

## 🏛️ High-Level Architecture | Загальна Архітектура

```mermaid
graph TB
    subgraph "📱 Presentation Layer"
        UI[app/ui<br/>Jetpack Compose<br/>Screens & Components]
        NAV[app/navigation<br/>NavGraph]
    end
    
    subgraph "🧠 Business Logic Layer"
        FEAT_DTC[features/dtc<br/>DTC ViewModel]
        FEAT_LIVE[features/live<br/>Live Data ViewModel]
        DOMAIN[core/domain<br/>Use Cases<br/>Business Models]
    end
    
    subgraph "💾 Data Layer"
        DATA[core/data<br/>Repositories<br/>Room DB]
        CACHE[Local Cache<br/>SQLite]
    end
    
    subgraph "🔌 Hardware Layer"
        PROTO[protocols/obd<br/>ELM327<br/>PID/DTC Parsers]
        TRANS[hardware/transport<br/>Bluetooth/USB/TCP]
        OBD[OBD-II Adapter<br/>Physical Device]
    end
    
    subgraph "🔐 System Layer"
        SEC[security/<br/>Threat Model<br/>Policies]
        UPD[updates/<br/>Version Check<br/>Data Sync]
    end
    
    UI --> NAV
    NAV --> FEAT_DTC
    NAV --> FEAT_LIVE
    FEAT_DTC --> DOMAIN
    FEAT_LIVE --> DOMAIN
    DOMAIN --> DATA
    DATA --> CACHE
    DOMAIN --> PROTO
    PROTO --> TRANS
    TRANS --> OBD
    DOMAIN --> SEC
    DOMAIN --> UPD
    
    style UI fill:#1e3a5f,stroke:#00d9ff,stroke-width:3px,color:#fff
    style DOMAIN fill:#2d1b4e,stroke:#ff00ff,stroke-width:3px,color:#fff
    style DATA fill:#1b3a2d,stroke:#00ff88,stroke-width:3px,color:#fff
    style PROTO fill:#4a1e1e,stroke:#ff4444,stroke-width:3px,color:#fff
    style SEC fill:#3a2a1e,stroke:#ffaa00,stroke-width:3px,color:#fff
```

**Опис шарів / Layer Description:**
- **Presentation:** UI компоненти (Jetpack Compose), навігація
- **Business Logic:** ViewModel, Use Cases, domain моделі
- **Data:** Репозиторії, Room DB, кешування
- **Hardware:** Протоколи OBD-II, транспортні інтерфейси
- **System:** Безпека, оновлення, логування

---

## 📦 Module Dependencies | Залежності Модулів

```mermaid
graph LR
    APP[app]
    CORE_DOM[core/domain]
    CORE_DATA[core/data]
    FEAT_DTC[features/dtc]
    FEAT_LIVE[features/live]
    PROTO[protocols/obd]
    TRANS[hardware/transport]
    SEC[security]
    UPD[updates]
    
    APP --> FEAT_DTC
    APP --> FEAT_LIVE
    APP --> CORE_DOM
    
    FEAT_DTC --> CORE_DOM
    FEAT_DTC --> CORE_DATA
    
    FEAT_LIVE --> CORE_DOM
    FEAT_LIVE --> CORE_DATA
    
    CORE_DATA --> CORE_DOM
    
    CORE_DOM --> PROTO
    CORE_DOM --> TRANS
    CORE_DOM --> SEC
    CORE_DOM --> UPD
    
    PROTO --> TRANS
    
    style APP fill:#ff6b6b,stroke:#c92a2a,stroke-width:2px,color:#fff
    style CORE_DOM fill:#4dabf7,stroke:#1971c2,stroke-width:2px,color:#fff
    style CORE_DATA fill:#51cf66,stroke:#2b8a3e,stroke-width:2px,color:#fff
    style PROTO fill:#ffd43b,stroke:#f59f00,stroke-width:2px,color:#000
    style SEC fill:#ff6b9d,stroke:#c92a62,stroke-width:2px,color:#fff
```

**Правила залежностей / Dependency Rules:**
- ✅ `app` може залежати від всіх `features/`
- ✅ `features/` може залежати від `core/`
- ✅ `core/domain` - незалежний (чиста бізнес-логіка)
- ✅ `core/data` → `core/domain` (тільки в одному напрямку)
- ❌ `core/` не може залежати від `features/` або `app`

---

## 🔄 Data Flow Diagram | Діаграма Потоку Даних

```mermaid
sequenceDiagram
    autonumber
    actor User
    participant UI as UI Screen<br/>(Compose)
    participant VM as ViewModel<br/>(features/)
    participant UC as UseCase<br/>(core/domain)
    participant Repo as Repository<br/>(core/data)
    participant DB as Room DB
    participant Proto as OBD Protocol<br/>(protocols/obd)
    participant HW as Transport<br/>(hardware/transport)
    participant Device as OBD-II Device
    
    User->>UI: Натиснути "Read DTC"
    UI->>VM: readDtcCodes()
    VM->>UC: Execute(ReadDtcUseCase)
    UC->>Repo: getDtcCodes()
    
    alt Cache available
        Repo->>DB: Query cached DTC
        DB-->>Repo: Return cached data
    else No cache
        Repo->>Proto: sendObdCommand("03")
        Proto->>HW: write("03\r")
        HW->>Device: Bluetooth/USB
        Device-->>HW: Response "43 01 33..."
        HW-->>Proto: Raw data
        Proto-->>Repo: Parsed DTC list
        Repo->>DB: Cache DTC codes
    end
    
    Repo-->>UC: DTC codes
    UC-->>VM: Result<List<DtcCode>>
    VM-->>UI: Update UiState
    UI-->>User: Відобразити DTC

    Note over UI,Device: 🚀 Офлайн-first: спочатку cache, потім device
```

**Ключові принципи / Key Principles:**
1. **Offline-first:** Завжди спочатку перевіряємо кеш
2. **Unidirectional flow:** Дані течуть UI → ViewModel → Domain → Data
3. **Result wrapping:** Всі операції повертають `Result<T>` для error handling

---

## 🤖 AI Agents Interaction | Взаємодія AI-Агентів

```mermaid
graph TB
    subgraph "👤 Human Developer"
        DEV[Developer<br/>Task Assignment]
    end
    
    subgraph "🤖 AI Agents"
        REPO[RepoBuilder<br/>Infrastructure<br/>DevOps]
        UI_AG[UI Agent<br/>Compose<br/>Screens]
        DATA_AG[Data Agent<br/>Room/Repo<br/>Persistence]
        PROTO_AG[Protocol Agent<br/>OBD-II<br/>Parsers]
        TEST_AG[Test Agent<br/>Unit/Integration<br/>Tests]
        SEC_AG[Security Agent<br/>Threat Review<br/>Audits]
    end
    
    subgraph "📂 Project Modules"
        APP_MOD[app/]
        CORE_MOD[core/]
        FEAT_MOD[features/]
        PROTO_MOD[protocols/]
        TEST_MOD[tests/]
    end
    
    subgraph "📚 Knowledge Base"
        KB[AI_AGENT_PROMPTS_LIBRARY<br/>AUTOMOTIVE_GUIDE<br/>AGENTS.md]
    end
    
    DEV -->|Assigns Task| REPO
    REPO -->|Coordinates| UI_AG
    REPO -->|Coordinates| DATA_AG
    REPO -->|Coordinates| PROTO_AG
    
    UI_AG -->|Reads| KB
    DATA_AG -->|Reads| KB
    PROTO_AG -->|Reads| KB
    
    UI_AG -->|Modifies| APP_MOD
    UI_AG -->|Modifies| FEAT_MOD
    
    DATA_AG -->|Modifies| CORE_MOD
    
    PROTO_AG -->|Modifies| PROTO_MOD
    
    UI_AG -->|Requests Tests| TEST_AG
    DATA_AG -->|Requests Tests| TEST_AG
    TEST_AG -->|Creates| TEST_MOD
    
    TEST_AG -->|Requests Review| SEC_AG
    SEC_AG -->|Audits| ALL[All Modules]
    
    style REPO fill:#ff6b6b,stroke:#c92a2a,stroke-width:3px,color:#fff
    style UI_AG fill:#4dabf7,stroke:#1971c2,stroke-width:2px,color:#fff
    style DATA_AG fill:#51cf66,stroke:#2b8a3e,stroke-width:2px,color:#fff
    style PROTO_AG fill:#ffd43b,stroke:#f59f00,stroke-width:2px,color:#000
    style TEST_AG fill:#ff6b9d,stroke:#c92a62,stroke-width:2px,color:#fff
    style SEC_AG fill:#9775fa,stroke:#5f3dc4,stroke-width:2px,color:#fff
```

**Ролі агентів / Agent Roles:**
- **RepoBuilder:** Інфраструктура, CI/CD, координація
- **UI Agent:** Jetpack Compose, навігація, UI-компоненти
- **Data Agent:** Room, репозиторії, кешування
- **Protocol Agent:** OBD-II, парсери, hardware integration
- **Test Agent:** Unit/integration тести, coverage
- **Security Agent:** Threat modeling, code audit

---

## 📁 Directory Tree Visualization | Дерево Директорій

```
QuantumForce_Code/
│
├── 📱 app/                      # Android Application (APK)
│   ├── 🎨 ui/                   # Jetpack Compose UI
│   │   ├── screens/             # DashboardScreen, DtcScreen, LiveScreen
│   │   └── components/          # Reusable UI components
│   ├── 🧭 navigation/           # NavGraph (Navigation Compose)
│   └── 📦 AndroidManifest.xml
│
├── 🧠 core/                     # Core Business Logic
│   ├── 💼 domain/               # Use Cases, Business Models
│   │   ├── UseCase.kt
│   │   ├── Vehicle.kt
│   │   └── DtcCode.kt
│   └── 💾 data/                 # Data Layer
│       ├── db/                  # Room Database
│       └── repo/                # Repositories
│
├── ⚡ features/                 # Feature Modules
│   ├── 🔴 dtc/                  # DTC Reading Feature
│   │   ├── DtcViewModel.kt
│   │   └── DtcUiState.kt
│   └── 📊 live/                 # Live Data Feature
│       ├── LiveDataViewModel.kt
│       └── LiveChartRenderer.kt
│
├── 🔌 hardware/                 # Hardware Integration
│   └── transport/               # Physical Connections
│       ├── Port.kt              # Abstract interface
│       ├── BluetoothPort.kt     # Bluetooth RFCOMM
│       ├── UsbSerialPort.kt     # USB OTG
│       └── TcpPort.kt           # Wi-Fi/TCP
│
├── 🛰️ protocols/                # Diagnostic Protocols
│   └── obd/                     # OBD-II Standard
│       ├── ObdInterface.kt
│       ├── Elm327.kt            # ELM327 adapter
│       ├── PidParser.kt         # PID responses
│       └── DtcParser.kt         # DTC codes
│
├── 🔐 security/                 # Security Module
│   ├── ThreatModel.md
│   └── SecurityPolicy.kt
│
├── 🔄 updates/                  # Update System
│   ├── UpdateChecker.kt
│   └── DataVersion.kt
│
├── 📚 docs/                     # Documentation
│   ├── architecture.md
│   ├── roadmap.md
│   └── adr/                     # Architecture Decision Records
│
├── 🤖 prompts/                  # AI Agent Prompts
│   ├── ui-agent-start.md
│   ├── data-agent-start.md
│   └── protocols-agent-start.md
│
└── ⚙️ scripts/                  # Automation Scripts
    ├── verify-local.sh          # Pre-PR checks
    └── run-tests.sh             # Test runner
```

---

## 🎨 Color Coding | Кольорова Кодування

### By Layer Type
- 🔵 **Blue (#1e3a5f):** UI/Presentation layer
- 🟣 **Purple (#2d1b4e):** Business Logic layer
- 🟢 **Green (#1b3a2d):** Data/Persistence layer
- 🔴 **Red (#4a1e1e):** Hardware/Protocols layer
- 🟡 **Orange (#3a2a1e):** Security/System layer

### By Component Type
- 📱 **App:** Main Android module
- 🧠 **Core:** Domain + Data
- ⚡ **Features:** Feature modules (dtc, live)
- 🔌 **Hardware:** Physical interfaces
- 🛰️ **Protocols:** Communication protocols
- 🔐 **Security:** Security policies
- 📚 **Docs:** Documentation
- 🤖 **Prompts:** AI agent prompts

---

## 🔗 Export Formats | Формати Експорту

### Mermaid Live Editor
1. Скопіювати будь-який `mermaid` блок
2. Відкрити [Mermaid Live Editor](https://mermaid.live/)
3. Вставити код
4. Експортувати: SVG, PNG, PDF

### GitHub Markdown
Діаграми автоматично рендеряться у GitHub Markdown (цей файл).

### VS Code
Використати extension: `Markdown Preview Mermaid Support`

### PlantUML (альтернатива)
Можна конвертувати у PlantUML для більш складних діаграм.

---

## 📖 Додаткові Діаграми / Additional Diagrams

Детальні діаграми для окремих компонентів:
- `OBD_PROTOCOL_FLOW.md` - Детальний flow OBD-II команд
- `ROOM_DATABASE_SCHEMA.md` - Схема БД з таблицями
- `COMPOSE_UI_TREE.md` - Дерево UI-компонентів
- `HILT_DI_GRAPH.md` - Граф Dependency Injection

---

**Generated by:** RepoBuilder AI Agent 🤖  
**Tools:** Mermaid.js, Markdown  
**Last Updated:** 2025 🚦⚡  
**Style:** Cyberpunk Tech Noir 🌃🔥
