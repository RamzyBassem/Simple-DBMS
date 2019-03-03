package eg.edu.alexu.cse.oop.db;

import org.apache.commons.lang3.math.NumberUtils;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.stream.*;
import javax.xml.stream.events.*;
import javax.xml.transform.stax.StAXSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Mohamed Abdelrehim on 4/15/2017.
 */


/*deals w/ xml  and xsd files dependencies -> org.apache.commons.lang3.math.NumberUtils*/

public class Parser {

    //writes a xml file for a given table DONE
    public void writeXML(String tableName, String path) throws XMLStreamException, IOException {

        StringWriter stringWriter = new StringWriter();
        XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();

        //write xml
        XMLStreamWriter xmlStreamWriter = xmlOutputFactory.createXMLStreamWriter(stringWriter);
        stringWriter.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
        stringWriter.write("\n");
        xmlStreamWriter.writeStartElement(tableName);
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeEndDocument();
        //stringWriter.write('x');
        xmlStreamWriter.flush();
        xmlStreamWriter.close();
        String xmlString = stringWriter.getBuffer().toString();

        stringWriter.close();

        //write xmlString into a xml file
        File xmlFile = new File(path + tableName + ".xml");
        FileWriter fileWriter = new FileWriter(xmlFile);
        fileWriter.write(xmlString);
        fileWriter.flush();
        fileWriter.close();

    }

    //writes schema file (.xsd) for a given table DONE
    public void writeXSD(List<String> tableElements, List<String> dataTypes, String tableName, String path) throws IOException {

        File xsdFile = new File(path + tableName + ".xsd");
        FileWriter fileWriter = new FileWriter(xsdFile);

        //start  xsd
        fileWriter.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
        fileWriter.write("<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\">\n");

        //start element table
        fileWriter.write("<xs:element name=\"" + tableName + "\">\n");
        fileWriter.write("<xs:complexType>\n");
        fileWriter.write("<xs:sequence>\n");

        //start element row
        fileWriter.write("<xs:element name=\"row\" maxOccurs=\"unbounded\" minOccurs=\"0\">\n");
        fileWriter.write("<xs:complexType>\n");
        fileWriter.write("<xs:sequence>\n");
        int size = tableElements.size();

        for(int i = 0; i < size; i++) {
            fileWriter.write("<xs:element name=\"" + tableElements.get(i) + "\" type=\""
                    +"xs:"+ dataTypes.get(i) + "\"");

            if (dataTypes.get(i).equalsIgnoreCase("string")){
                fileWriter.write(" default = \"NULL\"" + "/>\n");
            }
            else {
                fileWriter.write(" default = \"0\"" + "/>\n");
            }

        }

        fileWriter.write("</xs:sequence>\n");
        fileWriter.write("</xs:complexType>\n");
        fileWriter.write("</xs:element>\n");
        //end element row

        fileWriter.write("</xs:sequence>\n");
        fileWriter.write("</xs:complexType>\n");
        fileWriter.write("</xs:element>\n");
        //end element table

        fileWriter.write("</xs:schema>");
        //end xsd

        fileWriter.flush();
        fileWriter.close();
    }

    //deletes all files related to a table DONE
    public void deleteFiles(String tableName, String path) {

        File xmlFile = new File(path + tableName + ".xml");
        File xsdFile = new File(path + tableName + ".xsd");
        xmlFile.delete();
        xsdFile.delete();

    }

    //counts the rows in a xml file
    public int countRows(String tableName, String path) throws FileNotFoundException, XMLStreamException {

        int count = 0;

        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLEventReader eventReader = factory.createXMLEventReader(new FileReader(path+tableName + ".xml"));
        factory.createXMLEventReader(new FileReader(path + tableName + ".xml"));

        while (eventReader.hasNext()) {

            XMLEvent event = eventReader.nextEvent();
            switch(event.getEventType()) {

                case XMLStreamConstants.START_ELEMENT:

                    StartElement startElement = event.asStartElement();
                    String qName = startElement.getName().getLocalPart();
                    if(qName.equalsIgnoreCase("row")) {  // if start of row
                        count ++;
                    }

            }

        }

        return count;

    }

