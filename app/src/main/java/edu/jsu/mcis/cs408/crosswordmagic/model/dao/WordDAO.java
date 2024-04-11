package edu.jsu.mcis.cs408.crosswordmagic.model.dao;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import java.util.HashMap;

import edu.jsu.mcis.cs408.crosswordmagic.model.Puzzle;

public class WordDAO {

    private final DAOFactory daoFactory;

    WordDAO(DAOFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    public int create(HashMap<String, String> params) {

        /* use this method if there is NOT already a SQLiteDatabase open */

        SQLiteDatabase db = daoFactory.getWritableDatabase();
        int result = create(db, params);
        db.close();
        return result;

    }

    public int create(SQLiteDatabase db, HashMap<String, String> params) {

        /* use this method if there IS already a SQLiteDatabase open */

        String puzzleid = daoFactory.getProperty("sql_field_puzzleid");
        String row = daoFactory.getProperty("sql_field_row");
        String column = daoFactory.getProperty("sql_field_column");
        String box = daoFactory.getProperty("sql_field_box");
        String direction = daoFactory.getProperty("sql_field_direction");
        String word = daoFactory.getProperty("sql_field_word");
        String clue = daoFactory.getProperty("sql_field_clue");

        ContentValues values = new ContentValues();
        values.put(puzzleid, Integer.parseInt(params.get(puzzleid)));
        values.put(row, Integer.parseInt(params.get(row)));
        values.put(column, Integer.parseInt(params.get(column)));
        values.put(box, Integer.parseInt(params.get(box)));
        values.put(direction, Integer.parseInt(params.get(direction)));
        values.put(word, params.get(word));
        values.put(clue, params.get(clue));

        int key = (int)db.insert(daoFactory.getProperty("sql_table_words"), null, values);

        return key;

    }

}
