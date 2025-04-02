package com.example.mygoReaction.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.awt.*;
import java.util.Objects;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class G2DSybtitleConfig {
    private int width;
    private int height;
    private int offset;
    private Color borderColor;
    private Color innerColor;
    private double xScale;
    private double yScale;

    @Override
    public String toString() {
        return "G2DSybtitleConfig[" +
                "width=" + width + ", " +
                "height=" + height + ", " +
                "offset=" + offset + ", " +
                "borderColor=" + borderColor + ", " +
                "innerColor=" + innerColor + ", " +
                "xScale=" + xScale + ", " +
                "yScale=" + yScale + ']';
    }

}
