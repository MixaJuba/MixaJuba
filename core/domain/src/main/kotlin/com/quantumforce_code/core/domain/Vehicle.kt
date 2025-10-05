// 1. File Purpose: Vehicle domain entity model
// 2. Role: Represents vehicle metadata in diagnostics domain
// 3. Architecture: Core domain model, value object for vehicle identification
// 4. VIN Standard: 17-character code (ISO 3779), globally unique vehicle identifier
// 5. Usage:
//    - Links DiagnosticSession to specific vehicle
//    - Stores vehicle info for reports
//    - Enables vehicle-specific DTC lookups
// 6. Persistence: Stored in Room database (vehicles table)
// 7. Related: DiagnosticSession.kt (references Vehicle), VehicleRepository

package com.quantumforce_code.core.domain

/**
 * Представляє автомобіль в системі діагностики.
 * 
 * Vehicle є основною entity для ідентифікації автомобіля, який
 * діагностується. Містить мінімальну, але достатню інформацію для:
 * - Унікальної ідентифікації (VIN)
 * - Людиночитабельного відображення (make/model/year)
 * - Історії діагностик конкретного авто
 * 
 * **VIN (Vehicle Identification Number):**
 * - 17 символів (без I, O, Q для уникнення плутанини)
 * - Структура: WMI (3) + VDS (6) + VIS (8)
 * - Приклад: "1HGBH41JXMN109186"
 * - Можна декодувати: https://vpic.nhtsa.dot.gov/api/
 * 
 * **Приклади:**
 * ```kotlin
 * val vehicle = Vehicle(
 *     id = UUID.randomUUID().toString(),
 *     make = "Toyota",
 *     model = "Camry",
 *     year = 2018,
 *     vin = "4T1BF1FK5JU123456"
 * )
 * ```
 * 
 * @property id Внутрішній ID для Room database (UUID)
 * @property make Марка автомобіля (Toyota, Ford, BMW, тощо)
 * @property model Модель (Camry, F-150, X5, тощо)
 * @property year Рік випуску (1981+, коли запроваджено OBD)
 * @property vin VIN код (17 символів, ISO 3779 стандарт)
 * 
 * @see DiagnosticSession Сесії діагностики для цього авто
 * @see [VIN Decoder](https://vpic.nhtsa.dot.gov/api/)
 */
data class Vehicle(
    val id: String,         // UUID для Room DB
    val make: String,       // "Toyota", "Ford", "BMW"
    val model: String,      // "Camry", "F-150", "X5"
    val year: Int,          // 2018, 2020, тощо
    val vin: String         // "1HGBH41JXMN109186" (17 chars)
) {
    /**
     * Повертає людиночитабельну назву автомобіля.
     * Формат: "2018 Toyota Camry"
     */
    val displayName: String
        get() = "$year $make $model"
    
    /**
     * Валідація VIN коду (базова перевірка довжини).
     * Повна валідація з checksum потребує окремого UseCase.
     */
    val isVinValid: Boolean
        get() = vin.length == 17 && vin.all { it.isLetterOrDigit() }
    
    /**
     * Витягує WMI (World Manufacturer Identifier) з VIN.
     * Перші 3 символи визначають виробника та регіон.
     */
    val wmi: String
        get() = vin.take(3)
}