    //adds a valid node with given children to a given xml file TODO complete validation
    public boolean addRow(List<String> columns, List<String> values, String tableName, String path) throws IOException,
            XMLStreamException, SAXException {

        String input = this.inputString(columns, values, tableName, path);
        File validationTemp = new File(path + "validationTemp.xml");
        FileWriter fw = new FileWriter(path + "validationTemp.xml");
        fw.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
        fw.write("<"+ tableName + ">\n");
        fw.write(input + "\n");
        fw.write("</"+ tableName + ">\n");
        fw.close();
        boolean isValid = this.isValid("validationTemp.xml", tableName + ".xsd", path);
        if (input == null) {
            System.out.println("error invalid input");
            return false;
        }
        else if (isValid) {
            validationTemp = null;
            System.gc();
            Files.delete(Paths.get(path+ "validationTemp" + ".xml"));
            this.delLastLine(tableName, path);
            Files.write(Paths.get(path+tableName + ".xml"), (input + "\n</"+tableName+">").getBytes(), StandardOpenOption.APPEND);
            return true;
        }
        else if (!isValid) {
            System.out.println("schema error\n");
            return false;
        }
        else {
            System.out.println("another error occurred\n");
            return false;
        }
    }

    //converts the user input into a  xml block -> add to xml is responsible to call validate DONE
    private String inputString(List<String> columns, List<String> values, String tableName, String path)  throws IOException,
            XMLStreamException {

        List<String> originalColumns = this.getColumns(tableName, path);
        List<String> originalTypes = this.getTypes(tableName, path);
        List<String> missingColumns = new ArrayList<>();
        StringWriter stringWriter = new StringWriter();
        XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();
        boolean missing = true;
        boolean missingFromOriginal = true;

        //find missing columns by looking for each element in original columns in user input
        for (int i = 0; i < originalColumns.size(); i++) {
            for (int j = 0; j < columns.size(); j++) {
                if (originalColumns.get(i).equals(columns.get(j))) {
                    missing = false;
                }
            }
            if(missing) {
                missingColumns.add(originalColumns.get(i));
            }
            missing = true; //restart
        }

        // law el user estazraf w 7at 7aga zyada
        for (int i = 0; i < columns.size(); i++) {
            for (int j = 0; j < originalColumns.size(); j++) {
                if (originalColumns.get(j).equals(columns.get(i))) {
                    missingFromOriginal = false;
                }
            }
            if(missingFromOriginal) {
                return null;
            }
            missingFromOriginal = true; //restart
        }


        //write new row

        XMLStreamWriter xmlStreamWriter = xmlOutputFactory.createXMLStreamWriter(stringWriter);
        xmlStreamWriter.writeCharacters("\n");
        xmlStreamWriter.writeCharacters("\t");
        xmlStreamWriter.writeStartElement("row");
        xmlStreamWriter.writeCharacters("\n");
        boolean foundInColumns = false;

        // each element in original columns is either in columns or in missing columns
        for (int i = 0; i < originalColumns.size(); i++) {

            for (int j = 0; j < columns.size(); j++) {

                if (originalColumns.get(i).equals(columns.get(j))) {

                    xmlStreamWriter.writeCharacters("\t");
                    xmlStreamWriter.writeCharacters("\t");
                    xmlStreamWriter.writeStartElement(originalColumns.get(i));
                    xmlStreamWriter.writeCharacters(values.get(j));
                    xmlStreamWriter.writeEndElement();
                    xmlStreamWriter.writeCharacters("\n");
                    foundInColumns = true;

                }

            }
            if (!foundInColumns) {
                // go look in missing columns
                for (int j = 0; j < missingColumns.size(); j++) {

                    if (originalColumns.get(i).equals(missingColumns.get(j))) {

                        xmlStreamWriter.writeCharacters("\t");
                        xmlStreamWriter.writeCharacters("\t");
                        xmlStreamWriter.writeStartElement(originalColumns.get(i));

                        //write default value for either integer or string
                        if (originalTypes.get(i).equals("xs:integer")) {
                            xmlStreamWriter.writeCharacters("0");
                        }
                        else if (originalTypes.get(i).equals("xs:string")) {
                            xmlStreamWriter.writeCharacters("NULL");
                        }

                        xmlStreamWriter.writeEndElement();
                        xmlStreamWriter.writeCharacters("\n");

                    }

                }

            }

            foundInColumns = false; //restart

        }


        xmlStreamWriter.writeCharacters("\t");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeEndDocument();

        xmlStreamWriter.flush();
        xmlStreamWriter.close();

        String xmlString = stringWriter.getBuffer().toString();
        stringWriter.close();
        System.out.println(xmlString);

        return xmlString;

    }

