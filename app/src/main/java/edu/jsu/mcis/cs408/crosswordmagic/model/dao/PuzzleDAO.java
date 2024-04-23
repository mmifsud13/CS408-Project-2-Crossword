package edu.jsu.mcis.cs408.crosswordmagic.model.dao;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.opencsv.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;

import edu.jsu.mcis.cs408.crosswordmagic.R;
import edu.jsu.mcis.cs408.crosswordmagic.model.Puzzle;
import edu.jsu.mcis.cs408.crosswordmagic.model.Word;
import edu.jsu.mcis.cs408.crosswordmagic.model.WordDirection;

public class PuzzleDAO {

    private final DAOFactory daoFactory;

    PuzzleDAO(DAOFactory daoFactory) {
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

        String name = daoFactory.getProperty("sql_field_name");
        String description = daoFactory.getProperty("sql_field_description");
        String height = daoFactory.getProperty("sql_field_height");
        String width = daoFactory.getProperty("sql_field_width");

        ContentValues values = new ContentValues();
        values.put(name, params.get("name"));
        values.put(description, params.get("description"));
        values.put(height, Integer.parseInt(params.get("height")));
        values.put(width, Integer.parseInt(params.get("width")));

        int key = (int) db.insert(daoFactory.getProperty("sql_table_puzzles"), null, values);

        Log.d("PuzzleDAO", "Created puzzle with ID: " + key);

        if (key <= 0) {
            Log.e("PuzzleDAO", "Error creating puzzle");
        }

        return key;
    }



    public Puzzle find(int id) {

        /* use this method if there is NOT already a SQLiteDatabase open */

        SQLiteDatabase db = daoFactory.getWritableDatabase();
        Puzzle result = find(db, id);
        db.close();
        return result;

    }

    @SuppressLint("Range")
    public Puzzle find(SQLiteDatabase db, int id) {

        /* use this method if there is NOT already a SQLiteDatabase open */

        Puzzle puzzle = null;

        String query = daoFactory.getProperty("sql_get_puzzle");
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(id)});

        if (cursor.moveToFirst()) {

            Log.d("PuzzleDAO", "Cursor position before move to first: " + cursor.getPosition());
            boolean moveToFirstResult = cursor.moveToFirst();
            Log.d("PuzzleDAO", "Cursor position after move to first: " + cursor.getPosition());

            cursor.moveToFirst();

            HashMap<String, String> params = new HashMap<>();

            /* get data for new puzzle */

            params.put(daoFactory.getProperty("sql_field_name"), cursor.getString(cursor.getColumnIndex(daoFactory.getProperty("sql_field_name"))));
            params.put(daoFactory.getProperty("sql_field_description"), cursor.getString(cursor.getColumnIndex(daoFactory.getProperty("sql_field_description"))));
            params.put(daoFactory.getProperty("sql_field_height"), String.valueOf(cursor.getInt(cursor.getColumnIndex(daoFactory.getProperty("sql_field_height")))));
            params.put(daoFactory.getProperty("sql_field_width"), String.valueOf(cursor.getInt(cursor.getColumnIndex(daoFactory.getProperty("sql_field_width")))));


            if (!params.isEmpty())
                puzzle = new Puzzle(params);

            /* get list of words (if any) to add to puzzle */


            query = daoFactory.getProperty("sql_get_words");
            Log.d("PuzzleDAO", "Number of rows in cursor for words: " + cursor.getCount());
            cursor = db.rawQuery(query, new String[]{ String.valueOf(id) });

            Log.d("PuzzleDAO", "Number of rows in cursor: " + cursor.getCount());
            Log.d("PuzzleDAO", "Query for fetching words: " + query);

            if (cursor.moveToFirst()) {
                Log.d("PuzzleDAO", "Cursor position before move to first: " + cursor.getPosition());

                do {
                    HashMap<String, String> wordParams = new HashMap<>();

                    /* get data for the next word in the puzzle */

                    wordParams.put("row", String.valueOf(cursor.getInt(cursor.getColumnIndex("row"))));
                    wordParams.put("column", String.valueOf(cursor.getInt(cursor.getColumnIndex("column"))));
                    wordParams.put("box", String.valueOf(cursor.getInt(cursor.getColumnIndex("box"))));
                    wordParams.put("direction", String.valueOf(cursor.getInt(cursor.getColumnIndex("direction"))));
                    wordParams.put("word", cursor.getString(cursor.getColumnIndex("word")));
                    wordParams.put("clue", cursor.getString(cursor.getColumnIndex("clue")));
                    wordParams.put("puzzleid", String.valueOf(cursor.getInt(cursor.getColumnIndex("puzzleid"))));


                    Log.d("PuzzleDAO", "Word Params: " + wordParams.toString());

                    if (!wordParams.isEmpty())
                        puzzle.addWordToPuzzle(new Word(wordParams));

                } while (cursor.moveToNext());

                cursor.close();
            }




            /* get list of already-guessed words (if any) from "guesses" table */

            query = daoFactory.getProperty("sql_get_guesses");
            cursor = db.rawQuery(query, new String[]{String.valueOf(id)});

            if (cursor.moveToFirst()) {
                do {
                    params = new HashMap<>();

                    /* get data for the next word in the puzzle */
                    params.put("word", cursor.getString(cursor.getColumnIndex("word")));
                    params.put("box", String.valueOf(cursor.getInt(cursor.getColumnIndex("box"))));
                    params.put("direction", String.valueOf(cursor.getInt(cursor.getColumnIndex("direction"))));
                    params.put("clue", cursor.getString(cursor.getColumnIndex("clue")));

                    Log.d("PuzzleDAO", "Word data from database: " + params.toString());

                    if (!params.isEmpty())
                        puzzle.addWordToPuzzle(new Word(params));

                } while (cursor.moveToNext());

            }else {
                Log.d("PuzzleDAO", "Cursor is empty");
            }

        }

        return puzzle;

    }

}