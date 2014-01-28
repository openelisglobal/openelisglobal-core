/*
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations under
 * the License.
 *
 * The Original Code is OpenELIS code.
 *
 * Copyright (C) ITECH, University of Washington, Seattle WA.  All Rights Reserved.
 */

package us.mn.state.health.lims.plugin;

import org.apache.commons.io.IOUtils;
import org.dom4j.*;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class PluginLoader {
    private static final String PLUGIN_ANALYZER = "plugin" + File.separator;
    private static final String VERSION = "version";
    private static final String SUPPORTED_VERSION = "1.0";
    private static final String PATH = "path";
    private static final String ANALYZER_IMPORTER = "analyzerImporter";
    private static final String MENU = "menu";
    private static final String EXTENSION_POINT = "extention_point";
    private static final String EXTENSION = "extention";
    private ServletContext context;

    public PluginLoader(ServletContextEvent event) {
        context = event.getServletContext();
    }

    public void load() {
        File pluginDir = new File(context.getRealPath(PLUGIN_ANALYZER));
        File[] files = pluginDir.listFiles();

        if (files != null) {
            for (File pluginFile : files) {
                if (pluginFile.getName().endsWith("jar")) {
                    loadPlugin(pluginFile);
                }
            }
        }
    }

    private void loadPlugin(File pluginFile) {

        try {
            JarFile jar = new JarFile(pluginFile);

            final Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                final JarEntry entry = entries.nextElement();
                if (entry.getName().contains(".xml")) {
                    boolean valid = loadFromXML(jar, entry);
                    if (valid) {
                        break;
                    }
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private boolean loadFromXML(JarFile jar, JarEntry entry) {

        try {
            URL url = new URL("jar:file:///" + jar.getName() + "!/");
            InputStream input = jar.getInputStream(entry);

            String xml = IOUtils.toString(input, "UTF-8");

            //System.out.println(xml);

            Document doc = DocumentHelper.parseText(xml);

            Element versionElement = doc.getRootElement().element(VERSION);

            if (!SUPPORTED_VERSION.equals(versionElement.getData())) {
                System.out.println("Unsupported version number.  Expected " + SUPPORTED_VERSION + " got " + versionElement.getData());
                return false;
            }

            Element analyzerImporter = doc.getRootElement().element(ANALYZER_IMPORTER);

            if (analyzerImporter != null) {
                Attribute path = analyzerImporter.element(EXTENSION_POINT).element(EXTENSION).attribute(PATH);
                loadActualPlugin(url, path.getValue());
            }

            Element menu = doc.getRootElement().element(MENU);

            if (menu != null) {
                Attribute path = menu.element(EXTENSION_POINT).element(EXTENSION).attribute(PATH);
                loadActualPlugin(url, path.getValue());
            }

        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }


        return true;
    }


    @SuppressWarnings("unchecked")
    private void loadActualPlugin(URL url, String classPath) {
        try {
            URL[] urls = {url};
            ClassLoader classLoader = new URLClassLoader(urls, this.getClass().getClassLoader());

            Class<APlugin> aClass = (Class<APlugin>) classLoader.loadClass(classPath);
            APlugin instance = aClass.newInstance();
            instance.connect();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
