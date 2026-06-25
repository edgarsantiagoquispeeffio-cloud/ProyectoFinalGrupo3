package com.example.proyectofinalgrupo3.data.business

/**
 * Reglas puras de inventario.
 * No recibe Context ni referencias de UI para evitar memory leaks y facilitar pruebas unitarias.
 */
object InventarioBusinessRules {
    const val ESTADO_UTIL = "Útil / Buen Estado"
    const val ESTADO_DANADO = "Dañado / Defectuoso"
    const val STOCK_BAJO_LIMITE = 5

    fun debeReintegrarStock(estadoProducto: String): Boolean {
        return estadoProducto.trim().equals(ESTADO_UTIL, ignoreCase = true)
    }

    fun esStockBajo(stockActual: Int): Boolean {
        return stockActual < STOCK_BAJO_LIMITE
    }

    fun validarCantidad(cantidad: Int): Boolean {
        return cantidad > 0
    }

    fun calcularStockReintegrado(stockActual: Int, cantidadDevuelta: Int, estadoProducto: String): Int {
        return if (debeReintegrarStock(estadoProducto)) {
            stockActual + cantidadDevuelta
        } else {
            stockActual
        }
    }
}
