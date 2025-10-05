// 1. File Purpose: DAO for DTC codes
// 2. Role: Data access abstraction (placeholder)

package com.quantumforce_code.core.data.db

import androidx.room.Dao
import androidx.room.Query

@Dao
interface DtcDao {
    @Query("SELECT * FROM dtc_codes")
    fun getAllDtcCodes(): List<DtcEntity>
}
