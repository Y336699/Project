package com.qian.usercenter.comment;

import com.qian.usercenter.job.exception.BusinessException;

public enum TeamStatusEnum {
    PUBLIC(0,"公开"),
    PRIVATE(1,"私密"),
    SECRECT(2,"加密")
    ;

    private int value;
    private String stringTest;

    public static TeamStatusEnum getTeamStatus(Integer value) {
        if (value ==null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        TeamStatusEnum[] values = TeamStatusEnum.values();
        for (TeamStatusEnum status:values
             ) {
            if (status.getValue() == value) {
                return status;
            }
        }
        return null;
    }
    TeamStatusEnum(int value, String stringTest) {
        this.value = value;
        this.stringTest = stringTest;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getStringTest() {
        return stringTest;
    }

    public void setStringTest(String stringTest) {
        this.stringTest = stringTest;
    }
}
