package com.hahaxueche.model.examLib;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.hahaxueche.util.ExamLib;

import java.util.ArrayList;

/**
 * Created by wangshirui on 2016/10/18.
 */

public class Question implements Parcelable {
    public String id;
    public String question;
    public String answer;
    public String item1;
    public String item2;
    public String item3;
    public String item4;
    public String explains;
    public String url;
    public ArrayList<String> userAnswer;
    public boolean isSubmit;

    public String getQuestionType() {
        if (Integer.parseInt(answer) > 4) {
            return ExamLib.QUESTION_TYPE_MULTI_CHOICE;
        }
        if (TextUtils.isEmpty(item3) && TextUtils.isEmpty(item4)) {
            return ExamLib.QUESTION_TYPE_TRUE_FALSE;
        }
        return ExamLib.QUESTION_TYPE_SINGLE_CHOICE;
    }

    /**
     * @return
     * @explain "1": "A或者正确",
     * "2": "B或者错误",
     * "3": "C",
     * "4": "D",
     * "7": "AB",
     * "8": "AC",
     * "9": "AD",
     * "10": "BC",
     * "11": "BD",
     * "12": "CD",
     * "13": "ABC",
     * "14": "ABD",
     * "15": "ACD",
     * "16": "BCD",
     * "17": "ABCD"
     */
    public boolean isCorrect() {
        if (userAnswer != null && userAnswer.size() > 0) {
            if (answer.equals("1")) {
                return userAnswer.contains("1") && !userAnswer.contains("2") && !userAnswer.contains("3") && !userAnswer.contains("4");
            } else if (answer.equals("2")) {
                return !userAnswer.contains("1") && userAnswer.contains("2") && !userAnswer.contains("3") && !userAnswer.contains("4");
            } else if (answer.equals("3")) {
                return !userAnswer.contains("1") && !userAnswer.contains("2") && userAnswer.contains("3") && !userAnswer.contains("4");
            } else if (answer.equals("4")) {
                return !userAnswer.contains("1") && !userAnswer.contains("2") && !userAnswer.contains("3") && userAnswer.contains("4");
            } else if (answer.equals("7")) {
                return userAnswer.contains("1") && userAnswer.contains("2") && !userAnswer.contains("3") && !userAnswer.contains("4");
            } else if (answer.equals("8")) {
                return userAnswer.contains("1") && !userAnswer.contains("2") && userAnswer.contains("3") && !userAnswer.contains("4");
            } else if (answer.equals("9")) {
                return userAnswer.contains("1") && !userAnswer.contains("2") && !userAnswer.contains("3") && userAnswer.contains("4");
            } else if (answer.equals("10")) {
                return !userAnswer.contains("1") && userAnswer.contains("2") && userAnswer.contains("3") && !userAnswer.contains("4");
            } else if (answer.equals("11")) {
                return !userAnswer.contains("1") && userAnswer.contains("2") && !userAnswer.contains("3") && userAnswer.contains("4");
            } else if (answer.equals("12")) {
                return !userAnswer.contains("1") && !userAnswer.contains("2") && userAnswer.contains("3") && userAnswer.contains("4");
            } else if (answer.equals("13")) {
                return userAnswer.contains("1") && userAnswer.contains("2") && userAnswer.contains("3") && !userAnswer.contains("4");
            } else if (answer.equals("14")) {
                return userAnswer.contains("1") && userAnswer.contains("2") && !userAnswer.contains("3") && userAnswer.contains("4");
            } else if (answer.equals("15")) {
                return userAnswer.contains("1") && !userAnswer.contains("2") && userAnswer.contains("3") && userAnswer.contains("4");
            } else if (answer.equals("16")) {
                return !userAnswer.contains("1") && userAnswer.contains("2") && userAnswer.contains("3") && userAnswer.contains("4");
            } else if (answer.equals("17")) {
                return userAnswer.contains("1") && userAnswer.contains("2") && userAnswer.contains("3") && userAnswer.contains("4");
            }
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

    protected Question(Parcel in) {
        id = in.readString();
        question = in.readString();
        answer = in.readString();
        item1 = in.readString();
        item2 = in.readString();
        item3 = in.readString();
        item4 = in.readString();
        explains = in.readString();
        url = in.readString();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(question);
        dest.writeString(answer);
        dest.writeString(item1);
        dest.writeString(item2);
        dest.writeString(item3);
        dest.writeString(item4);
        dest.writeString(explains);
        dest.writeString(url);
        dest.writeStringList(userAnswer);
        dest.writeByte((byte) (isSubmit ? 1 : 0));
    }
}