    //get dataTypes in order DONE
    private List<String> getTypes(String tableName, String path) throws XMLStreamException, FileNotFoundException {

        int counter = 0;
        List<String> dataTypes = new ArrayList<>();

        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLEventReader eventReader = factory.createXMLEventReader(new FileReader(path + tableName + ".xsd"));

        while (eventReader.hasNext()) {

            XMLEvent event = eventReader.nextEvent();
            switch (event.getEventType()) {

                case XMLStreamConstants.START_ELEMENT:
                    StartElement startElement = event.asStartElement();
                    String qName = startElement.getName().getLocalPart();

                    if (qName.equalsIgnoreCase("sequence")) {
                        counter++;
                    }
                    //only need second occurrence  of "sequence"
                    if(counter == 2) {
                        if (qName.equalsIgnoreCase("element")) {
                            Iterator<Attribute> attributes = startElement.getAttributes();
                            attributes.next();
                            attributes.next();
                            String x =attributes.next().getValue();
                            dataTypes.add(x);

                            //System.out.println(x);
                        }
                    }
                    break;
            }

        }
        return dataTypes;
    }

    /*returns a string ArrayList (retrieved from the xsd file) of the columns that should exist in a table DONE */
    public List<String> getColumns(String tableName, String path) throws XMLStreamException, FileNotFoundException {

        int counter = 0;
        List<String>columns = new ArrayList<>();

        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLEventReader eventReader = factory.createXMLEventReader(new FileReader(path+tableName + ".xsd"));

        while (eventReader.hasNext()) {

            XMLEvent event = eventReader.nextEvent();
            switch (event.getEventType()) {

                case XMLStreamConstants.START_ELEMENT:
                    StartElement startElement = event.asStartElement();
                    String qName = startElement.getName().getLocalPart();

                    if (qName.equalsIgnoreCase("sequence")) {
                        counter++;
                    }
                    //only need second occurrence  of "sequence"
                    if(counter == 2) {
                        if (qName.equalsIgnoreCase("element")) {
                            Iterator<Attribute> attributes = startElement.getAttributes();
                            attributes.next();
                            String x = attributes.next().getValue();
                            columns.add(x);
                            //System.out.println(x);
                        }
                    }
                    break;
            }

        }
        return columns;
    }

    //deletes the last line of an xml file DONE
    private void delLastLine (String tableName, String path) throws IOException {

        File file = new File(path+tableName + ".xml");
        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
        byte b;
        long length = randomAccessFile.length() ;
        if (length != 0) {
            do {
                length -= 1;
                randomAccessFile.seek(length);
                b = randomAccessFile.readByte();
            } while (b != 10 && length > 0);
            randomAccessFile.setLength(length);
            randomAccessFile.close();
        }

    }

