package com.jayzhu.easypan.entity.enums;

/**
 * @author jayzhu
 * @version 1.0.0
 * @createDate 2023年10月09日 10:35:09
 * @packageName com.jayzhu.easypan.enums
 * @className PageSize
 * @describe TODO
 */

public enum PageSize {
    SIZE15(15), SIZE20(20), SIZE30(30), SIZE40(40), SIZE50(50);
    int size;

    private PageSize(int size) {
        this.size = size;
    }

    public int getSize() {
        return this.size;
    }
}
