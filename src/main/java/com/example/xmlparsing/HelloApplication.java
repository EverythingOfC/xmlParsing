package com.example.xmlparsing;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.ws.rs.core.Application;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;


public class HelloApplication extends Application {


    public static void main(String[] args) {

        try{
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

            Document origin = DocumentBuild.getDocument("T_BASEFILE_TB.xml");    // 원본 문서


            NodeList rowList = origin.getElementsByTagName("ROW");// ROWS태그에 있는 ROW들을 가져옴.
            int index = 0;
            for(int i=0;i<rowList.getLength();i++){

                Node row = rowList.item(i);    // 첫 번쨰 ROW 데이터를 얻어옴.

                Element element = (Element)row;

                // 1) T_BASEFILE_TB.xml FILE_ID에 해당하는 F_{FILE_ID}_TB, P_{FILE_ID}_TB 파일 접근
                String fileId = element.getElementsByTagName("FILE_ID").item(0).getTextContent();
                String fFileName = "F_" + fileId + "_TB.xml";
                String pFileName = "P_" + fileId + "_TB.xml";

                Document fDocument = DocumentBuild.getDocument(fFileName);  // f 파일의 데이터를 가져와서 DOM객체로 변환
                Document pDocument = DocumentBuild.getDocument(pFileName);  // p 파일의 데이터를 가져와서 DOM객체로 변환

                NodeList fD = fDocument.getElementsByTagName("ROW");
                NodeList pD = pDocument.getElementsByTagName("ROW");

                Document newDocument = dBuilder.newDocument();     // 새 문서
                Element bestRoot = newDocument.createElement("TABLE");
                Element rows = newDocument.createElement("ROWS");
                newDocument.appendChild(bestRoot);
                bestRoot.appendChild(rows);
                if (fD!=null && pD != null) {

                    for(int j=0;j<fD.getLength();j++){
                        Node node = fD.item(j);
                        Element e = (Element) node;
                        String rate = e.getElementsByTagName("SIMILAR_RATE").item(0).getTextContent();

                        //   2) SIMILAR_RATE / 100 값이 50보다 큰 ROW 검색
                        if(Integer.parseInt(rate)/100 >= 50){
                            String pId = e.getElementsByTagName("P_ID").item(0).getTextContent();   // F_{FILE_ID}_TB.xml에 있는 P_ID

                            for(int k=0;k<pD.getLength();k++){

                                Node node2 = pD.item(k);
                                Element e2 = (Element) node2;
                                String pId2 = e2.getElementsByTagName("P_ID").item(0).getTextContent();

                                //   3) P_{FILE_ID}_TB.xml에서 P_ID가 동일한 ROW 검색
                                if(pId2.equals(pId) && pId2.length()>0 && pId.length()>0){

                                    //   4)  LICENSE_ID에 값이 있으면,  F_{FILE_ID}_TB.xml COMMENT 컨텐츠를 LICENSE_ID로 변경
                                    if(e2.getElementsByTagName("LICENSE_ID").item(0).getTextContent().length()>0){
                                        String pLicenseId = e2.getElementsByTagName("LICENSE_ID").item(0).getTextContent();

                                        //   5) 변경된 내용 XML로 생성

                                        Element root = newDocument.createElement("ROW");
                                        rows.appendChild(root);

                                        Element rowId = newDocument.createElement("ROWID");
                                        rowId.appendChild(newDocument.createTextNode(e.getElementsByTagName("ROWID").item(0).getTextContent()));
                                        root.appendChild(rowId);

                                        Element volume = newDocument.createElement("VOLUME");
                                        volume.appendChild(newDocument.createTextNode(e.getElementsByTagName("VOLUME").item(0).getTextContent()));
                                        root.appendChild(volume);

                                        Element fileName = newDocument.createElement("FILE_NAME");
                                        fileName.appendChild(newDocument.createTextNode(e.getElementsByTagName("FILE_NAME").item(0).getTextContent()));
                                        root.appendChild(fileName);

                                        Element release = newDocument.createElement("RELEASE_NAME");
                                        release.appendChild(newDocument.createTextNode(e.getElementsByTagName("RELEASE_NAME").item(0).getTextContent()));
                                        root.appendChild(release);

                                        Element SIMILAR_RATE = newDocument.createElement("SIMILAR_RATE");
                                        SIMILAR_RATE.appendChild(newDocument.createTextNode(e.getElementsByTagName("SIMILAR_RATE").item(0).getTextContent()));
                                        root.appendChild(SIMILAR_RATE);

                                        Element FILE_PATH = newDocument.createElement("FILE_PATH");
                                        FILE_PATH.appendChild(newDocument.createTextNode(e.getElementsByTagName("FILE_PATH").item(0).getTextContent()));
                                        root.appendChild(FILE_PATH);

                                        Element p_id = newDocument.createElement("P_ID");
                                        p_id.appendChild(newDocument.createTextNode(pId));
                                        root.appendChild(p_id);

                                        Element exclusion = newDocument.createElement("EXCLUSION");
                                        exclusion.appendChild(newDocument.createTextNode(e.getElementsByTagName("EXCLUSION").item(0).getTextContent()));
                                        root.appendChild(exclusion);

                                        if(!pLicenseId.isEmpty()) {
                                            Element license = newDocument.createElement("COMMENT");
                                            license.appendChild(newDocument.createTextNode(pLicenseId));
                                            root.appendChild(license);
                                        }else{
                                            Element comment = newDocument.createElement("COMMENT");
                                            comment.appendChild(newDocument.createTextNode(e.getElementsByTagName("COMMENT").item(0).getTextContent()));
                                            root.appendChild(comment);
                                        }
                                        index++;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    resultCheck(rows.getChildNodes().getLength(),newDocument,fileId);
                }
            }

            System.out.println(index);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public static void resultCheck(int length,Document newDocument,String fileId)throws Exception{

        if (length>0) {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(newDocument);
            StreamResult result = new StreamResult(new File("C:\\tool\\T_"+fileId+"_TB.xml"));
            transformer.transform(source, result);
        }
    }
}