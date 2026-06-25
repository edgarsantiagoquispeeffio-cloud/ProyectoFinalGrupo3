package com.example.proyectofinalgrupo3.data.repository

import com.example.proyectofinalgrupo3.data.dao.ProductoDao
import com.example.proyectofinalgrupo3.data.entities.ProductoEntity
import kotlinx.coroutines.flow.Flow

class CatalogoRepository(val productoDao: ProductoDao) {
    fun obtenerProductos(): Flow<List<ProductoEntity>> {
        return productoDao.obtenerCatalogo()
    }
}
