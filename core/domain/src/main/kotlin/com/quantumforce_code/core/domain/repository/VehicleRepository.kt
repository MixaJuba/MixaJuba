// 1. File Purpose: Repository interface for vehicles
// 2. Role: Defines contract for vehicle data access
// 3. Принципи: Repository Pattern, CRUD operations

package com.quantumforce_code.core.domain.repository

import com.quantumforce_code.core.domain.Vehicle

/**
 * Інтерфейс репозиторію для роботи з автомобілями.
 * 
 * Призначення: Визначає операції CRUD для управління інформацією про автомобілі.
 * Реалізація знаходиться в data шарі, інтерфейс - в domain для інверсії залежностей.
 */
interface VehicleRepository {
    /**
     * Отримує всі збережені автомобілі користувача.
     * 
     * @return список автомобілів, відсортований за датою додавання
     */
    suspend fun getAllVehicles(): List<Vehicle>
    
    /**
     * Отримує автомобіль за унікальним ідентифікатором.
     * 
     * @param id ідентифікатор автомобіля
     * @return Vehicle або null якщо не знайдено
     */
    suspend fun getVehicleById(id: String): Vehicle?
    
    /**
     * Отримує автомобіль за VIN номером.
     * 
     * @param vin VIN номер автомобіля (17 символів)
     * @return Vehicle або null якщо не знайдено
     */
    suspend fun getVehicleByVin(vin: String): Vehicle?
    
    /**
     * Зберігає новий автомобіль у сховище.
     * 
     * @param vehicle автомобіль для збереження
     * @return Result.success з ID створеного автомобіля
     */
    suspend fun saveVehicle(vehicle: Vehicle): Result<String>
    
    /**
     * Оновлює інформацію про існуючий автомобіль.
     * 
     * @param vehicle автомобіль з оновленою інформацією
     * @return Result.success якщо успішно
     */
    suspend fun updateVehicle(vehicle: Vehicle): Result<Unit>
    
    /**
     * Видаляє автомобіль зі сховища.
     * Також видаляє всі пов'язані DTC коди та історію діагностики.
     * 
     * @param id ідентифікатор автомобіля для видалення
     * @return Result.success якщо успішно
     */
    suspend fun deleteVehicle(id: String): Result<Unit>
    
    /**
     * Шукає автомобілі за маркою, моделлю або VIN.
     * 
     * @param query пошуковий запит
     * @return список знайдених автомобілів
     */
    suspend fun searchVehicles(query: String): List<Vehicle>
    
    /**
     * Отримує останній використаний автомобіль.
     * 
     * @return Vehicle або null якщо немає збережених
     */
    suspend fun getLastUsedVehicle(): Vehicle?
    
    /**
     * Позначає автомобіль як останній використаний.
     * 
     * @param id ідентифікатор автомобіля
     * @return Result.success якщо успішно
     */
    suspend fun setLastUsedVehicle(id: String): Result<Unit>
}
