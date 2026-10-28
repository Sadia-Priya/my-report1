package com.reportsystem.report.pdf;

import org.openpdf.extend.FontContext;
import org.openpdf.pdf.ITextFSFont;
import org.openpdf.pdf.ITextTextRenderer;
import org.openpdf.render.FSFont;
import org.openpdf.text.pdf.BaseFont;
import org.openpdf.text.pdf.GlyphLayoutManager;

import java.awt.font.GlyphVector;

// HTML renderer lekha koto choura ta mape, tarpor line bhanga, center ar underline thik kore.
// Bangla te juktakkhor howar por lekha chhoto hoye jay, tai age shaping kore tarpor mapi.
public class BanglaTextRenderer extends ITextTextRenderer {

    private final GlyphLayoutManager glyphLayoutManager;

    public BanglaTextRenderer(GlyphLayoutManager glyphLayoutManager) {
        this.glyphLayoutManager = glyphLayoutManager;
    }

    @Override
    public int getWidth(FontContext context, FSFont font, String text) {
        BaseFont baseFont = ((ITextFSFont) font).getFontDescription().getFont();
        if (!glyphLayoutManager.supportsFont(baseFont)) {
            return super.getWidth(context, font, text);
        }
        GlyphVector shaped = glyphLayoutManager.computeGlyphVector(baseFont, font.getSize2D(), text);
        return (int) Math.ceil(shaped.getGlyphPosition(shaped.getNumGlyphs()).getX());
    }
}