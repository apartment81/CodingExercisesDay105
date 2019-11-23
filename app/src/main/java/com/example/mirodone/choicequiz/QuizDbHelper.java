package com.example.mirodone.choicequiz;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class QuizDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "quiz.db";
    public static final int DATABASE_VERSION = 1;

    private SQLiteDatabase db;

    public QuizDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        this.db = db;

        final String SQL_CREATE_QUESTIONS_TABLE = "CREATE TABLE " +
                QuizContract.QuestionsTable.TABLE_NAME + " ( " +
                QuizContract.QuestionsTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                QuizContract.QuestionsTable.COLUMN_QUESTION + " TEXT, " +
                QuizContract.QuestionsTable.COLUMN_OPTION1 + " TEXT, " +
                QuizContract.QuestionsTable.COLUMN_OPTION2 + " TEXT, " +
                QuizContract.QuestionsTable.COLUMN_OPTION3 + " TEXT, " +
                QuizContract.QuestionsTable.COLUMN_ANSWER_NO + " INTEGER" + ")";
        db.execSQL(SQL_CREATE_QUESTIONS_TABLE);
        fillQuestionsTable();

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + QuizContract.QuestionsTable.TABLE_NAME);
        onCreate(db);
    }

    private void fillQuestionsTable() {
        Questions q1 = new Questions("A is correct", "A", "B", "C", 1);
        addQuestion(q1);
        Questions q2 = new Questions("B is correct", "A", "B", "C", 2);
        addQuestion(q2);
        Questions q3 = new Questions("C is correct", "A", "B", "C", 3);
        addQuestion(q3);
        Questions q4 = new Questions("A is correct", "A", "B", "C", 1);
        addQuestion(q4);
        Questions q5 = new Questions("B is correct", "A", "B", "C", 2);
        addQuestion(q5);
    }

    private  void addQuestion (Questions questions) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(QuizContract.QuestionsTable.COLUMN_QUESTION, questions.getQuestion());
        contentValues.put(QuizContract.QuestionsTable.COLUMN_OPTION1, questions.getOption1());
        contentValues.put(QuizContract.QuestionsTable.COLUMN_OPTION2, questions.getOption2());
        contentValues.put(QuizContract.QuestionsTable.COLUMN_OPTION3, questions.getOption3());
        contentValues.put(QuizContract.QuestionsTable.COLUMN_ANSWER_NO, questions.getAnswerNo());
        db.insert(QuizContract.QuestionsTable.TABLE_NAME, null, contentValues);

    }

    public ArrayList<Questions> getAllQuestions () {
        ArrayList <Questions> questionsList = new ArrayList<>();
        db =  getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + QuizContract.QuestionsTable.TABLE_NAME, null);

        if (cursor.moveToFirst()){
            do{
                Questions question = new Questions();
                question.setQuestion(cursor.getString(cursor.getColumnIndex(QuizContract.QuestionsTable.COLUMN_QUESTION)));
                question.setOption1(cursor.getString(cursor.getColumnIndex(QuizContract.QuestionsTable.COLUMN_OPTION1)));
                question.setOption2(cursor.getString(cursor.getColumnIndex(QuizContract.QuestionsTable.COLUMN_OPTION2)));
                question.setOption3(cursor.getString(cursor.getColumnIndex(QuizContract.QuestionsTable.COLUMN_OPTION3)));
                question.setAnswerNo(cursor.getInt(cursor.getColumnIndex(QuizContract.QuestionsTable.COLUMN_ANSWER_NO)));
                questionsList.add(question);

            }while (cursor.moveToNext());
        }

        cursor.close();
        return questionsList;
    }

}
