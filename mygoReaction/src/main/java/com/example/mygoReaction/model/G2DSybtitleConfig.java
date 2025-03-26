package com.example.mygoReaction.model;

import java.util.Objects;

public final class G2DSybtitleConfig {
    private final int width;
    private final int height;
    private final int offset;
    private final double xScale;
    private final double yScale;

    public G2DSybtitleConfig(int width, int height, int offset, double xScale, double yScale) {
        this.width = width;
        this.height = height;
        this.offset = offset;
        this.xScale = xScale;
        this.yScale = yScale;
    }

    public G2DSybtitleConfig(int width, int height, int offset) {
        this.width = width;
        this.height = height;
        this.offset = offset;
        this.xScale = 1;
        this.yScale = 1;
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public int offset() {
        return offset;
    }

    public double xScale() {
        return xScale;
    }

    public double yScale() {
        return yScale;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (G2DSybtitleConfig) obj;
        return this.width == that.width &&
                this.height == that.height &&
                this.offset == that.offset &&
                Double.doubleToLongBits(this.xScale) == Double.doubleToLongBits(that.xScale) &&
                Double.doubleToLongBits(this.yScale) == Double.doubleToLongBits(that.yScale);
    }

    @Override
    public int hashCode() {
        return Objects.hash(width, height, offset, xScale, yScale);
    }

    @Override
    public String toString() {
        return "G2DSybtitleConfig[" +
                "width=" + width + ", " +
                "height=" + height + ", " +
                "offset=" + offset + ", " +
                "xScale=" + xScale + ", " +
                "yScale=" + yScale + ']';
    }


}
