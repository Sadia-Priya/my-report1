package com.reportsystem.report.pdf;

import org.openpdf.pdf.ITextFSFont;
import org.openpdf.pdf.ITextOutputDevice;
import org.openpdf.render.FSFont;
import org.openpdf.render.JustificationInfo;
import org.openpdf.text.pdf.BaseFont;
import org.openpdf.text.pdf.GlyphLayoutManager;

import java.awt.font.GlyphVector;

// PDF e lekha aanke. Justify kora line e renderer protita okkhor alada kore boshay,
// tate Bangla juktakkhor bhenge jay. Tai ekhane shobdo gula ekshathe aanki,
// ar line bhoranor jonno shudhu shobder majher faka ta barai.
public class BanglaOutputDevice extends ITextOutputDevice {

    private final GlyphLayoutManager glyphLayoutManager;
    private FSFont currentFont;

    public BanglaOutputDevice(float dotsPerPoint, GlyphLayoutManager glyphLayoutManager) {
        super(dotsPerPoint);
        this.glyphLayoutManager = glyphLayoutManager;
    }

    @Override
    public void setFont(FSFont font) {
        super.setFont(font);
        currentFont = font;
    }

    @Override
    public void drawString(String text, float x, float y, JustificationInfo info) {
        if (info == null || currentFont == null) {
            super.drawString(text, x, y, info);
            return;
        }
        BaseFont baseFont = ((ITextFSFont) currentFont).getFontDescription().getFont();
        if (!glyphLayoutManager.supportsFont(baseFont)) {
            super.drawString(text, x, y, info);
            return;
        }

        // Renderer kototuku extra jayga chay, ta hisab kori
        int spaces = 0;
        int others = 0;
        for (int i = 0; i < text.length() - 1; i++) {
            if (text.charAt(i) == ' ') {
                spaces++;
            } else {
                others++;
            }
        }
        if (spaces == 0) {
            super.drawString(text, x, y, null);
            return;
        }
        float extraPerSpace = (info.spaceAdjust() * spaces + info.nonSpaceAdjust() * others) / spaces;

        // Ekta ekta shobdo aanki, majhe faka + extra jayga
        float size = currentFont.getSize2D();
        float spaceWidth = width(baseFont, size, " ");
        float cursor = x;
        String[] words = text.split(" ", -1);
        for (int i = 0; i < words.length; i++) {
            if (!words[i].isEmpty()) {
                super.drawString(words[i], cursor, y, null);
                cursor += width(baseFont, size, words[i]);
            }
            if (i < words.length - 1) {
                cursor += spaceWidth + extraPerSpace;
            }
        }
    }

    private float width(BaseFont font, float size, String text) {
        GlyphVector shaped = glyphLayoutManager.computeGlyphVector(font, size, text);
        return (float) shaped.getGlyphPosition(shaped.getNumGlyphs()).getX();
    }
}