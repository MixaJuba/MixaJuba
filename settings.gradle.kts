// Root settings.gradle.kts placeholder
rootProject.name = "QuantumForce_Code"
include(
  "app",
  "core:domain",
  "core:data",
  "hardware:transport",
  "protocols:obd",
  "features:dtc",
  "features:live",
  "security",
  "updates"
)
