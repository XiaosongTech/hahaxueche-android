package com.hahaxueche.model.examLib;

import android.text.TextUtils;

import com.hahaxueche.utils.ExamLib;

import java.io.Serializable;

/**
 * Created by wangshirui on 16/8/13.
 */
public class Question implements Serializable {
    private String id;
    private String question;
    private String answer;
    private String item1;
    private String item2;
    private String item3;
    private String item4;
    private String explains;
    private String url;
    private String userAnswer;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getItem1() {
        return item1;
    }

    public void setItem1(String item1) {
        this.item1 = item1;
    }

    public String getItem2() {
        return item2;
    }

    public void setItem2(String item2) {
        this.item2 = item2;
    }

    public String getItem3() {
        return item3;
    }

    public void setItem3(String item3) {
        this.item3 = item3;
    }

    public String getItem4() {
        return item4;
    }

    public void setItem4(String item4) {
        this.item4 = item4;
    }

    public String getExplains() {
        return explains;
    }

    public void setExplains(String explains) {
        this.explains = explains;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUserAnswer() {
        return userAnswer;
    }

    public void setUserAnswer(String userAnswer) {
        this.userAnswer = userAnswer;
    }

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
        return userAnswer.equals(answer);
    }
}
