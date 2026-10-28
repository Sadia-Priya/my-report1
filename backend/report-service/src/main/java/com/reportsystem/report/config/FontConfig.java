package com.reportsystem.report.config;

import org.openpdf.text.pdf.BaseFont;
import org.openpdf.text.pdf.GlyphLayoutManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;

// Font gula Spring Bean: app chalu howar shomoy ekbar load hoy,
// tarpor je class e dorkar, sekhane @Autowired kore use kora jay
@Configuration
public class FontConfig {

    // Bangla juktakkhor ar kar thik rakhar manager. Puro app e ekta-i.
    @Bean
    public GlyphLayoutManager glyphLayoutManager() {
        return new GlyphLayoutManager();
    }

    @Bean
    public BaseFont anekBanglaRegular(GlyphLayoutManager glyphLayoutManager) throws Exception {
        try (InputStream file = new ClassPathResource("fonts/AnekBangla-Regular.ttf").getInputStream()) {
            return glyphLayoutManager.loadFont("AnekBangla-Regular.ttf", file, 11).getBaseFont();
        }
    }

    @Bean
    public BaseFont anekBanglaSemiBold(GlyphLayoutManager glyphLayoutManager) throws Exception {
        try (InputStream file = new ClassPathResource("fonts/AnekBangla-SemiBold.ttf").getInputStream()) {
            return glyphLayoutManager.loadFont("AnekBangla-SemiBold.ttf", file, 11).getBaseFont();
        }
    }
}