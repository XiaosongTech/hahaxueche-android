package com.hahaxueche.model.student;

/**
 * 支付方式
 * Created by Administrator on 2016/5/27.
 */
public class Payment {
    public Payment(int drawableLogo, String name, String remarks, boolean isSelect, boolean isActive, int method) {
        this.drawableLogo = drawableLogo;
        this.name = name;
        this.remarks = remarks;
        this.isSelect = isSelect;
        this.isActive = isActive;
        this.method = method;
    }

    /**
     * 图标
     */
    private int drawableLogo;
    /**
     * 名称
     */
    private String name;
    /**
     * 提示内容
     */
    private String remarks;
    /**
     * 是否选中
     */
    private boolean isSelect;
    /**
     * 是否可用
     */
    private boolean isActive;
    private int method;

    public int getDrawableLogo() {
        return drawableLogo;
    }

    public void setDrawableLogo(int drawableLogo) {
        this.drawableLogo = drawableLogo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public int getMethod() {
        return method;
    }

    public void setMethod(int method) {
        this.method = method;
    }
}
