package com.reportsystem.report.service;

import com.reportsystem.report.pdf.BanglaOutputDevice;
import com.reportsystem.report.pdf.BanglaTextRenderer;
import org.openpdf.pdf.ITextFontResolver;
import org.openpdf.pdf.ITextOutputDevice;
import org.openpdf.pdf.ITextRenderer;
import org.openpdf.pdf.ITextReplacedElementFactory;
import org.openpdf.pdf.ITextUserAgent;
import org.openpdf.text.pdf.BaseFont;
import org.openpdf.text.pdf.GlyphLayoutManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;
import java.util.Map;

// Je kono Thymeleaf template ke PDF banay.
// Report er design thake templates folder er HTML e, Java te kono cell banano hoy na.
@Service
public class PdfService {

    @Autowired
    private TemplateEngine templateEngine;

    // FontConfig er bean gula
    @Autowired
    private GlyphLayoutManager glyphLayoutManager;

    @Autowired
    @Qualifier("anekBanglaRegular")
    private BaseFont anekBanglaRegular;

    @Autowired
    @Qualifier("anekBanglaSemiBold")
    private BaseFont anekBanglaSemiBold;

    // templateName: templates folder er html file er naam, jemon "pf-report"
    // data: template e je value gula dekhabe
    public byte[] createPdf(String templateName, Map<String, Object> data) throws Exception {

        // 1. Thymeleaf: template + data = HTML
        Context context = new Context();
        context.setVariables(data);
        String html = templateEngine.process(templateName, context);

        // 2. openpdf-html: HTML = PDF
        //    Bangla er jonno nijeder mapjok (BanglaTextRenderer) ar aankiye (BanglaOutputDevice) diye banai
        float dotsPerPoint = ITextRenderer.DEFAULT_DOTS_PER_POINT;
        int dotsPerPixel = ITextRenderer.DEFAULT_DOTS_PER_PIXEL;
        ITextOutputDevice device = new BanglaOutputDevice(dotsPerPoint, glyphLayoutManager);
        ITextRenderer renderer = new ITextRenderer(dotsPerPoint, dotsPerPixel, device,
                new ITextUserAgent(device, dotsPerPixel), new ITextFontResolver(),
                new ITextReplacedElementFactory(device), new BanglaTextRenderer(glyphLayoutManager));
        renderer.setGlyphLayoutManager(glyphLayoutManager);
        renderer.getFontResolver().addFont(anekBanglaRegular, "fonts/AnekBangla-Regular.ttf", "Anek Bangla");
        renderer.getFontResolver().addFont(anekBanglaSemiBold, "fonts/AnekBangla-SemiBold.ttf", "Anek Bangla");

        // HTML e images/... path gula templates folder theke khujbe
        String baseUrl = new ClassPathResource("templates/").getURL().toString();
        renderer.setDocumentFromString(html, baseUrl);
        renderer.layout();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        renderer.createPDF(out);
        return out.toByteArray();
    }
}