package com.mmd.core.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import org.junit.After
import org.junit.Before

/**
 * 각 DAO 테스트 클래스가 상속받는 공통 베이스.
 * @Before/@After로 in-memory MmdDatabase를 매 테스트마다 새로 생성/해제.
 */
abstract class DatabaseTestRule {
    protected lateinit var db: MmdDatabase
        private set

    @Before
    fun setupDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, MmdDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun tearDownDb() {
        db.close()
    }
}