    //get the markers between which you want to delete or select DONE
    private List<Integer> getMarkers (String conditionLHS, String operator, String conditionRHS, String tableName, String path)
            throws FileNotFoundException, XMLStreamException {


        int counterOne = 0; //for first while
        int beginMarker = -1; //to put in markers array - marks when to begin deletion
        int endMarker = -1; //to put in markers array - marks when to stop deletion
        int LHSmarker = -1; //to mark when the conditionLHS appeared

        List<Integer> markers = new ArrayList<>(); //each value in an even index is begin, in an odd index is end
        int markersIndex = 0;
        boolean equalsLHS = false;
        boolean conditionHolds = false;

        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLEventReader eventReader = factory.createXMLEventReader(new FileReader(path+tableName + ".xml"));
        factory.createXMLEventReader(new FileReader(path+tableName + ".xml"));

        while (eventReader.hasNext()) {
            XMLEvent event = eventReader.nextEvent();
            switch(event.getEventType()) {

                case XMLStreamConstants.START_ELEMENT:
                    StartElement startElement = event.asStartElement();
                    String qName = startElement.getName().getLocalPart();
                    if(qName.equalsIgnoreCase("row")) {  // if start of row
                        beginMarker = counterOne;
                    }
                    else if(qName.equalsIgnoreCase(conditionLHS)) {  // if column = given column
                        equalsLHS = true;
                        LHSmarker = counterOne;
                    }

                    break;

                case  XMLStreamConstants.END_ELEMENT:
                    EndElement endElement = event.asEndElement();
                    String qEndName = endElement.getName().getLocalPart();
                    if (qEndName.equalsIgnoreCase("row") && conditionHolds) {
                        endMarker = counterOne;

                        // if at end of rows and condition holds put marker of start tag and end tag of that row in arr
                        markers.add(beginMarker);
                        markers.add(endMarker);

                        //restart
                        conditionHolds = false;
                        equalsLHS = false;
                        LHSmarker = -1;
                        beginMarker = -1;
                        endMarker = -1;
                    }

                    break;

                case XMLStreamConstants.CHARACTERS:
                    Characters characters = event.asCharacters();

                    if (operator.equals("=")) {
                        if (characters.getData().equals(conditionRHS) && counterOne == LHSmarker + 1 && equalsLHS) {
                            conditionHolds = true;
                        }
                    }
                    else if (operator.equals(">") && NumberUtils.isDigits(characters.getData()) &&
                            NumberUtils.isDigits(conditionRHS) && counterOne == LHSmarker + 1 && equalsLHS) {
                        int x = Integer.parseInt(characters.getData());
                        int y = Integer.parseInt(conditionRHS);
                        if (x > y) {
                            conditionHolds = true;
                        }
                    }
                    else if (operator.equals("<") && NumberUtils.isDigits(characters.getData()) &&
                            NumberUtils.isDigits(conditionRHS) && counterOne == LHSmarker + 1 && equalsLHS) {
                        int x = Integer.parseInt(characters.getData());
                        int y = Integer.parseInt(conditionRHS);
                        if (x < y) {
                            conditionHolds = true;
                        }
                    }
                    equalsLHS = false; //restart boolean

                    break;

            }
            counterOne ++;

        }
        return markers;

    }

    //delete row -> copy all elements except delete section ito a new file then rename the new file then del the old one
    public void deleteRow (String conditionLHS, String operator, String conditionRHS, String tableName, String path)
            throws IOException, XMLStreamException {

        int counterTwo = 0;
        List<Integer> markers = this.getMarkers(conditionLHS, operator, conditionRHS, tableName, path);
        int  markersIndex = 0;
        if (markers.size() == 0) {
            System.out.println("Condition doesn't exist anywhere");
            return;
        }

        File source = new File(path+tableName + ".xml");
        File target = new File(path+"new.xml");

        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
        InputStream in = new FileInputStream(source);
        XMLEventReader reader = inputFactory.createXMLEventReader(in);

        OutputStream out = new FileOutputStream(target);
        XMLEventWriter writer = outputFactory.createXMLEventWriter(out);
        XMLEvent eventX;

        boolean end = false;
        while (reader.hasNext()) {
            eventX = reader.nextEvent();
            if (counterTwo == markers.get(markersIndex)&& markersIndex%2==0) {
                end = true;
                if((markersIndex+1)!=markers.size())
                    markersIndex++;
            }
            else if(counterTwo ==markers.get(markersIndex)+1&&markersIndex%2!=0){
                end = false;

                if((markersIndex + 1) != markers.size())
                    markersIndex++;

            }


            if(end == false) {
                writer.add(eventX);
            }
            counterTwo++;

        }

        in.close();
        out.close();
        writer.close();
        reader.close();
        source = null;
        System.gc();
        Files.delete(Paths.get(path+tableName + ".xml"));
        new File(path+"new.xml").renameTo(new File(path+tableName + ".xml"));
    }

    //get row -> copy only get section into a new file
    private String getRow (String conditionLHS, String operator, String conditionRHS, String tableName, String path)
            throws IOException, XMLStreamException {

        int counterTwo = 0;
        List<Integer> markers = this.getMarkers(conditionLHS, operator, conditionRHS, tableName, path);
        int  markersIndex = 0;
        StringWriter stringWriter = new StringWriter();

        File source = new File(path+tableName + ".xml");

        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
        InputStream in = new FileInputStream(source);
        XMLEventReader reader = inputFactory.createXMLEventReader(in);


        XMLEventWriter writer = outputFactory.createXMLEventWriter(stringWriter);
        XMLEvent eventX;

        boolean end = false;

        while (reader.hasNext()) {

            eventX = reader.nextEvent();
            if (counterTwo == markers.get(markersIndex) && markersIndex % 2 == 0) {
                end = true;

                if((markersIndex + 1)!= markers.size())
                    markersIndex++;
            }
            else  if(counterTwo == markers.get(markersIndex) + 1 && markersIndex %2 != 0){
                end = false;

                if((markersIndex +1 )!=markers.size()) {
                    markersIndex++;
                }

            }


            if(end == true) {

                writer.add(eventX);

            }
            counterTwo++;
        }

        XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();
        StringWriter stringWriter1 = new StringWriter();

        //write xml
        XMLStreamWriter xmlStreamWriter = xmlOutputFactory.createXMLStreamWriter(stringWriter1);
        xmlStreamWriter.writeStartDocument();
        stringWriter.write("\n");
        xmlStreamWriter.writeStartElement(tableName);
        xmlStreamWriter.writeCharacters("\n");

        stringWriter1.append(stringWriter.getBuffer().toString());
        stringWriter.write("\n");
        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeEndDocument();

        String row = stringWriter1.getBuffer().toString();
        stringWriter.close();
        stringWriter1.close();

        return row;

    }

