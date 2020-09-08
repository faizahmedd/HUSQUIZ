package edu.tacoma.uw.adibk.husquiz.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.io.SerializablePermission;
import java.util.ArrayList;
import java.util.List;

public class Flashcard implements Serializable
{
    private int mFlashcardID;
    private String mQuizID;
    private String mQuestion;
    private String mAnswer;

    private static final String FLASHCARD_ID = "flashcardid";
    private static final String QUIZ_ID = "quizid";
    private static final String QUESTION = "question";
    private static final String ANSWER = "answer";

    public Flashcard(int mFlashcardID, String mQuizID, String mQuestion, String mAnswer)
    {
        this.mFlashcardID = mFlashcardID;
        this.mQuizID = mQuizID;
        this.mQuestion = mQuestion;
        this.mAnswer = mAnswer;
    }

    public static List<Flashcard> parseFlashcardJson(String flashcardJson, String QuizID) throws JSONException
    {
        List<Flashcard> flashcardList = new ArrayList<>();
        if (flashcardJson != null)
        {
            JSONArray arr = new JSONArray(flashcardJson);
            for (int i = 0; i < arr.length(); i++)
            {
                JSONObject obj = arr.getJSONObject(i);
                Flashcard flashcard = new Flashcard(obj.getInt(Flashcard.getFlashcardID()), obj.getString(Flashcard.getQuizID()), obj.getString(Flashcard.getQUESTION()), obj.getString(Flashcard.getANSWER()));
                if (QuizID.equals(flashcard.mQuizID))
                {
                    flashcardList.add(flashcard);
                }
            }
        }
        return flashcardList;
    }

    public static String getFlashcardID()
    {
        return FLASHCARD_ID;
    }

    public static String getQuizID()
    {
        return QUIZ_ID;
    }

    public static String getQUESTION()
    {
        return QUESTION;
    }

    public static String getANSWER()
    {
        return ANSWER;
    }

    public String getmQuizID()
    {
        return mQuizID;
    }

    public void setmQuizID(String mQuizID)
    {
        this.mQuizID = mQuizID;
    }

    public String getmQuestion()
    {
        return mQuestion;
    }

    public void setmQuestion(String mQuestion)
    {
        this.mQuestion = mQuestion;
    }

    public String getmAnswer()
    {
        return mAnswer;
    }

    public void setmAnswer(String mAnswer)
    {
        this.mAnswer = mAnswer;
    }

    public int getmFlashcardID()
    {
        return mFlashcardID;
    }

    public void setmFlashcardID(int mFlashcardID)
    {
        this.mFlashcardID = mFlashcardID;
    }




}
