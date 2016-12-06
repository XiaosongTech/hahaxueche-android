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
    public String question_id;
    public String question;
    public String answer;
    public ArrayList<String> answers;
    public String explain;
    public String media_type;//0：没有，1：图片，2：mp4
    public String media_content;
    public String option_type;//0：判断，1：单选，2：多选
    public String chapter_id;
    public ArrayList<String> answer_arr;
    public String chapter_desc;
    public ArrayList<String> userAnswer;
    public boolean isSubmit;


    protected Question(Parcel in) {
        question_id = in.readString();
        question = in.readString();
        answer = in.readString();
        answers = in.createStringArrayList();
        explain = in.readString();
        media_type = in.readString();
        media_content = in.readString();
        option_type = in.readString();
        chapter_id = in.readString();
        answer_arr = in.createStringArrayList();
        chapter_desc = in.readString();
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
        try {
            if (option_type.equals("0")) {
                return ExamLib.QUESTION_TYPE_TRUE_FALSE;
            } else if (option_type.equals("2")) {
                return ExamLib.QUESTION_TYPE_MULTI_CHOICE;
            } else {
                return ExamLib.QUESTION_TYPE_SINGLE_CHOICE;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
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
        dest.writeString(question_id);
        dest.writeString(question);
        dest.writeString(answer);
        dest.writeStringList(answers);
        dest.writeString(explain);
        dest.writeString(media_type);
        dest.writeString(media_content);
        dest.writeString(option_type);
        dest.writeString(chapter_id);
        dest.writeStringList(answer_arr);
        dest.writeString(chapter_desc);
        dest.writeStringList(userAnswer);
        dest.writeByte((byte) (isSubmit ? 1 : 0));
    }
}
