package com.beckytech.og_artiiwwankutaa5ffaa.db;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "book_progress")
public class BookProgress {
    @PrimaryKey
    private int bookId = 1;
    private int lastChapterPosition;
    private long lastReadTimestamp;

    public BookProgress(int lastChapterPosition, long lastReadTimestamp) {
        this.lastChapterPosition = lastChapterPosition;
        this.lastReadTimestamp = lastReadTimestamp;
    }

    public int getBookId() { return bookId; }
    public void setBookId(int bookId) { this.bookId = bookId; }

    public int getLastChapterPosition() { return lastChapterPosition; }
    public void setLastChapterPosition(int lastChapterPosition) { this.lastChapterPosition = lastChapterPosition; }

    public long getLastReadTimestamp() { return lastReadTimestamp; }
    public void setLastReadTimestamp(long lastReadTimestamp) { this.lastReadTimestamp = lastReadTimestamp; }
}
