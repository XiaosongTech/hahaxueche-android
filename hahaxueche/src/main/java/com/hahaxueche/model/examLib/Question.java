package com.hahaxueche.model.examLib;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.hahaxueche.util.ExamLib;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by wangshirui on 2016/10/18.
 */

public class Question implements Parcelable {
    public String questionid;
    public String question;
    public String answer;
    public ArrayList<String> answers;
    public String explain;
    public String mediatype;//0：没有，1：图片，2：mp4
    public String mediacontent;
    public String optiontype;//0：判断，1：单选，2：多选
    public String chapterid;
    public ArrayList<String> answer_arr;
    public String chapterDesc;
    public ArrayList<String> userAnswer;
    public boolean isSubmit;

    protected Question(Parcel in) {
        questionid = in.readString();
        question = in.readString();
        answer = in.readString();
        answers = in.createStringArrayList();
        explain = in.readString();
        mediatype = in.readString();
        mediacontent = in.readString();
        optiontype = in.readString();
        chapterid = in.readString();
        answer_arr = in.createStringArrayList();
        chapterDesc = in.readString();
        userAnswer = in.createStringArrayList();
        isSubmit = in.readByte() != 0;
    }

    public static final Creator<Question> CREATOR = new Creator<Question>() {
        @Override
        public Question createFromParcel(Parcel in) {
            return new Question(in);
        }

        @Override
        public Question[] newArray(int size) {
            return new Question[size];
        }
    };

    public String getQuestionType() {
        if (optiontype.equals("0")) {
            return ExamLib.QUESTION_TYPE_TRUE_FALSE;
        } else if (optiontype.equals("2")) {
            return ExamLib.QUESTION_TYPE_MULTI_CHOICE;
        } else {
            return ExamLib.QUESTION_TYPE_SINGLE_CHOICE;
        }
    }

    public boolean isCorrect() {
        if (userAnswer != null && userAnswer.size() > 0 && answer_arr != null && answer_arr.size() > 0) {
            if (answer_arr.size() != userAnswer.size()) return false;
            Collections.sort(answer_arr);
            Collections.sort(userAnswer);
            for (int i = 0; i < userAnswer.size(); i++) {
                if (!userAnswer.get(i).equals(answer_arr.get(i)))
                    return false;
            }
            return true;
        }
        return false;

    }

    public boolean hasAnswers() {
        if (getQuestionType().equals(ExamLib.QUESTION_TYPE_MULTI_CHOICE)) {
            return isSubmit;
        } else {
            return userAnswer != null && userAnswer.size() > 0;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(questionid);
        dest.writeString(question);
        dest.writeString(answer);
        dest.writeStringList(answers);
        dest.writeString(explain);
        dest.writeString(mediatype);
        dest.writeString(mediacontent);
        dest.writeString(optiontype);
        dest.writeString(chapterid);
        dest.writeStringList(answer_arr);
        dest.writeString(chapterDesc);
        dest.writeStringList(userAnswer);
        dest.writeByte((byte) (isSubmit ? 1 : 0));
    }
}
