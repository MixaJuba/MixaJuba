# 🤖 AI Agent Prompts Library for Automotive Software Development

Quick reference library of proven AI agent prompts for automotive diagnostic software development.

---

## 📋 Table of Contents

1. [Architecture & Design Prompts](#architecture--design-prompts)
2. [Code Generation Prompts](#code-generation-prompts)
3. [Protocol Implementation Prompts](#protocol-implementation-prompts)
4. [Testing & QA Prompts](#testing--qa-prompts)
5. [Documentation Prompts](#documentation-prompts)
6. [Debugging & Optimization Prompts](#debugging--optimization-prompts)

---

## 🏗️ Architecture & Design Prompts

### System Architecture Design

```markdown
**Agent:** Claude-3 Opus / GPT-4
**Task:** Design system architecture
**Context:** Automotive diagnostic application

Prompt:
"""
Role: Senior Software Architect with 15+ years in automotive software

Design a scalable architecture for an automotive diagnostic application with these requirements:

Technical Requirements:
- Platform: Android (Kotlin)
- Hardware: OBD-II adapter (Bluetooth/USB)
- Supported protocols: {list_protocols}
- Database: SQLite (50,000+ DTCs)
- Features: {list_features}

Constraints:
- Offline-first operation
- < 500MB memory usage
- Support for 50+ vehicle brands
- Real-time data processing
- Secure coding/programming operations

Deliverables:
1. Architecture diagram (Mermaid syntax)
2. Component breakdown with responsibilities
3. Data flow diagrams
4. Technology stack recommendations
5. Scalability considerations
6. Security architecture
7. Performance optimization strategies

Format: Technical specification document with diagrams
"""
```

### Database Schema Design

```markdown
**Agent:** GPT-4 / Claude
**Task:** Design database schema

Prompt:
"""
Design a comprehensive database schema for automotive diagnostics:

Entities:
1. Vehicles (make, model, year, VIN patterns)
2. DTCs (50,000+ codes with descriptions, causes, solutions)
3. PIDs (parameters with formulas, units, ranges)
4. Diagnostic Procedures (step-by-step)
5. User Sessions (history, reports)
6. Update Metadata (versions, checksums)

Requirements:
- Optimized for mobile (SQLite)
- Multi-language support (5+ languages)
- Full-text search capability
- Efficient indexing strategy
- Support for incremental updates
- Version control for data

Output:
1. SQL DDL statements
2. ER diagram (Mermaid)
3. Index definitions
4. Sample queries with execution plans
5. Migration strategy
6. Data seeding approach
"""
```

---

## 💻 Code Generation Prompts

### OBD-II Communication Module

```markdown
**Agent:** GitHub Copilot / GPT-4
**Task:** Implement OBD-II communication

Prompt:
"""
Implement OBD-II communication layer in Kotlin:

Requirements:
1. ELM327 adapter support (Bluetooth/WiFi/USB)
2. Protocol auto-detection
3. AT command handling
4. PID request/response parsing
5. DTC read/clear operations
6. Async communication with coroutines
7. Connection pooling and retry logic
8. Error handling with custom exceptions

Classes to implement:
- enum class OBDProtocol
- class ELM327Connector(ioStream: IOStream)
- sealed class OBDCommand
- data class OBDResponse
- interface OBDRepository
- class ConnectionManager

Code style:
- Follow Google Kotlin style guide
- Include KDoc comments
- Use sealed classes for type safety
- Implement proper resource cleanup
- Add logging (Timber)

Include:
- Complete implementation
- Unit tests (MockK)
- Usage examples
- Error scenarios handling
"""
```

### UI Component with Jetpack Compose

```markdown
**Agent:** GitHub Copilot
**Task:** Create Compose UI component

Prompt:
"""
Create a Jetpack Compose screen for displaying diagnostic trouble codes (DTCs):

Features:
1. Top bar with scan button
2. LazyColumn of DTC cards
3. Each card shows:
   - Code (e.g., P0420)
   - Description
   - Status badge (Confirmed/Pending/Historical)
   - Severity indicator (color-coded)
   - Tap to expand for details
4. Floating action button to clear codes
5. Pull-to-refresh
6. Empty state
7. Loading state with shimmer effect
8. Error state

Requirements:
- Material 3 design
- Dark theme support
- Smooth animations
- Accessibility labels
- State hoisting
- ViewModel integration

Code structure:
```kotlin
@Composable
fun DTCScreen(
    viewModel: DTCViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // Implementation
}

@Composable
private fun DTCCard(
    dtc: DTC,
    onExpand: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Implementation
}
```

Include preview functions for different states
"""
```

---

## 🔌 Protocol Implementation Prompts

### VAG KWP2000 Protocol

```markdown
**Agent:** Specialized Automotive AI / Claude
**Task:** Implement VAG protocol

Prompt:
"""
You are an automotive protocol expert specializing in Volkswagen Audi Group diagnostics.

Implement KWP2000 protocol for VAG vehicles:

Specifications:
- Protocol: ISO 14230-4 (KWP2000)
- Baud rate: 10400 bps (init), variable (post-init)
- Addressing: Physical (ECU-specific)
- Transport: K-Line

Features to implement:
1. Fast initialization sequence
2. Service 0x10 - Diagnostic Session Control
3. Service 0x27 - Security Access (login)
4. Service 0x22 - Read Data By Identifier
5. Service 0x31 - Routine Control
6. Service 0x2E - Write Data By Identifier
7. Module identification (0x1A)
8. Coding (0x2C)
9. Adaptations
10. Basic settings

Include:
- Complete protocol implementation
- Security access algorithms (if publicly available)
- Module address mapping
- Error code handling
- Timing parameters
- Example usage for common tasks
- Safety checks

Format: Kotlin class with detailed comments
"""
```

### CAN Bus Communication

```markdown
**Agent:** GPT-4 / Claude
**Task:** Implement CAN bus handler

Prompt:
"""
Implement CAN Bus communication for automotive diagnostics:

Protocols:
- ISO 15765-4 (ISO-TP)
- CAN 2.0A (11-bit identifier)
- CAN 2.0B (29-bit identifier)
- Speeds: 125, 250, 500, 1000 kbps

Features:
1. Multi-frame message handling (First Frame, Consecutive Frame, Flow Control)
2. CAN ID filtering
3. Message priority handling
4. Timeout management
5. Error detection (CRC, bit stuffing)
6. Broadcast vs. point-to-point
7. ISO-TP segmentation/reassembly

Implementation:
```kotlin
class CANBusManager {
    suspend fun sendMessage(
        id: Int,
        data: ByteArray,
        functional: Boolean = false
    ): Result<ByteArray>
    
    suspend fun sendMultiFrame(
        id: Int,
        data: ByteArray
    ): Result<ByteArray>
    
    fun startMonitoring(filter: CANFilter): Flow<CANMessage>
}
```

Include error handling, logging, and tests
"""
```

---

## 🧪 Testing & QA Prompts

### Unit Test Generation

```markdown
**Agent:** Test Generation AI / GPT-4
**Task:** Generate comprehensive unit tests

Prompt:
"""
Generate comprehensive unit tests for the following class:

```kotlin
{paste_your_class_here}
```

Requirements:
- Framework: JUnit 5
- Mocking: MockK
- Assertions: Google Truth
- Coverage: > 90%

Test types:
1. Happy path tests
2. Edge cases (null, empty, boundary values)
3. Error scenarios with proper exception handling
4. Concurrent access scenarios (if applicable)
5. Performance tests (if applicable)

Test structure:
```kotlin
@Test
fun `test_name should expected_behavior when given_condition`() {
    // Given
    // When
    // Then
}
```

Additional requirements:
- Use test data builders
- Parameterized tests for multiple inputs
- Proper test isolation (no shared state)
- Clear assertion messages
- Mock verification where appropriate
- Setup/teardown where needed

Generate complete test class with all scenarios
"""
```

### Integration Test Plan

```markdown
**Agent:** QA Planning AI / GPT-4
**Task:** Create integration test plan

Prompt:
"""
Create a comprehensive integration test plan for automotive diagnostic software:

Test Scope:
1. Hardware communication (OBD adapter)
2. Database operations
3. Network calls (updates, cloud sync)
4. UI flows
5. Permission handling

Test Vehicles:
- VW Golf Mk7 (2015)
- BMW 3 Series F30 (2014)
- Mercedes C-Class W205 (2016)
- Toyota Corolla E210 (2019)
- Ford Focus Mk4 (2018)

For each vehicle:
1. Connection test (all protocols)
2. DTC operations (read, clear, verify)
3. Live data (20+ PIDs)
4. Freeze frame data
5. Vehicle info retrieval
6. Module scanning
7. Special functions (brand-specific)

Output format:
| Test ID | Test Case | Vehicle | Expected Result | Priority |
|---------|-----------|---------|-----------------|----------|

Include:
- Prerequisites
- Test data requirements
- Pass/fail criteria
- Edge cases
- Performance benchmarks
- Automation possibilities
"""
```

---

## 📚 Documentation Prompts

### API Documentation

```markdown
**Agent:** GPT-4
**Task:** Generate API documentation

Prompt:
"""
Generate comprehensive API documentation for the automotive diagnostic module:

Code:
```kotlin
{paste_your_code_here}
```

Documentation should include:
1. Class/interface overview
2. Method descriptions
3. Parameter explanations
4. Return value details
5. Exceptions thrown
6. Usage examples
7. Code snippets
8. Best practices
9. Common pitfalls
10. Performance considerations

Format: KDoc style with markdown formatting

Example format:
```kotlin
/**
 * Manages OBD-II communication with vehicle ECUs.
 *
 * This class provides a high-level interface for diagnostic operations
 * including DTC reading, live data monitoring, and ECU commands.
 *
 * ## Usage
 * ```kotlin
 * val manager = OBDManager(bluetoothAdapter)
 * manager.connect("00:11:22:33:44:55")
 * val dtcs = manager.readDTCs()
 * ```
 *
 * @property adapter The hardware adapter for communication
 * @constructor Creates an OBD manager with the specified adapter
 */
```
"""
```

### User Guide

```markdown
**Agent:** GPT-4 / Claude
**Task:** Write user documentation

Prompt:
"""
Write a comprehensive user guide for the automotive diagnostic app:

Target audience: Professional mechanics with moderate tech skills

Sections needed:
1. Getting Started
   - Hardware requirements
   - App installation
   - Initial setup
   - Connecting to vehicle

2. Basic Operations
   - Reading diagnostic codes
   - Understanding code descriptions
   - Clearing codes
   - Viewing live data

3. Advanced Features
   - Module coding
   - Component adaptations
   - Key programming
   - Special functions

4. Troubleshooting
   - Connection issues
   - Communication errors
   - App crashes
   - Hardware problems

5. FAQ
   - Common questions
   - Tips and tricks
   - Performance optimization

Format: Markdown with:
- Screenshots (placeholders)
- Step-by-step instructions
- Warning boxes for critical info
- Tip boxes for best practices
- Code examples where applicable

Tone: Professional but friendly, clear and concise
"""
```

---

## 🐛 Debugging & Optimization Prompts

### Performance Analysis

```markdown
**Agent:** GPT-4 / Claude
**Task:** Analyze and optimize performance

Prompt:
"""
Analyze the following code for performance bottlenecks:

```kotlin
{paste_your_code_here}
```

Context:
- Platform: Android mobile app
- Constraint: < 500MB memory, < 2s response time
- Usage: Real-time vehicle diagnostics

Analyze:
1. Algorithmic complexity (Big O)
2. Memory allocations
3. Database query efficiency
4. Network calls optimization
5. UI rendering performance
6. Battery consumption

Provide:
1. Performance issues identified (prioritized)
2. Detailed explanation of each issue
3. Optimized code implementation
4. Benchmarking comparison
5. Best practices recommendations

Include:
- Before/after metrics
- Profiling suggestions
- Android-specific optimizations
- Caching strategies
"""
```

### Bug Root Cause Analysis

```markdown
**Agent:** Debugging AI / Claude
**Task:** Analyze bug and find root cause

Prompt:
"""
Analyze the following bug and identify root cause:

**Bug Report:**
- Symptom: {describe_what_happens}
- Expected: {describe_expected_behavior}
- Frequency: {always/sometimes/rare}
- Environment: {Android version, device, etc.}

**Logs:**
```
{paste_error_logs}
```

**Stack Trace:**
```
{paste_stack_trace}
```

**Relevant Code:**
```kotlin
{paste_code_context}
```

**Reproduction Steps:**
1. {step_1}
2. {step_2}
3. {step_3}

Provide:
1. Root cause analysis
2. Why this happens (technical explanation)
3. Affected components/modules
4. Potential side effects
5. Fix recommendations (multiple approaches)
6. Prevention strategies
7. Test cases to verify fix
8. Related issues to check

Format: Detailed technical report with code snippets
"""
```

---

## 🎯 Quick Reference: Agent Selection Guide

| Task Type | Recommended Agent | Best For |
|-----------|------------------|----------|
| Architecture Design | Claude-3 Opus | Complex system design, trade-off analysis |
| Code Generation | GitHub Copilot | Day-to-day coding, boilerplate |
| Protocol Implementation | GPT-4 + Domain Knowledge | Technical specifications, standards |
| Testing | GPT-4 | Comprehensive test coverage |
| Documentation | GPT-4 / Claude | Clear, well-structured docs |
| Debugging | Claude-3 Opus | Complex bug analysis |
| Optimization | GPT-4 | Performance analysis |
| UI/UX Design | GPT-4 + DALL-E | Interface design, user flows |

---

## 📝 Prompt Engineering Tips

### ✅ DO's:

1. **Be Specific** - Include exact requirements, constraints, technologies
2. **Provide Context** - Explain the domain, use case, target audience
3. **Request Examples** - Ask for code samples, usage examples
4. **Specify Format** - Request specific output format (markdown, code, diagrams)
5. **Include Tests** - Always ask for test cases
6. **Set Standards** - Specify coding style, conventions
7. **Request Alternatives** - Ask for multiple solutions with trade-offs

### ❌ DON'Ts:

1. **Don't Be Vague** - Avoid ambiguous requirements
2. **Don't Skip Context** - Always provide background information
3. **Don't Trust Blindly** - Always review and validate output
4. **Don't Ignore Edge Cases** - Explicitly ask for error handling
5. **Don't Skip Documentation** - Always request comments and docs
6. **Don't Forget Security** - Ask about security implications

---

## 🔄 Iterative Prompt Refinement

If you don't get the desired result, refine your prompt:

**Initial Prompt:**
```
Create a function to read DTCs
```

**Refined Prompt:**
```
Create a Kotlin suspend function to read diagnostic trouble codes from OBD-II:

Requirements:
- Input: OBDConnection interface
- Output: Result<List<DTC>>
- Protocol: ISO 15765-4
- Error handling: Timeout, connection loss
- Logging: Include debug logs

Format:
```kotlin
suspend fun readDTCs(connection: OBDConnection): Result<List<DTC>> {
    // implementation
}
```

Include unit tests with MockK
```

---

## 📚 Additional Resources

- [Main Guide](AUTOMOTIVE_DIAGNOSTIC_SOFTWARE_GUIDE.md)
- [GitHub Copilot Documentation](https://docs.github.com/en/copilot)
- [Claude API Documentation](https://docs.anthropic.com/)
- [OpenAI API Documentation](https://platform.openai.com/docs)

---

## 📞 Contributing

Have a great prompt? Submit a PR to add it to this library!

**Template for new prompts:**
```markdown
### Prompt Title

**Agent:** {Recommended AI Agent}
**Task:** {What this prompt accomplishes}

Prompt:
"""
{Your detailed prompt here}
"""
```

---

*Last Updated: 2024*
*Version: 1.0*
