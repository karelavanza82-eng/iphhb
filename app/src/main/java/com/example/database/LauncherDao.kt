package com.example.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface LauncherDao {

    @Query("SELECT * FROM home_items ORDER BY pageIndex ASC, gridRow ASC, gridCol ASC")
    fun observeHomeItems(): Flow<List<HomeItemEntity>>

    @Query("SELECT * FROM home_items ORDER BY pageIndex ASC, gridRow ASC, gridCol ASC")
    suspend fun getAllHomeItemsOnce(): List<HomeItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertHomeItems(items: List<HomeItemEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertHomeItem(item: HomeItemEntity)

    @Query("DELETE FROM home_items WHERE id = :id")
    suspend fun deleteHomeItemById(id: String)

    @Query("DELETE FROM home_items WHERE packageName = :packageName AND itemType = 'APP'")
    suspend fun deleteAppsByPackage(packageName: String)

    @Query("DELETE FROM home_items")
    suspend fun clearAllHomeItems()

    @Transaction
    suspend fun replaceAllHomeItems(items: List<HomeItemEntity>) {
        clearAllHomeItems()
        upsertHomeItems(items)
    }

    @Query("SELECT * FROM badge_prefs")
    fun observeBadgePrefs(): Flow<List<AppBadgePrefEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertBadgePref(pref: AppBadgePrefEntity)

    @Query("SELECT * FROM recent_searches ORDER BY timestamp DESC LIMIT 8")
    fun observeRecentSearches(): Flow<List<RecentSearchEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertRecentSearch(search: RecentSearchEntity)

    @Query("DELETE FROM recent_searches")
    suspend fun clearRecentSearches()
}
