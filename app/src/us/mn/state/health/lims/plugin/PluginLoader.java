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
    private static final String PLUGIN_ANALYZER = "plugin\\";
    private static final String VERSION = "version";
    private static final String SUPPORTED_VERSION = "1.0";
    private static final String PATH = "path";
    private static final String ANALYZER_IMPORTER = "analyzerImporter";
    private static final String MENU = "menu";
    private static final String PERMISSION = "permission";
    private static final String EXTENSION_POINT = "extension_point";
    private static final String EXTENSION = "extension";
    private static final String DESCRIPTION = "description";
    private static final String VALUE = "value";
    private ServletContext context;

    public PluginLoader(ServletContextEvent event) {
        context = event.getServletContext();
    }

    public void load() {
        File pluginDir = new File(context.getRealPath(PLUGIN_ANALYZER));
        loadDirectory( pluginDir );
    }

    private void loadDirectory( File pluginDir ){
        File[] files = pluginDir.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.getName().endsWith("jar")) {
                    loadPlugin(file);
                }else if(file.isDirectory()){
                    System.out.println("Checking plugin subfolder: " + file.getName());
                    loadDirectory( file );
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
                Attribute description = analyzerImporter.element(EXTENSION_POINT).element(DESCRIPTION).attribute(VALUE);
                System.out.println( "Loading: " + description.getValue());
                Attribute path = analyzerImporter.element(EXTENSION_POINT).element(EXTENSION).attribute(PATH);
                loadActualPlugin(url, path.getValue());
            }

            Element menu = doc.getRootElement().element(MENU);

            if (menu != null) {
                Attribute description = menu.element(EXTENSION_POINT).element(DESCRIPTION).attribute(VALUE);
                System.out.println( "Loading: " + description.getValue());
                Attribute path = menu.element(EXTENSION_POINT).element(EXTENSION).attribute(PATH);
                loadActualPlugin(url, path.getValue());
            }

            Element permissions = doc.getRootElement().element(PERMISSION);

            if (permissions != null) {
                Attribute description = permissions.element(EXTENSION_POINT).element(DESCRIPTION).attribute(VALUE);
                Attribute path = permissions.element(EXTENSION_POINT).element(EXTENSION).attribute(PATH);
                boolean loaded = loadActualPlugin(url, path.getValue());
                if( loaded ){
                    System.out.println( "Loading: " + description.getValue());
                }else{
                    System.out.println( "Failed Loading: " + description.getValue());
                }
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }


        return true;
    }


    @SuppressWarnings("unchecked")
    private boolean loadActualPlugin(URL url, String classPath) {
        try {
            URL[] urls = {url};
            ClassLoader classLoader = new URLClassLoader(urls, this.getClass().getClassLoader());

            Class<APlugin> aClass = (Class<APlugin>) classLoader.loadClass(classPath);
            APlugin instance = aClass.newInstance();
            return instance.connect();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return false;
    }
}
