package com.andywang.p2p.model;

public enum Education {

    PhD(1), Master(2), Bachelor(3), HighSchool(4);

    private int code;

    Education(int code) {
        this.code = code;
    }

    public static Education findByCode(int code) {
        for (Education e : Education.values()) {
            if (e.code == code) return e;
        }
        return null;
    }
}
