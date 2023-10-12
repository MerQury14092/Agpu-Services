package com.merqury.agpu;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class Resources {

    public static String getSiteMainPage(){
        return tryToReadSiteMainPage();
    }

    private static String tryToReadSiteMainPage() {
        try {
            return readSiteMainPage();
        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    private static String readSiteMainPage() throws IOException{
        InputStream siteMainPageInputStream = getSiteMainPageInputStream();
        return readFromInputStream(siteMainPageInputStream);
    }

    private static InputStream getSiteMainPageInputStream(){
        return Resources.class.getResourceAsStream("/it-institut-main-page.html.txt");
    }

    private static String readFromInputStream(InputStream source) throws IOException {
        return new String(source.readAllBytes(), StandardCharsets.UTF_8);
    }
}
