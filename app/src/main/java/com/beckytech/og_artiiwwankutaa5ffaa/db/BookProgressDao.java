package com.beckytech.og_artiiwwankutaa5ffaa.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface BookProgressDao {
    @Query("SELECT * FROM book_progress WHERE bookId = 1")
    BookProgress getProgress();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveProgress(BookProgress progress);
}
