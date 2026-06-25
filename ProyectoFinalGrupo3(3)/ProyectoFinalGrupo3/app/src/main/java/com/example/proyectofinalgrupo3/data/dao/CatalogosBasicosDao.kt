package com.example.proyectofinalgrupo3.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.example.proyectofinalgrupo3.data.entities.CategoriaEntity
import com.example.proyectofinalgrupo3.data.entities.ColeccionEntity
import com.example.proyectofinalgrupo3.data.entities.RolEntity

@Dao
interface CatalogosBasicosDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoles(roles: List<RolEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategorias(categorias: List<CategoriaEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertColecciones(colecciones: List<ColeccionEntity>)
}
