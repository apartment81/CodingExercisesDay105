package com.example.mirodone.choicequiz;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.xml.datatype.Duration;

public class QuizActivity extends AppCompatActivity {

    public static final String SENT_SCORE = "sentScore";

    public static final long COUNTDOWN_IN_MILLIS = 30000;

    public static final String KEY_SCORE ="keyScore";
    public static final String KEY_QUESTION_COUNT ="keyCount";
    public static final String KEY_MILLIS_LEFT ="keyTimer";
    public static final String KEY_ANSWERED ="keyAns";
    public static final String KEY_QUESTION_LIST ="keyQL";


    private TextView tvQuestion;
    private TextView tvScore;
    private TextView tvQuestionCount;
    private TextView tvTimer;
    private RadioGroup mRadioGroup;
    private RadioButton rb1;
    private RadioButton rb2;
    private RadioButton rb3;
    private Button buttonSubmit;

    private ArrayList<Questions> questionList;

    private ColorStateList textColorDefaultRb;
    private ColorStateList textColorDefaultCountdown;

    private CountDownTimer mCountDownTimer;
    private long timeLeftInMillis;

    private int questionCounter;
    private int questionCountTotal;
    private Questions currentQuestion;

    private int score;
    private boolean answered;

    private long backPressedTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        tvQuestion = findViewById(R.id.tv_question);
        tvScore = findViewById(R.id.tv_score);
        tvQuestionCount = findViewById(R.id.tv_quest_count);
        tvTimer = findViewById(R.id.tv_timer);
        mRadioGroup = findViewById(R.id.radio_group);
        rb1 = findViewById(R.id.radio_bt1);
        rb2 = findViewById(R.id.radio_bt2);
        rb3 = findViewById(R.id.radio_bt3);
        buttonSubmit = findViewById(R.id.bt_submit);

        textColorDefaultRb = rb1.getTextColors();
        textColorDefaultCountdown = tvTimer.getTextColors();


        //execute only if there is no savedInstance , first start
        if(savedInstanceState == null) {
            QuizDbHelper dbHelper = new QuizDbHelper(this);
            questionList = dbHelper.getAllQuestions();
            questionCountTotal = questionList.size();
            Log.v("QuizActivity", "quizcounter-list> " +questionCountTotal );
            Collections.shuffle(questionList);

            showNextQuestion();
        }else {
            questionList = savedInstanceState.getParcelableArrayList(KEY_QUESTION_LIST);
            questionCountTotal = questionList.size();
            Log.v("QuizActivity", "quizcounter-list: " +questionCountTotal );
            questionCounter = savedInstanceState.getInt(KEY_QUESTION_COUNT);
            //questionCounter starts at 1 and the questionCounter index starts at 0
            currentQuestion  = questionList.get(questionCounter - 1);
            score = savedInstanceState.getInt(KEY_SCORE);
            timeLeftInMillis = savedInstanceState.getLong(KEY_MILLIS_LEFT);
            answered = savedInstanceState.getBoolean(KEY_ANSWERED);

            if(!answered) {
                startCountDown();
            }else {
                updateTimerText();
                showSolution();
            }

        }

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!answered) {
                    if (rb1.isChecked() || rb2.isChecked() || rb3.isChecked()) {
                        checkAnswer();
                    } else {
                        Toast.makeText(QuizActivity.this, "Please select an answer !", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    showNextQuestion();
                }
            }
        });

    }

    private void showNextQuestion() {

        rb1.setTextColor(textColorDefaultRb);
        rb2.setTextColor(textColorDefaultRb);
        rb3.setTextColor(textColorDefaultRb);
        mRadioGroup.clearCheck();

        if (questionCounter < questionCountTotal) {
            currentQuestion = questionList.get(questionCounter);
            tvQuestion.setText(currentQuestion.getQuestion());
            rb1.setText(currentQuestion.getOption1());
            rb2.setText(currentQuestion.getOption2());
            rb3.setText(currentQuestion.getOption3());

            questionCounter++;

            tvQuestionCount.setText("Question: " + questionCounter + "/" + questionCountTotal);
            answered = false;
            buttonSubmit.setText("Confirm");

            timeLeftInMillis = COUNTDOWN_IN_MILLIS;
            startCountDown();

        } else {
            finishQuiz();
        }
    }

    private void startCountDown() {
        mCountDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateTimerText();
            }

            @Override
            public void onFinish() {
                timeLeftInMillis = 0;
                updateTimerText();
                checkAnswer();
            }
        }.start();
    }


    private void updateTimerText() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;

        String formattedTime = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);

        tvTimer.setText(formattedTime);

        if (timeLeftInMillis < 10000) {
            tvTimer.setTextColor(Color.RED);
        } else
            tvTimer.setTextColor(textColorDefaultCountdown);

    }

    private void checkAnswer() {
        answered = true;

        //stop counter is
        mCountDownTimer.cancel();
        RadioButton rbSelected = findViewById(mRadioGroup.getCheckedRadioButtonId());
        int answerNr = mRadioGroup.indexOfChild(rbSelected) + 1;

        if (answerNr == currentQuestion.getAnswerNo()) {
            score++;
            tvScore.setText("Score: " + score);

        }
        showSolution();
    }

    private void showSolution() {
        rb1.setTextColor(Color.RED);
        rb2.setTextColor(Color.RED);
        rb3.setTextColor(Color.RED);

        switch ((currentQuestion.getAnswerNo())) {
            case 1:
                rb1.setTextColor(Color.GREEN);
                tvQuestion.setText("Answer 1 is correct.");
                break;
            case 2:
                rb2.setTextColor(Color.GREEN);
                tvQuestion.setText("Answer 2 is correct.");
                break;
            case 3:
                rb3.setTextColor(Color.GREEN);
                tvQuestion.setText("Answer 3 is correct.");
                break;
        }

        if (questionCounter < questionCountTotal) {
            buttonSubmit.setText("Next");
          //  mCountDownTimer.cancel();
        } else {
            buttonSubmit.setText("Finish");
          //  mCountDownTimer.cancel();
        }
    }

    private void finishQuiz() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(SENT_SCORE, score);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    @Override
    public void onBackPressed() {

        // if the back button is not pressed twice in 2 seconds, activity will remain active.
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            finishQuiz();
        } else {
            Toast.makeText(this, "Press back again to finish", Toast.LENGTH_SHORT).show();
        }

        backPressedTime = System.currentTimeMillis();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//check if timer started, if timer is 0 the app will crash
        if(mCountDownTimer !=null){
            mCountDownTimer.cancel();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState ) {
        super.onSaveInstanceState(outState);

        outState.putInt(KEY_SCORE, score);
        outState.putInt(KEY_QUESTION_COUNT, questionCounter);
        outState.putLong(KEY_MILLIS_LEFT, timeLeftInMillis);
        outState.putBoolean(KEY_ANSWERED, answered);
        outState.putParcelableArrayList(KEY_QUESTION_LIST,  questionList);


    }
}
