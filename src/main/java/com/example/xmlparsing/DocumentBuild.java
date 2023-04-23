package com.example.xmlparsing;

import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

public class DocumentBuild{

    // 원본 문서 생성
    public static Document getDocument(String fileName) throws Exception{
        String savePath = System.getProperty("user.dir") + "\\files\\" + fileName; // 기본파일 경로
        File file = new File(savePath); // XML파일이 저장된 경로
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

        return dBuilder.parse(file);
    }
}
