package org.objenesis.website;

import org.codehaus.xsite.XSite;
import org.codehaus.xsite.LinkValidator;
import org.codehaus.xsite.validators.StartsWithLinkValidator;
import org.codehaus.xsite.skins.FreemarkerSkin;
import org.codehaus.xsite.extractors.SiteMeshPageExtractor;
import org.codehaus.xsite.loaders.XStreamSiteMapLoader;

import java.io.File;
import java.io.IOException;

public class BuildWebsite {

    public static void main(String[] args) throws IOException {
        if (args.length != 3) {
            System.err.println("Args: path/to/sitemap.xml path/to/skin.html outputdir");
            System.exit(-1);
        }

        File contentSitemap = checkFile(args[0]);
        File skinFile = checkFile(args[1]);
        File outDir = checkFile(args[2]);

        XSite xSite = new XSite(
                new XStreamSiteMapLoader(new SiteMeshPageExtractor()),
                new FreemarkerSkin(),
                new LinkValidator[] { new StartsWithLinkValidator("http://") });
        xSite.build(contentSitemap, skinFile, outDir);
    }

    private static File checkFile(String name) {
        File file = new File(name);
        if (!file.exists()) {
            throw new IllegalArgumentException("Cannot find file " + name + " (" + file.getAbsolutePath() + ")");
        }
        return file;
    }
}
