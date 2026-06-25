package com.example.proyectofinalgrupo3.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.proyectofinalgrupo3.data.dao.*
import com.example.proyectofinalgrupo3.data.entities.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

@Database(
    entities = [
        RolEntity::class,
        CategoriaEntity::class,
        ColeccionEntity::class,
        UsuarioEntity::class,
        ProductoEntity::class,
        PedidoEntity::class,
        DetallePedidoEntity::class,
        CarritoEntity::class,
        MovimientoInventarioEntity::class,
        DevolucionEntity::class,
        MermaEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao
    abstract fun productoDao(): ProductoDao
    abstract fun catalogosBasicosDao(): CatalogosBasicosDao
    abstract fun ecommerceDao(): EcommerceDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ecommerce_db"
                )
                .addCallback(AppDatabaseCallback(CoroutineScope(Dispatchers.IO)) {
                    getDatabase(context)
                })
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
