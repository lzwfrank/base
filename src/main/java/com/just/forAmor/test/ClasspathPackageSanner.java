package com.just.forAmor.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

/**
 * 2016年11月16日
 * @author liu
 *
 */
public class ClasspathPackageSanner {
	
	private String basePackage;
    private ClassLoader cl;
 
    /**
     * Construct an instance and specify the base package it should scan.
     * @param basePackage The base package to scan.
     */
    public ClasspathPackageSanner(String basePackage) {
        this.basePackage = basePackage;
        this.cl = getClass().getClassLoader();
 
    }
 
    /**
     * Construct an instance with base package and class loader.
     * @param basePackage The base package to scan.
     * @param cl Use this class load to locate the package.
     */
    public ClasspathPackageSanner(String basePackage, ClassLoader cl) {
        this.basePackage = basePackage;
        this.cl = cl;
    }
 
    /**
     * Get all fully qualified names located in the specified package
     * and its sub-package.
     *
     * @return A list of fully qualified names.
     * @throws IOException
     */
    public List<String> getFullyQualifiedClassNameList() throws IOException {
//        logger.info("开始扫描包{}下的所有类", basePackage);
 
        return doScan(basePackage, new ArrayList<String>());
    }
 
    /**
     * Actually perform the scanning procedure.
     *
     * @param basePackage
     * @param nameList A list to contain the result.
     * @return A list of fully qualified names.
     *
     * @throws IOException
     */
    private List<String> doScan(String basePackage, List<String> nameList) throws IOException {
        // replace dots with splashes
        String splashPath = StringUtil.dotToSplash(basePackage);
 
        // get file path
        URL url = cl.getResource(splashPath);//用包地址
        String filePath = StringUtil.getRootPath(url);
        //记录类名
        List<String> names = null; 
        if (isJarFile(filePath)) {
            // jar file
//            if (logger.isDebugEnabled()) {
//                logger.debug("{} 是一个JAR包", filePath);
//            }
 
            names = readFromJarFile(filePath, splashPath);
        } else {
            // directory
//            if (logger.isDebugEnabled()) {
//                logger.debug("{} 是一个目录", filePath);
//            }
 
            names = readFromDirectory(filePath);
        }
 
        for (String name : names) {
            if (isClassFile(name)) {
                //nameList.add(basePackage + "." + StringUtil.trimExtension(name));
                nameList.add(toFullyQualifiedName(name, basePackage));
            } else {
                // this is a directory
                // check this directory for more classes
                // do recursive invocation
                doScan(basePackage + "." + name, nameList);
            }
        }
        System.out.println("ok");
     
        return nameList;
    }
 
    /**
     * Convert short class name to fully qualified name.
     * e.g., String -> java.lang.String
     */
    private String toFullyQualifiedName(String shortName, String basePackage) {
        StringBuilder sb = new StringBuilder(basePackage);
        sb.append('.');
        sb.append(StringUtil.trimExtension(shortName));
 
        return sb.toString();
    }
 
    private List<String> readFromJarFile(String jarPath, String splashedPackageName) throws IOException {
//        if (logger.isDebugEnabled()) {
//            logger.debug("从JAR包中读取类: {}", jarPath);
//        }
 
        JarInputStream jarIn = new JarInputStream(new FileInputStream(jarPath));
        JarEntry entry = jarIn.getNextJarEntry();
 
        List<String> nameList = new ArrayList<>();
        while (null != entry) {
            String name = entry.getName();
            if (name.startsWith(splashedPackageName) && isClassFile(name)) {
                nameList.add(name);
            }
 
            entry = jarIn.getNextJarEntry();
        }
 
        return nameList;
    }
 
    private List<String> readFromDirectory(String path) {
        File file = new File(path);
        String[] names = file.list();
 
        if (null == names) {
            return null;
        }
 
        return Arrays.asList(names);
    }
 
    private boolean isClassFile(String name) {
        return name.endsWith(".class");
    }
 
    private boolean isJarFile(String name) {
        return name.endsWith(".jar");
    }
 
//    /**
//     * For test purpose.
//     */
//    public static void main(String[] args) throws Exception {
//    	ClasspathPackageSanner scan = new ClasspathPackageSanner("com.just.forAmor");
//       List<String> classList =  scan.getFullyQualifiedClassNameList();
//       System.out.println(classList);
//    }
// 
}