    //from row str -> 2D obj arr null pointer exception im user selection is not found
    public Object[][] getSelected (String conditionLHS, String operator, String conditionRHS,
                                   List<String> requiredColumns ,String tableName, String path)
            throws Exception {

        String selectedRows = this.getRow(conditionLHS, operator, conditionRHS, tableName, path);
        Object[][] rowsArray;

        int columnsSize = requiredColumns.size();

        byte[] byteArray = selectedRows.getBytes("UTF-8");
        ByteArrayInputStream inputStream = new ByteArrayInputStream(byteArray);
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        XMLEventReader reader = inputFactory.createXMLEventReader(inputStream);
        int rowSize = 0;
        List<String> data = new ArrayList<>();
        String qName = "";
        int counter=0;
        int temp=0;
        boolean y=false;
        for (int i = 0; i < columnsSize; i++) {
            data.add(requiredColumns.get(i));
        }

        while (reader.hasNext()) {

            XMLEvent e = reader.nextEvent();
            if (e.getEventType() == XMLStreamConstants.START_ELEMENT) {
                StartElement startElement = e.asStartElement();
                qName = startElement.getName().getLocalPart();
                if (qName.equalsIgnoreCase("row")) {
                    rowSize ++;

                }
                for (int i=0;i<requiredColumns.size();i++)
                    if (qName.equalsIgnoreCase(requiredColumns.get(i))){
                        temp=counter;
                        y=true;
                    }

            }
            else if (e.getEventType() == XMLStreamConstants.CHARACTERS) {
                Characters c = e.asCharacters();
                String x = c.getData().toString();
                if(counter==temp+1&&y==true){
                    y=false;
                    data.add(x);

                }

            }
            counter++;
        }

        int k = 0;


        if (rowSize != 0 && columnsSize != 0) {
            rowsArray = new Object[rowSize + 1][requiredColumns.size()];
            for (int i = 0; i < rowSize+1; i++) {
                for (int j = 0; j < requiredColumns.size(); j++) {
                    String dataStr = data.get(k);
                    if (NumberUtils.isDigits(dataStr)) {
                        Integer dataInt = Integer.parseInt(dataStr);
                        rowsArray[i][j] = dataInt;
                    }
                    else {
                        rowsArray[i][j] = dataStr;
                    }
                    System.out.print(rowsArray[i][j] + "\t");
                    k++;
                }
                System.out.println();
            }

        }
        else {
            Exception x = new NullPointerException("no such row");
            throw x;
        }
        ;
        for(int i=0;i< rowsArray.length;i++) {
            for (int j = 0; j < rowsArray[0].length; j++)
                System.out.print(rowsArray[i][j]+"\t");
            System.out.println();
        }

        return rowsArray;

    }

    //return false if the table is not valid against it's schema
    public boolean isValid(String xmlPath, String xsdPath, String path) {

        try {
            XMLStreamReader reader =
                    XMLInputFactory.newInstance().createXMLStreamReader(new FileInputStream(path+xmlPath));

            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(new File(path+xsdPath));
            Validator validator = schema.newValidator();
            validator.validate(new StAXSource(reader));
            return true; //no exception is thrown

        } catch (SAXException e) {
            System.out.println("schema error sax exception\n");
            return false;
        } catch (IOException e) {
            System.out.println("schema error i-o exception\n");
            return false;
        } catch (XMLStreamException e) {
            System.out.println("file error xml stream exception");
            return false;
        }

    }


}

