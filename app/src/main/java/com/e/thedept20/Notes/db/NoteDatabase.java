package com.e.thedept20.Notes.db;


import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.e.thedept20.Notes.dao.DaoAccess;
import com.e.thedept20.Notes.model.Note;

@Database(entities = {Note.class}, version = 1, exportSchema = false)
public abstract class NoteDatabase extends RoomDatabase
{

    public abstract DaoAccess daoAccess();
}
