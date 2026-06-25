package com.example.proyectofinalgrupo3.data.business

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class InventarioBusinessRulesTest {

    @Test
    fun devolucionUtilDebeReintegrarStock() {
        val stockNuevo = InventarioBusinessRules.calcularStockReintegrado(
            stockActual = 10,
            cantidadDevuelta = 3,
            estadoProducto = InventarioBusinessRules.ESTADO_UTIL
        )

        assertTrue(InventarioBusinessRules.debeReintegrarStock(InventarioBusinessRules.ESTADO_UTIL))
        assertEquals(13, stockNuevo)
    }

    @Test
    fun devolucionDanadaNoDebeReintegrarStock() {
        val stockNuevo = InventarioBusinessRules.calcularStockReintegrado(
            stockActual = 10,
            cantidadDevuelta = 3,
            estadoProducto = InventarioBusinessRules.ESTADO_DANADO
        )

        assertFalse(InventarioBusinessRules.debeReintegrarStock(InventarioBusinessRules.ESTADO_DANADO))
        assertEquals(10, stockNuevo)
    }

    @Test
    fun stockMenorACincoDebeSerStockBajo() {
        assertTrue(InventarioBusinessRules.esStockBajo(4))
        assertFalse(InventarioBusinessRules.esStockBajo(5))
    }
}
